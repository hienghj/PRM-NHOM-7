namespace schand.DTOS.Message
{
    public class MessageReadDTO
    {
        public int Id { get; set; }
        public int SenderId { get; set; }
        public string SenderName { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime SentAt { get; set; }
        public bool IsMine { get; set; } // <== thêm vào để FE dùng

    }
}
