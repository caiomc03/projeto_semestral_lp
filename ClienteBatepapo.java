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
    private JButton updateSaldoButton;

    private String usr_login;
    private boolean login_sucesso = false;
    private String logstring;


    private double balance = 0.0;

    public void setBalance(double _balance) {
        balance = _balance;
    }
  

   

    public ClienteBatepapo(){
       
        frame = new JFrame("Cliente Batepapo");
        textField = new JTextField(50); // aumentando o tamanho da caixa de texto
        button = new JButton("Enviar");
        saldoButton = new JButton("Verificar Saldo");
        updateSaldoButton = new JButton("Atualiza Saldo");

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
            
            // System.out.printf("\n-> %s\n", msg);
            // if (msg.equals("-> Login efetuado com sucesso!")) {
            //     messageLoop();
                
            // }
            

            

            // if(usr_login.equals("admin") && usr_password.equals("admin")){
            //     System.out.println("Login efetuado com sucesso!");
            //     messageLoop();
            // }
            // else{
            //     Integer tentativas = 0;
            //     System.out.println("Login ou senha incorretos!");
            //     while(tentativas < 3){
            //         System.out.println("Digite novamente o login e a senha!");
            //         login = new Login();
            //         try {
            //             Thread.sleep(10000);
            //         } catch (InterruptedException e) {
            //             e.printStackTrace();
            //         }
            //         logstring = login.getLogmsg();
            //         parts = logstring.split("---");
            //         usr_login = parts[1];
            //         usr_password = parts[2];
            //         if(usr_login.equals("admin") && usr_password.equals("admin")){
            //             System.out.println("Login efetuado com sucesso!");
            //             messageLoop();
            //             break;
            //         }
            //         else{
            //             tentativas++;
            //         }
                // }


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

            else if(msg.split("---")[0].equals("balance---")){
                setBalance(Double.parseDouble(msg.split("---")[1])) ;
                System.out.println("Seu saldo é: " + balance); 
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
        frame.add(updateSaldoButton);
        frame.setSize(200, 100); // aumentando o tamanho da janela
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

        updateSaldoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String msg = SqlUtils.getUpdateSaldoQuery(usr_login,20,0); //pegar valores de uma caixa de texto
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


