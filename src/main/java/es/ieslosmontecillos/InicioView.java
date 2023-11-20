package es.ieslosmontecillos;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class InicioView {
    private InicioController inicioController;

    public View getView() {
        try {
            FXMLLoader loader = new
                    FXMLLoader(InicioView.class.getResource("fxml/inicio.fxml"));
            View view = loader.load();
            inicioController = loader.getController();
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
    public InicioController getInicioController() {
        return inicioController;
    }
}