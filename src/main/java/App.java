import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class App {

    private static ArrayList<Car> carList = new ArrayList<>();

    public static void main(String[] args) {
        externalStaticFileLocation("C:\\appfolder\\src\\main\\resources\\public");
        staticFiles.location("/public");
        get("/add", (req, res) -> AddFunction(req,res));
        get("/deleteAll", (req, res) -> deleteAllFunction(req,res));
        get("/deleteId/:id", (req, res) -> deleteIdFunction(req,res));
        get("/deleteSelected", (req, res) -> deleteSelectedFunction(req,res));
        get("/update/:id", (req, res) -> UpdateIdFunction(req,res));

        get("/text", (req, res) -> TextFunction(req,res));
        get("/json", (req, res) -> JsonFunction(req,res));
        get("/html", (req, res) -> HtmlFunction(req,res));
    }
    static String AddFunction(Request req, Response res) {
        Car car = new Car(req.queryParams("name"),req.queryParams("doors"),req.queryParams("country"));
        if(req.queryParams().contains("uszkodzony")){
          car.setUszkodzony(true);
        }
        carList.add(car);
        return "dodano auto; list = " + carList.size();
    }
    static String deleteAllFunction(Request req, Response res) {
        carList.clear();
        return "usunieto samochody; list = " + carList.size();
    }
    static String deleteIdFunction(Request req, Response res) {
        carList.remove(Integer.parseInt(req.params("id")));
        return "usunieto samochod; list = " + carList.size();
    }
    static String UpdateIdFunction(Request req, Response res) {
        Car car = carList.remove(Integer.parseInt(req.params("id")));
        car.setUszkodzony(!car.isUszkodzony());
        carList.add(car);
        return "zmieniono stan; list = " + carList.size();
    }

    static String TextFunction(Request req, Response res) {
        Type listType = new TypeToken<ArrayList<Car>>() {}.getType();
        Gson gson = new Gson();
        return gson.toJson(carList, listType );
    }
    static String JsonFunction(Request req, Response res) {
        res.type("application/json");
        return carList.toString();
    }
    static String HtmlFunction(Request req, Response res) {
        StringBuilder html = new StringBuilder();
        html.append("<form action='deleteSelected' method='get'>");
        html.append("<table border=1>");

        for(Car c: carList){
            html.append("<tr>");
            html.append("<td>"+carList.indexOf(c)+"</td>");
            html.append("<td>"+c.getName()+"</td>");
            html.append("<td>"+c.isUszkodzony()+"</td>");
            html.append("<td>"+c.getDoors()+"</td>");
            html.append("<td>"+c.getCountry()+"</td>");
            html.append("<td><input type='checkbox' name='car"+carList.indexOf(c)+"'/></td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<input type='submit' value='usun'/>");
        html.append("</form>");
        return html.toString();
    }

    static String deleteSelectedFunction(Request req, Response res) {
        System.out.println(req.queryParams());

        return "a";
    }
}
class Car{
    private String name;
    private String doors;
    private boolean uszkodzony = false;
    private String country;

    public Car(String name, String doors, String country) {
        this.name = name;
        this.doors = doors;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getDoors() {
        return doors;
    }

    public boolean isUszkodzony() {
        return uszkodzony;
    }

    public String getCountry() {
        return country;
    }

    public void setUszkodzony(boolean uszkodzony) {
        this.uszkodzony = uszkodzony;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", doors='" + doors + '\'' +
                ", uszkodzony=" + uszkodzony +
                ", country='" + country + '\'' +
                '}';
    }
}
