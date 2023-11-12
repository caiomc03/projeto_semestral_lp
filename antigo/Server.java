package antigo;
import java.io.IOException;
import java.net.ServerSocket;

import ClientSocket;

public class Server
{
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 4000; //ou 3334
    private ServerSocket serverSocket;

    public void start() throws IOException
    {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta: "+ PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException
    {
        System.out.println("Aguardando conexao de um cliente!");
        do
        {
            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        } while(true);
    }

    public void clientMessageLoop(ClientSocket clientSocket){
        String msg;
        while(true){
            
            if((msg = clientSocket.getMessage()) != null && !msg.equalsIgnoreCase("sair"));
                {
                    System.out.printf("Mensagem recebida do cliente %s: %s\n", clientSocket.getRemoteSocketAddress(),msg);

                }
            if(msg.equalsIgnoreCase("sair")){
                clientSocket.close();
                System.out.printf("Cliente  %s desconctado \n", clientSocket.getRemoteSocketAddress());
                break;
            }
        }
      
        

    }

    public static void main(String args [])
    {
        System.out.println("*v*v*v* CONSOLE DO SERVIDOR *v*v*v*");
        try{
            Server server = new Server();
            server.start();
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao inciar o servidor: " + ex.getMessage());

        }

        System.out.println("Servidor finalizado!");
    }
}