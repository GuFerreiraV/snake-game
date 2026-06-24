
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int LARGURA_TELA = 1300;
    static final int ALTURA_TELA = 750;
    static final int TAMANHO_BLOCO = 50;
    static final int UNIDADE_JOGO = (LARGURA_TELA * ALTURA_TELA) / (TAMANHO_BLOCO * TAMANHO_BLOCO);
    static final int INTERVALO = 200; // milissegundos, velocidade da cobra
    private static final String NOME_FONTE = "Ink Free";
    private final int eixoX[] = new int[UNIDADE_JOGO];
    private final int eixoY[] = new int[UNIDADE_JOGO];
    private int corpoDaCobra = 6;
    private int blocosComidos; // irá aumentar de acordo com que vamos comendo a maçã
    private int blocoX;
    private int blocoY;
    private char direcao = 'D'; // D - direita, W - cima, A - esquerda, S - baixo

    private boolean estaRodando = false; // condição

    Timer timer; // Um recurso para threads que agenda tarefas para execução futura em um linha
                 // de fundo
    Random random;

    // Construtor
    GamePanel() {
        random = new Random();

        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA)); // Define a dimensão da tela

        setBackground(Color.white); // Cor de fundo da tela

        setFocusable(true);

        addKeyListener(new LeitorDeTeclasAdapter()); // addKeyListener - Irá ler as teclas especificadas

        iniciarJogo();
    }

    public void iniciarJogo() {
        criarBloco();
        estaRodando = true;
        timer = new Timer(INTERVALO, this);

        timer.start(); //
    }

    public void criarBloco() {
        blocoX = random.nextInt(LARGURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        blocoY = random.nextInt(ALTURA_TELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
    }

    public void desenharTela(Graphics g) {

        if (estaRodando) {
            g.setColor(Color.red); // todos os objetos desenhados serão da cor vermelho

            g.fillOval(blocoX, blocoY, TAMANHO_BLOCO, TAMANHO_BLOCO);// filOval está desenhando a maçã

            for (int i = 0; i < corpoDaCobra; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // Cor da cabeça da cobra
                    g.fillRect(eixoX[0], eixoY[0], TAMANHO_BLOCO, TAMANHO_BLOCO);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Cor do corpo da cobra
                    g.fillRect(eixoX[i], eixoY[i], TAMANHO_BLOCO, TAMANHO_BLOCO);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font(NOME_FONTE, Font.BOLD, 40)); // Define a fonte, tamanho e tipo da letra
            FontMetrics metrics = getFontMetrics(g.getFont()); // Define as métricas da fonte
            g.drawString("Pontos: " + blocosComidos,
                    (LARGURA_TELA - metrics.stringWidth("Pontos: " + blocosComidos)) / 2, g.getFont().getSize());
        } else {
            fimDeJogo(g);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenharTela(g);
    }

    private void cobraAndando() {
        for (int i = corpoDaCobra; i > 0; i--) {
            eixoX[i] = eixoX[i - 1];
            eixoY[i] = eixoY[i - 1];
        }
        switch (direcao) {
            case 'W':
                eixoY[0] = eixoY[0] - TAMANHO_BLOCO; // A cobra estará andando para cima
                break;
            case 'S':
                eixoY[0] = eixoY[0] + TAMANHO_BLOCO; // A cobra estará andando para baixo
                break;
            case 'A':
                eixoX[0] = eixoX[0] - TAMANHO_BLOCO; // A cobra estará andando para esquerda
                break;
            case 'D':
                eixoX[0] = eixoX[0] + TAMANHO_BLOCO; // A cobra estará andando para direita
                break;
            default:
                break;
        }
    }

    private void alcancarBloco() {
        /*
         * Verifica se a cabeça da cobra é igual ao blocoX,
         * se sim, o corpo irá aumentar de tamanho
         */
        if (eixoX[0] == blocoX && eixoY[0] == blocoY) {
            corpoDaCobra++;
            blocosComidos++;
            criarBloco();
        }
    }

    public void fimDeJogo(Graphics g) {
        g.setColor(Color.red); // Define a cor do fim do jogo
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 40));
        FontMetrics fontePontuacao = getFontMetrics(g.getFont());
        // Métricas de fonte
        g.drawString("Pontos: " + blocosComidos,
                (LARGURA_TELA - fontePontuacao.stringWidth("Pontos: " + blocosComidos)) / 2, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(new Font(NOME_FONTE, Font.BOLD, 75));
        FontMetrics fonteFinal = getFontMetrics(g.getFont());
        String textoFimDeJogo = "\uD83D\uDE1D Fim de Jogo.";
        g.drawString(textoFimDeJogo, (LARGURA_TELA - fonteFinal.stringWidth(textoFimDeJogo)) / 2,
                ALTURA_TELA / 2);
    }

    @Override
    // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBod
    public void actionPerformed(ActionEvent e) {
        if (estaRodando) {
            cobraAndando();
            alcancarBloco();
            validarLimites();
        }
        repaint();
    }

    private void validarLimites() {
        // A cabeça bateu na cabeça?
        for (int i = corpoDaCobra; i > 0; i--) {
            if (eixoX[0] == eixoX[i] && eixoY[0] == eixoY[i]) {
                estaRodando = false;
                break;
            }
        }

        // A cabeça tocou em alguma borda direita ou esquerda?
        if (eixoX[0] < 0 || eixoX[0] >= LARGURA_TELA) {
            estaRodando = false;
        }

        // A cabeça tocou no piso ou no teto?
        if (eixoY[0] < 0 || eixoY[0] >= ALTURA_TELA) {
            estaRodando = false;
        }

        if (!estaRodando) {
            timer.stop();
        }

    }

    public class LeitorDeTeclasAdapter extends KeyAdapter { // Classe abstrata para receber eventos de teclado.

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    // Caso a direção da cobra seja diferente de direita, ela pode ir para esquerda
                    if (direcao != 'D') {
                        direcao = 'A';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    // Caso a direção da cobra seja diferente de esquerda, ela pode ir para direita
                    if (direcao != 'A') {
                        direcao = 'D';
                    }
                    break;
                case KeyEvent.VK_UP:
                    // Caso a direção da cobra seja diferente de baixo, ela pode ir para cima
                    if (direcao != 'S') {
                        direcao = 'W';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    // Caso a direção da cobra seja diferente de cima, ela pode ir para baixo
                    if (direcao != 'W') {
                        direcao = 'S';
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
