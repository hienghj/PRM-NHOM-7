using Microsoft.AspNetCore.SignalR;
using System.Threading.Tasks;

namespace schand.Hubs
{
    public class ChatHub : Hub
    {
        public async Task SendMessage(string receiverId, object message)
        {
            await Clients.User(receiverId).SendAsync("ReceiveMessage", message);
        }
    }
}
