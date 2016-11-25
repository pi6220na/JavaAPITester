//package java.wolfe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.exit;

/*
 * Created by Jeremy on 11/17/2016.
 */

public class Main {

    static Scanner stringScanner = new Scanner(System.in);
    static Scanner numberScanner = new Scanner(System.in);

    static int totalMethods = 0;

    public static void main(String[] args) throws Exception { //TODO handle exceptions properly

        FileSearch fileSearch = new FileSearch();

        searchForFiles(fileSearch); // copied entirely from:
        // https://www.mkyong.com/java/search-directories-recursively-for-file-in-java/

        int items = 0;
        for (String dir : fileSearch.getResult()){
            items++;
            System.out.println(items + "  processing dir = " + dir);

            loadPackageTable(dir);

            loadKlassTable(dir);

            System.out.println();
            System.out.println("******* end of job ********");
            System.out.println("totalMethods count = " + totalMethods);
        }

    }




    private static void loadPackageTable(String dir) throws Exception {


        //File input = new File("C:/Users/myrlin/Desktop/Java/JavaDocs/docs/api/java/util/package-summary.html");
        File input = new File(dir);
        Document doc = Jsoup.parse(input, "UTF-8");


        String foreignKey = null;

        try {
            Elements items = doc.select("div[class=header]");
            Iterator<Element> iterator = items.select("h1").iterator();
            String nameField = null;
            nameField = iterator.next().text();
            nameField = nameField.replace("Package", "");
            System.out.println("name = " + nameField);

            items = doc.select("div[class=docSummary]");
            String description = null;
            iterator = items.select("div[class=block]").iterator();
            description = iterator.next().text();
            System.out.println("description = " + description);

        } catch (Exception e) {
            System.out.println("in loadPackage method");
            e.printStackTrace();
            System.out.println();
        }


    } // end loadPackageTable



    private static void loadKlassTable(String dir) throws Exception {


        //File input = new File("C:/Users/myrlin/Desktop/Java/JavaDocs/docs/api/java/util/package-summary.html");
        File input = new File(dir);
        Document doc = Jsoup.parse(input, "UTF-8");



        // Element table = doc.select("table[summary=Interface Summary table, listing interfaces, and an explanation]").first();
        Element table = doc.select("table[summary=Class Summary table, listing classes, and an explanation]").first();

        File currentDir = new File(dir);

        String inputName = null;
        try {
            if (table.hasText()) {

                Iterator<Element> iterator = table.select("td").iterator();
                int count = 1;
                while(iterator.hasNext()) {

                    inputName = iterator.next().text();
                    if (inputName.length() > 200) {
                        inputName = inputName.substring(0, 199);
                    }
                    String inputDescription = iterator.next().text();
                    if (inputDescription.length() > 400) {
                        inputDescription = inputDescription.substring(0, 399);
                    }

                    loadMethodTable(inputName, currentDir);

                }

            }
        }
        catch (Exception e) {
            System.out.println("in loadKlass method");
            e.printStackTrace();
            System.out.println();
        }

    } // end loadKlassTable




    private static void loadMethodTable(String searchname, File directory) throws Exception {

        //TODO strip off < from searchname
        String trimmed = searchname.split("<", 2)[0];

        File newDir = directory.getParentFile();
        System.out.println("in loadMethodTable: parent directory = " + newDir);
        String methodFile = newDir + "\\" + trimmed + ".html";
        File testFile = new File(methodFile);

        System.out.println("in loadMethodTable: filepath = " + methodFile);

        int countMethods = 0;

        try {
            if (testFile.isFile()) {
                File input = new File(methodFile);
                //File input = new File("C:/Users/myrlin/Desktop/Java/JavaDocs/docs/api/java/util/Arraylist.html");
                Document doc = Jsoup.parse(input, "UTF-8");


                Element table = doc.select("table[summary=Method Summary table, listing methods, and an explanation]").first();
                Iterator<Element> iterator = table.select("td[class=colFirst], td[class=colLast]").iterator(); //, div[class=block]

                String type = null;                       // type should be called modifier
                String name = null;
//                String trimmed = null;

                while (iterator.hasNext()) {
                    type = iterator.next().text();
                    name = iterator.next().text();

//                    trimmed = name.split("\\)", 2)[0];   // concept from:http://stackoverflow.com/questions/18220022/how-to-trim-a-string-after-a-specific-character-in-java
//                    trimmed = trimmed + ")";

                    countMethods++;
                }

            } else {
                System.out.println("no matching method html file found");
            }


        } catch (Exception e) {
            System.out.println("in loadMethod");
            e.printStackTrace();
            System.out.println();
        }

        totalMethods = totalMethods + countMethods;
    }



    private static void deleteTables() throws Exception {


    }

    // copied entirely from:
    // https://www.mkyong.com/java/search-directories-recursively-for-file-in-java/
    private static void searchForFiles(FileSearch fileSearch) {

        //FileSearch fileSearch = new FileSearch();

        //try different directory and filename :)
        //  fileSearch.searchDirectory(new File("/Users/mkyong/websites"), "post.php");
        searchDirectory(new File("C:\\Users\\myrlin\\Desktop\\Java\\JavaDocs\\docs\\api\\java"), "package-summary.html", fileSearch);
        //searchDirectory(new File("C:\\Users\\myrlin\\Desktop\\Java\\JavaDocs\\docs\\api"), "package-summary.html", fileSearch);

        int count = fileSearch.getResult().size();
        if(count ==0){
            System.out.println("\nNo result found!");
        }else{
            System.out.println("\nFound " + count + " result!\n");
            for (String matched : fileSearch.getResult()){
                System.out.println("Found : " + matched);
            }
        }
    }

    // copied entirely from:
    // https://www.mkyong.com/java/search-directories-recursively-for-file-in-java/
    public static void searchDirectory(File directory, String fileNameToSearch, FileSearch fileSearch) {

        // FileSearch fileSearch = new FileSearch();

        fileSearch.setFileNameToSearch(fileNameToSearch);

        if (directory.isDirectory()) {
            search(directory, fileSearch);
        } else {
            System.out.println(directory.getAbsoluteFile() + " is not a directory!");
        }

    }

    // copied entirely from:
    // https://www.mkyong.com/java/search-directories-recursively-for-file-in-java/
    private static void search(File file, FileSearch fileSearch) {

        //  FileSearch fileSearch = new FileSearch();

        if (file.isDirectory()) {
            System.out.println("Searching directory ... " + file.getAbsoluteFile());

            //do you have permission to read this directory?
            if (file.canRead() && file.listFiles() != null) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        search(temp, fileSearch);
                    } else {
                        if (fileSearch.getFileNameToSearch().equals(temp.getName().toLowerCase())) {
                            fileSearch.result.add(temp.getAbsoluteFile().toString());
                        }

                    }
                }

            } else {
                System.out.println(file.getAbsoluteFile() + "Permission Denied");
            }
        }

    }


    private static File getDirectory(File file) {

        File newDir = file.getParentFile();
        String newFileName = file.getName();

        System.out.println("directory = " + newDir.toString());
        System.out.println("file name = " + newFileName);

        return newDir;
    }

} // end class main

