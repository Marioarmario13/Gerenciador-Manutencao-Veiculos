package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Pequenos utilitários para montar as telas. */
class UI {

    static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component pai, String mensagem) {
        return JOptionPane.showConfirmDialog(pai, mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Modelo de tabela somente leitura. */
    static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static JTable tabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(24);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    /** Rótulo em cima, campo embaixo. */
    static JPanel campo(String rotulo, JComponent componente) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.add(new JLabel(rotulo), BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    static JPanel painelBusca(JTextField busca, JComboBox<String> ordem1, JComboBox<String> ordem2,
                              JButton btnBuscar, JButton btnLimpar) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.add(new JLabel("Buscar:"));
        p.add(busca);
        p.add(new JLabel("Ordenar por:"));
        p.add(ordem1);
        p.add(new JLabel("depois por:"));
        p.add(ordem2);
        p.add(btnBuscar);
        p.add(btnLimpar);
        return p;
    }
}
