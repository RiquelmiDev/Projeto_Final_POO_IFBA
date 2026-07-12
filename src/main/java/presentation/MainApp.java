package presentation;

import business.ColetaService;
import business.Conteiner;
import business.Relatorio;
import business.TipoResiduo;
import data.ColetaRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ColetaRepository repository = new ColetaRepository();
    private final ColetaService service = new ColetaService(repository);

    private final TextField idContainerField = new TextField();
    private final TextField capacidadeField = new TextField();
    private final TextField tipoContainerField = new TextField();
    private final TextField localizacaoField = new TextField();

    private final TextField coletaIdField = new TextField();
    private final ComboBox<String> containerCombo = new ComboBox<>();
    private final ComboBox<TipoResiduo> tipoResiduoCombo = new ComboBox<>();
    private final TextField volumeField = new TextField();
    private final TextArea relatorioArea = new TextArea();
    private final ListView<String> logList = new ListView<>();

    @Override
    public void start(Stage stage) {
        tipoResiduoCombo.getItems().setAll(TipoResiduo.values());
        tipoResiduoCombo.setValue(TipoResiduo.RECICLAVEL);

        idContainerField.setPromptText("Ex.: C-001");
        capacidadeField.setPromptText("Ex.: 100.0");
        tipoContainerField.setPromptText("Ex.: Reciclável");
        localizacaoField.setPromptText("Ex.: Bloco A");
        coletaIdField.setPromptText("Ex.: R-001");
        volumeField.setPromptText("Ex.: 20.0");

        Button cadastrarContainerButton = new Button("Cadastrar Conteiner");
        cadastrarContainerButton.setOnAction(event -> cadastrarConteiner());

        Button registrarColetaButton = new Button("Registrar coleta");
        registrarColetaButton.setOnAction(event -> registrarColeta());

        Button atualizarRelatorioButton = new Button("Gerar relatório");
        atualizarRelatorioButton.setOnAction(event -> atualizarRelatorio());

        Label titulo = new Label("Sistema de Gestão de Resíduos Sólidos");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#eb8a0c"));

        Label subTitulo = new Label("Gestão de coleta, reciclagem e relatórios");
        subTitulo.setTextFill(Color.web("#4b5563"));

        Label tituloContainer = new Label("Cadastro de Conteineres");
        tituloContainer.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label tituloColeta = new Label("Registro de Coleta");
        tituloColeta.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label tituloRelatorio = new Label("Relatório");
        tituloRelatorio.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label tituloEventos = new Label("Eventos");
        tituloEventos.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox containerBox = criarBloco(tituloContainer, 8,
                new Label("ID do Conteiner"),
                idContainerField,
                new Label("Capacidade máxima"),
                capacidadeField,
                new Label("Tipo do Conteiner"),
                tipoContainerField,
                new Label("Localização"),
                localizacaoField,
                cadastrarContainerButton
        );

        VBox coletaBox = criarBloco(tituloColeta, 8,
                new Label("ID da coleta"),
                coletaIdField,
                new Label("Conteiner"),
                containerCombo,
                new Label("Tipo de resíduo"),
                tipoResiduoCombo,
                new Label("Volume coletado"),
                volumeField,
                registrarColetaButton
        );

        VBox relatorioBox = criarBloco(tituloRelatorio, 8,
                relatorioArea,
                tituloEventos,
                logList,
                atualizarRelatorioButton
        );

        relatorioArea.setPrefHeight(180);
        logList.setPrefHeight(180);
        relatorioArea.setEditable(false);
        logList.setEditable(false);

        HBox cardsBox = new HBox(12, containerBox, coletaBox, relatorioBox);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setFillHeight(true);

        VBox mainContent = new VBox(12,
                titulo,
                subTitulo,
                cardsBox
        );
        mainContent.setPadding(new Insets(18));
        mainContent.setFillWidth(true);
        mainContent.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(mainContent);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fbff, #eef4f9);");

        stage.setTitle("Sistema de Gestão de Resíduos Sólidos");
        stage.setScene(new Scene(root, 1150, 680));
        stage.show();

        atualizarListaConteineres();
        atualizarRelatorio();
    }

    private void cadastrarConteiner() {
        try {
            String id = idContainerField.getText().trim();
            double capacidade = Double.parseDouble(capacidadeField.getText().trim());
            String tipo = tipoContainerField.getText().trim();
            String localizacao = localizacaoField.getText().trim();

            service.cadastrarContainer(id, capacidade, tipo, localizacao);
            atualizarListaConteineres();
            logList.getItems().add("Conteiner " + id + " cadastrado com sucesso.");
            limparCamposContainer();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void registrarColeta() {
        try {
            String containerId = containerCombo.getValue();
            String coletaId = coletaIdField.getText().trim();
            TipoResiduo tipoResiduo = tipoResiduoCombo.getValue();
            double volume = Double.parseDouble(volumeField.getText().trim());

            service.registrarColeta(containerId, coletaId, tipoResiduo, volume);
            logList.getItems().add("Coleta " + coletaId + " registrada com sucesso.");
            limparCamposColeta();
            atualizarRelatorio();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizarRelatorio() {
        Relatorio relatorio = service.gerarRelatorio();
        relatorioArea.setText(
                "Total de coletas: " + relatorio.getTotalColetas() + "\n" +
                "Volume total coletado: " + relatorio.getVolumeTotal() + "\n" +
                "Taxa de reciclagem: " + relatorio.getTaxaReciclagem()
        );
    }

    private void atualizarListaConteineres() {
        containerCombo.getItems().clear();
        for (Conteiner Conteiner : repository.listarConteineres()) {
            containerCombo.getItems().add(Conteiner.getId());
        }
    }

    private void limparCamposContainer() {
        idContainerField.clear();
        capacidadeField.clear();
        tipoContainerField.clear();
        localizacaoField.clear();
    }

    private void limparCamposColeta() {
        coletaIdField.clear();
        volumeField.clear();
    }

    private VBox criarBloco(Label titulo, double spacing, javafx.scene.Node... nodes) {
        VBox box = new VBox(spacing);
        box.setPadding(new Insets(14));
        box.setPrefWidth(320);
        
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c7d6e4; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.getChildren().add(titulo);
        for (javafx.scene.Node node : nodes) {
            box.getChildren().add(node);
        }
        return box;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Operação não concluída");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
