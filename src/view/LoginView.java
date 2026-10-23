package view;

import controller.AuthController;
import util.AppException;

import javax.swing.*;
import java.awt.*;

/** Tela de login. */
public class LoginView extends JFrame {
    private final AuthController ctrl = new AuthController();
    private final JTextField txtLogin = new JTextField(18);
    private final JPasswordField txtSenha = new JPasswordField(18);

    public LoginView() {
        super("MANU0001 - Acesso ao Sistema");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JButton btnEntrar = new JButton("Entrar");
        JButton btnSair = new JButton("Sair");
        btnEntrar.addActionListener(e -> entrar());
        btnSair.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnEntrar);

        JPanel conteudo = new JPanel(new BorderLayout(0, 6));
        conteudo.add(UI.secao("Acesso ao Sistema",
                UI.grade(1, "Usuário", txtLogin, "Senha", txtSenha)), BorderLayout.CENTER);
        conteudo.add(UI.barraBotoes(btnEntrar, btnSair), BorderLayout.SOUTH);
        setContentPane(UI.raiz(conteudo));

        pack();
        setLocationRelativeTo(null);
    }

    private void entrar() {
        try {
            ctrl.login(txtLogin.getText(), new String(txtSenha.getPassword()));
            dispose();
            new PrincipalView().setVisible(true);
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
            txtSenha.setText("");
        }
    }
}
