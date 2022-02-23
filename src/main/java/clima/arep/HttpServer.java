package clima.arep;


import java.net.*;
import java.io.*;
import com.google.gson.Gson;

public class HttpServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean primeraLinea = true;
            String req = "";
            String ciudad = "";
            outputLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (primeraLinea){
                    String[] peticion = inputLine.split(" ");
                    req = peticion[1];
                    System.out.println("REQ: " + req);
                    primeraLinea = false;
                }
                if (!in.ready()) {break; }
            }
            if(req.startsWith("/consulta")){
                ciudad = req.split("=")[1];
                System.out.println(ciudad);
                String apiconection = "https://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&appid=ee1482f403f8b501850103d18417d06f";
                URL obj = new URL(apiconection);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.getResponseCode();
                out.print("HTTP/1.1 200 OK\r\n Content-Type: text/json \r\n\r\n");
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line+"\n");
                }
                Gson gson = new Gson();
                outputLine = gson.toJson(sb.toString());
            }
            else if(req.startsWith("/clima")){
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text /html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>WeatherApp</title>\n"
                        + "<style>"
                        + "body {\n" +
                        "            background: rgb(9,58,121);\n" +
                        "            background: linear-gradient(90deg, rgba(9,58,121,1) 0%, rgba(145,0,255,1) 100%);\n" +
                        "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                        "            color: white;\n"  +
                        "        }"
                        + ".titulo{\n" +
                        "            font-size: 40px;\n" +
                        "        }"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "CLIMA"
                        + "</body>"
                        + "</html>";
            }
            else {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text /html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>WeatherApp</title>\n"
                        + "<style>"
                        + "body {\n" +
                        "            background: rgb(9,58,121);\n" +
                        "            background: linear-gradient(90deg, rgba(9,58,121,1) 0%, rgba(145,0,255,1) 100%);\n" +
                        "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                        "            color: white;\n"  +
                        "        }"
                        + ".titulo{\n" +
                        "            font-size: 40px;\n" +
                        "        }"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<h1 class=\"titulo\">Consultar el clima de una ciudad</h1>"
                        + "<form action=\"/consulta\" methord=\"get\">"
                        + "<label style=\"color=white\" for=\"lugar\" class=\"text2\">Ingrese el nombre de la ciudad: </label><br>\n" +
                        "                <input type=\"text\" id=\"lugar\" name=\"lugar\" value=\"\"><br><br>\n" +
                        "                <input type=\"submit\" value=\"Submit\">"
                        + "</body>"
                        + "</html>";
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000;
        //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}