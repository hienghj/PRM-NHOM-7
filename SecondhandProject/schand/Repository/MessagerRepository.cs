using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.Models;
using System;

namespace schand.Repository
{

    
        public class MessagerRepository : IMessageRepository
    {
            private readonly SchuserContext _context;

            public MessagerRepository(SchuserContext context)
            {
                _context = context;
            }

            public async Task CreateAsync(Messager message)
            {
                message.SentAt = DateTime.UtcNow;
                _context.Messagers.Add(message);
                await _context.SaveChangesAsync();
            }

            public async Task<IEnumerable<Messager>> GetMessagesAsync(int user1Id, int user2Id, int productId)
            {
                return await _context.Messagers
                    .Where(m =>
                        ((m.SenderId == user1Id && m.ReceiverId == user2Id) ||
                         (m.SenderId == user2Id && m.ReceiverId == user1Id)) &&
                         m.ProductId == productId)
                    .OrderBy(m => m.SentAt)
                    .Include(m => m.Sender)
                    .ToListAsync();
            }

            public async Task<IEnumerable<User>> GetContactsAsync(int userId)
            {
                var senderIds = await _context.Messagers
                    .Where(m => m.SenderId == userId)
                    .Select(m => m.ReceiverId)
                    .Distinct()
                    .ToListAsync();

                var receiverIds = await _context.Messagers
                    .Where(m => m.ReceiverId == userId)
                    .Select(m => m.SenderId)
                    .Distinct()
                    .ToListAsync();

                var contactIds = senderIds.Union(receiverIds).Distinct();
                return await _context.Users
                    .Where(u => contactIds.Contains(u.Id))
                    .ToListAsync();
            }

            public async Task<bool> DeleteAsync(int messageId, int userId)
            {
                var message = await _context.Messagers.FirstOrDefaultAsync(m => m.Id == messageId && m.SenderId == userId);
                if (message == null) return false;

                _context.Messagers.Remove(message);
                await _context.SaveChangesAsync();
                return true;
            }
        }

    }
