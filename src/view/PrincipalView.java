package view;

import javax.swing.*;
import java.awt.*;

/** Menu principal com acesso às telas. */
public class PrincipalView extends JFrame {
    private ServicosView servicos;
    private MecanicosView mecanicos;
    private CarrosView carros;

    /** Monta o menu principal com os botões das três telas de cadastro. */
    public PrincipalView() {
        super("ManuPrev");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton btnCarros = new JButton("Carros e Agenda de Serviços");
        JButton btnServicos = new JButton("Serviços de Manutenção");
        JButton btnMecanicos = new JButton("Mecânicos");
        JButton btnSair = new JButton("Sair");
        btnCarros.addActionListener(e -> abrirCarros());
        btnServicos.addActionListener(e -> abrirServicos());
        btnMecanicos.addActionListener(e -> abrirMecanicos());
        btnSair.addActionListener(e -> System.exit(0));

        JPanel botoes = new JPanel(new GridLayout(0, 1, 0, 6));
        for (JButton b : new JButton[]{btnCarros, btnServicos, btnMecanicos}) {
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setPreferredSize(new Dimension(360, 30));
            botoes.add(b);
        }

        JPanel conteudo = new JPanel(new BorderLayout(0, 6));
        conteudo.add(UI.secao("Menu Principal", botoes), BorderLayout.CENTER);
        conteudo.add(UI.barraBotoes(btnSair), BorderLayout.SOUTH);
        setContentPane(UI.raiz(conteudo));

        pack();
        setLocationRelativeTo(null);
    }

    /** Abre a tela de carros, ou traz para a frente se já estiver aberta. */
    private void abrirCarros() {
        if (carros == null || !carros.isDisplayable()) carros = new CarrosView();
        carros.setVisible(true);
        carros.toFront();
    }

    /** Abre a tela de serviços, ou traz para a frente se já estiver aberta. */
    private void abrirServicos() {
        if (servicos == null || !servicos.isDisplayable()) servicos = new ServicosView();
        servicos.setVisible(true);
        servicos.toFront();
    }

    /** Abre a tela de mecânicos, ou traz para a frente se já estiver aberta. */
    private void abrirMecanicos() {
        if (mecanicos == null || !mecanicos.isDisplayable()) mecanicos = new MecanicosView();
        mecanicos.setVisible(true);
        mecanicos.toFront();
    }
}
