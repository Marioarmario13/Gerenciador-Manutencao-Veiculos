package view;

import controller.AgendaController;
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

/** Tela do carro: informações, problema, mecânico atribuído, serviços e agenda. */
public class CarrosView extends JFrame {
    private static final Mecanico NENHUM = criarNenhum();

    private final CarroController ctrl = new CarroController();
    private final AgendaController agendaCtrl = new AgendaController();

    // ----- busca e lista de carros -----
    private final JTextField txtBusca = new JTextField(14);
    private final JComboBox<String> cbOrdem1 = new JComboBox<>(ctrl.colunas());
    private final JComboBox<String> cbOrdem2 = new JComboBox<>(ctrl.colunas());
    private final DefaultTableModel modeloCarros =
            UI.modelo("Placa", "Modelo", "Ano", "Km", "Problema", "Mecânico");
    private final JTable tabelaCarros = UI.tabela(modeloCarros);
    private List<Carro> carros = new ArrayList<>();

    // ----- formulário do carro -----
    private final JTextField txtPlaca = new JTextField();
    private final JTextField txtModelo = new JTextField();
    private final JTextField txtAno = new JTextField();
    private final JTextField txtKm = new JTextField();
    private final JTextField txtProblema = new JTextField();
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private int idCarro = 0;   // 0 = novo carro

    // ----- agenda e serviços do carro -----
    private final JLabel lblAgenda = new JLabel();
    private final DefaultTableModel modeloAgenda = UI.modelo("Serviço", "Preço", "Data", "Status");
    private final JTable tabelaAgenda = UI.tabela(modeloAgenda);
    private List<Agendamento> agenda = new ArrayList<>();
    private final JComboBox<Servico> cbServico = new JComboBox<>();
    private final JTextField txtData = new JTextField(Util.hoje(), 9);
    private final JLabel lblTotal = new JLabel();

    public CarrosView() {
        super("Carros - Informações, Serviços e Agenda");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1040, 780);
        setLocationRelativeTo(null);
        cbOrdem2.setSelectedIndex(1);

        // Ao voltar para esta janela, atualiza as listas de mecânicos e serviços
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                carregarCombos();
            }
        });

        // ---------- parte de cima: busca + tabela + formulário ----------
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar busca");
        btnBuscar.addActionListener(e -> recarregar());
        txtBusca.addActionListener(e -> recarregar());
        btnLimpar.addActionListener(e -> { txtBusca.setText(""); recarregar(); });

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 6));
        formulario.add(UI.campo("Placa *", txtPlaca));
        formulario.add(UI.campo("Modelo *", txtModelo));
        formulario.add(UI.campo("Ano *", txtAno));
        formulario.add(UI.campo("Quilometragem (km) *", txtKm));
        formulario.add(UI.campo("Problema relatado", txtProblema));
        formulario.add(UI.campo("Mecânico atribuído", cbMecanico));

        JButton btnNovo = new JButton("Novo carro");
        JButton btnSalvar = new JButton("Salvar carro");
        JButton btnExcluir = new JButton("Excluir carro");
        btnNovo.addActionListener(e -> limparForm());
        btnSalvar.addActionListener(e -> salvarCarro());
        btnExcluir.addActionListener(e -> excluirCarro());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        JPanel painelForm = new JPanel(new BorderLayout(0, 4));
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do carro (selecione uma linha para editar)"));
        painelForm.add(formulario, BorderLayout.CENTER);
        painelForm.add(botoes, BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout(0, 6));
        topo.add(UI.painelBusca(txtBusca, cbOrdem1, cbOrdem2, btnBuscar, btnLimpar), BorderLayout.NORTH);
        topo.add(new JScrollPane(tabelaCarros), BorderLayout.CENTER);
        topo.add(painelForm, BorderLayout.SOUTH);

        tabelaCarros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarCarro();
        });

        // ---------- parte de baixo: serviços e agenda do carro ----------
        JButton btnAgendar = new JButton("Agendar serviço");
        JButton btnConcluir = new JButton("Marcar concluído");
        JButton btnCancelar = new JButton("Cancelar serviço");
        JButton btnRemover = new JButton("Remover da agenda");
        btnAgendar.addActionListener(e -> agendar());
        btnConcluir.addActionListener(e -> mudarStatus(Agendamento.CONCLUIDO));
        btnCancelar.addActionListener(e -> mudarStatus(Agendamento.CANCELADO));
        btnRemover.addActionListener(e -> removerAgenda());

        JPanel novoAgendamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        novoAgendamento.add(new JLabel("Serviço:"));
        cbServico.setPrototypeDisplayValue(new Servico() {{ setNome("Alinhamento e balanceamento"); setPreco(BigDecimal.ZERO); }});
        novoAgendamento.add(cbServico);
        novoAgendamento.add(new JLabel("Data (dd/mm/aaaa):"));
        novoAgendamento.add(txtData);
        novoAgendamento.add(btnAgendar);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acoes.add(btnConcluir);
        acoes.add(btnCancelar);
        acoes.add(btnRemover);
        acoes.add(Box.createHorizontalStrut(20));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        acoes.add(lblTotal);

        JPanel controlesAgenda = new JPanel(new GridLayout(2, 1));
        controlesAgenda.add(novoAgendamento);
        controlesAgenda.add(acoes);

        lblAgenda.setFont(lblAgenda.getFont().deriveFont(Font.BOLD, 14f));
        JPanel baixo = new JPanel(new BorderLayout(0, 4));
        baixo.setBorder(BorderFactory.createTitledBorder("Serviços e agenda do carro selecionado"));
        baixo.add(lblAgenda, BorderLayout.NORTH);
        baixo.add(new JScrollPane(tabelaAgenda), BorderLayout.CENTER);
        baixo.add(controlesAgenda, BorderLayout.SOUTH);

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topo, baixo);
        divisor.setResizeWeight(0.58);
        divisor.setBorder(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        raiz.add(divisor, BorderLayout.CENTER);
        setContentPane(raiz);

        carregarCombos();
        recarregar();
        carregarAgenda();
    }

    private static Mecanico criarNenhum() {
        Mecanico m = new Mecanico();
        m.setId(0);
        m.setNome("(sem mecânico)");
        return m;
    }

    // ===================== combos =====================

    private void carregarCombos() {
        try {
            Mecanico mecAnterior = (Mecanico) cbMecanico.getSelectedItem();
            Servico servAnterior = (Servico) cbServico.getSelectedItem();

            cbMecanico.removeAllItems();
            cbMecanico.addItem(NENHUM);
            for (Mecanico m : ctrl.mecanicosAtivos()) cbMecanico.addItem(m);
            selecionarMecanico(mecAnterior);

            cbServico.removeAllItems();
            for (Servico s : agendaCtrl.servicos()) cbServico.addItem(s);
            if (servAnterior != null) {
                for (int i = 0; i < cbServico.getItemCount(); i++) {
                    if (cbServico.getItemAt(i).getId() == servAnterior.getId()) {
                        cbServico.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    /** Seleciona o mecânico no combo; se ele estiver inativo, inclui na lista como "(inativo)". */
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

    // ===================== carros =====================

    private void recarregar() {
        try {
            carros = ctrl.listar(txtBusca.getText(), (String) cbOrdem1.getSelectedItem(),
                    (String) cbOrdem2.getSelectedItem());
            modeloCarros.setRowCount(0);
            for (Carro c : carros) {
                modeloCarros.addRow(new Object[]{c.getPlacaFormatada(), c.getModelo(), c.getAno(), c.getKm(),
                        c.getProblema(), c.getMecanicoNome() == null ? "-" : c.getMecanicoNome()});
            }
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void selecionarCarro() {
        int i = tabelaCarros.getSelectedRow();
        if (i < 0 || i >= carros.size()) return;
        Carro c = carros.get(i);
        idCarro = c.getId();
        txtPlaca.setText(c.getPlacaFormatada());
        txtModelo.setText(c.getModelo());
        txtAno.setText(String.valueOf(c.getAno()));
        txtKm.setText(String.valueOf(c.getKm()));
        txtProblema.setText(c.getProblema());
        if (c.getMecanicoId() == 0) {
            selecionarMecanico(null);
        } else {
            Mecanico m = new Mecanico();
            m.setId(c.getMecanicoId());
            m.setNome(c.getMecanicoNome() + " (inativo)");   // só aparece se não estiver na lista de ativos
            selecionarMecanico(m);
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

    // ===================== agenda =====================

    private void carregarAgenda() {
        modeloAgenda.setRowCount(0);
        if (idCarro == 0) {
            agenda = new ArrayList<>();
            lblAgenda.setText("Selecione um carro na tabela acima");
            lblTotal.setText("Total previsto: " + Util.moeda(BigDecimal.ZERO));
            return;
        }
        try {
            agenda = agendaCtrl.listar(idCarro);
            BigDecimal total = BigDecimal.ZERO;
            for (Agendamento a : agenda) {
                modeloAgenda.addRow(new Object[]{a.getServicoNome(), Util.moeda(a.getPreco()),
                        Util.formatData(a.getData()), a.getStatusTexto()});
                if (Agendamento.AGENDADO.equals(a.getStatus())) total = total.add(a.getPreco());
            }
            lblAgenda.setText(txtPlaca.getText() + " - " + txtModelo.getText());
            lblTotal.setText("Total previsto (agendados): " + Util.moeda(total));
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void agendar() {
        try {
            agendaCtrl.agendar(idCarro, (Servico) cbServico.getSelectedItem(), txtData.getText());
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private Agendamento agendamentoSelecionado() {
        int i = tabelaAgenda.getSelectedRow();
        if (i < 0 || i >= agenda.size()) {
            UI.erro(this, "Selecione um serviço na tabela da agenda.");
            return null;
        }
        return agenda.get(i);
    }

    private void mudarStatus(String status) {
        Agendamento a = agendamentoSelecionado();
        if (a == null) return;
        try {
            agendaCtrl.mudarStatus(a.getId(), status);
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }

    private void removerAgenda() {
        Agendamento a = agendamentoSelecionado();
        if (a == null) return;
        if (!UI.confirmar(this, "Remover este serviço da agenda?")) return;
        try {
            agendaCtrl.remover(a.getId());
            carregarAgenda();
        } catch (AppException e) {
            UI.erro(this, e.getMessage());
        }
    }
}
