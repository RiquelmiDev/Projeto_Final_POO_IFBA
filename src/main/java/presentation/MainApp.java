package presentation;

import business.Coleta;
import business.ColetaService;
import business.Conteiner;
import business.GenericService;
import business.PerfilUsuario;
import business.Relatorio;
import business.TipoResiduo;
import business.Usuario;
import business.UsuarioService;
import data.ColetaRepository;
import data.UsuarioRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainApp extends Application {

    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ColetaRepository repository = new ColetaRepository();
    private final GenericService<Coleta, String> coletaService = new ColetaService(repository);
    private final GenericService<Usuario, String> usuarioService = new UsuarioService(new UsuarioRepository());

    private Stage primaryStage;
    private Usuario usuarioLogado;

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
    private final ListView<String> usuariosList = new ListView<>();

    private final TextField usuarioNomeField = new TextField();
    private final TextField usuarioUsernameField = new TextField();
    private final PasswordField usuarioSenhaField = new PasswordField();
    private final ComboBox<PerfilUsuario> usuarioPerfilCombo = new ComboBox<>();

    private final TextField loginUsernameField = new TextField();
    private final PasswordField loginPasswordField = new PasswordField();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        prepararDadosIniciais();
        configurarFormularioPrincipal();
        stage.setTitle("Sistema de Gestão de Resíduos Sólidos");
        stage.setScene(criarSceneLogin());
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.show();
    }

    private void prepararDadosIniciais() {
        tipoResiduoCombo.getItems().setAll(TipoResiduo.values());
        tipoResiduoCombo.setValue(TipoResiduo.RECICLAVEL);
        usuarioPerfilCombo.getItems().setAll(PerfilUsuario.values());
        usuarioPerfilCombo.setValue(PerfilUsuario.COLABORADOR);

        idContainerField.setPromptText("Ex.: C-001");
        capacidadeField.setPromptText("Ex.: 100.0");
        tipoContainerField.setPromptText("Ex.: Reciclável");
        localizacaoField.setPromptText("Ex.: Bloco A");
        coletaIdField.setPromptText("Ex.: R-001");
        volumeField.setPromptText("Ex.: 20.0");
        loginUsernameField.setPromptText("Usuário");
        loginPasswordField.setPromptText("Senha");

        try {
            usuarioService.cadastrarUsuario("admin", "admin123", PerfilUsuario.ADMIN, "Administrador do Sistema");
        } catch (IllegalArgumentException ignored) {
            // Usuário admin já existente.
        }

        try {
            usuarioService.cadastrarUsuario("teste", "123456", PerfilUsuario.COLABORADOR, "Teste da Silva JR");
        } catch (IllegalArgumentException ignored) {
            // Usuário teste já existente.
        }
    }

    private void configurarFormularioPrincipal() {
        relatorioArea.setPrefHeight(260);
        logList.setPrefHeight(180);
        relatorioArea.setEditable(false);
        relatorioArea.setWrapText(true);
        relatorioArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 12px;");
        logList.setEditable(false);
        usuariosList.setEditable(false);
        atualizarListaConteineres();
        atualizarRelatorio();
        atualizarListaUsuarios();
    }

    private Scene criarSceneLogin() {
        Label titulo = new Label("Login");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titulo.setTextFill(Color.web("#ffffff"));
        titulo.setAlignment(Pos.CENTER);
        titulo.setMaxWidth(Double.MAX_VALUE);

        Label usuarioLabel = new Label("Usuário");
        usuarioLabel.setTextFill(Color.web("#e5e7eb"));
        Label senhaLabel = new Label("Senha");
        senhaLabel.setTextFill(Color.web("#e5e7eb"));

        loginUsernameField.setPrefWidth(260);
        loginPasswordField.setPrefWidth(260);
        loginUsernameField.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-prompt-text-fill: #d1d5db; -fx-border-color: rgba(255,255,255,0.9); -fx-border-radius: 8; -fx-background-radius: 8;");
        loginPasswordField.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-prompt-text-fill: #d1d5db; -fx-border-color: rgba(255,255,255,0.9); -fx-border-radius: 8; -fx-background-radius: 8;");

        Button entrarButton = new Button("Entrar");
        entrarButton.setOnAction(event -> autenticarUsuario());
        entrarButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 18 8 18;");

        Button cadastrarNoLoginButton = new Button("Cadastrar usuário");
        cadastrarNoLoginButton.setOnAction(event -> abrirTelaCadastroUsuario());
        cadastrarNoLoginButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 18 8 18;");

        HBox botoesBox = new HBox(12, entrarButton, cadastrarNoLoginButton);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);

        VBox loginContent = new VBox(12);
        loginContent.setAlignment(Pos.CENTER_LEFT);
        loginContent.setPrefWidth(320);
        loginContent.setMaxWidth(420);
        loginContent.getChildren().addAll(
                titulo,
                usuarioLabel,
                loginUsernameField,
                senhaLabel,
                loginPasswordField,
                botoesBox
        );

        StackPane loginContainer = new StackPane(loginContent);
        loginContainer.setPrefWidth(420);
        loginContainer.setMaxWidth(420);
        loginContainer.setPrefHeight(300);
        loginContainer.setMaxHeight(300);
        loginContainer.setPadding(new Insets(16));
        loginContainer.setStyle("-fx-background-color: rgba(17, 24, 39, 0.88); -fx-border-color: rgba(255,255,255,0.9); -fx-border-width: 1.5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 18, 0.2, 0, 8);");

        StackPane background = new StackPane();
        Image bgImage = null;
        try {
            bgImage = new Image(getClass().getResourceAsStream("/Plano-de-Gerenciamento-de-Residuos-Solidos1-1200x675.png"));
        } catch (Exception ignored) {
            bgImage = null;
        }

        if (bgImage != null) {
            ImageView imageView = new ImageView(bgImage);
            imageView.setFitWidth(1600);
            imageView.setFitHeight(900);
            imageView.setPreserveRatio(false);
            imageView.setOpacity(0.88);
            background.getChildren().add(imageView);
        } else {
            background.setStyle("-fx-background-color: linear-gradient(to bottom, #dfeecf, #aabf8b);");
        }

        StackPane overlay = new StackPane(loginContainer);
        overlay.setPadding(new Insets(18));
        overlay.setAlignment(Pos.CENTER);
        background.getChildren().add(overlay);

        return new Scene(background, 900, 620);
    }

    private Scene criarSceneSistema() {
        Label titulo = new Label("EcoCiclo SYS");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titulo.setTextFill(Color.web("#1f2937"));

        Label usuarioInfo = new Label(usuarioLogado.getNome());
        usuarioInfo.setTextFill(Color.web("#0f172a"));
        usuarioInfo.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            usuarioLogado = null;
            loginUsernameField.clear();
            loginPasswordField.clear();
            primaryStage.setScene(criarSceneLogin());
        });
        logoutButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 14 8 14;");

        HBox userBox = new HBox(12, usuarioInfo, logoutButton);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(userBox, javafx.scene.layout.Priority.ALWAYS);

        HBox header = new HBox(18, titulo, userBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 20, 16, 20));
        header.setSpacing(18);
        header.setStyle("-fx-background-color: #f8fbff; -fx-border-color: transparent transparent #d7e3ef transparent; -fx-border-width: 0 0 1 0; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.10), 8, 0.25, 0, 2);");
        header.setMaxWidth(Double.MAX_VALUE);

        Button dashboardButton = new Button("Dashboard");
        dashboardButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");

        Button usuariosButton = new Button("Usuários");
        usuariosButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");

        Button conteineresButton = new Button("Contêineres");
        conteineresButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");

        Button coletasButton = new Button("Coletas");
        coletasButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            usuariosButton.setDisable(true);
            usuariosButton.setOpacity(0.55);
        }

        HBox modulosNav = new HBox(12, dashboardButton, conteineresButton, coletasButton, usuariosButton);
        modulosNav.setAlignment(Pos.CENTER_LEFT);
        modulosNav.setPadding(new Insets(18, 20, 0, 20));

        VBox dashboardContent = criarDashboardContent();
        VBox usuariosContent = criarUsuariosContent();
        VBox conteineresContent = criarConteineresContent();
        VBox coletasContent = criarColetasContent();
        StackPane conteudoModulo = new StackPane(dashboardContent, usuariosContent, conteineresContent, coletasContent);
        conteudoModulo.setPadding(new Insets(0, 18, 18, 18));

        dashboardButton.setOnAction(event -> trocarTelaModulo("dashboard", conteudoModulo, dashboardContent, usuariosContent, conteineresContent, coletasContent, dashboardButton, usuariosButton, conteineresButton, coletasButton));
        usuariosButton.setOnAction(event -> trocarTelaModulo("usuarios", conteudoModulo, dashboardContent, usuariosContent, conteineresContent, coletasContent, dashboardButton, usuariosButton, conteineresButton, coletasButton));
        conteineresButton.setOnAction(event -> trocarTelaModulo("conteineres", conteudoModulo, dashboardContent, usuariosContent, conteineresContent, coletasContent, dashboardButton, usuariosButton, conteineresButton, coletasButton));
        coletasButton.setOnAction(event -> trocarTelaModulo("coletas", conteudoModulo, dashboardContent, usuariosContent, conteineresContent, coletasContent, dashboardButton, usuariosButton, conteineresButton, coletasButton));

        trocarTelaModulo("dashboard", conteudoModulo, dashboardContent, usuariosContent, conteineresContent, coletasContent, dashboardButton, usuariosButton, conteineresButton, coletasButton);

        VBox content = new VBox(0, modulosNav, conteudoModulo);
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fbff, #eef4f9);");

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(content);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fbff, #eef4f9);");

        atualizarListaConteineres();
        atualizarRelatorio();
        atualizarListaUsuarios();

        return new Scene(root, 1200, 760);
    }

    private VBox criarDashboardContent() {
        Label tituloResumo = new Label("Resumo do Sistema");
        tituloResumo.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label statusLabel = new Label("Sistema em operação");
        statusLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-weight: bold;");

        Button atualizarRelatorioButton = new Button("Atualizar relatório");
        atualizarRelatorioButton.setOnAction(event -> atualizarRelatorioManual());

        Label tituloRelatorio = new Label("Relatório geral");
        tituloRelatorio.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label tituloEventos = new Label("Atividades recentes");
        tituloEventos.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox resumoBox = criarBloco(tituloResumo, 8,
                statusLabel,
                tituloRelatorio,
                relatorioArea,
                tituloEventos,
                logList,
                atualizarRelatorioButton
        );

        resumoBox.setPrefWidth(1100);
        relatorioArea.setPrefHeight(220);
        logList.setPrefHeight(180);

        VBox dashboardContent = new VBox(16, resumoBox);
        dashboardContent.setPadding(new Insets(18));
        dashboardContent.setVisible(true);
        dashboardContent.setManaged(true);
        return dashboardContent;
    }

    private VBox criarConteineresContent() {
        Button cadastrarContainerButton = new Button("Cadastrar Conteiner");
        cadastrarContainerButton.setOnAction(event -> cadastrarConteiner());

        Label tituloContainer = new Label("Cadastro de Conteineres");
        tituloContainer.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox conteineresBox = criarBloco(tituloContainer, 8,
                new Label("ID do Conteiner"), idContainerField,
                new Label("Capacidade máxima"), capacidadeField,
                new Label("Tipo do Conteiner"), tipoContainerField,
                new Label("Localização"), localizacaoField,
                cadastrarContainerButton
        );

        VBox conteineresContent = new VBox(16, conteineresBox);
        conteineresContent.setPadding(new Insets(18));
        conteineresContent.setVisible(false);
        conteineresContent.setManaged(false);
        return conteineresContent;
    }

    private VBox criarColetasContent() {
        Button registrarColetaButton = new Button("Registrar coleta");
        registrarColetaButton.setOnAction(event -> registrarColeta());

        Label tituloColeta = new Label("Registro de Coleta");
        tituloColeta.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox coletasBox = criarBloco(tituloColeta, 8,
                new Label("ID da coleta"), coletaIdField,
                new Label("Conteiner"), containerCombo,
                new Label("Tipo de resíduo"), tipoResiduoCombo,
                new Label("Volume coletado"), volumeField,
                registrarColetaButton
        );

        VBox coletasContent = new VBox(16, coletasBox);
        coletasContent.setPadding(new Insets(18));
        coletasContent.setVisible(false);
        coletasContent.setManaged(false);
        return coletasContent;
    }

    private VBox criarUsuariosContent() {
        VBox adminBox = criarBoxAdmin();
        VBox usuariosContent = new VBox(16, adminBox);
        usuariosContent.setPadding(new Insets(18));
        usuariosContent.setVisible(false);
        usuariosContent.setManaged(false);
        return usuariosContent;
    }

    private void trocarTelaModulo(String modulo, StackPane conteudoModulo, VBox dashboardContent, VBox usuariosContent, VBox conteineresContent, VBox coletasContent, Button dashboardButton, Button usuariosButton, Button conteineresButton, Button coletasButton) {
        boolean mostrarDashboard = "dashboard".equals(modulo);
        boolean mostrarUsuarios = "usuarios".equals(modulo);
        boolean mostrarConteineres = "conteineres".equals(modulo);
        boolean mostrarColetas = "coletas".equals(modulo);

        dashboardContent.setVisible(mostrarDashboard);
        dashboardContent.setManaged(mostrarDashboard);
        usuariosContent.setVisible(mostrarUsuarios);
        usuariosContent.setManaged(mostrarUsuarios);
        conteineresContent.setVisible(mostrarConteineres);
        conteineresContent.setManaged(mostrarConteineres);
        coletasContent.setVisible(mostrarColetas);
        coletasContent.setManaged(mostrarColetas);

        dashboardButton.setStyle(mostrarDashboard ? "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;" : "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
        conteineresButton.setStyle(mostrarConteineres ? "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;" : "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
        coletasButton.setStyle(mostrarColetas ? "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;" : "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
        usuariosButton.setStyle(mostrarUsuarios ? "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;" : "-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");

        conteudoModulo.setVisible(true);
        conteudoModulo.setManaged(true);
    }

    private VBox criarBoxAdmin() {
        if (usuarioLogado == null) {
            return new VBox();
        }

        if (usuarioLogado.getPerfil() == PerfilUsuario.COLABORADOR) {
            return new VBox();
        }

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            VBox perfilBox = criarBloco(new Label("Perfil do usuário"), 8,
                    new Label("Nome: " + usuarioLogado.getNome()),
                    new Label("Usuário: " + usuarioLogado.getUsername()),
                    new Label("Perfil: " + usuarioLogado.getPerfil())
            );
            perfilBox.setPrefWidth(1000);
            return perfilBox;
        }

        Label tituloUsuario = new Label("Gerenciamento de Usuários");
        tituloUsuario.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label nomeLabel = new Label("Nome completo");
        Label usernameLabel = new Label("Usuário");
        Label senhaLabel = new Label("Senha");
        Label perfilLabel = new Label("Perfil");

        Button cadastrarUsuarioButton = new Button("Cadastrar usuário");
        cadastrarUsuarioButton.setOnAction(event -> cadastrarUsuarioSistema());

        Button removerUsuarioButton = new Button("Excluir usuário selecionado");
        removerUsuarioButton.setOnAction(event -> excluirUsuarioSelecionado());

        HBox camposUsuario = new HBox(10,
                new VBox(6, nomeLabel, usuarioNomeField),
                new VBox(6, usernameLabel, usuarioUsernameField),
                new VBox(6, senhaLabel, usuarioSenhaField),
                new VBox(6, perfilLabel, usuarioPerfilCombo),
                cadastrarUsuarioButton,
                removerUsuarioButton
        );
        camposUsuario.setAlignment(Pos.BOTTOM_LEFT);

        VBox usuariosSection = criarBloco(tituloUsuario, 8,
                camposUsuario,
                usuariosList
        );
        usuariosSection.setPrefWidth(900);
        usuariosList.setPrefHeight(220);

        VBox adminBox = new VBox(10, usuariosSection);
        adminBox.setPadding(new Insets(8));
        return adminBox;
    }

    private void autenticarUsuario() {
        try {
            String username = loginUsernameField.getText().trim();
            String senha = loginPasswordField.getText();

            usuarioLogado = usuarioService.autenticar(username, senha);
            primaryStage.setScene(criarSceneSistema());
            limparCamposCadastroUsuario();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void abrirTelaCadastroUsuario() {
        Stage janela = new Stage();
        janela.initOwner(primaryStage);
        janela.initModality(Modality.WINDOW_MODAL);
        janela.setTitle("Cadastro de Usuário");

        Label titulo = new Label("Cadastrar Usuário");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#ffffff"));
        titulo.setAlignment(Pos.CENTER);
        titulo.setMaxWidth(Double.MAX_VALUE);

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome completo");
        nomeField.setPrefWidth(280);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        usernameField.setPrefWidth(280);

        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");
        senhaField.setPrefWidth(280);

        ComboBox<PerfilUsuario> perfilField = new ComboBox<>();
        perfilField.getItems().setAll(PerfilUsuario.values());
        perfilField.setValue(PerfilUsuario.COLABORADOR);
        perfilField.setPrefWidth(280);

        Label nomeLabel = new Label("Nome completo");
        nomeLabel.setTextFill(Color.web("#e5e7eb"));
        Label usernameLabel = new Label("Usuário");
        usernameLabel.setTextFill(Color.web("#e5e7eb"));
        Label senhaLabel = new Label("Senha");
        senhaLabel.setTextFill(Color.web("#e5e7eb"));
        Label perfilLabel = new Label("Perfil");
        perfilLabel.setTextFill(Color.web("#e5e7eb"));

        Button salvarButton = new Button("Salvar");
        salvarButton.setOnAction(event -> {
            try {
                usuarioService.cadastrarUsuario(usernameField.getText(), senhaField.getText(), perfilField.getValue(), nomeField.getText());
                atualizarListaUsuarios();
                janela.close();
                mostrarInfo("Usuário cadastrado com sucesso!");
            } catch (Exception ex) {
                mostrarErro(ex.getMessage());
            }
        });
        salvarButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20 8 20;");

        VBox formulario = new VBox(10,
                titulo,
                nomeLabel,
                nomeField,
                usernameLabel,
                usernameField,
                senhaLabel,
                senhaField,
                perfilLabel,
                perfilField,
                salvarButton
        );
        formulario.setAlignment(Pos.CENTER_LEFT);
        formulario.setPrefWidth(340);
        formulario.setMaxWidth(340);
        formulario.setPrefHeight(380);
        formulario.setMaxHeight(380);
        formulario.setPadding(new Insets(22, 24, 22, 24));
        formulario.setStyle("-fx-background-color: rgba(17, 24, 39, 0.88); -fx-border-color: rgba(255,255,255,0.9); -fx-border-width: 1.5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 18, 0.2, 0, 8);");

        StackPane tela = new StackPane(formulario);
        tela.setPadding(new Insets(28));
        tela.setStyle("-fx-background-color: linear-gradient(to bottom, #dfeecf, #aabf8b);");

        janela.setScene(new Scene(tela, 500, 520));
        janela.showAndWait();
    }

    private void cadastrarUsuarioSistema() {
        try {
            String nome = usuarioNomeField.getText().trim();
            String username = usuarioUsernameField.getText().trim();
            String senha = usuarioSenhaField.getText();
            PerfilUsuario perfil = usuarioPerfilCombo.getValue();

            usuarioService.cadastrarUsuario(username, senha, perfil, nome);
            atualizarListaUsuarios();
            limparCamposCadastroUsuario();
            mostrarInfo("Cadastro realizado com sucesso.");
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void excluirUsuarioSelecionado() {
        try {
            String item = usuariosList.getSelectionModel().getSelectedItem();
            if (item == null || item.isBlank()) {
                throw new IllegalArgumentException("Selecione um usuário para excluir.");
            }

            String username = item.split("\\|")[0].trim();
            usuarioService.removerUsuario(username);
            atualizarListaUsuarios();
            mostrarInfo("Usuário removido com sucesso.");
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void cadastrarConteiner() {
        try {
            String id = idContainerField.getText().trim();
            double capacidade = Double.parseDouble(capacidadeField.getText().trim());
            String tipo = tipoContainerField.getText().trim();
            String localizacao = localizacaoField.getText().trim();

            coletaService.cadastrarContainer(id, capacidade, tipo, localizacao);
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

            coletaService.registrarColeta(containerId, coletaId, tipoResiduo, volume);
            logList.getItems().add("Coleta " + coletaId + " registrada com sucesso.");
            limparCamposColeta();
            atualizarRelatorio();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizarRelatorio() {
        Relatorio relatorio = coletaService.gerarRelatorio();
        relatorioArea.setText(relatorio.toString());
    }

    private void atualizarRelatorioManual() {
        atualizarRelatorio();
        String momento = LocalDateTime.now().format(DATA_HORA_FORMATTER);
        logList.getItems().add("Relatório atualizado manualmente em " + momento + ".");
    }

    private void atualizarListaConteineres() {
        containerCombo.getItems().clear();
        for (Conteiner conteiner : repository.listarConteineres()) {
            containerCombo.getItems().add(conteiner.getId());
        }
    }

    private void atualizarListaUsuarios() {
        usuariosList.getItems().clear();
        for (Usuario usuario : usuarioService.listarUsuarios()) {
            usuariosList.getItems().add(usuario.getUsername() + " | " + usuario.getNome() + " | " + usuario.getPerfil());
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

    private void limparCamposCadastroUsuario() {
        usuarioNomeField.clear();
        usuarioUsernameField.clear();
        usuarioSenhaField.clear();
        usuarioPerfilCombo.setValue(PerfilUsuario.COLABORADOR);
    }

    private VBox criarBloco(Label titulo, double spacing, javafx.scene.Node... nodes) {
        VBox box = new VBox(spacing);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c7d6e4; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.getChildren().add(titulo);
        for (javafx.scene.Node node : nodes) {
            box.getChildren().add(node);
        }
        return box;
    }

    private VBox criarBloco(javafx.scene.Node... nodes) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c7d6e4; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.getChildren().addAll(nodes);
        return box;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Operação não concluída");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Operação concluída");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
