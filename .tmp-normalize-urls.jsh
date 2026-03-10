import java.sql.*;
Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travelapp", "postgres", "postgres");
PreparedStatement select = conn.prepareStatement("select id, image_url from destinations where image_url like '%\\u0026%'");
PreparedStatement update = conn.prepareStatement("update destinations set image_url = ? where id = ?");
ResultSet rs = select.executeQuery();
while (rs.next()) {
    long id = rs.getLong(1);
    String url = rs.getString(2).replace("\\u0026", "&");
    update.setString(1, url);
    update.setLong(2, id);
    update.executeUpdate();
    System.out.println("NORMALIZED|" + id + "|" + url);
}
rs.close();
select.close();
update.close();
conn.close();
/exit
