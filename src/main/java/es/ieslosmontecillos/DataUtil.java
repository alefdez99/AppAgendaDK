package es.ieslosmontecillos;

import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.JsonConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.json.JsonObject;

public class DataUtil
{
    private ObservableList<Provincia> olProvincias =
            FXCollections.observableArrayList();
    private ObservableList<Persona> olPersonas =
            FXCollections.observableArrayList();
    public void obtenerTodasProvincias()
    {
        System.out.println("Se están solicitando las provincias...");
        RestClient restClient = RestClient.create()
                .method("GET")
                .host("http://localhost:8080")
                .path("/api/v1/PROVINCIA");
        GluonObservableList<Provincia> provincias =
                DataProvider.retrieveList(restClient.createListDataReader(Provincia.class));
        provincias.addListener(new ListChangeListener<Provincia>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<?
                    extends Provincia> c) {
                if(c.next()){
                    olProvincias.add(c.getList().get(c.getFrom()));
                }
                System.out.println("Lista provincias: " +
                        olProvincias.get(c.getFrom()).getCodigo() + "-" +
                        olProvincias.get(c.getFrom()).getNombre());
            }
        });
    }
    public ObservableList<Provincia> getOlProvincias() {
        return olProvincias;
    }
    public void obtenerTodasPersonas(){

        RestClient restClient = RestClient.create()
                .method("GET")
                .host("http://localhost:8080")
                .path("/api/v1/PERSONA");
        GluonObservableList<Persona> personas =
                DataProvider.retrieveList(restClient.createListDataReader(Persona.class));
        personas.addListener(new ListChangeListener<Persona>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<?
                    extends Persona> c) {
                if(c.next()){
                    olPersonas.add(c.getList().get(c.getFrom()));
                }
                System.out.println("Lista personas: " +
                        olPersonas.get(c.getFrom()).getNombre() + "-" +
                        olPersonas.get(c.getFrom()).getApellidos());
            }
        });
    }
    public ObservableList<Persona> getOlPersonas() {
        return olPersonas;
    }
    public void eliminarPersona(Persona persona){
        int idPersona = persona.getId().intValue();

        RestClient restClient = RestClient.create()
                .method("DELETE")
                .host("http://localhost:8080")
                .path("/api/v1/PERSONA/"+idPersona);
        GluonObservableList<Persona> personas =
                DataProvider.retrieveList(restClient.createListDataReader(Persona.class));
    }
    public void addPersona(Persona persona)
    {
        int idPersona = persona.getId().intValue();

        JsonConverter<Persona> converter = new JsonConverter<>(Persona.class);
        JsonObject json = converter.writeToJson(persona);
        String dataBody = json.toString();

        RestClient restClient = RestClient.create()
                .method("POST")
                .host("http://localhost:8080")
                .path("/api/v1/PERSONA")
                .dataString(dataBody)
                .contentType("application/json");
        GluonObservableObject<Persona> personaNueva =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));

        System.out.println(personaNueva);
    }

    public void actualizarPersona(Persona persona){
        int idPersona = persona.getId().intValue();

        JsonConverter<Persona> converter = new JsonConverter<>(Persona.class);
        JsonObject json = converter.writeToJson(persona);
        String dataBody = json.toString();

        RestClient restClient = RestClient.create()
                .method("PUT")
                .host("http://localhost:8080")
                .path("/api/v1/PERSONA/"+idPersona)
                .dataString(dataBody)
                .contentType("application/json");
        GluonObservableObject<Persona> personaActualizada =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));
    }
    public Persona findPersonaByID(Integer id){
        int idPersona = id.intValue();
        RestClient restClient = RestClient.create()
                .method("GET")
                .host("http://localhost:8080")
                .path("/api/v1/PERSONA/"+idPersona);
        GluonObservableObject<Persona> persona =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));
        persona.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv && persona.get() != null) {
                System.out.println("Recuperando persona seleccionada de la BD "+persona.get().getNombre()+" "+persona.get().getApellidos());
            }
        });
        return persona.get();
    }
    public Provincia findProvinciaByID(Integer id){
        int idProvincia = id.intValue();
        RestClient restClient = RestClient.create()
                .method("GET")
                .host("http://localhost:8080")
                .path("/api/v1/PROVINCIA/"+idProvincia);
        GluonObservableObject<Provincia> provincia =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Provincia.class)
                );
        provincia.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv && provincia.get() != null) {
                System.out.println("Recuperando provincia seleccionada de la BD "+provincia.get().getCodigo()+" - "+provincia.get().getNombre());
            }
        });
        return provincia.get();
    }
}

