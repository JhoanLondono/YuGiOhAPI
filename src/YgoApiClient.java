
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YgoApiClient
{
    private final HttpClient client;


    public YgoApiClient()
    {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }


    public Card obtenerCartaAleatoria()
            throws IOException, InterruptedException
    {
        int intentos = 0;

        while (intentos < 10)
        {
            intentos++;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "https://db.ygoprodeck.com/api/v7/randomcard.php"
                            ))
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200)
            {
                throw new IOException(
                        "No se pudo cargar la carta. Código: "
                                + response.statusCode()
                );
            }

            JSONObject json =
                    new JSONObject(response.body());

            JSONArray cartas;

            if (json.has("data"))
            {
                cartas = json.getJSONArray("data");
            }
            else
            {
                cartas = new JSONArray();
                cartas.put(json);
            }

            if (cartas.isEmpty())
            {
                continue;
            }

            JSONObject datosCarta =
                    cartas.getJSONObject(0);

            String tipo =
                    datosCarta.optString("type", "");

            // Solo aceptamos cartas de tipo Monster.
            if (!tipo.contains("Monster"))
            {
                continue;
            }

            String nombre =
                    datosCarta.getString("name");

            int atk =
                    datosCarta.optInt("atk", 0);

            int def =
                    datosCarta.optInt("def", 0);

            JSONArray imagenes =
                    datosCarta.getJSONArray("card_images");

            String imagen =
                    imagenes.getJSONObject(0)
                            .getString("image_url");

            return new Card(
                    nombre,
                    atk,
                    def,
                    imagen
            );
        }

        throw new IOException(
                "No se pudo cargar una carta Monster después de varios intentos."
        );
    }
}
