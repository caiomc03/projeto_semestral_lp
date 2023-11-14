import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Boleto {
    private String nomePagador;
    private String nomeRecebedor;
    private double valor;
    private LocalDate dataHoje;
    private LocalDate dataVencimento;

    public Boleto(String nomePagador, String nomeRecebedor, double valor) {
        this.nomePagador = nomePagador;
        this.nomeRecebedor = nomeRecebedor;
        this.valor = valor;
        this.dataHoje = LocalDate.now();
        this.dataVencimento = LocalDate.now().plusDays(30);
    }

    public void gerarArquivo() {
        try {
            FileWriter writer = new FileWriter("boleto.txt");
            writer.write("Nome do Pagador: " + nomePagador + "\n");
            writer.write("Nome do Recebedor: " + nomeRecebedor + "\n");
            writer.write("Valor: " + valor + "\n");
            writer.write("Data de Hoje: " + dataHoje + "\n");
            writer.write("Data de Vencimento: " + dataVencimento + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Erro ao gerar arquivo: " + e.getMessage());
        }
    }


}
