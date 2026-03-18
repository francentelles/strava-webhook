package org.example;

import static spark.Spark.*;
import org.json.JSONObject;

public class StravaWebhookServer {

    static String[] titulos = {
            "\uD835\uDE0F\uD835\uDE22 \uD835\uDE29\uD835\uDE22\uD835\uDE23\uD835\uDE2A\uD835\uDE25\uD835\uDE30 \uD835\uDE36\uD835\uDE2F \uD835\uDE28\uD835\uDE2A\uD835\uDE33\uD835\uDE30 \uD835\uDE25\uD835\uDE33\uD835\uDE22\uD835\uDE2Eá\uD835\uDE35\uD835\uDE2A\uD835\uDE24\uD835\uDE30 \uD835\uDE25\uD835\uDE26 \uD835\uDE2D\uD835\uDE30\uD835\uDE34 \uD835\uDE22\uD835\uDE24\uD835\uDE30\uD835\uDE2F\uD835\uDE35\uD835\uDE26\uD835\uDE24\uD835\uDE2A\uD835\uDE2E\uD835\uDE2A\uD835\uDE26\uD835\uDE2F\uD835\uDE35\uD835\uDE30\uD835\uDE34",
            "ha habido un giro dramático"
    };
    static String tituloRandom() {
        int i = (int)(Math.random() * titulos.length);
        return titulos[i];
    }
    static void cambiarTitulo(long activityId) {

        try {
            String accessToken = "fa7ef2b85387c00d5d36ac75c78fdbcc440a490c";
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
        port(Integer.parseInt(System.getenv().getOrDefault("PORT", "4567")));
        get("/webhook", (req, res) -> {
            String challenge = req.queryParams("hub.challenge");
            if (challenge != null) {
                res.type("application/json");
                return "{\"hub.challenge\":\"" + challenge + "\"}";
            }
            return "OK";
        });

        post("/webhook", (req, res) -> {
            System.out.println("Evento recibido:");
            System.out.println(req.body());

            return "OK";
        });
    }
}