/**
 * 
 */
package com.africanbank.assessment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author satheeshk
 *
 */
public class IPDetailsCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length <2) {

			System.out.println("Not all parameters were supplied \nUsage: IPDetailsCollector <<filename1 with absolute path>> <<filename2 with absolute path>>");
			return;

		}
		try {
			System.out.println("Working Directory = " + System.getProperty("user.dir"));

			List<String> dataFirst = readFileInList(args[0]);//Loading data from first file.
			List<String> dataSecond = readFileInList(args[1]);//Loading data from second file.
			Map<String, String> masterDataCollector = new HashMap<String, String>();//Creating a masterDataMap to store parsed values

			if(dataFirst.isEmpty()) {
				System.out.println(args[0] + " is an empty file! Exiting!!");//Assumption is that if one of the files are empty, the program may need to halt
				return;
			}else {
				splitDataToMap(dataFirst, masterDataCollector);
			}

			if(dataSecond.isEmpty()) {
				System.err.println(args[1] + " is an empty file! Exiting!!");
				return;
			}else{
				splitDataToMap(dataSecond, masterDataCollector);
			}

			removeDuplicates(masterDataCollector);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Method to read lines from a file and return it as a list
	 * @param fileName - name of the file with absolute file path
	 * @return lines - list of data from each line
	 * @throws Exception
	 */
	private static List<String> readFileInList(String fileName) throws Exception 
	{ 

		List<String> lines = Collections.emptyList(); 
		try{ 
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.US_ASCII); //These files will contain 7-bit ASCII text as per the requirement.
		}catch (Exception e){ 
			throw new Exception("Unable to read data from file due to "+e.getMessage());
		} 
		return lines; 
	} 


	/**
	 * Method to split the line data to map with X.X.X.X as the key Y,Y,Y as values
	 * @param dataSet - line data from the file.
	 * @param masterDataCollector - Master Data Set to store the key and value
	 * @return masterDataCollector - Master Data Set to store the key and value
	 * @throws Exception
	 */
	private static Map<String, String> splitDataToMap (List<String> dataSet,  Map<String, String> masterDataCollector) throws Exception{

		try {
			for(String lineCursor : dataSet) {
				String[] dataSplit = lineCursor.split(":");

				if(dataSplit.length<2) {
					System.out.println("Ignoring the current line as it is an empty whitespace!");//Assumption is sometimes the next line could be empty
				}else {
					if(masterDataCollector.get(dataSplit[0]) != null) {//If the key already exists, append the value
						String dataBuffer = masterDataCollector.get(dataSplit[0]);
						masterDataCollector.put(dataSplit[0], dataBuffer+","+dataSplit[1]);
					}else {//Else create a new record
						masterDataCollector.put(dataSplit[0], dataSplit[1]);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to convert data to map due to "+e.getMessage());
		}
		return masterDataCollector;
	}

	/**
	 * Method to remove duplicates from the Y,Y,Y and sort it ascending
	 * @param masterDataCollector - Master Data Set to store the key and value
	 * @throws Exception
	 */
	private static void removeDuplicates(Map<String, String> masterDataCollector) throws Exception {

		System.out.println("\n=====FINAL RESULT=====\n");
		try {
			for (Map.Entry<String,String> entry : masterDataCollector.entrySet()) {
				String numberData = entry.getValue().replaceAll("\\s", "");//Removing all spaces.

				HashSet<String> numberSet=new HashSet<String>(Arrays.asList(numberData.split(",")));//To remove duplicates

				List<Integer> convertedIntegerList = new ArrayList<Integer>();

				for(String numberCursor: numberSet) {//Iterating the string hashset to convert the string to integer for better sorting
					convertedIntegerList.add(Integer.parseInt(numberCursor));
				}

				Collections.sort(convertedIntegerList); 

				String convertedIntegerToString = convertedIntegerList.toString();
				String csv = convertedIntegerToString.substring(1, convertedIntegerToString.length() - 1).replace(", ", ",");
				System.out.println(entry.getKey()+": "+csv);

			}  
		} catch (Exception e) {
			throw new Exception("Unable to remove duplicates due to "+e.getMessage());
		}
	}



}
