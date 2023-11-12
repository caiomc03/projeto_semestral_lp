import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ClienteBatepapo implements Runnable {
    private SocketCliente clientSocket;
 
    private JFrame frame;
    private JTextField textField;
    private JButton button;
    private JButton saldoButton;

    private String usr_login;
    private boolean login_sucesso = false;

    String logstring;
   

    public ClienteBatepapo(){
       
        frame = new JFrame("Cliente Batepapo");
        textField = new JTextField(50); // aumentando o tamanho da caixa de texto
        button = new JButton("Enviar");
        saldoButton = new JButton("Verificar Saldo");

        // adicionando WindowListener para fechar o socket e a janela
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    clientSocket.close();
                } finally {
                    frame.dispose();
                }
            }
        });
    }


    public void start() throws IOException {
        try {
            clientSocket = new SocketCliente(new Socket(ServidorBatepapo.ADDRESS, ServidorBatepapo.PORT));
            new Thread(this).start();

            Login login = new Login();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ready");

            while(true){
                // System.out.println("looping");
                logstring = login.getLogmsg();
                if (logstring != "||empty||"){
                    break;
                }
            }


            logstring = login.getLogmsg();
            String[] parts = logstring.split("---");
            usr_login = parts[1];
            clientSocket.sendMsg(logstring); //login---
    
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(login_sucesso){
                messageLoop();
            }
            else{
                System.out.println("Login ou senha incorretos!");
            }


            }
      
         finally {
            clientSocket.close();
        }
    }

    @Override
    public void run()
    {
        String msg;
        while((msg = clientSocket.getMessage()) != null){

            if(msg.equals("Login efetuado com sucesso!")){
                login_sucesso = true; //trocar por um setter

            }

            System.out.printf("\n-> %s\n", msg);
            System.out.print("Digite uma mensagem (ou <sair> para finalizar): \n<-");
        }
    }

    private void messageLoop() throws IOException{

        frame.setLayout(new FlowLayout());
        frame.add(textField);
        frame.add(button);
        frame.add(saldoButton);
        frame.setSize(600, 200); // aumentando o tamanho da janela
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textField.getText();
                clientSocket.sendMsg(msg);
                textField.setText("");
                if (msg.equals("sair")) {
                    try {
                        clientSocket.close();

                    } finally {
                        System.exit(0);
                    }
                }
            }
        });
        saldoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String msg = SqlUtils.getSaldoQuery(usr_login);
                clientSocket.sendMsg(msg);
            }
        });


        System.out.println("Digite uma mensagem (ou <sair> para finalizar):");
        do
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(true);
    }

    public static void main(String args[]){
        System.out.println("*v*v*v* CONSOLE DO CLIENTE *v*v*v*");
        try{
            ClienteBatepapo client = new ClienteBatepapo();
            client.start();
        }
        catch(IOException ex){
            System.out.println("Erro ao iniciar o cliente: " + ex.getMessage());
        }
        System.out.println("Cliente finalizado! ");
    }
    
}


