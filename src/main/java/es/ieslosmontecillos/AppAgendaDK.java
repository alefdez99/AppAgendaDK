package es.ieslosmontecillos;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;

/**
 * @author profesora
 */

public class AppAgendaDK extends Application {

    private DataUtil dataUtil;
    private InicioView inicioView = new InicioView();
    private InicioController inicioController;

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        StackPane rootMain = new StackPane();
        View inicio = inicioView.getView();
        rootMain.getChildren().add(inicio);
        inicioController = inicioView.getInicioController();
        inicioController.setRootMain(rootMain);

        // Solicitamos los datos a los servicios de dataUtil
        dataUtil = new DataUtil();
        dataUtil.obtenerTodasProvincias();
        ObservableList<Provincia> olProv = dataUtil.getOlProvincias();
        dataUtil.obtenerTodasPersonas();
        ObservableList<Persona> olPers = dataUtil.getOlPersonas();

        // Pasamos los datos obtenidos a la clase controladora de inicio
        inicioController.setDataUtil(dataUtil);
        inicioController.setOlProv(olProv);
        inicioController.setOlPers(olPers);
        inicioController.setRootMain(rootMain);
        Scene scene = new Scene(rootMain,650,400);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop() {
        System.out.println("Stop: Se cerró la app");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

