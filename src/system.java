import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

public class system {



    public static ArrayList<String[]> parseFile(String fileName){

        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            ArrayList<String[]> parsedFile = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                parsedFile.add(parts);

            }
            scanner.close();
            return parsedFile;
        }
             catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        return null;
    }

    public static void writeToFile(String fileName , String data) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(data);
            writer.write(System.lineSeparator()); // Add a new line after appending
            //System.out.println("Data appended to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void unloadFromMemoryToDisk(int start,int end,int processId){

        Word [] mainMemory = Memory.getMainMemory();
        String fileName = processId+"";

        for(int i=start ; i<end ; i++){
            Word word =mainMemory[i];
            String wordToMemory="";

            if(word.data==null){
                wordToMemory="null";
            }
            else if (word.data instanceof Variable){
                Variable variable =(Variable)word.data;
                wordToMemory=variable.variable+" "+variable.value;
            }
            else{//then it is an instruction
                String [] arr= (String[]) word.getData();

                for(int j=0;j<arr.length;j++){
                    if(j!= arr.length-1)
                        wordToMemory+=arr[j]+" ";
                    else
                        wordToMemory+=arr[j];

                }
            }
            system.writeToFile(fileName,wordToMemory);

        }

    }


    public static void unloadFromDiskToMemory(int processId){
       ArrayList<String [] > processOnDisk = parseFile(processId+"");
       deleteFile(processId+"");
       Word [] mainMemory =getMainMemory();
       if(/*mainMemory[0]==null ||*/ ((Integer) mainMemory[1].getData()).intValue()!=2){

           int start=10;
           int end=((Integer) mainMemory[4].getData()).intValue();
           int pIdToBeRemoved = ( (Integer) mainMemory[0].getData() ).intValue();

           unloadFromMemoryToDisk(0,5,pIdToBeRemoved);
           unloadFromMemoryToDisk(start , end , pIdToBeRemoved);

           int state=Integer.parseInt(processOnDisk.get(1)[1]);
           int pc=Integer.parseInt(processOnDisk.get(2)[1]);
           int memoryStart=Integer.parseInt(processOnDisk.get(3)[1]);
           int memoryEnd=Integer.parseInt(processOnDisk.get(4)[1]);
//           int processSize=memoryEnd-memoryStart+1;
//
//           system.loadPCB(0, processId , state, pc , 10 , 10+processSize);//instructions and variable start is 10 and end is 10+9

           ArrayList<String[]> parsedFile= new ArrayList<>();
           for(int i=5;i<processOnDisk.size();i++)
               parsedFile.add(processOnDisk.get(i));

           system.loadPCB(0, processId , state, pc , 10 , 10+parsedFile.size());//instructions and variable start is 10 and end is 10+9
           system.putInstructionsAndVariable(10,parsedFile);


           System.out.println("PROCESS "+pIdToBeRemoved+" IS PUT ON DISK");
           System.out.println("PROCESS "+processId+" IS PUT IN MAIN MEMORY");
           System.out.println(Arrays.deepToString(parsedFile.toArray())+"unloaded from disk to memory");
           System.out.println("memory after "+processId+" is brought from disk to main memory");
           printMemory();


       }
       else if (/*mainMemory[5]==null || */((Integer) mainMemory[6].getData()).intValue()!=2){

           int start=25;
           int end=((Integer) mainMemory[9].getData()).intValue();
           int pIdToBeRemoved = ( (Integer) mainMemory[5].getData() ).intValue();

           unloadFromMemoryToDisk(5,10,pIdToBeRemoved);
           unloadFromMemoryToDisk(start , end , pIdToBeRemoved);

           int state=Integer.parseInt(processOnDisk.get(1)[1]);
           int pc=Integer.parseInt(processOnDisk.get(2)[1]);
           int memoryStart=Integer.parseInt(processOnDisk.get(3)[1]);
           int memoryEnd=Integer.parseInt(processOnDisk.get(4)[1]);
//           int processSize=memoryEnd-memoryStart+1;
//
//           system.loadPCB(5, processId , state, pc , 25 , 25+processSize);//instructions and variable start is 10 and end is 10+9

           ArrayList<String[]> parsedFile= new ArrayList<>();
           for(int i=5;i<processOnDisk.size();i++)
               parsedFile.add(processOnDisk.get(i));

           system.loadPCB(5, processId , state, pc , 25 , 25+parsedFile.size());//instructions and variable start is 10 and end is 10+9
           system.putInstructionsAndVariable(25,parsedFile);

           System.out.println("PROCESS "+pIdToBeRemoved+" IS PUT ON DISK");
           System.out.println("PROCESS "+processId+" IS PUT IN MAIN MEMORY");
           System.out.println(Arrays.deepToString(parsedFile.toArray())+"unloaded from disk to memory");
           System.out.println("memory after "+processId+" is brought from disk to main memory");
           printMemory();



       }

    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File "+fileName+" is deleted successfully.");
            } else {
                System.out.println("Unable to delete the file.");
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    public static int getPc(int processId) throws Exception {
        Word [] mainMemory = Memory.getMainMemory();

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
            return ((Integer) mainMemory[0+2].getData()).intValue();

        if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
            return ((Integer) mainMemory[5+2].getData()).intValue();
        else
            throw new Exception("PROCESS ID DOES NOT EXIST!!");

    }


    //only used in process arrivals
    public static void initializeInstructionsAndVariable(int start , ArrayList<String[]> parsedFile){

        Word [] mainMemory = getMainMemory();



        mainMemory[start++]=new Word();
        mainMemory[start++]=new Word();
        mainMemory[start++]=new Word();
        //variables done

        for(int i=0;i<parsedFile.size();i++)
            mainMemory[start++]=new Word( parsedFile.get(i) );
        //instructions done

    }

    //used when swapping from disk to main memory
    public static void putInstructionsAndVariable(int start , ArrayList<String[]> parsedFile){

        Word [] mainMemory = getMainMemory();



       for(int i=0;i<3;i++)
       {
           String []arr = parsedFile.get(i);
           if(arr[0].equals("null"))
               mainMemory[start++]=new Word();
           else
               mainMemory[start++]=new Word(arr[0],arr[1]);
       }
        //variables done

        for(int i=3;i<parsedFile.size();i++)
            mainMemory[start++]=new Word( parsedFile.get(i) );
        //instructions done

    }



    public static void loadPCB(int index , int processId,int processState  , int pc, int start,int memoryEnd){
        Word [] mainMemory = getMainMemory();
        mainMemory[index++]=new Word("processId"     ,processId);
        mainMemory[index++]=new Word("processState"  ,processState); // 0 for created process state
        mainMemory[index++]=new Word("programCounter",pc);
        mainMemory[index++]=new Word("memoryStart"   ,start);
        mainMemory[index]=new Word("memoryEnd"     ,memoryEnd);

    }

    public static Word[] getMainMemory(){
        return Memory.getMainMemory();
    }


    public static String takeInput(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your input: ");
        String userInput = scanner.nextLine();
        return userInput;
    }


    public static String readFromFile(String fileName) {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static void printFromTo(String start , String end)
    {
        int a= Integer.parseInt(start);
        int b =Integer.parseInt(end);

        for(int i=a;i<=b;i++)
            System.out.print(i+" ");
        System.out.println();

    }
    public static void print(String s){
        System.out.println(s);
    }

    public static void printMemory(){
        Word [] mainMemory=getMainMemory();

        for(int i=0;i<40;i++){
            System.out.print("INDEX: "+i+" ");

            Word word =mainMemory[i];
            if(word==null)
                System.out.println("null");
            else if(word.getData() ==null)
                System.out.println("null");
            else if (word.data instanceof Variable)
            {
                Variable variable = (Variable)word.data;
                System.out.println("VARIABLE: "+variable.variable+" VALUE: "+variable.value);
            }
            else
            {
                String [] arr= (String[]) word.data;
                System.out.print("INSTRUCTION: ");
                for(int j=0;j<arr.length;j++)
                {
                    System.out.print(arr[j]+" ");
                }
                System.out.println();

            }
        }
    }



}



