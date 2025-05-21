-- Create sample users
INSERT INTO users (id, username, email, created_at) VALUES
(1, 'john_doe', 'john@example.com', CURRENT_TIMESTAMP),
(2, 'jane_smith', 'jane@example.com', CURRENT_TIMESTAMP);

-- Create sample conversations
INSERT INTO chat_conversation (user_1, user_2, created_at) VALUES
('john_doe', 'jane_smith', CURRENT_TIMESTAMP);

-- Create sample messages
INSERT INTO chat_message (conversation_id, sender_id, content, created_at) VALUES
(1, 'john_doe', 'Hi Jane! How are you?', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(1, 'jane_smith', 'Hello John! I''m good, thanks for asking. How about you?', CURRENT_TIMESTAMP - INTERVAL '55 minutes'),
(1, 'john_doe', 'I''m doing great! Just working on some new features.', CURRENT_TIMESTAMP - INTERVAL '50 minutes'),
(1, 'jane_smith', 'That sounds interesting! What kind of features?', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(1, 'john_doe', 'I''m implementing a real-time chat system with WebSocket!', CURRENT_TIMESTAMP - INTERVAL '40 minutes'); 