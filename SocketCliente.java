import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;


public class SocketCliente {
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter saida;

    public SocketCliente(final Socket socket) throws IOException{
        this.socket = socket;
        System.out.println("Conectado com " + socket.getRemoteSocketAddress() + "!");
        this.entrada = new BufferedReader(new InputStreamReader (socket.getInputStream()));
        this.saida = new PrintWriter(socket.getOutputStream(), true);
    }

    public SocketAdress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }
    public void close(){
        try{
            entrada.close();
            saida.close();
            socket.close();
        }
        catch(IOException ex){
            System.out.println("Erro o fechar socket: " + ex.getMessage());
        }
    }
    public String getMessage(){
        try{
            return entrada.readLine();
        }
        catch(IOException ex){
            return null;
        }
    }
    public boolean sendMsg(String msg){
        saida.println(msg);
        return !saida.checkError();
    }
}
