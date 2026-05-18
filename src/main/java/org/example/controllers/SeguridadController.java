package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import org.example.models.SeguridadModel;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.Usuario;
import org.example.models.RolItem;
import org.example.models.UsuarioSeguridadModel;
import java.util.Objects;
import org.example.models.Privilegio;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.Acceso;
public class SeguridadController {

    @FXML private ImageView logoImage;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtPasswordNuevo;
    @FXML private PasswordField txtPasswordConfirmar;

    @FXML private VBox panelCambiarPassword;
    @FXML private VBox panelUsuarios;

    @FXML private TextField txtBuscarUsuario;
    @FXML private TextField txtUsuarioNuevo;
    @FXML private PasswordField txtPasswordUsuario;
    @FXML private TextField txtNombreCompletoUsuario;

    @FXML private ComboBox<RolItem> cbRolUsuario;
    @FXML private ComboBox<String> cbEstadoUsuario;

    @FXML private TableView<Usuario> tablaUsuarios;

    @FXML private TableColumn<Usuario, Integer> colUsuarioId;
    @FXML private TableColumn<Usuario, String> colUsuarioLogin;
    @FXML private TableColumn<Usuario, String> colUsuarioNombre;
    @FXML private TableColumn<Usuario, String> colUsuarioRol;
    @FXML private TableColumn<Usuario, Integer> colUsuarioEstado;
    @FXML private TableColumn<Usuario, String> colUsuarioFecha;
    @FXML private VBox panelAccesos;

    @FXML private TextField txtBuscarAcceso;

    @FXML private TableView<Acceso> tablaAccesos;

    @FXML private TableColumn<Acceso, Integer> colAccesoId;
    @FXML private TableColumn<Acceso, String> colAccesoUsuario;
    @FXML private TableColumn<Acceso, String> colAccesoNombre;
    @FXML private TableColumn<Acceso, String> colAccesoFecha;
    @FXML private TableColumn<Acceso, String> colAccesoEstado;

    @FXML private VBox panelPrivilegios;

    @FXML private ComboBox<RolItem> cbRolPrivilegios;

    @FXML private TableView<Privilegio> tablaPrivilegios;

    @FXML private TableColumn<Privilegio, String> colPrivModulo;
    @FXML private TableColumn<Privilegio, String> colPrivAccion;
    @FXML private TableColumn<Privilegio, Boolean> colPrivAsignado;
    private final SeguridadModel seguridadModel = new SeguridadModel();

    @FXML
    public void initialize() {
        cargarLogo();
        configurarTablaUsuarios();
        cargarRoles();
        cargarEstados();
        cargarUsuarios();
        configurarBusquedaUsuarios();
        detectarSeleccionUsuario();
        configurarTablaAccesos();
        configurarBusquedaAccesos();
        configurarTablaPrivilegios();
        cargarRolesPrivilegios();
    }

    private void cargarLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/org/example/img/logo.jpeg")
                    )
            );
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo");
        }
    }

    @FXML
    private void cambiarPassword() {
        String usuario = txtUsuario.getText().trim();
        String actual = txtPasswordActual.getText().trim();
        String nuevo = txtPasswordNuevo.getText().trim();
        String confirmar = txtPasswordConfirmar.getText().trim();

        if (usuario.isEmpty() || actual.isEmpty() || nuevo.isEmpty() || confirmar.isEmpty()) {
            mostrarMensaje("Completa todos los campos.");
            return;
        }

        if (nuevo.length() < 4) {
            mostrarMensaje("El nuevo password debe tener al menos 4 caracteres.");
            return;
        }

        if (!nuevo.equals(confirmar)) {
            mostrarMensaje("El nuevo password y la confirmación no coinciden.");
            return;
        }

        if (actual.equals(nuevo)) {
            mostrarMensaje("El nuevo password no puede ser igual al actual.");
            return;
        }

        boolean passwordCorrecto = seguridadModel.validarPasswordActual(usuario, actual);

        if (!passwordCorrecto) {
            mostrarMensaje("Usuario o password actual incorrecto.");
            return;
        }

        boolean ok = seguridadModel.cambiarPassword(usuario, nuevo);

        if (ok) {
            mostrarMensaje("Password actualizado correctamente.");
            limpiarFormulario();
        } else {
            mostrarMensaje("No se pudo actualizar el password.");
        }
    }

    private final UsuarioSeguridadModel usuarioModel = new UsuarioSeguridadModel();

    private void configurarTablaUsuarios() {
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsuarioLogin.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colUsuarioNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colUsuarioRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colUsuarioEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colUsuarioFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    }

    private void cargarUsuarios() {
        tablaUsuarios.setItems(
                FXCollections.observableArrayList(
                        usuarioModel.listarUsuarios()
                )
        );
    }

    private void cargarRoles() {
        cbRolUsuario.setItems(
                FXCollections.observableArrayList(
                        usuarioModel.listarRoles()
                )
        );
    }

    private void cargarEstados() {
        cbEstadoUsuario.setItems(
                FXCollections.observableArrayList("Activo", "Inactivo")
        );
    }

    private Usuario usuarioSeleccionado;

    private void configurarBusquedaUsuarios() {
        txtBuscarUsuario.textProperty().addListener((obs, oldValue, newValue) -> {
            tablaUsuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioModel.buscarUsuarios(newValue)
                    )
            );
        });
    }

    private void detectarSeleccionUsuario() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, usuario) -> {
                    if (usuario != null) {
                        usuarioSeleccionado = usuario;

                        txtUsuarioNuevo.setText(usuario.getUsuario());
                        txtNombreCompletoUsuario.setText(usuario.getNombreCompleto());
                        txtPasswordUsuario.clear();

                        seleccionarRol(usuario.getIdRol());

                        cbEstadoUsuario.setValue(
                                usuario.getEstado() == 1 ? "Activo" : "Inactivo"
                        );
                    }
                }
        );
    }

    private void seleccionarRol(int idRol) {
        for (RolItem rol : cbRolUsuario.getItems()) {
            if (rol.getIdRol() == idRol) {
                cbRolUsuario.setValue(rol);
                break;
            }
        }
    }

    private void configurarTablaAccesos() {
        colAccesoId.setCellValueFactory(new PropertyValueFactory<>("idAcceso"));
        colAccesoUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colAccesoNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colAccesoFecha.setCellValueFactory(new PropertyValueFactory<>("fechaAcceso"));
        colAccesoEstado.setCellValueFactory(new PropertyValueFactory<>("estadoAcceso"));
    }

    private void cargarAccesos() {
        tablaAccesos.setItems(
                FXCollections.observableArrayList(
                        usuarioModel.listarAccesos()
                )
        );
    }

    private void configurarBusquedaAccesos() {
        txtBuscarAcceso.textProperty().addListener((obs, oldValue, newValue) -> {
            tablaAccesos.setItems(
                    FXCollections.observableArrayList(
                            usuarioModel.buscarAccesos(newValue)
                    )
            );
        });
    }

    private void configurarTablaPrivilegios() {
        colPrivModulo.setCellValueFactory(
                new PropertyValueFactory<>("modulo")
        );

        colPrivAccion.setCellValueFactory(
                new PropertyValueFactory<>("accion")
        );

        colPrivAsignado.setCellValueFactory(
                new PropertyValueFactory<>("asignado")
        );
    }

    private void cargarRolesPrivilegios() {
        cbRolPrivilegios.setItems(
                FXCollections.observableArrayList(
                        usuarioModel.listarRoles()
                )
        );
    }

    @FXML
    private void cargarPrivilegiosRol() {
        RolItem rol = cbRolPrivilegios.getValue();

        if (rol == null) {
            mostrarMensaje("Selecciona un rol.");
            return;
        }

        tablaPrivilegios.setItems(
                FXCollections.observableArrayList(
                        usuarioModel.listarPrivilegiosPorRol(
                                rol.getIdRol()
                        )
                )
        );
    }

    @FXML
    private void asignarPrivilegio() {
        RolItem rol = cbRolPrivilegios.getValue();
        Privilegio privilegio = tablaPrivilegios.getSelectionModel().getSelectedItem();

        if (rol == null) {
            mostrarMensaje("Selecciona un rol.");
            return;
        }

        if (privilegio == null) {
            mostrarMensaje("Selecciona un privilegio.");
            return;
        }

        if (privilegio.isAsignado()) {
            mostrarMensaje("Este privilegio ya está asignado.");
            return;
        }

        boolean ok = usuarioModel.asignarPrivilegio(
                rol.getIdRol(),
                privilegio.getIdPrivilegio()
        );

        if (ok) {
            mostrarMensaje("Privilegio asignado correctamente.");
            cargarPrivilegiosRol();
        } else {
            mostrarMensaje("No se pudo asignar el privilegio.");
        }
    }

    @FXML
    private void quitarPrivilegio() {
        RolItem rol = cbRolPrivilegios.getValue();
        Privilegio privilegio = tablaPrivilegios.getSelectionModel().getSelectedItem();

        if (rol == null) {
            mostrarMensaje("Selecciona un rol.");
            return;
        }

        if (privilegio == null) {
            mostrarMensaje("Selecciona un privilegio.");
            return;
        }

        if (!privilegio.isAsignado()) {
            mostrarMensaje("Este privilegio no está asignado.");
            return;
        }

        boolean ok = usuarioModel.quitarPrivilegio(
                rol.getIdRol(),
                privilegio.getIdPrivilegio()
        );

        if (ok) {
            mostrarMensaje("Privilegio quitado correctamente.");
            cargarPrivilegiosRol();
        } else {
            mostrarMensaje("No se pudo quitar el privilegio.");
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtUsuario.clear();
        txtPasswordActual.clear();
        txtPasswordNuevo.clear();
        txtPasswordConfirmar.clear();
    }

    @FXML
    private void mostrarCambiarPassword() {
        panelUsuarios.setVisible(false);
        panelUsuarios.setManaged(false);

        panelAccesos.setVisible(false);
        panelAccesos.setManaged(false);

        panelPrivilegios.setVisible(false);
        panelPrivilegios.setManaged(false);

        panelCambiarPassword.setVisible(true);
        panelCambiarPassword.setManaged(true);
    }

    @FXML
    private void mostrarUsuarios() {
        panelCambiarPassword.setVisible(false);
        panelCambiarPassword.setManaged(false);

        panelAccesos.setVisible(false);
        panelAccesos.setManaged(false);

        panelPrivilegios.setVisible(false);
        panelPrivilegios.setManaged(false);

        panelUsuarios.setVisible(true);
        panelUsuarios.setManaged(true);

        cargarUsuarios();
    }

    @FXML
    private void mostrarPrivilegios() {
        panelCambiarPassword.setVisible(false);
        panelCambiarPassword.setManaged(false);

        panelUsuarios.setVisible(false);
        panelUsuarios.setManaged(false);

        panelAccesos.setVisible(false);
        panelAccesos.setManaged(false);

        panelPrivilegios.setVisible(true);
        panelPrivilegios.setManaged(true);

        cargarRolesPrivilegios();
    }

    @FXML
    private void mostrarAccesos() {
        panelCambiarPassword.setVisible(false);
        panelCambiarPassword.setManaged(false);

        panelUsuarios.setVisible(false);
        panelUsuarios.setManaged(false);

        panelPrivilegios.setVisible(false);
        panelPrivilegios.setManaged(false);

        panelAccesos.setVisible(true);
        panelAccesos.setManaged(true);

        cargarAccesos();
    }

    @FXML
    private void abrirDashboard() {
        cambiarVentana(
                "/org/example/views/dashboard.fxml",
                "dashboard.css",
                "SIG-CB - Dashboard"
        );
    }
    @FXML
    private void limpiarUsuario() {
        usuarioSeleccionado = null;

        txtUsuarioNuevo.clear();
        txtPasswordUsuario.clear();
        txtNombreCompletoUsuario.clear();

        cbRolUsuario.getSelectionModel().clearSelection();
        cbEstadoUsuario.setValue("Activo");

        tablaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarUsuario() {
        String usuario = txtUsuarioNuevo.getText().trim();
        String password = txtPasswordUsuario.getText().trim();
        String nombre = txtNombreCompletoUsuario.getText().trim();
        RolItem rol = cbRolUsuario.getValue();

        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || rol == null) {
            mostrarMensaje("Completa usuario, password, nombre y rol.");
            return;
        }

        if (usuarioModel.existeUsuario(usuario)) {
            mostrarMensaje("Ya existe un usuario con ese nombre.");
            return;
        }

        boolean ok = usuarioModel.insertarUsuario(
                usuario,
                password,
                nombre,
                rol.getIdRol()
        );

        if (ok) {
            mostrarMensaje("Usuario guardado correctamente.");
            cargarUsuarios();
            limpiarUsuario();
        } else {
            mostrarMensaje("No se pudo guardar el usuario.");
        }
    }

    @FXML
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Selecciona un usuario.");
            return;
        }

        String usuario = txtUsuarioNuevo.getText().trim();
        String nombre = txtNombreCompletoUsuario.getText().trim();
        RolItem rol = cbRolUsuario.getValue();
        String estadoTexto = cbEstadoUsuario.getValue();

        if (usuario.isEmpty() || nombre.isEmpty() || rol == null || estadoTexto == null) {
            mostrarMensaje("Completa usuario, nombre, rol y estado.");
            return;
        }

        int estado = estadoTexto.equals("Activo") ? 1 : 0;

        boolean ok = usuarioModel.actualizarUsuario(
                usuarioSeleccionado.getIdUsuario(),
                usuario,
                nombre,
                rol.getIdRol(),
                estado
        );

        if (ok) {
            mostrarMensaje("Usuario actualizado correctamente.");
            cargarUsuarios();
            limpiarUsuario();
        } else {
            mostrarMensaje("No se pudo actualizar el usuario.");
        }
    }

    @FXML
    private void cambiarEstadoUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Selecciona un usuario.");
            return;
        }

        int nuevoEstado = usuarioSeleccionado.getEstado() == 1 ? 0 : 1;

        boolean ok = usuarioModel.cambiarEstadoUsuario(
                usuarioSeleccionado.getIdUsuario(),
                nuevoEstado
        );

        if (ok) {
            mostrarMensaje(
                    nuevoEstado == 1
                            ? "Usuario activado correctamente."
                            : "Usuario desactivado correctamente."
            );

            cargarUsuarios();
            limpiarUsuario();
        } else {
            mostrarMensaje("No se pudo cambiar el estado del usuario.");
        }
    }

    private void cambiarVentana(String fxml, String cssModulo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxml))
            );

            Scene scene = new Scene(loader.load(), 1100, 780);

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/css/global.css")
                    ).toExternalForm()
            );

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/css/" + cssModulo)
                    ).toExternalForm()
            );

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje("No se pudo abrir la ventana.");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Seguridad");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}