package view;

import controller.MecanicoController;
import model.Mecanico;
import util.AppException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Cadastro e consulta de mecânicos. */
public class MecanicosView extends JFrame {
    private final MecanicoController ctrl = new MecanicoController();
    private final JTextField txtBusca = new JTextField(14);
    private final JComboBox<String> cbOrdem1 = new JComboBox<>(ctrl.colunas());
    private final JComboBox<String> cbOrdem2 = new JComboBox<>(ctrl.colunas());
    private final DefaultTableModel modelo =
            UI.modelo("Código", "Nome", "Especialidade", "Telefone", "Em atividade");
    private final JTable tabela = UI.tabela(modelo);

    private final JTextField txtNome = new JTextField();
    private final JTextField txtEspecialidade = new JTextField();
    private final JTextField txtTelefone = new JTextField();
    private final JCheckBox chkAtivo = new JCheckBox("Em atividade", true);
    private List<Mecanico> dados = new ArrayList<>();
    private int idAtual;

    public MecanicosView() {
        super("Cadastro de Mecânicos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(920, 580);
        setLocationRelativeTo(null);
        cbOrdem2.setSelectedIndex(1);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar busca");
        btnBuscar.addActionListener(e -> recarregar());
        txtBusca.addActionListener(e -> recarregar());
        btnLimpar.addActionListener(e -> txtBusca.setText(""));
        UI.buscarAoDigitar(txtBusca, this::recarregar);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        JPanel dadosMecanico = new JPanel(new BorderLayout());
        dadosMecanico.add(UI.secao("Dados do Mecânico", UI.grade(2,
                "Nome *", txtNome, "Especialidade", txtEspecialidade,
                "Telefone *", txtTelefone, "", chkAtivo)), BorderLayout.CENTER);
        dadosMecanico.add(UI.barraBotoes(btnNovo, btnSalvar, btnExcluir), BorderLayout.SOUTH);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionar();
        });

        JPanel conteudo = new JPanel(new BorderLayout(0, 4));
        conteudo.add(UI.painelBusca(txtBusca, cbOrdem1, cbOrdem2, btnBuscar, btnLimpar), BorderLayout.NORTH);
        conteudo.add(UI.rolagem(tabela), BorderLayout.CENTER);
        conteudo.add(dadosMecanico, BorderLayout.SOUTH);
        setContentPane(UI.raiz(conteudo));
        recarregar();
    }

    private void recarregar() {
        try {
            dados = ctrl.listar(txtBusca.getText(), (String) cbOrdem1.getSelectedItem(),
                    (String) cbOrdem2.getSelectedItem());
            modelo.setRowCount(0);
            for (Mecanico mecanico : dados) {
                modelo.addRow(new Object[]{mecanico.getId(), mecanico.getNome(), mecanico.getEspecialidade(),
                        mecanico.getTelefone(), mecanico.isAtivo() ? "Sim" : "Não"});
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= dados.size()) return;
        Mecanico mecanico = dados.get(linha);
        idAtual = mecanico.getId();
        txtNome.setText(mecanico.getNome());
        txtEspecialidade.setText(mecanico.getEspecialidade());
        txtTelefone.setText(mecanico.getTelefone());
        chkAtivo.setSelected(mecanico.isAtivo());
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
        if (!UI.confirmar(this, "Excluir o mecânico selecionado?")) return;
        try {
            ctrl.excluir(idAtual);
            limpar();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }
}
