package mk.ukim.finki;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.jena.query.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {
    public static void main(String[] args) {
        List<University> un = executeQuery();
        mongo(un);
    }

    public static List<University> executeQuery() {

        String sparqlQuery = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "\n"
                + "SELECT ?place ?name (sample(?longitude) as ?long) (sample(?latitude) as ?lat)\n"
                + "       (sample(?image) as ?img)\n"
                + "WHERE {\n"
                + "  ?place geo:long ?longitude ;\n"
                + "         geo:lat ?latitude ;\n"
                + "         foaf:depiction ?image ;\n"
                + "         dbp:name ?name ;\n"
                + "         rdf:type dbo:University .\n"
                + "\n"
                + "  FILTER(isLiteral(?name))\n"
                + "}\n"
                + "GROUP BY ?place ?name";


        List<University> universities = new ArrayList<>();

        Query query = QueryFactory.create(sparqlQuery);

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String name = solution.get("name").toString();
                String longitude = solution.get("long").toString();
                String latitude = solution.get("lat").toString();
                String image = solution.get("img").toString();
                String place = solution.get("place").toString();

                University university = new University();
                university.setName(name);
                university.setImage(image);
                university.setPlace(place);
                university.setLat(formatLocation(latitude));
                university.setLon(formatLocation(longitude));

                universities.add(university);

                //System.out.println("Place: " + place);
                System.out.println("Longitude: " + longitude);
                System.out.println("Latitude: " + latitude);
                //System.out.println("Image: " + image);
                //System.out.println("-----------------------");
            }
        }

        return universities;
    }

    private static double formatLocation(String location) {
        try {
            return Double.parseDouble(location.split("\\^")[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }
    public static void mongo(List<University> universities) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:pass123@banks.qosr0.mongodb.net/banks?retryWrites=true&w=majority");

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {

            MongoDatabase mongoDatabase = mongoClient.getDatabase("banks");
            mongoDatabase.createCollection("universities");

            MongoCollection<University> mongoCollection = mongoDatabase.getCollection("universities", University.class);

            mongoCollection.insertMany(universities);
        }
    }
}