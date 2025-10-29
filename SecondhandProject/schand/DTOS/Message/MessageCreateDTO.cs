namespace schand.DTOS.Message
{
    public class MessageCreateDTO
    {
        public int ReceiverId { get; set; }
        public int ProductId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
