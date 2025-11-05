//Pacotes do Java que contêm as classes necessários para a interface gráfica e eventos.
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Classe principal da Calculadora.
 * Herda de JFrame, o que significa que esta classe É uma janela.
 * Implementa KeyListener, o que significa que ela "promete" ter métodos para responder
 * a eventos do teclado.
 */

public class CalculadoraSwing extends JFrame implements KeyListener {

    // Essa parte é a declaração das variáveis

    // Esse são os componentes da interface gráfica.
    private JTextField display;
    private JLabel equationLabel;
    private JPanel panelBotoes;
    private JPanel displayPanel;

    //Esses são as variáveis de estado lógico.
    private double primeiroNumero;
    private String primeiroNumeroTexto;
    private String operadorPendente;
    private boolean limpadorDisplay;

    //Construtor da classe. É aqui que toda a interface é montada e configurada.
    public CalculadoraSwing() {

        //1- Configuração da janela (JFrame)
        super("Calculadora Científica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350,450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //2- Configuração do teclado.
        addKeyListener(this);
        setFocusable(true);

        //3- Criação do painel de visores.
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.BLACK);;

        equationLabel = new JLabel("");
        equationLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        equationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        equationLabel.setForeground(Color.LIGHT_GRAY);
        equationLabel.setBorder(BorderFactory.createEmptyBorder(5,10,0,10));
        displayPanel.add(equationLabel, BorderLayout.NORTH);

        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 40));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(Color.BLACK);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));

        displayPanel.add(display, BorderLayout.CENTER);

        //4 - Criação do painel de botões.
        panelBotoes = new JPanel();
        panelBotoes.setLayout(new GridLayout(5,4,5,5));

        String[] textosBotoes = {
            "C", "√", "^", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "Primo", "0", ".", "="
        };

        ButtonClickListener listener = new ButtonClickListener(); //Um único "ouvinte" para todos os botões.

        for (String texto : textosBotoes) {

            JCustomButton botao = new JCustomButton(texto);

            if (texto.equals("Primo")){
                botao.setFont(new Font("Arial", Font.BOLD, 16));
            }
            else {
                botao.setFont(new Font("Arial", Font.BOLD, 20));
            }

            botao.setFocusable(false); //Remove uma borda de foco que pode aparecer ao clicar.

            if ("C/=*+-^√".contains(texto) || texto.equals("Primo")) {
                botao.setBackground(new Color(255, 153,0)); //Laranja para operadores.
                botao.setForeground(Color.WHITE);
            }
            else {
                botao.setBackground(new Color(64,64,64)); //Cinza escuro para números.
                botao.setForeground(Color.WHITE);
            }

            botao.addActionListener(listener); // Adiciona o "ouvinte" ao botão.
            panelBotoes.add(botao);
        }

        //5- Adição dos componentes á janela.
        add(displayPanel, BorderLayout.NORTH);
        add(panelBotoes, BorderLayout.CENTER);

        //6- Inicialização do estado da Calculadora.
        operadorPendente = "";
        primeiroNumero = 0;
        limpadorDisplay = true; //Começamos prontos para substituir o "0" inicial.

    }

    /**
     * Método obrigatório do KeyListener. É chamado quando uma tecla é pressionada.
     */

    @Override
    public void keyPressed(KeyEvent e) {

        char keyChar = e.getKeyChar();
        int  keyCode = e.getKeyCode();

        // Roteia o evento do teclado para o método de lógica apropriado.
        if (keyChar >= '0' && keyChar <= '9') {
            handleNumero(String.valueOf(keyChar));
        }
        else if (keyChar == '.') {
            handleNumero(".");
        }
        else if (keyChar == '+') {
            handleOperador("+");
        }
        else if (keyChar == '-') {
            handleOperador("-");
        }
        else if (keyChar == '*') {
            handleOperador("*");
        }
        else if (keyChar == '/') {
            handleOperador("/");
        }
        else if (keyCode == KeyEvent.VK_ENTER) {
            handleEquals();
        }
        else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            handleClear();
        }
        else if (keyCode == KeyEvent.VK_DELETE) {
            handleClear();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {} // Não precisamos deste.

    @Override
    public void keyReleased(KeyEvent e) {} // Nem deste.
    /**
     * Classe interna que "ouve" os cliques nos botões.
     * Uma única instância dela serve para todos os botões.
     */

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand();

            // Roteia o comando para o método de lógica apropriado.
            if ("0123456789.".contains(comando)) {
                handleNumero(comando);
            }
            else if ("C".equals(comando)) {
                handleClear();
            }
            else if ("=".equals(comando)) {
                handleEquals();
            }
            else if ("Primo".equals(comando)) {
                handlePrimeCheck();
            }
            else {
                handleOperador(comando);
            }
        }
    }

    /**
     * Verifica se o número atual no visor é primo.
     */

    private void handlePrimeCheck() {
        try {
            double numeroDouble = Double.parseDouble(display.getText());

            if (numeroDouble != (long) numeroDouble) {
                equationLabel.setText("Não é inteiro");
                return;
            }

            long numero = (long) numeroDouble;

            boolean isPrimeResult = isPrime(numero);

            if (isPrimeResult) {
                equationLabel.setText(numero + " é Primo");                
            }
            else {
                equationLabel.setText(numero + " não é Primo");
            }
        }
        catch (NumberFormatException ex) {
            equationLabel.setText("Número inválido");
        }
    }

    private boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }

        if (n == 2) {
            return true;
        }

        if (n % 2 == 0) {
            return false;
        }

        for (long i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Lida com a entrada de dígitos (0-9) e do ponto decimal.
     * Atualiza os visores em tempo real.
     */

    private void handleNumero(String num) {

        if (equationLabel.getText().contains("=") || equationLabel.getText().contains("Primo")) {
            equationLabel.setText("");
        }

        String textoAtual = display.getText();

       if (limpadorDisplay || textoAtual.equals("0")) {
            if (num.equals(".")) display.setText("0.");            
            else display.setText(num);
            limpadorDisplay = false;
       }
       else {
            if (num.equals(".") && textoAtual.contains(".")) return;
            display.setText(textoAtual + num);
        }
        
        if (operadorPendente.isEmpty()) {
            equationLabel.setText(display.getText());
        }
        else {
            equationLabel.setText(primeiroNumeroTexto + " " + operadorPendente + " " + display.getText());
        }

    }

    /**
     * Reseta a calculadora para o estado inicial.
     */

    private void handleClear() {
        display.setText("0");
        equationLabel.setText("");
        operadorPendente = "";
        primeiroNumero = 0;
        primeiroNumeroTexto = "";
        limpadorDisplay = true;
    }

    /**
     * Lida com o clique em um operador (+, -, *, /, ^, √).
     */

    private void handleOperador(String op) {
        if (!operadorPendente.isEmpty() && !limpadorDisplay) {
            handleEquals();
        }

        String numeroAtualNoVisor = display.getText();
        primeiroNumeroTexto = numeroAtualNoVisor;

        primeiroNumero = Double.parseDouble(numeroAtualNoVisor);
        operadorPendente = op;

        equationLabel.setText(numeroAtualNoVisor + " " + op);
        limpadorDisplay = true;

        if (op.equals("√")) {
            double resultado = Math.sqrt(primeiroNumero);
            equationLabel.setText("√(" + numeroAtualNoVisor + ")");
            display.setText(formatarResultado(resultado));
            operadorPendente = "";
        }
    }

    /**
     * Realiza o cálculo final quando o botão "=" é pressionado.
     */

    private void handleEquals() {
        if (operadorPendente.isEmpty()) {
            return;
        }

        String segundoNumeroTexto = display.getText();
        double segundoNumero = Double.parseDouble(display.getText());
        double resultado = 0;

        equationLabel.setText(primeiroNumeroTexto + " " + operadorPendente + " " + segundoNumeroTexto + " = ");

        switch (operadorPendente) {
            case "+": resultado = primeiroNumero + segundoNumero; break;
            case "-": resultado = primeiroNumero - segundoNumero; break;
            case "*": resultado = primeiroNumero * segundoNumero; break;
            case "^": resultado = Math.pow(primeiroNumero, segundoNumero); break;
            case "/":
                if (segundoNumero == 0) {
                    display.setText("Erro");
                    operadorPendente = "";
                    limpadorDisplay = true;
                    return;
                }

                resultado = primeiroNumero / segundoNumero;
                break;
        }

        display.setText(formatarResultado(resultado));
        operadorPendente = "";
        limpadorDisplay = true;
    }

    /**
     * Formata um número 'double' para exibição limpa no visor.
     * Remove o ".0" de inteiros e limita as casas decimais.
     */

    private String formatarResultado(double resultado) {
        if (resultado == (long) resultado) {
            return String.format(Locale.US,"%d", (long) resultado);
        }
        else {
            DecimalFormat df = new DecimalFormat("#.##########");
            df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
            return df.format(resultado);
        }
    }

    /**
     * O método principal que inicia o programa.
     */

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            CalculadoraSwing calc = new CalculadoraSwing();
            calc.setVisible(true);
        });
        
    }
}

/**
 * Uma classe de botão personalizada que usa RenderingHints para desenhar
 * o texto com anti-aliasing, deixando-o mais suave e profissional.
 */

class JCustomButton extends JButton {
    
    public JCustomButton(String text) {
        super(text);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        super.paintComponent(g);
    }
}
