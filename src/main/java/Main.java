import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> listEmployee = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> colMapStrategy = new ColumnPositionMappingStrategy<>();
            colMapStrategy.setType(Employee.class);
            colMapStrategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(colMapStrategy).build();
            listEmployee = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listEmployee;
    }


    public static List<Employee> parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> listEmployee = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filename));
        NodeList nodeList = document.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                if (element.getElementsByTagName("id").getLength() > 0) {
                    listEmployee.add(new Employee(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    ));
                }
            }
        }
        return listEmployee;
    }

    public static String listToJson(List<Employee> listEmployee) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(listEmployee, listType);
    }

    public static List<Employee>  jsonToList(String fileName)  {
        List<Employee> employeeList = new ArrayList<>();
        try {
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(new BufferedReader(new FileReader(fileName)));
            JSONArray arr = (JSONArray) obj;
            for (Object o : arr) {
                JSONObject itemJson = (JSONObject) o;
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                employeeList.add(gson.fromJson(itemJson.toJSONString(), Employee.class));
            }
        } catch ( ParseException | IOException e) {
            e.printStackTrace();
        }
        return  employeeList;
    }

    public static void writeToFile(String jsonString, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void task1() {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, "data.csv");
        String json = listToJson(list);
        writeToFile(json, "data.json");
    }

    public static void task2() {
        try {
            List<Employee> list = parseXML("data.xml");
            String json = listToJson(list);
            writeToFile(json, "data2.json");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static void task3() {
        List<Employee> list = jsonToList("data.json");
        list.forEach(System.out::println);
    }

    public static void main(String[] args) {
        task1();
        task2();
        task3();
    }
}