package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Utilitários para montar as telas no estilo de sistema corporativo (ERP clássico). */
public class UI {
    static final Color FUNDO = new Color(224, 224, 224);
    static final Color CAMPO = new Color(224, 255, 255);
    static final Color AZUL = new Color(0, 0, 128);
    static final Color VERMELHO = new Color(204, 0, 0);
    static final Color BORDA = new Color(128, 128, 128);

    private static final Font FONTE = new Font("Tahoma", Font.PLAIN, 11);
    private static final Font FONTE_NEGRITO = new Font("Tahoma", Font.BOLD, 11);

    /** Visual cinza simples, campos em ciano claro e texto azul-marinho. */
    public static void instalarTema() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ignorada) {
            // segue com o visual padrão
        }
        for (String chave : new String[]{"Label", "Button", "TextField", "PasswordField", "ComboBox",
                "CheckBox", "Table", "TableHeader", "TabbedPane", "TitledBorder", "Menu", "MenuItem",
                "MenuBar", "OptionPane", "List"}) {
            UIManager.put(chave + ".font", FONTE);
        }
        UIManager.put("Label.font", FONTE_NEGRITO);
        UIManager.put("Button.font", FONTE_NEGRITO);
        UIManager.put("TabbedPane.font", FONTE_NEGRITO);
        UIManager.put("Label.foreground", Color.BLACK);

        UIManager.put("Panel.background", FUNDO);
        UIManager.put("OptionPane.background", FUNDO);
        UIManager.put("CheckBox.background", FUNDO);
        UIManager.put("TabbedPane.background", FUNDO);
        UIManager.put("TabbedPane.selected", FUNDO);
        UIManager.put("TabbedPane.contentAreaColor", FUNDO);
        UIManager.put("SplitPane.background", FUNDO);
        UIManager.put("MenuBar.background", FUNDO);

        UIManager.put("TextField.background", CAMPO);
        UIManager.put("TextField.foreground", AZUL);
        UIManager.put("PasswordField.background", CAMPO);
        UIManager.put("PasswordField.foreground", AZUL);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", AZUL);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA), BorderFactory.createEmptyBorder(1, 3, 1, 3)));
        UIManager.put("PasswordField.border", UIManager.get("TextField.border"));

        UIManager.put("Button.background", FUNDO);
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(2, 12, 2, 12)));
        UIManager.put("Button.focus", FUNDO);

        UIManager.put("Table.foreground", AZUL);
        UIManager.put("Table.selectionBackground", AZUL);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(192, 192, 192));
        UIManager.put("TableHeader.background", FUNDO);
        UIManager.put("TableHeader.font", FONTE_NEGRITO);

        UIManager.put("OptionPane.warningIcon", new IconeAviso('!'));
        UIManager.put("OptionPane.informationIcon", new IconeAviso('i'));
        UIManager.put("OptionPane.questionIcon", new IconeAviso('?'));
        UIManager.put("OptionPane.errorIcon", new IconeAviso('x'));
    }

    /** Ícones das caixas de mensagem desenhados no estilo do Windows 7 (com brilho e degradê). */
    private static class IconeAviso implements Icon {
        private final char tipo;

        IconeAviso(char tipo) {
            this.tipo = tipo;
        }

        @Override
        public int getIconWidth() {
            return 32;
        }

        @Override
        public int getIconHeight() {
            return 32;
        }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.translate(x, y);
            if (tipo == '!') {
                Polygon triangulo = new Polygon(new int[]{16, 31, 1}, new int[]{2, 29, 29}, 3);
                g.setPaint(new GradientPaint(0, 2, new Color(255, 236, 140), 0, 29, new Color(240, 170, 20)));
                g.fillPolygon(triangulo);
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(new Color(150, 95, 0));
                g.drawPolygon(triangulo);
                simbolo(g, "!", Color.BLACK, 17);
            } else {
                Color claro = tipo == 'x' ? new Color(250, 120, 110) : new Color(110, 170, 245);
                Color escuro = tipo == 'x' ? new Color(180, 20, 20) : new Color(20, 80, 180);
                g.setPaint(new GradientPaint(0, 1, claro, 0, 31, escuro));
                g.fillOval(1, 1, 30, 30);
                g.setColor(escuro.darker());
                g.setStroke(new BasicStroke(1.2f));
                g.drawOval(1, 1, 30, 30);
                g.setPaint(new GradientPaint(0, 2, new Color(255, 255, 255, 170), 0, 16,
                        new Color(255, 255, 255, 20)));
                g.fillOval(5, 2, 22, 14);
                if (tipo == 'x') {
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(11, 11, 21, 21);
                    g.drawLine(21, 11, 11, 21);
                } else {
                    simbolo(g, String.valueOf(tipo), Color.WHITE, 16);
                }
            }
            g.dispose();
        }

        private static void simbolo(Graphics2D g, String texto, Color cor, int base) {
            g.setFont(new Font("Times New Roman", Font.BOLD, 20));
            g.setColor(cor);
            FontMetrics fm = g.getFontMetrics();
            int largura = fm.stringWidth(texto);
            int altura = fm.getAscent() - fm.getDescent();
            g.drawString(texto, 16 - largura / 2, base + altura / 2 + 1);
        }
    }

    static void buscarAoDigitar(JTextField campo, Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                acao.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                acao.run();
            }
        });
    }

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
        t.setRowHeight(18);
        t.setShowGrid(true);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    static JScrollPane rolagem(JTable tabela) {
        JScrollPane s = new JScrollPane(tabela);
        s.setBorder(BorderFactory.createLoweredBevelBorder());
        s.getViewport().setBackground(Color.WHITE);
        return s;
    }

    /** Quadro com título vermelho centralizado, como "Critérios para Seleção dos Dados". */
    static JPanel secao(String titulo, JComponent conteudo) {
        JPanel p = new JPanel(new BorderLayout());
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), titulo, TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 13), VERMELHO);
        p.setBorder(BorderFactory.createCompoundBorder(borda, BorderFactory.createEmptyBorder(2, 6, 6, 6)));
        p.add(conteudo, BorderLayout.CENTER);
        return p;
    }

    /**
     * Formulário em grade: pares (rótulo, campo) lado a lado, com o rótulo à direita
     * alinhado e terminando em ":". {@code pares} indica quantos pares cabem em cada linha.
     */
    static JPanel grade(int pares, Object... rotulosECampos) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 4, 2, 4);
        for (int i = 0; i < rotulosECampos.length / 2; i++) {
            int linha = i / pares;
            int coluna = (i % pares) * 2;
            String rotulo = (String) rotulosECampos[i * 2];
            JLabel lbl = new JLabel(rotulo.isEmpty() ? "" : rotulo + " :");
            c.gridx = coluna;
            c.gridy = linha;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            p.add(lbl, c);

            c.gridx = coluna + 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            p.add((Component) rotulosECampos[i * 2 + 1], c);
        }
        return p;
    }

    /** Barra de botões alinhada à direita, separada por uma linha. */
    static JPanel barraBotoes(Component... itens) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDA));
        for (Component item : itens) p.add(item);
        return p;
    }

    /** Linha de rótulo + componentes, alinhada à esquerda. */
    static JPanel linha(Object... itens) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        for (Object item : itens) {
            p.add(item instanceof String ? new JLabel((String) item) : (Component) item);
        }
        return p;
    }

    /** Critérios de pesquisa no topo das telas de cadastro. */
    static JPanel painelBusca(JTextField busca, JComboBox<String> ordem1, JComboBox<String> ordem2,
                              JButton btnBuscar, JButton btnLimpar) {
        busca.setColumns(24);
        JPanel linhas = new JPanel(new GridLayout(0, 1));
        linhas.add(linha("Buscar :", busca, btnBuscar, btnLimpar));
        linhas.add(linha("Ordenar por :", ordem1, "Depois por :", ordem2));
        return secao("Critérios de Pesquisa", linhas);
    }

    /** Raiz padrão de uma janela, com margem pequena. */
    static JPanel raiz(JComponent conteudo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        p.add(conteudo, BorderLayout.CENTER);
        return p;
    }
}
