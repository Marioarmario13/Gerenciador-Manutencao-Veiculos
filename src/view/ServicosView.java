package view;

import controller.ServicoController;
import model.Servico;
import util.AppException;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Tela de serviços: cadastro, consulta e preços. */
public class ServicosView extends JFrame {
    private final ServicoController ctrl = new ServicoController();

    private final JTextField txtBusca = new JTextField(14);
    private final JComboBox<String> cbOrdem1 = new JComboBox<>(ctrl.colunas());
    private final JComboBox<String> cbOrdem2 = new JComboBox<>(ctrl.colunas());

    private final DefaultTableModel modelo = UI.modelo("Código", "Nome", "Descrição", "Preço", "Intervalo (km)");
    private final JTable tabela = UI.tabela(modelo);

    private final JTextField txtNome = new JTextField();
    private final JTextField txtPreco = new JTextField();
    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtIntervalo = new JTextField();

    private List<Servico> dados = new ArrayList<>();
    private int idAtual = 0;   // 0 = novo serviço

    public ServicosView() {
        super("Cadastro de Serviços de Manutenção");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(920, 600);
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

        JPanel sul = new JPanel(new BorderLayout());
        sul.add(UI.secao("Dados do Serviço", UI.grade(2,
                "Nome do serviço *", txtNome, "Preço (R$) *", txtPreco,
                "Descrição", txtDescricao, "Repetir a cada (km) *", txtIntervalo)), BorderLayout.CENTER);
        sul.add(UI.barraBotoes(btnNovo, btnSalvar, btnExcluir), BorderLayout.SOUTH);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionar();
        });

        JPanel conteudo = new JPanel(new BorderLayout(0, 4));
        conteudo.add(UI.painelBusca(txtBusca, cbOrdem1, cbOrdem2, btnBuscar, btnLimpar), BorderLayout.NORTH);
        conteudo.add(UI.rolagem(tabela), BorderLayout.CENTER);
        conteudo.add(sul, BorderLayout.SOUTH);
        setContentPane(UI.raiz(conteudo));

        recarregar();
    }

    private void recarregar() {
        try {
            dados = ctrl.listar(txtBusca.getText(), (String) cbOrdem1.getSelectedItem(),
                    (String) cbOrdem2.getSelectedItem());
            modelo.setRowCount(0);
            for (Servico s : dados) {
                modelo.addRow(new Object[]{s.getId(), s.getNome(), s.getDescricao(),
                        Util.moeda(s.getPreco()), s.getIntervaloKm()});
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionar() {
        int i = tabela.getSelectedRow();
        if (i < 0 || i >= dados.size()) return;
        Servico s = dados.get(i);
        idAtual = s.getId();
        txtNome.setText(s.getNome());
        txtDescricao.setText(s.getDescricao());
        txtPreco.setText(s.getPreco().toPlainString().replace('.', ','));
        txtIntervalo.setText(String.valueOf(s.getIntervaloKm()));
    }

    private void limpar() {
        idAtual = 0;
        tabela.clearSelection();
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtIntervalo.setText("");
        txtNome.requestFocus();
    }

    private void salvar() {
        try {
            ctrl.salvar(idAtual, txtNome.getText(), txtDescricao.getText(), txtPreco.getText(), txtIntervalo.getText());
            UI.info(this, "Serviço salvo com sucesso!");
            limpar();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void excluir() {
        if (idAtual == 0) {
            UI.erro(this, "Selecione um serviço na tabela para excluir.");
            return;
        }
        if (!UI.confirmar(this, "Excluir o serviço selecionado?")) return;
        try {
            ctrl.excluir(idAtual);
            limpar();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }
}
