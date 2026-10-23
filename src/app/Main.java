package app;

import view.LoginView;
import view.UI;

import javax.swing.*;

/** Ponto de entrada: aplica o tema visual e abre a tela de login. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI.instalarTema();
            new LoginView().setVisible(true);
        });
    }
}
