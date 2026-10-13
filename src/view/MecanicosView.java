package view;

import controller.MecanicoController;
import model.Mecanico;
import util.AppException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Tela de mecânicos (quem trabalha na oficina). */
public class MecanicosView extends JFrame {
    private final MecanicoController ctrl = new MecanicoController();

    private final JTextField txtBusca = new JTextField(14);
    private final JComboBox<String> cbOrdem1 = new JComboBox<>(ctrl.colunas());
    private final JComboBox<String> cbOrdem2 = new JComboBox<>(ctrl.colunas());

    private final DefaultTableModel modelo = UI.modelo("Código", "Nome", "Especialidade", "Telefone", "Em atividade");
    private final JTable tabela = UI.tabela(modelo);

    private final JTextField txtNome = new JTextField();
    private final JTextField txtEspecialidade = new JTextField();
    private final JTextField txtTelefone = new JTextField();
    private final JCheckBox chkAtivo = new JCheckBox("Em atividade (trabalha atualmente)", true);

    private List<Mecanico> dados = new ArrayList<>();
    private int idAtual = 0;

    public MecanicosView() {
        super("Mecânicos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(920, 580);
        setLocationRelativeTo(null);
        cbOrdem2.setSelectedIndex(1);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar busca");
        btnBuscar.addActionListener(e -> recarregar());
        txtBusca.addActionListener(e -> recarregar());
        btnLimpar.addActionListener(e -> { txtBusca.setText(""); recarregar(); });

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 8));
        formulario.add(UI.campo("Nome *", txtNome));
        formulario.add(UI.campo("Especialidade", txtEspecialidade));
        formulario.add(UI.campo("Telefone (com DDD) *", txtTelefone));
        formulario.add(UI.campo(" ", chkAtivo));

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        JPanel sul = new JPanel(new BorderLayout(0, 6));
        sul.setBorder(BorderFactory.createTitledBorder("Dados do mecânico (selecione uma linha para editar)"));
        sul.add(formulario, BorderLayout.CENTER);
        sul.add(botoes, BorderLayout.SOUTH);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionar();
        });

        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        raiz.add(UI.painelBusca(txtBusca, cbOrdem1, cbOrdem2, btnBuscar, btnLimpar), BorderLayout.NORTH);
        raiz.add(new JScrollPane(tabela), BorderLayout.CENTER);
        raiz.add(sul, BorderLayout.SOUTH);
        setContentPane(raiz);

        recarregar();
    }

    private void recarregar() {
        try {
            dados = ctrl.listar(txtBusca.getText(), (String) cbOrdem1.getSelectedItem(),
                    (String) cbOrdem2.getSelectedItem());
            modelo.setRowCount(0);
            for (Mecanico m : dados) {
                modelo.addRow(new Object[]{m.getId(), m.getNome(), m.getEspecialidade(),
                        m.getTelefone(), m.isAtivo() ? "Sim" : "Não"});
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionar() {
        int i = tabela.getSelectedRow();
        if (i < 0 || i >= dados.size()) return;
        Mecanico m = dados.get(i);
        idAtual = m.getId();
        txtNome.setText(m.getNome());
        txtEspecialidade.setText(m.getEspecialidade());
        txtTelefone.setText(m.getTelefone());
        chkAtivo.setSelected(m.isAtivo());
    }

    private void limpar() {
        idAtual = 0;
        tabela.clearSelection();
        txtNome.setText("");
        txtEspecialidade.setText("");
        txtTelefone.setText("");
        chkAtivo.setSelected(true);
        txtNome.requestFocus();
    }

    private void salvar() {
        try {
            ctrl.salvar(idAtual, txtNome.getText(), txtEspecialidade.getText(), txtTelefone.getText(),
                    chkAtivo.isSelected());
            UI.info(this, "Mecânico salvo com sucesso!");
            limpar();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void excluir() {
        if (idAtual == 0) {
            UI.erro(this, "Selecione um mecânico na tabela para excluir.");
            return;
        }
        if (!UI.confirmar(this, "Excluir o mecânico selecionado?\n(Se ele estiver atribuído a algum carro, a exclusão será bloqueada.)")) return;
        try {
            ctrl.excluir(idAtual);
            limpar();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }
}
