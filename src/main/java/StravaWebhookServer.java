import static spark.Spark.*;
import org.json.JSONObject;

public class StravaWebhookServer {

    static String[] titulos = {
            "\uD835\uDE0F\uD835\uDE22 \uD835\uDE29\uD835\uDE22\uD835\uDE23\uD835\uDE2A\uD835\uDE25\uD835\uDE30 \uD835\uDE36\uD835\uDE2F \uD835\uDE28\uD835\uDE2A\uD835\uDE33\uD835\uDE30 \uD835\uDE25\uD835\uDE33\uD835\uDE22\uD835\uDE2Eá\uD835\uDE35\uD835\uDE2A\uD835\uDE24\uD835\uDE30 \uD835\uDE25\uD835\uDE26 \uD835\uDE2D\uD835\uDE30\uD835\uDE34 \uD835\uDE22\uD835\uDE24\uD835\uDE30\uD835\uDE2F\uD835\uDE35\uD835\uDE26\uD835\uDE24\uD835\uDE2A\uD835\uDE2E\uD835\uDE2A\uD835\uDE26\uD835\uDE2F\uD835\uDE35\uD835\uDE30\uD835\uDE34",
            "ha habido un giro dramático de los acontecimientos"
    };
    static String tituloRandom() {
        int i = (int)(Math.random() * titulos.length);
        return titulos[i];
    }
    static void cambiarTitulo(long activityId) {

        try {

            String accessToken = "816d0b3c83cb1b8015d89812adba9711c610609a";

            String nuevoTitulo = tituloRandom();

            String url = "https://www.strava.com/api/v3/activities/" + activityId;

            ProcessBuilder pb = new ProcessBuilder(
                    "curl",
                    "-X", "PUT",
                    url,
                    "-H", "Authorization: Bearer " + accessToken,
                    "-d", "name=" + nuevoTitulo
            );

            Process process = pb.start();

            java.io.InputStream is = process.getInputStream();
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String response = s.hasNext() ? s.next() : "";

            System.out.println("Respuesta Strava:");
            System.out.println(response);

            System.out.println("Título cambiado a: " + nuevoTitulo);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        port(4567);

        get("/webhook", (req, res) -> {
            String challenge = req.queryParams("hub.challenge");

            if (challenge != null) {
                JSONObject json = new JSONObject();
                json.put("hub.challenge", challenge);
                res.type("application/json");
                return json.toString();
            }
            return "Webhook activoo";
        });

        post("/webhook", (req, res) -> {

            JSONObject json = new JSONObject(req.body());

            if (json.getString("aspect_type").equals("create")) {

                long activityId = json.getLong("object_id");

                System.out.println("Nueva actividad detectada: " + activityId);

                new Thread(() -> {

                    try {
                        Thread.sleep(30000); // esperar 30 segundos
                        cambiarTitulo(activityId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();
            }

            return "OK";
        });

    }
}