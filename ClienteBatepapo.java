import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ClienteBatepapo implements Runnable {
    private SocketCliente clientSocket;
 
    private JFrame frame;

    JPanel panel;
    JPanel panel1;
    JPanel panel2;


    //for panel1
    private JLabel numberLabel;
    private JTextField textField;

    Boolean mostrarSaldo = false;
    JButton button_VerifSaldo;
    JButton button_Sacar;
    JButton button_Depositar;
    JButton button_Deletar;
    JButton button_Sair;
    JButton button_GerarBoleto;

    private String usr_login;
    private boolean login_sucesso = false;
    private double balance = 0.0;
    private String logstring;

    private String usuario;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String contato;
    private String genero;

    CryptoDummy cdummy = new CryptoDummy();
    String dummyPath = "chave.dummy";

    byte[]   bMsgClara = null;
    byte[]   bMsgCifrada = null;



    public void setBalance(double _balance) {
        balance = _balance;
    }
   
    public ClienteBatepapo(){

       
        frame = new JFrame("Cliente ");
        panel = new JPanel();


        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        // PANEL 1
        numberLabel = new JLabel("Saldo Conta: ---");
        panel1.add(numberLabel);
        textField = new JTextField(5); // 15 columns
        panel1.add(textField);
        

        panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        button_VerifSaldo = new JButton("Verificar Saldo");
        panel2.add(button_VerifSaldo);
        button_Sacar = new JButton("Sacar Dinheiro");
        panel2.add(button_Sacar);
        button_Depositar = new JButton("Depositar");
        panel2.add(button_Depositar);
        button_GerarBoleto = new JButton("Gerar Boleto");
        panel2.add(button_GerarBoleto);
        button_Sair = new JButton("Sair");
        panel2.add(button_Sair);
        button_Deletar = new JButton("Deletar Conta");
        panel2.add(button_Deletar);

        panel.add(panel1);
        panel.add(panel2);

        // adicionando data no canto superior direito do frame
        JLabel dateLabel = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        frame.add(dateLabel, BorderLayout.NORTH);

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

        // adicionando JMenu com botão "Profile"
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem profileItem = new JMenuItem("Profile");
        profileItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                clientSocket.sendMsg("sqlgetall---"+usr_login);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                
                String[] colunas = {"Nome de usuario","Nome Completo", "Email", "CPF", "Telefone", "Genero"};
                Object[][] dados = {
                    {usuario,nomeCompleto,email,cpf,contato,genero},
                };
                JTable table = new JTable(dados, colunas);

                
                JFrame frame = new JFrame("Informações do usuario");
                frame.add(new JScrollPane(table));
                frame.setSize(800, 100); // set the size of the frame
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

        });
        menu.add(profileItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
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

                if(login.getCadStart()){
                    //aqui calvetti
                    Cadastro cadastro = new Cadastro();
                    //while loop dentro do cadastro
                    String cadstring = cadastro.wait_for_cadastro_input();
                    clientSocket.sendMsg(cadstring);
                    System.out.println(cadstring);
                    login.setCadStartFALSE();

                }
                
                if(!login.getCadStart()){  
                    logstring = login.getLogmsg();
                    if (logstring != "||empty||"){
                        break;
                    }
                }
            }

            //aqui calvetti
            logstring = login.getLogmsg();
            String[] parts = logstring.split("---");
            usr_login = parts[1];
            try{
            logstring = parts[0] + "---" + (cdummy.autoCifra(parts[1], new File(dummyPath))) + "---" + (cdummy.autoCifra(parts[2], new File(dummyPath)));
            }
            catch(Exception e){
            }

            clientSocket.sendMsg(logstring); //login---


    
            try {
                Thread.sleep(1000);
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
            else if(msg.equals("---delete---")  ){
                System.out.println(msg.split("---")[0]);
                System.out.println("Conta deletada com sucesso!");
                frame.dispose();
                System.exit(0);

            }

            else if(msg.split("---")[0].equals("balance") || msg.split("---")[0].equals("newbalance")  ){
                setBalance(Double.parseDouble(msg.split("---")[1]));
                
                if(mostrarSaldo == false){
                    numberLabel.setText("Saldo Conta: ---");
                }
                else{
                    numberLabel.setText("Saldo Conta: " + balance);
                }
                
                System.out.println("Seu saldo é: " + balance); 
            }

            else if(msg.split("---")[0].equals("profile")){
                System.out.println(msg);
                usuario = msg.split("---")[1];
                nomeCompleto = msg.split("---")[2];
                email = msg.split("---")[3];
                cpf = msg.split("---")[4];
                contato = msg.split("---")[5];
                genero = msg.split("---")[6];

            }

            System.out.printf("\n-> %s\n", msg);
            System.out.print("Digite uma mensagem (ou <sair> para finalizar): \n<-");
        }
    }

    private void messageLoop() throws IOException{

        frame.setLayout(new FlowLayout());

        frame.setSize(1000, 200); // aumentando o tamanho da janela
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        frame.add(panel);

        cdummy = new CryptoDummy();

     button_Deletar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "sqldeleteuser---" + usr_login ;
                clientSocket.sendMsg(msg);
                System.exit(0);
            }
        });
       
        
        button_VerifSaldo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = SqlUtils.getSaldoQuery(usr_login);
                clientSocket.sendMsg(msg);
                if(mostrarSaldo == false){
                    mostrarSaldo = true;
                }
                else{
                    mostrarSaldo = false;
                }


            }
        });
        button_Sair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              frame.dispose();
              System.exit(0);

            }
        });
        button_Sacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double value;
                String text = textField.getText();
                if (text.equals("")) {
                    value = 0;
                } else {
                    value = Double.parseDouble(text);
                }
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login, 0, value); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
            }
        });
        button_Depositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double value;
                String text = textField.getText();
                if (text.equals("")) {
                    value = 0;
                } else {
                    value = Double.parseDouble(text);
                }
                String msg = SqlUtils.getUpdateSaldoQuery(usr_login,value,0); //pegar valores de uma caixa de texto
                clientSocket.sendMsg(msg);
            }
        });
        button_GerarBoleto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame boletoFrame = new JFrame("Gerar Boleto");
                JPanel boletoPanel = new JPanel();
                JLabel boletoLabelPagador = new JLabel("Pagador:");
                JLabel boletoLabelRecebedor = new JLabel("Recebedor:");
                JLabel boletoLabelValor = new JLabel("Valor:");
                JTextField boletoFieldPagador = new JTextField(20);
                JTextField boletoFieldRecebedor = new JTextField(20);
                JTextField boletoFieldValor = new JTextField(20);
                JButton boletoButtonGerar = new JButton("Gerar Boleto");

                boletoPanel.add(boletoLabelPagador);
                boletoPanel.add(boletoFieldPagador);
                boletoPanel.add(boletoLabelRecebedor);
                boletoPanel.add(boletoFieldRecebedor);
                boletoPanel.add(boletoLabelValor);
                boletoPanel.add(boletoFieldValor);
                boletoPanel.add(boletoButtonGerar);
                boletoFrame.add(boletoPanel);
                boletoFrame.setSize(220,280);
                boletoFrame.setVisible(true);
                boletoFrame.setLocationRelativeTo(null);

                boletoButtonGerar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String pagador = boletoFieldPagador.getText();
                    String recebedor = boletoFieldRecebedor.getText();
                    double valor = Double.parseDouble(boletoFieldValor.getText());
                    Boleto boleto = new Boleto(pagador, recebedor, valor);
                    boleto.gerarArquivo();
                    boletoFrame.dispose();
                    
                }
                });
            }
        });

        
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
