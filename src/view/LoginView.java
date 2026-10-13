package view;

import controller.AuthController;
import model.Usuario;
import util.AppException;

import javax.swing.*;
import java.awt.*;

/** Tela de login. */
public class LoginView extends JFrame {
    private final AuthController ctrl = new AuthController();
    private final JTextField txtLogin = new JTextField(16);
    private final JPasswordField txtSenha = new JPasswordField(16);

    public LoginView() {
        super("Login - Manutenção Preventiva");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Manutenção Preventiva de Veículos", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.add(UI.campo("Usuário", txtLogin));
        form.add(UI.campo("Senha", txtSenha));

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(btnEntrar);   // Enter também faz login

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        raiz.add(titulo, BorderLayout.NORTH);
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(btnEntrar, BorderLayout.SOUTH);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private void entrar() {
        try {
            Usuario u = ctrl.login(txtLogin.getText(), new String(txtSenha.getPassword()));
            dispose();
            new PrincipalView(u).setVisible(true);
        } catch (AppException ex) {
            UI.erro(this, ex.getMessage());
            txtSenha.setText("");
        }
    }
}
