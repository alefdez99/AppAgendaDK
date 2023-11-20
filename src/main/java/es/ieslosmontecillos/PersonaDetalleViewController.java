package es.ieslosmontecillos;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

public class PersonaDetalleViewController
{
    //Atributos
    @javafx.fxml.FXML
    private AnchorPane rootPersonaDetalleView;
    @javafx.fxml.FXML
    private TextField textFieldNombre;
    @javafx.fxml.FXML
    private TextField textFieldApellidos;
    @javafx.fxml.FXML
    private TextField textFieldTelefono;
    @javafx.fxml.FXML
    private TextField textFieldEmail;
    @javafx.fxml.FXML
    private TextField textFieldNumHijos;
    @javafx.fxml.FXML
    private TextField textFieldSalario;
    @javafx.fxml.FXML
    private CheckBox checkBoxJubilado;
    @javafx.fxml.FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @javafx.fxml.FXML
    private RadioButton radioButtonSoltero;
    public static final String SOLTERO="S";
    @javafx.fxml.FXML
    private RadioButton radioButtonCasado;
    public static final String CASADO="C";
    @javafx.fxml.FXML
    private RadioButton radioButtonViudo;
    public static final String VIUDO="V";
    @javafx.fxml.FXML
    private DatePicker datePickerFechaNacimiento;
    @javafx.fxml.FXML
    private ImageView imageViewFoto;
    public static final String CARPETA_FOTOS="Fotos";
    private AnchorPane rootAgendaView;
    private TableView tableViewPrevio;
    private Persona persona;
    private DataUtil dataUtil;
    private boolean nuevaPersona;
    private boolean errorFormato = false;

    //Métodos
    public void setRootAgendaView(AnchorPane rootAgendaView) {this.rootAgendaView = rootAgendaView;}

    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public void setTableViewPrevio(TableView tableViewPrevio) {this.tableViewPrevio=tableViewPrevio;}

    //El booleano hace referencia si la persona es nueva o no
    public void setPersona(Persona persona, Boolean nuevaPersona)
    {
        if (!nuevaPersona)
            this.persona= persona;
        else
            this.persona = new Persona();
        this.nuevaPersona=nuevaPersona;
    }

    public void mostrarDatos() throws ParseException
    {
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());

        // Falta implementar el código para el resto de controles

        if (persona.getNumHijos() != null)
            textFieldNumHijos.setText(persona.getNumHijos().toString());

        if (persona.getSalario() != null)
            textFieldSalario.setText(persona.getSalario().toString());

        //Jubilado
        if (persona.getJubilado() != null && persona.getJubilado() == 1)
            checkBoxJubilado.setSelected(true);
        else
            checkBoxJubilado.setSelected(false);

        //Estado Civil
        if (persona.getEstadoCivil() != null)
        {
            switch(persona.getEstadoCivil())
            {
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }

        //Fecha Nacimiento
        if (persona.getFechaNacimiento() != null)
        {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fecNac = formato.parse(persona.getFechaNacimiento());
            LocalDate fechaNac =
                    fecNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            datePickerFechaNacimiento.setValue(fechaNac);
        }

        //Provincia
        comboBoxProvincia.setItems(dataUtil.getOlProvincias());

        if (persona.getProvincia() != null)
            comboBoxProvincia.setValue(persona.getProvincia());

        comboBoxProvincia.setCellFactory(
                (ListView<Provincia> l)-> new ListCell<Provincia>()
                {
                    @Override
                    protected void updateItem(Provincia provincia, boolean empty)
                    {
                        super.updateItem(provincia, empty);

                        if (provincia == null || empty)
                            setText("");
                        else
                            setText(provincia.getCodigo() + "-" + provincia.getNombre());
                    }
                });

        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){
            @Override
            public String toString(Provincia provincia)
            {
                if (provincia == null)
                    return null;
                else
                    return provincia.getCodigo()+"-"+provincia.getNombre();
            }
            @Override
            public Provincia fromString(String userId)
            {
                return null;
            }
        });

        //Foto
        if (persona.getFoto() != null){
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if (file.exists())
            {
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            }
            else
            {
                Alert alert=new Alert(Alert.AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString());
                alert.showAndWait();
            }
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonExaminar(ActionEvent event)
    {
        File carpetaFotos = new File(CARPETA_FOTOS);

        if (!carpetaFotos.exists())
            carpetaFotos.mkdir();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg",
                        "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(
                rootPersonaDetalleView.getScene().getWindow());

        if (file != null)
        {
            try
            {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+
                        "/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            }
            catch (FileAlreadyExistsException ex)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING,"Nombre de archivo duplicado");
                        alert.showAndWait();
            }
            catch (IOException ex)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING,"No se ha podido guardar la imagen");
                        alert.showAndWait();
            }
        }
    }

    @javafx.fxml.FXML
    private void onActionSuprimirFoto(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
                alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener,
                buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeEliminar)
        {
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);

            if (file.exists())
                file.delete();

            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
        else if (result.get() == buttonTypeMantener)
        {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonGuardar(ActionEvent event) throws ParseException
    {
        //Actualizar las propiedades del objeto Persona obteniendo la información que se encuentre en cada
        //uno de los controles de la ventana

        // Recoger datos de pantalla
        if (!errorFormato)
        {
            // Los datos introducidos son correctos
            try
            {
                persona.setNombre(textFieldNombre.getText());

                persona.setApellidos(textFieldApellidos.getText());

                // Aquí va el código para guardar el objeto en BD, enviar al servidor
                // y ocultar la vista actual

                //Teléfono
                persona.setTelefono(textFieldTelefono.getText());

                //Email
                persona.setEmail(textFieldEmail.getText());

                //Num Hijos
                if (!textFieldNumHijos.getText().isEmpty())
                {
                    try
                    {
                        persona.setNumHijos(Integer.valueOf(textFieldNumHijos.getText()));
                    }
                    catch(NumberFormatException ex)
                    {
                        errorFormato = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Número de hijos no válido");
                        alert.showAndWait();
                        textFieldNumHijos.requestFocus();
                    }
                }

                //Salario
                if (!textFieldSalario.getText().isEmpty())
                {
                    try
                    {
                        Double dSalario =
                                Double.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue()
                                );
                        persona.setSalario(dSalario);
                    }
                    catch(NumberFormatException ex)
                    {
                        errorFormato = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Salario no válido");
                        alert.showAndWait();
                        textFieldSalario.requestFocus();
                    }
                }

                //Checkbox
                if(checkBoxJubilado.isSelected())
                {
                    Integer jubilado = 1;
                    persona.setJubilado(jubilado);
                }

                //Radio buttons
                if (radioButtonCasado.isSelected())
                    persona.setEstadoCivil(CASADO);
                else if (radioButtonSoltero.isSelected())
                    persona.setEstadoCivil(SOLTERO);
                else if (radioButtonViudo.isSelected())
                    persona.setEstadoCivil(VIUDO);

                //Fecha Nacimiento
                if (datePickerFechaNacimiento.getValue() != null)
                {
                    LocalDate localDate = datePickerFechaNacimiento.getValue();
                    ZonedDateTime zonedDateTime =
                            localDate.atStartOfDay(ZoneId.systemDefault());
                    Instant instant = zonedDateTime.toInstant();
                    Date date = Date.from(instant);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaComoCadena = sdf.format(date);
                    persona.setFechaNacimiento(fechaComoCadena);
                }
                else
                {
                    persona.setFechaNacimiento(null);
                }

                //ComboBox Provincia
                if (comboBoxProvincia.getValue() != null)
                    persona.setProvincia(comboBoxProvincia.getValue());
                else
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,"Debe indicar una provincia");
                    alert.showAndWait();
                    errorFormato = true;
                }

            }
            catch (Exception ex)
            {
                //Los datos introducidos no cumplen requisitos
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. "
                        + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
        else
        {
            //Los datos introducidos no cumplen requisitos
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No se han podido guardar los cambios. "
                    + "Compruebe que los datos cumplen los requisitos");
            alert.setContentText("Los campos Nombre y Apellidos son obligatorios");
            alert.showAndWait();
        }




        if (nuevaPersona)
            dataUtil.addPersona(persona);
        else
            dataUtil.actualizarPersona(persona);

        //Actualizar los nuevos datos en el TableView y seleccionar la
        //última fila del TableView, ya que ahí se debe encontrar el nuevo objeto que se añade
        int numFilaSeleccionada;
        if (nuevaPersona)
        {
            tableViewPrevio.getItems().add(persona);
            numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
            tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
            tableViewPrevio.scrollTo(numFilaSeleccionada);
        }
        else
        {
            numFilaSeleccionada=
                    tableViewPrevio.getSelectionModel().getSelectedIndex();
            tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
        }

        TablePosition pos = new TablePosition(tableViewPrevio,
                numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();

        //Volver a hacer visible el contenedor de la lista de personas, y previamente se
        //eliminará del StackPane principal la vista del detalle.
        StackPane rootMain =
                (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }

    @javafx.fxml.FXML
    private void onActionButtonCancelar(ActionEvent event)
    {
        //Devolver al foco al TableView, concretamente a la misma fila que estuviera
        //seleccionada previamente
        int numFilaSeleccionada =
                tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio,
                numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();

        //Volver a hacer visible el contenedor de la lista de personas, y previamente se
        //eliminará del StackPane principal la vista del detalle.
        StackPane rootMain =
                (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }
}
