import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.regex.*;

String accessKey = "8lzA8R-rI4fIbZ9hegBRNBxhA72WuFsUHy-EnfLe-0k";
Pattern regularPattern = Pattern.compile("\\\"regular\\\":\\\"([^\\\"]+)\\\"");
HttpClient client = HttpClient.newHttpClient();
Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travelapp", "postgres", "postgres");
PreparedStatement select = conn.prepareStatement("select id, name, country from destinations where image_url is null or btrim(image_url) = '' or lower(btrim(image_url)) = 'string' order by id");
PreparedStatement update = conn.prepareStatement("update destinations set image_url = ? where id = ?");
ResultSet rs = select.executeQuery();
while (rs.next()) {
    long id = rs.getLong("id");
    String name = rs.getString("name");
    String country = rs.getString("country");
    String query = country == null || country.isBlank() ? name : name + ", " + country;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
    URI uri = URI.create("https://api.unsplash.com/search/photos?orientation=landscape&per_page=1&query=" + encodedQuery);
    HttpRequest request = HttpRequest.newBuilder(uri)
        .header("Accept-Version", "v1")
        .header("Authorization", "Client-ID " + accessKey)
        .GET()
        .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    Matcher matcher = regularPattern.matcher(response.body());
    if (matcher.find()) {
        String imageUrl = matcher.group(1).replace("\\/", "/");
        update.setString(1, imageUrl);
        update.setLong(2, id);
        update.executeUpdate();
        System.out.println("UPDATED|" + id + "|" + name + "|" + imageUrl);
    } else {
        System.out.println("NO_IMAGE|" + id + "|" + name + "|response=" + response.statusCode());
    }
}
rs.close();
select.close();
update.close();
conn.close();
/exit
