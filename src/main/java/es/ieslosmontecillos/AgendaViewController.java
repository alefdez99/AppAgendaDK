package es.ieslosmontecillos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgendaViewController implements Initializable
{
    private DataUtil dataUtil;
    private ObservableList<Provincia> olProvincias;
    private ObservableList<Persona> olPersonas;
    @javafx.fxml.FXML
    private TableView<Persona> tableViewAgenda;
    @javafx.fxml.FXML
    private TableColumn<Persona, String> columnNombre;
    @javafx.fxml.FXML
    private TableColumn<Persona, String> columnApellidos;
    @javafx.fxml.FXML
    private TableColumn<Persona, String> columnEmail;
    @javafx.fxml.FXML
    private TableColumn<Persona, String> columnProvincia;
    @javafx.fxml.FXML
    private TextField textFieldNombre;
    @javafx.fxml.FXML
    private TextField textFieldApellidos;
    private Persona personaSeleccionada;
    @javafx.fxml.FXML
    private AnchorPane rootAgendaView;

    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public void setOlProvincias(ObservableList<Provincia> olProvincias) {
        this.olProvincias = olProvincias;
    }

    public void setOlPersonas(ObservableList<Persona> olPersonas) {
        this.olPersonas = olPersonas;
    }

    public void cargarTodasPersonas(){tableViewAgenda.setItems(FXCollections.observableArrayList(olPersonas));}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        //columnProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        columnProvincia.setCellValueFactory(
                cellData->{
                    SimpleStringProperty property=new SimpleStringProperty();
                    if (cellData.getValue().getProvincia() != null){
                        System.out.println("Provincia: " + cellData.getValue().getProvincia().getNombre());
                        property.setValue(cellData.getValue().getProvincia().getNombre());
                    }
                    return property;
                });

        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable,oldValue,newValue)->{
                    personaSeleccionada=newValue;
                    if (personaSeleccionada != null){
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
                });
    }

    @javafx.fxml.FXML
    public void onActionButtonGuardar(ActionEvent actionEvent)
    {
        if (personaSeleccionada != null)
        {
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            dataUtil.actualizarPersona(personaSeleccionada);

            int numFilaSeleccionada =
                    tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);

            TablePosition pos = new
                    TablePosition(tableViewAgenda,numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonNuevo(ActionEvent event)
    {
        try
        {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new
                    FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView=fxmlLoader.load();
            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);
            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain =
                    (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

            //Pasamos el StackPane (contenedor principal) de est vista a la vista PersonaDetalleView
            PersonaDetalleViewController personaDetalleViewController =
                    (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            //Intercambio de datos funcionales con el detalle
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaDetalleViewController.setDataUtil(dataUtil);

            // Para el botón Nuevo:
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(personaSeleccionada,true); //El booleano hace referencia a que la persona es nueva

            personaDetalleViewController.mostrarDatos();
        }
        catch (IOException ex)
        {
            System.out.println("Error volcado"+ex);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonEditar(ActionEvent event)
    {
        try
        {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new
                    FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView=fxmlLoader.load();
            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);
            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain =
                    (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

            //Pasamos el StackPane (contenedor principal) de est vista a la vista PersonaDetalleView
            PersonaDetalleViewController personaDetalleViewController =
                    (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            //Intercambio de datos funcionales con el detalle
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaDetalleViewController.setDataUtil(dataUtil);

            // Para el botón Editar
            personaDetalleViewController.setPersona(personaSeleccionada,false); //El booleano hace referencia a que la persona no es nueva

            personaDetalleViewController.mostrarDatos();
        }
        catch (IOException | ParseException ex)
        {
            System.out.println("Error volcado"+ex);
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonSuprimir(ActionEvent event)
    {
        //Ventana emergente de alerta
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK)
        {
            // Acciones a realizar si el usuario acepta
            dataUtil.eliminarPersona(personaSeleccionada);
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        }
        else
        {
            // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada=
                    tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda,
                    numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }

    }
}
