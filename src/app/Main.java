package app;

import view.LoginView;
import view.UI;

import javax.swing.*;

/** Ponto de entrada do sistema. */
public class Main {
    public static void main(String[] args) {
        // No Swing as telas devem ser criadas na thread de eventos (EDT), por isso o invokeLater.
        // O tema vem antes da tela porque cada componente pega as cores e fontes quando é criado.
        SwingUtilities.invokeLater(() -> {
            UI.instalarTema();
            new LoginView().setVisible(true);
        });
    }
}
