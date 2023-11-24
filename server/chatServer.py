import socket
import threading

# List to store connected client sockets
connected_clients = []

messages_sent = []

def handle_client(client_socket, address):
    try:
        # Send existing messages to the newly connected client
        for message in messages_sent:
            client_socket.send(message.encode('utf-8'))

        print(f"Client {address} connected")

        while True:
            data = client_socket.recv(1024)
            if not data:
                break
            message = data.decode('utf-8')
            print(f"Received message from {address}")

            # Broadcast the message to all connected clients
            messages_sent.append(message)
            broadcast(message, client_socket)
    except Exception as e:
        print(f"Error handling client {address}: {e}")
    finally:
        # Remove the client socket from the list when the connection is closed
        connected_clients.remove(client_socket)
        client_socket.close()
        print(f"Client {address} disconnected")


def broadcast(message, sender_socket):
    # Iterate through all connected clients and send the message
    for client in connected_clients:
        # Avoid sending the message back to the sender
        if client != sender_socket:
            try:
                client.send(message.encode('utf-8'))
            except Exception as e:
                # Handle exceptions if a client is no longer reachable
                print(f"Error broadcasting message: {e}")

def start_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('0.0.0.0', 8888))
    server_socket.listen(5)

    print("Server listening on port 8888...")

    while True:
        client_socket, addr = server_socket.accept()
        print(f"Accepted connection from {addr}")

        # Add the client socket to the list of connected clients
        connected_clients.append(client_socket)

        # Start a thread to handle the client
        client_handler = threading.Thread(target=handle_client, args=(client_socket, addr))
        client_handler.start()

if __name__ == "__main__":
    start_server()
