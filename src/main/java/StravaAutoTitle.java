import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class StravaAutoTitle {

    private static final String ACCESS_TOKEN = "804d5344acd00843840fad90d48d35824900fbc5";

    public static void main(String[] args) throws Exception {

        int activityId = obtenerUltimaActividad();

        String titulo = generarTituloRandom();

        actualizarTitulo(activityId, titulo);

        System.out.println("Actividad actualizada con título: " + titulo);
    }

    private static int obtenerUltimaActividad() throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.strava.com/api/v3/athlete/activities?per_page=1"))
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONArray activities = new JSONArray(response.body());

        JSONObject activity = activities.getJSONObject(0);

        return activity.getInt("id");
    }

    private static String generarTituloRandom() {

        String[] frases = {
                "Rodando fuerte 🚴",
                "Día de sufrir 😅",
                "Acumulando kilómetros",
                "Entreno serio 🔥",
                "Explorando rutas nuevas",
                "Piernas on fire 🔥",
                "Otra más al saco",
                "Disfrutando la bici"
        };

        Random random = new Random();

        return frases[random.nextInt(frases.length)];
    }

    private static void actualizarTitulo(int activityId, String titulo) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        String json = "{\"name\":\"" + titulo + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.strava.com/api/v3/activities/" + activityId))
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}