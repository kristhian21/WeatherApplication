package clima.arep;

import java.net.*;
import java.io.*;

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

        while (true) {
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

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
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
                    + "</html>" + inputLine;
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
        //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}