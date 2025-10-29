using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace schand.Helper
{
    public static class PayOSHelpers
    {
        public static string HmacSha256Hex(string key, string data)
        {
            using var h = new HMACSHA256(Encoding.UTF8.GetBytes(key));
            var hash = h.ComputeHash(Encoding.UTF8.GetBytes(data));
            return BitConverter.ToString(hash).Replace("-", "").ToLowerInvariant();
        }

        // build "key=value&key=value" từ JsonElement data (sort alphabet)
        // ✅ thêm tham số ignoreSignatureField để bỏ qua "signature" nếu có
        public static string BuildSignatureStringFromData(JsonElement data, bool ignoreSignatureField = false)
        {
            var dict = new SortedDictionary<string, string>(StringComparer.Ordinal);
            foreach (var p in data.EnumerateObject())
            {
                if (ignoreSignatureField && string.Equals(p.Name, "signature", StringComparison.OrdinalIgnoreCase))
                    continue;

                var v = p.Value;
                string valueStr = v.ValueKind switch
                {
                    JsonValueKind.Null => "",
                    JsonValueKind.String => v.GetString() ?? "",
                    JsonValueKind.Number => v.ToString(),
                    JsonValueKind.True => "true",
                    JsonValueKind.False => "false",
                    _ => v.ToString() // array/object: JsonElement -> minified string
                };
                dict[p.Name] = valueStr;
            }
            return string.Join("&", dict.Select(kv => $"{kv.Key}={kv.Value}"));
        }
        public static bool VerifySignatureDual(string checksumKey, JsonElement data, string providedSig)
        {
            // C1: key=value (sort) – bỏ qua field signature nếu có
            var kv = BuildSignatureStringFromData(data, ignoreSignatureField: true);
            var sigKv = HmacSha256Hex(checksumKey, kv);

            // C2: raw json (minified)
            var raw = data.GetRawText();
            var sigRaw = HmacSha256Hex(checksumKey, raw);

            return string.Equals(providedSig, sigKv, StringComparison.OrdinalIgnoreCase)
                || string.Equals(providedSig, sigRaw, StringComparison.OrdinalIgnoreCase);
        }


        // orderCode của PayOS là INT. Hàm này sinh số int gần-unique theo thời gian.
        public static int NewOrderCode()
        {
            /// Lấy Unix time theo giây (long), rồi ép về int một cách an toàn
            // 2_000_000_000 < int.MaxValue, nên luôn an toàn
            var unix = DateTimeOffset.UtcNow.ToUnixTimeSeconds(); // long ~ 1.7e9 hiện tại
            int baseCode = (int)(unix % 2_000_000_000);           // đảm bảo < int.MaxValue

            // Thay 3 số cuối bằng ngẫu nhiên để giảm va chạm khi nhiều request trong 1s
            int suffix = Random.Shared.Next(0, 1000);             // 0..999
            return baseCode - (baseCode % 1000) + suffix;         // cùng kích thước, không tràn
        }
    }
}
