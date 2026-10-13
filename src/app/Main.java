package app;

import view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // mantém o visual padrão do Java
            }
            new LoginView().setVisible(true);
        });
    }
}
