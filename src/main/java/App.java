import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import spark.Request;
import spark.Response;


import java.lang.reflect.Type;

class SortbyId implements Comparator<Car> {
    @Override
    public int compare(Car o1, Car o2) {
        return o1.getId() - o2.getId();
    }
}

class Car{
    private int id;
    private String name;
    private String doors;
    private boolean uszkodzony = false;
    private String country;

    public Car(int id, String name, String doors, String country) {
        this.id = id;
        this.name = name;
        this.doors = doors;
        this.country = country;
    }

    public int getId() {
        return id;
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

public class App {

    private static ArrayList<Car> carList = new ArrayList<>();
    private static int carId = 0;

    public static void main(String[] args) {
        externalStaticFileLocation("C:\\appfolder\\src\\main\\resources\\public");
        staticFiles.location("/public");
        get("/add", (req, res) -> AddFunction(req,res));
        get("/deleteAll", (req, res) -> deleteAllFunction(req,res));
        get("/deleteId/:id", (req, res) -> deleteIdFunction(req,res));
        get("/deleteSelected", (req, res) -> deleteSelectedFunction(req,res));
        get("/update/:id", (req, res) -> UpdateIdFunction(req,res));
        get("/editSelected/:id", (req, res) -> UpdateSelectedFunction(req,res));
        get("/acceptEdit", (req, res) -> AcceptEditFunction(req,res));

        get("/text", (req, res) -> TextFunction(req,res));
        get("/json", (req, res) -> JsonFunction(req,res));
        get("/html", (req, res) -> HtmlFunction(req,res));
    }
    static String AddFunction(Request req, Response res) {
        Car car = new Car(carId,req.queryParams("name"),req.queryParams("doors"),req.queryParams("country"));
        if(req.queryParams().contains("uszkodzony")){
          car.setUszkodzony(true);
        }
        carId += 1;
        carList.add(car);
        return "dodano auto; list = " + carList.size();
    }
    static String deleteAllFunction(Request req, Response res) {
        carList.clear();
        return "usunieto samochody; list = " + carList.size();
    }
    static String deleteIdFunction(Request req, Response res) {
        for(Car car : carList){
            if(car.getId() == Integer.parseInt(req.params("id"))){
                carList.remove(car);
            }
        }
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
        carList.sort(new SortbyId());
        for(Car c: carList){
            html.append("<tr>");
            html.append("<td>"+c.getId()+"</td>");
            html.append("<td>"+c.getName()+"</td>");
            html.append("<td>"+c.isUszkodzony()+"</td>");
            html.append("<td>"+c.getDoors()+"</td>");
            html.append("<td>"+c.getCountry()+"</td>");
            html.append("<td><input type='checkbox' name='car"+c.getId()+"'/></td>");
            html.append("<td><a href='/editSelected/car"+c.getId()+"'>edit</a></td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<input type='submit' value='usun'/>");
        html.append("</form>");
        return html.toString();
    }

    static String deleteSelectedFunction(Request req, Response res) {
        ArrayList<Integer> idList = new ArrayList<>();
        for(String s : req.queryParams()){
            idList.add(Integer.parseInt(s.substring(3)));
        }
        ArrayList<Car> helper = carList;
        for(int ind : idList){
            for(Car car : helper){
                if(car.getId() == ind){
                    carList.remove(car);
                    break;
                }
            }
            helper = carList;
        }
        return "usunięto elementy o id = " + idList.toString();
    }
    static String UpdateSelectedFunction(Request req, Response res) {
        int id = Integer.parseInt(req.params("id").substring(3));
        StringBuilder html = new StringBuilder();
        html.append("<form action='/acceptEdit' method='get'>");
        html.append("<table border=1>");
        carList.sort(new SortbyId());
        for(Car c: carList){
            html.append("<tr>");
            html.append("<td>"+c.getId()+"</td>");
            if(c.getId() == id){
                html.append("<td><input type='text' value='"+c.getName()+"' name='name'/></td>");
                html.append("<td><input type='checkbox' name='uszkodzony' checked="+c.isUszkodzony()+"/></td>");
                html.append("<td><select name=\"doors\">" +
                    "      <option value=\"1\">1</option>" +
                    "      <option value=\"2\">2</option>" +
                    "      <option value=\"3\">3</option>" +
                    "      <option value=\"4\">4</option>" +
                    "    </select></td>");
                if(c.getCountry() == "polski"){
                    html.append("<td><input type='radio' value='polski' name='country' checked=true/>Polska");
                    html.append("<input type='radio' value='angielski' name='country'/>Angielska</td>");
                }else{
                    html.append("<td><input type='radio' value='polski' name='country' />Polska");
                    html.append("<input type='radio' value='angielski' name='country' checked=true/>Anigielska</td>");
                }

                html.append("<input type='text' name='id' value='car"+c.getId()+"' style='display:none'/>");
                html.append("<td><input type='submit' value='accept'> | <a href='/html'>cancel</a></td>");
                html.append("</tr>");
            }else{
                html.append("<td>"+c.getName()+"</td>");
                html.append("<td>"+c.isUszkodzony()+"</td>");
                html.append("<td>"+c.getDoors()+"</td>");
                html.append("<td>"+c.getCountry()+"</td>");
                html.append("<td><a href='car"+carList.indexOf(c)+"'>edit</a></td>");
                html.append("</tr>");
            }
        }
        html.append("</table>");
        html.append("</form>");
        return html.toString();
    }
    static String AcceptEditFunction(Request req, Response res) {
        System.out.println(carList.toString());
        System.out.println(req.queryParams());
        carList.remove(Integer.parseInt(req.queryParams("id").substring(3)));
        System.out.println(carList.toString());

        Car car = new Car(Integer.parseInt(req.queryParams("id").substring(3)),req.queryParams("name"),req.queryParams("doors"),req.queryParams("country"));
        if(req.queryParams().contains("uszkodzony")){
            car.setUszkodzony(true);
        }
        carList.add(car);
        System.out.println(carList.toString());
        return "edytowano auto; id = " + req.queryParams("id");
    }
}

