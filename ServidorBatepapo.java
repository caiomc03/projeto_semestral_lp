import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;


public class ServidorBatepapo {
    
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<SocketCliente> clients = new LinkedList<>();

    public void start() throws IOException{
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta: " + PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException{
        System.out.println("Aguardando conexao de um cliente!");
        while(true){

            SocketCliente clientSocket = new SocketCliente(serverSocket.accept());
            clients.add(clientSocket);
            new Thread(() -> clientMessageLoop(clientSocket)).start();

        }
    }

    private void clientMessageLoop(SocketCliente clientSocket){

        clientLoginLoop(clientSocket);




        System.out.printf("%s logged in\n", clientSocket.getRemoteSocketAddress());

        String msg;
        try
        {
            
            while((msg = clientSocket.getMessage()) != null){
                if("sair".equalsIgnoreCase(msg)) return;
                System.out.printf("<- Client %s: %s\n", 
                                clientSocket.getRemoteSocketAddress(), msg);
                sendMsg2Client(clientSocket, msg);
                // sendMsgToAll(clientSocket, msg);
            }

        }

        finally
        {
            clientSocket.close();
        }

    }

    private void clientLoginLoop(SocketCliente clientSocket){
        String login_info;
        // String usr_login;
        // String usr_password;
        Boolean login_status = false;

        while((login_info = clientSocket.getMessage()) != null){
                // System.out.println(login_info);

                //                                          separar admin|admin
                // String[] parts = login_info.split("|");
                // usr_login = parts[0];
                // usr_password = parts[1];
            
            System.out.println(login_info);

            // if(checkUserInfo(login_info)){
            //     login_status = true;
            //     System.out.println(login_info);
            // }
            // if(login_status == true){
            //     break;
            // }


        }
    }


    // private void sendMsgClient2Client(SocketCliente recipient, SocketCliente sender, String msg){
    //     if (clients.contains(recipient) && !sender.equals(recipient)) {
    //         recipient.sendMsg("Cliente " + sender.getRemoteSocketAddress() + ": " + msg);
    //     }
    // }

    private void sendMsg2Client(SocketCliente sender, String msg){

        Iterator<SocketCliente> iterator = clients.iterator();
        while(iterator.hasNext()){
            SocketCliente clientSocket = iterator.next();
            if(sender.equals(clientSocket)){
                if(!clientSocket.sendMsg("[" + sender.getRemoteSocketAddress() + "] : " + msg)){
                    iterator.remove();
                }
            }   
        }
    }


    // private void sendMsgToAll(SocketCliente sender, String msg){

    //     Iterator<SocketCliente> iterator = clients.iterator();
    //     while(iterator.hasNext()){
    //         SocketCliente clientSocket = iterator.next();
    //         if(!sender.equals(clientSocket)){
    //             if(!clientSocket.sendMsg("Cliente gay" + sender.getRemoteSocketAddress() + ": " + msg)){
    //                 iterator.remove();
    //             }
    //         }   
    //     }
    // }

    private Boolean checkUserInfo(String input){
        // String[] userList = {"admin|admin"};
        String userList = "admin|admin";

        if(input == userList){
            return true;
        }
        else{
            return false;
        }
    }

    public static void main(String agrs[]){
        System.out.println("*v*v*v* CONSOLE DO SERVIDOR *v*v*v*");
        try{
            ServidorBatepapo server = new ServidorBatepapo();
            server.start();
        }
        catch(IOException ex){
            System.out.println("Erro ao iniciar o servidor: " + ex.getMessage());
        }
        System.out.println("Servidor finalizado!");
    }

}
