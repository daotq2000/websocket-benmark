#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080"

# Test 1: Get conversations for john_doe
echo "Test 1: Get conversations for john_doe"
curl -X GET "${BASE_URL}/api/chat/conversations?userId=john_doe" \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -v

echo -e "\n\n"

# Test 2: Get messages for conversation 1
echo "Test 2: Get messages for conversation 1"
curl -X GET "${BASE_URL}/api/chat/conversation/1/messages" \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -v

echo -e "\n\n"

# Test 3: Send a message from john_doe to jane_smith
echo "Test 3: Send a message from john_doe to jane_smith"
curl -X POST "${BASE_URL}/api/chat/send" \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "recipientId": "jane_smith",
    "content": "This is a test message sent via curl!"
  }' \
  -v

echo -e "\n\n"

# Test 4: WebSocket connection test using wscat (if installed)
echo "Test 4: WebSocket connection test"
echo "To test WebSocket connection, install wscat and run:"
echo "wscat -c ws://localhost:8080/ws/websocket"
echo "Then subscribe to your personal queue:"
echo '{"command":"subscribe","headers":{"destination":"/user/john_doe/queue/messages"}}'
echo "And send a message:"
echo '{"command":"send","headers":{"destination":"/app/chat.send"},"body":"{\"recipientId\":\"jane_smith\",\"content\":\"Test WebSocket message\"}"}' 