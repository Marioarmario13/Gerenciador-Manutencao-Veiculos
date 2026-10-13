package view;

import model.Usuario;

import javax.swing.*;
import java.awt.*;

/** Menu principal com acesso às telas. */
public class PrincipalView extends JFrame {
    private ServicosView servicos;
    private MecanicosView mecanicos;
    private CarrosView carros;

    public PrincipalView(Usuario usuario) {
        super("Gestão de Manutenção Preventiva de Veículos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel boasVindas = new JLabel("Bem-vindo, " + usuario.getLogin() + "!", SwingConstants.CENTER);
        boasVindas.setFont(boasVindas.getFont().deriveFont(Font.BOLD, 18f));

        JButton btnCarros = new JButton("Carros (dados, mecânico, serviços e agenda)");
        JButton btnServicos = new JButton("Serviços (cadastro, consulta e preços)");
        JButton btnMecanicos = new JButton("Mecânicos");
        JButton btnSair = new JButton("Sair");

        btnCarros.addActionListener(e -> {
            if (carros == null || !carros.isDisplayable()) carros = new CarrosView();
            carros.setVisible(true);
            carros.toFront();
        });
        btnServicos.addActionListener(e -> {
            if (servicos == null || !servicos.isDisplayable()) servicos = new ServicosView();
            servicos.setVisible(true);
            servicos.toFront();
        });
        btnMecanicos.addActionListener(e -> {
            if (mecanicos == null || !mecanicos.isDisplayable()) mecanicos = new MecanicosView();
            mecanicos.setVisible(true);
            mecanicos.toFront();
        });
        btnSair.addActionListener(e -> System.exit(0));

        JPanel botoes = new JPanel(new GridLayout(0, 1, 0, 10));
        botoes.add(btnCarros);
        botoes.add(btnServicos);
        botoes.add(btnMecanicos);
        botoes.add(btnSair);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        raiz.add(boasVindas, BorderLayout.NORTH);
        raiz.add(botoes, BorderLayout.CENTER);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }
}
