import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Iterator;


public class CassandraTest {

    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        Session session = cluster.connect("mykeyspace");
        ResultSet results = session.execute("SELECT * FROM users");
        StringBuilder line = new StringBuilder();

        for (Iterator<Row> iterator = results.iterator(); iterator.hasNext();) {
            Row row = iterator.next();
            line.delete(0, line.length());
            line.append("FirstName = ").                   
                    append(row.getString("fname")).
                    append(",").append(" ").
                    append("LastName = ").
                    append(row.getString("lname"));
            System.out.println(line.toString());
        }
    }
}
