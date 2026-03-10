import java.sql.*;
Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travelapp", "postgres", "postgres");
PreparedStatement ps = conn.prepareStatement("select id, name, image_url from destinations order by id");
ResultSet rs = ps.executeQuery();
while (rs.next()) {
    System.out.println(rs.getLong(1) + "|" + rs.getString(2) + "|" + rs.getString(3));
}
rs.close();
ps.close();
conn.close();
/exit
