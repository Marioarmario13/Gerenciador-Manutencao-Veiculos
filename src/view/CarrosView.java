package view;

import controller.CarroController;
import model.Agendamento;
import model.Carro;
import model.Mecanico;
import model.Servico;
import util.AppException;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Cadastro de carros, mecânicos atribuídos e agenda de serviços. */
public class CarrosView extends JFrame {
    private static final Mecanico NENHUM = criarNenhum();

    private final CarroController ctrl = new CarroController();

    private final JTextField txtBusca = new JTextField(14);
    private final JComboBox<String> cbOrdem1 = new JComboBox<>(ctrl.colunas());
    private final JComboBox<String> cbOrdem2 = new JComboBox<>(ctrl.colunas());
    private final DefaultTableModel modeloCarros =
            UI.modelo("Placa", "Modelo", "Ano", "Km", "Problema", "Mecânico");
    private final JTable tabelaCarros = UI.tabela(modeloCarros);
    private List<Carro> carros = new ArrayList<>();

    private final JTextField txtPlaca = new JTextField();
    private final JTextField txtModelo = new JTextField();
    private final JTextField txtAno = new JTextField();
    private final JTextField txtKm = new JTextField();
    private final JTextField txtProblema = new JTextField();
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private int idCarro;

    private final JLabel lblAgenda = new JLabel();
    private final DefaultTableModel modeloAgenda = UI.modelo("Serviço", "Preço", "Data", "Status");
    private final JTable tabelaAgenda = UI.tabela(modeloAgenda);
    private List<Agendamento> agenda = new ArrayList<>();
    private final JComboBox<Servico> cbServico = new JComboBox<>();
    private final JTextField txtData = new JTextField(Util.hoje(), 9);
    private final JLabel lblTotal = new JLabel();

    public CarrosView() {
        super("Carros e Agenda de Serviços");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);
        cbOrdem2.setSelectedIndex(1);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                carregarCombos();
            }
        });

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar busca");
        btnBuscar.addActionListener(e -> recarregar());
        txtBusca.addActionListener(e -> recarregar());
        btnLimpar.addActionListener(e -> txtBusca.setText(""));
        UI.buscarAoDigitar(txtBusca, this::recarregar);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limparForm());
        btnSalvar.addActionListener(e -> salvarCarro());
        btnExcluir.addActionListener(e -> excluirCarro());

        JPanel abaCarro = new JPanel(new BorderLayout());
        abaCarro.add(UI.grade(3,
                "Placa *", txtPlaca, "Modelo *", txtModelo, "Ano *", txtAno,
                "Km *", txtKm, "Problema", txtProblema, "Mecânico", cbMecanico), BorderLayout.NORTH);
        abaCarro.add(UI.barraBotoes(btnNovo, btnSalvar, btnExcluir), BorderLayout.SOUTH);

        tabelaCarros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarCarro();
        });

        JButton btnAgendar = new JButton("Agendar");
        JButton btnConcluir = new JButton("Concluir");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnRemover = new JButton("Remover");
        btnAgendar.addActionListener(e -> agendar());
        btnConcluir.addActionListener(e -> mudarStatus(Agendamento.CONCLUIDO));
        btnCancelar.addActionListener(e -> mudarStatus(Agendamento.CANCELADO));
        btnRemover.addActionListener(e -> removerAgenda());

        Servico exemplo = new Servico();
        exemplo.setNome("Alinhamento e balanceamento");
        exemplo.setPreco(BigDecimal.ZERO);
        cbServico.setPrototypeDisplayValue(exemplo);
        lblAgenda.setForeground(UI.AZUL);
        lblTotal.setForeground(UI.VERMELHO);

        JPanel topoAgenda = new JPanel(new GridLayout(0, 1));
        topoAgenda.add(UI.linha("Carro :", lblAgenda));
        topoAgenda.add(UI.linha("Serviço :", cbServico, "Data :", txtData, btnAgendar));

        JPanel abaAgenda = new JPanel(new BorderLayout(0, 4));
        abaAgenda.add(topoAgenda, BorderLayout.NORTH);
        abaAgenda.add(UI.rolagem(tabelaAgenda), BorderLayout.CENTER);
        abaAgenda.add(UI.barraBotoes(lblTotal, Box.createHorizontalStrut(20),
                btnConcluir, btnCancelar, btnRemover), BorderLayout.SOUTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Dados do Carro", abaCarro);
        abas.addTab("Agenda de Serviços", abaAgenda);

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, UI.rolagem(tabelaCarros), abas);
        divisor.setResizeWeight(0.6);
        divisor.setBorder(null);

        JPanel conteudo = new JPanel(new BorderLayout(0, 4));
        conteudo.add(UI.painelBusca(txtBusca, cbOrdem1, cbOrdem2, btnBuscar, btnLimpar), BorderLayout.NORTH);
        conteudo.add(divisor, BorderLayout.CENTER);
        setContentPane(UI.raiz(conteudo));

        carregarCombos();
        recarregar();
        carregarAgenda();
    }

    private static Mecanico criarNenhum() {
        Mecanico mecanico = new Mecanico();
        mecanico.setId(0);
        mecanico.setNome("(sem mecânico)");
        return mecanico;
    }

    private void carregarCombos() {
        try {
            Mecanico mecanicoSelecionado = (Mecanico) cbMecanico.getSelectedItem();
            Servico servicoSelecionado = (Servico) cbServico.getSelectedItem();

            cbMecanico.removeAllItems();
            cbMecanico.addItem(NENHUM);
            for (Mecanico mecanico : ctrl.mecanicosAtivos()) cbMecanico.addItem(mecanico);
            selecionarMecanico(mecanicoSelecionado);

            cbServico.removeAllItems();
            for (Servico servico : ctrl.servicos()) {
                cbServico.addItem(servico);
                if (servicoSelecionado != null && servico.getId() == servicoSelecionado.getId()) {
                    cbServico.setSelectedItem(servico);
                }
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionarMecanico(Mecanico alvo) {
        if (alvo == null || alvo.getId() == 0) {
            cbMecanico.setSelectedItem(NENHUM);
            return;
        }
        for (int i = 0; i < cbMecanico.getItemCount(); i++) {
            if (cbMecanico.getItemAt(i).getId() == alvo.getId()) {
                cbMecanico.setSelectedIndex(i);
                return;
            }
        }
        cbMecanico.addItem(alvo);
        cbMecanico.setSelectedItem(alvo);
    }

    private void recarregar() {
        try {
            carros = ctrl.listar(txtBusca.getText(), (String) cbOrdem1.getSelectedItem(),
                    (String) cbOrdem2.getSelectedItem());
            modeloCarros.setRowCount(0);
            for (Carro carro : carros) {
                modeloCarros.addRow(new Object[]{carro.getPlacaFormatada(), carro.getModelo(), carro.getAno(),
                        carro.getKm(), carro.getProblema(),
                        carro.getMecanicoNome() == null ? "-" : carro.getMecanicoNome()});
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionarCarro() {
        int linha = tabelaCarros.getSelectedRow();
        if (linha < 0 || linha >= carros.size()) return;
        Carro carro = carros.get(linha);
        idCarro = carro.getId();
        txtPlaca.setText(carro.getPlacaFormatada());
        txtModelo.setText(carro.getModelo());
        txtAno.setText(String.valueOf(carro.getAno()));
        txtKm.setText(String.valueOf(carro.getKm()));
        txtProblema.setText(carro.getProblema());

        if (carro.getMecanicoId() == 0) {
            selecionarMecanico(null);
        } else {
            Mecanico mecanico = new Mecanico();
            mecanico.setId(carro.getMecanicoId());
            mecanico.setNome(carro.getMecanicoNome() + " (inativo)");
            selecionarMecanico(mecanico);
        }
        carregarAgenda();
    }

    private void limparForm() {
        idCarro = 0;
        tabelaCarros.clearSelection();
        txtPlaca.setText("");
        txtModelo.setText("");
        txtAno.setText("");
        txtKm.setText("");
        txtProblema.setText("");
        cbMecanico.setSelectedItem(NENHUM);
        carregarAgenda();
        txtPlaca.requestFocus();
    }

    private void salvarCarro() {
        try {
            ctrl.salvar(idCarro, txtPlaca.getText(), txtModelo.getText(), txtAno.getText(), txtKm.getText(),
                    txtProblema.getText(), (Mecanico) cbMecanico.getSelectedItem());
            UI.info(this, "Carro salvo com sucesso!");
            limparForm();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void excluirCarro() {
        if (idCarro == 0) {
            UI.erro(this, "Selecione um carro na tabela para excluir.");
            return;
        }
        if (!UI.confirmar(this, "Excluir o carro selecionado e toda a sua agenda de serviços?")) return;
        try {
            ctrl.excluir(idCarro);
            limparForm();
            recarregar();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void carregarAgenda() {
        modeloAgenda.setRowCount(0);
        agenda.clear();
        if (idCarro == 0) {
            lblAgenda.setText("Selecione um carro na tabela acima");
            lblTotal.setText("Total previsto : " + Util.moeda(BigDecimal.ZERO));
            return;
        }

        try {
            agenda = ctrl.listarAgenda(idCarro);
            BigDecimal total = BigDecimal.ZERO;
            for (Agendamento item : agenda) {
                modeloAgenda.addRow(new Object[]{item.getServicoNome(), Util.moeda(item.getPreco()),
                        Util.formatData(item.getData()), item.getStatusTexto()});
                if (Agendamento.AGENDADO.equals(item.getStatus())) total = total.add(item.getPreco());
            }
            lblAgenda.setText(txtPlaca.getText() + " - " + txtModelo.getText());
            lblTotal.setText("Total previsto (agendados) : " + Util.moeda(total));
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void agendar() {
        try {
            ctrl.agendar(idCarro, (Servico) cbServico.getSelectedItem(), txtData.getText());
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private Agendamento agendamentoSelecionado() {
        int linha = tabelaAgenda.getSelectedRow();
        if (linha < 0 || linha >= agenda.size()) {
            UI.erro(this, "Selecione um serviço na tabela da agenda.");
            return null;
        }
        return agenda.get(linha);
    }

    private void mudarStatus(String status) {
        Agendamento item = agendamentoSelecionado();
        if (item == null) return;
        try {
            ctrl.mudarStatus(item.getId(), status);
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void removerAgenda() {
        Agendamento item = agendamentoSelecionado();
        if (item == null || !UI.confirmar(this, "Remover este serviço da agenda?")) return;
        try {
            ctrl.removerDaAgenda(item.getId());
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }
}
