import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Cadastro extends JFrame {
    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JTextField nomeCompletoField;
    private JTextField cpfField;
    private JTextField contatoField;
    private JTextField emailField;
    private JComboBox<String> generoComboBox; // Adicionando o JComboBox para selecionar o gênero
    private String cadmsg;
    public Boolean cadSent = false;

    public Cadastro() {
        super("Formulário de Cadastro");

        // Cria os componentes gráficos
        JLabel usuarioLabel = new JLabel("Usuário:");
        usuarioField = new JTextField(20);

        JLabel senhaLabel = new JLabel("Senha:");
        senhaField = new JPasswordField(20);

        JLabel confirmarSenhaLabel = new JLabel("Confirmar senha:");
        confirmarSenhaField = new JPasswordField(20);

        JLabel nomeCompletoLabel = new JLabel("Nome completo:");
        nomeCompletoField = new JTextField(20);

        JLabel cpfLabel = new JLabel("CPF:");
        cpfField = new JTextField(20);

        JLabel contatoLabel = new JLabel("Contato:");
        contatoField = new JTextField(20);

        JLabel emailLabel = new JLabel("E-mail:");
        emailField = new JTextField(20);

        JLabel generoLabel = new JLabel("Gênero:");
        String[] generos = {"Masculino", "Feminino", "Outros"}; // Opções do JComboBox
        generoComboBox = new JComboBox<>(generos);

        JButton cadastrarButton = new JButton("Cadastrar");

        // Adiciona os componentes gráficos ao JFrame usando um layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        add(usuarioLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(usuarioField, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(senhaLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(senhaField, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        add(confirmarSenhaLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(confirmarSenhaField, c);
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        add(nomeCompletoLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(nomeCompletoField, c);
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_END;
        add(cpfLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(cpfField, c);
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_END;
        add(contatoLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(contatoField, c);
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        add(emailLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(emailField, c);
        c.gridx = 0;
        c.gridy = 7;
        c.anchor = GridBagConstraints.LINE_END;
        add(generoLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(generoComboBox, c);
        c.gridx = 1;
        c.gridy = 8;
        c.anchor = GridBagConstraints.CENTER;
        add(cadastrarButton, c);

        // Adiciona um listener para o botão de cadastro
        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrar();
                setVisible(false);
                // System.out.println(getCadmsg());

            }
        });

        // Configura o JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cadastrar() {
        // Obtém os valores dos campos de texto
        String usuario = usuarioField.getText();
        String senha = new String(senhaField.getPassword());
        String confirmarSenha = new String(confirmarSenhaField.getPassword());
        String nomeCompleto = nomeCompletoField.getText();
        String cpf = cpfField.getText();
        String contato = contatoField.getText();
        String email = emailField.getText();
        String genero = (String) generoComboBox.getSelectedItem(); // Obtendo o valor selecionado no JComboBox

        // Valida os campos de texto
        if (usuario.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty() || nomeCompleto.isEmpty() || cpf.isEmpty() || contato.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }


        else{
        JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        cadmsg = "cadastro" + "---" + usuario + "---" + senha + "---" + nomeCompleto + "---" + email + "---" + cpf + "---" + contato + "---" + genero; // Adicionando o gênero na mensagem de cadastro
        cadSent = true;
        }
    }

    public String getCadmsg(){
        if (cadSent == true){
            
            return cadmsg;
        }
        else{
            return "||empty||";
        }
    }
    public Boolean getCadSent(){
        return cadSent;
    }
    public String wait_for_cadastro_input(){
        while(!cadSent){
            System.out.print("");
        }
        return cadmsg;
    }
    public static void main(String args[]){

        Cadastro cadastro = new Cadastro();
        System.out.println(cadastro.wait_for_cadastro_input());
    }
}
