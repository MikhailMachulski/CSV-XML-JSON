import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVParserBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import  com.google.gson.*;
import org.w3c.dom.*;

import javax.management.Attribute;
import javax.sql.rowset.spi.XmlWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String[] employeeOne = "1,John,Smith,USA,25".split(",");
        String[] employeeTwo = "2,Ivan,Petrov,RU,23".split(",");
        String fileName = "data.csv";
        String xmlFileName = "data.xml";

        /*csvWriter(columnMapping,employeeOne, employeeTwo, fileName);
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);*/

        xmlWriter(columnMapping, employeeOne, employeeTwo);
        List<Employee> xmlList = parseXML(xmlFileName);
        String xmlJSON = listToJson(xmlList);
        writeString(xmlJSON);

    }

    public static List<Employee> parseXML(String xmlFileName) {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFileName);

            NodeList employeeElements = document.getDocumentElement().getElementsByTagName("employee");
            for (int i = 0; i < employeeElements.getLength(); i++) {
                Node employee = employeeElements.item(i);
                NamedNodeMap attributes = employee.getAttributes();
                list.add(new Employee(Long.parseLong(attributes.getNamedItem("id").getNodeValue()), attributes.getNamedItem("firstName").getNodeValue(),
                attributes.getNamedItem("lastName").getNodeValue(), attributes.getNamedItem("country").getNodeValue(),
                        Integer.parseInt(attributes.getNamedItem("age").getNodeValue())));
                }
    } catch (Exception exception) {
        exception.printStackTrace();
        }
        return list;
    }


    private static void xmlWriter(String[] columnMapping, String[] employeeOne, String[] employeeTwo){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("root");
            document.appendChild(root);
            Element staff = document.createElement("staff");
            root.appendChild(staff);

            Element employeeUno = document.createElement("employee");
            for (int i = 0; i < columnMapping.length; i++) {
                employeeUno.setAttribute(columnMapping[i], employeeOne[i]);
            }
            staff.appendChild(employeeUno);

            Element employeeDos = document.createElement("employee");
            for (int i = 0; i < columnMapping.length; i++) {
                employeeDos.setAttribute(columnMapping[i], employeeTwo[i]);
            }
            staff.appendChild(employeeDos);

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("data.xml"));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, streamResult);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void csvWriter(String[] columnMapping, String[] employeeOne, String[] employeeTwo, String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(employeeOne);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filename, true))) {
            writer.writeNext(employeeTwo);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csvToBean.parse();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json) {
        try(FileWriter fileWriter = new FileWriter("data.json")) {
            fileWriter.write(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
