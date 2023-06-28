import java.util.*;

public class Parser {

    static Hashtable<Integer,String> tmp=new Hashtable<>();


    private Parser()
    {
    }


    public static void parseFile(String fileName , int processId)   //the file is present in the project directory?
    {

            ArrayList<String[]> parsedFile = system.parseFile(fileName);
            int processSize = 5 + 3; //5 for pcb and 3 for variables
            processSize+=parsedFile.size(); // add the instructions size to the process size

            //check if the main memory has space for the parsed process
            Word [] mainMemory = system.getMainMemory();
            boolean spaceFound=false;
            Integer memoryEnd;

            if(mainMemory[0]==null){
                memoryEnd =10+parsedFile.size()+3;
                spaceFound=true;

                system.loadPCB(0, processId , 0 , 0 , 10 , memoryEnd);//instructions and variable start is 10 and end is 10+9
                system.initializeInstructionsAndVariable(10,parsedFile);

            }

            else if (mainMemory[5]==null){
                memoryEnd =25+parsedFile.size()+3;
                spaceFound=true;

                system.loadPCB(5, processId , 0 , 0 , 25 , memoryEnd); //instructions and variables start is 25 and end is 25+9
                system.initializeInstructionsAndVariable(25,parsedFile);

            }

            if(!spaceFound){//space not found we have to unload a non-running process

                //System.out.println(processId+"balablablab");
                if( ( (Integer) mainMemory[1].getData()).intValue() !=2  ){//this process is not running
                    int start=10;
                    int end=((Integer) mainMemory[4].getData()).intValue();//end of the process to be removed
                    int pIdToBeRemoved = ( (Integer) mainMemory[0].getData() ).intValue();

                    system.unloadFromMemoryToDisk(0,5,pIdToBeRemoved);//unload pcb
                    system.unloadFromMemoryToDisk(start , end , pIdToBeRemoved); //unload instructions and variables

                    system.loadPCB(0, processId , 0 , 0 , 10 , 10+3+ parsedFile.size());//instructions and variable start is 10 and end is 10+9
                    system.initializeInstructionsAndVariable(10,parsedFile);

                    System.out.println("PROCESS "+pIdToBeRemoved+" IS PUT ON DISK");
                    System.out.println("PROCESS "+processId+" IS PUT IN MAIN MEMORY");

                }
                else if (( (Integer) mainMemory[6].getData()).intValue() !=2){//this process is not running
                    int start=25;
                    int end=((Integer) mainMemory[9].getData()).intValue();//end of the process to be removed
                    int pIdToBeRemoved = ( (Integer) mainMemory[5].getData() ).intValue();

                    system.unloadFromMemoryToDisk(5,10,pIdToBeRemoved); //unload pcb
                    system.unloadFromMemoryToDisk(start , end , pIdToBeRemoved); // unload instructions and variables

                    system.loadPCB(5, processId , 0 , 0 , 25 , 25+3+parsedFile.size()); //instructions and variables start is 25 and end is 25+9
                    system.initializeInstructionsAndVariable(25,parsedFile);

                    System.out.println("PROCESS "+pIdToBeRemoved+" IS PUT ON DISK");
                    System.out.println("PROCESS "+processId+" IS PUT IN MAIN MEMORY");

                }

            }






    }



    public static boolean executeLine(int processId , int pc) throws Exception {
        String [] instruction=null;
        Word [] mainMemory = system.getMainMemory();
        Integer memoryEnd = null ;
        Integer newPc;
        Integer memoryStart=null;

        // getting the instruction
        //System.out.println(processId+" "+pc);
        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
        {
            instruction = (String[]) mainMemory[13+pc].getData();
            memoryEnd=((Integer) mainMemory[4].getData()).intValue();
            memoryStart=((Integer) mainMemory[3].getData()).intValue();
        }

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
        {
            instruction = (String[]) mainMemory[28+pc].getData();
            memoryEnd=((Integer) mainMemory[9].getData()).intValue();
            memoryStart=((Integer) mainMemory[8].getData()).intValue();
            ((Integer) mainMemory[8].getData()).intValue();
        }
        else{
            throw new Exception("process not found in memory!!");
        }


        String s =instruction[0];

        System.out.println("EXECUTING INSTRUCTION: "+Arrays.deepToString(instruction));

        if(s.equals("semWait") || s.equals("semSignal")) //requesting a resource
        {
            Kernel.requestResource(instruction, processId);
            newPc = incrementPc(processId);
        }

        else if(s.equals("assign"))//could be one cycle or two cycles
            newPc = assign(processId , instruction , pc );

        else{
            switch (s)
            {
                case "printFromTo" : printFromTo(instruction,processId)     ; newPc = incrementPc(processId)  ; break;
                case "print"       : print(instruction,processId)           ; newPc = incrementPc(processId)  ; break;
                case "writeFile"   : writeFile(instruction,processId)       ; newPc = incrementPc(processId)  ; break;
                case "readFile"    : readFile(instruction[1],processId)     ; newPc = incrementPc(processId)  ; break;
                default:
                throw new Exception("error in parsing instruction "+s);
            }

        }
        if(newPc+memoryStart+3==memoryEnd)
            return true;  // the program finished execution
        return false;

    }

    public static int assign(int processId,String [] instruction,int oldPc) throws Exception {
        String anotherInstruction = instruction[2];

        if(tmp.containsKey(processId)){//second cycle of this instruction
            String variable =instruction[1];
            String value = tmp.get(processId); //get the temp variable from the previous clock cycle
            addVariableValueToMemory(processId,variable,value);//add variable to memory
            tmp.remove(processId);
            return incrementPc(processId);
        }

        else if (anotherInstruction.equals("input")){ //pc will not be incremented here
            String userInput = takeInput();
            tmp.put(processId,userInput);   //store the value in a temporary place
            return oldPc;

        }

        else if (anotherInstruction.equals("readFile")){ //pc will not be incremented here
           String fileName = instruction[3];
           String dataReadFromFile =readFile(fileName,processId);
           tmp.put(processId,dataReadFromFile); //store the value in a temporary place
            return oldPc;
        }

        else  { //normal assign (immediate value)
            String variable = instruction[1];
            String value =instruction[2];
            addVariableValueToMemory(processId,variable,value);
            return incrementPc(processId);
        }




    }

    public static String takeInput(){
        return system.takeInput();
    }

    public static void printFromTo(String [] instruction,int processId){
        String variable1=instruction[1];
        String variable2 =instruction[2];
        Object value1=null;
        Object value2=null;
        Word[] mainMemory =system.getMainMemory();
        Integer start=null;

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
           start=10;

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
            start=25;


        for(int i=start;i<start+3;i++)
        {
            Word word =mainMemory[i];
            if(word!=null && word.data!=null && word.data instanceof Variable){
                Variable variable =(Variable) word.data;
                if(variable.variable.equals(variable1))
                    value1=variable.value;
                else if (variable.variable.equals(variable2))
                    value2=variable.value;
            }
        }


        system.printFromTo(value1+"",value2+"");
    }
    public static String readFile(String fileName, int processId){
       // System.out.println(fileName+" this is the filname i am trying to read!!!");

        String variableName = fileName;
        Object value=null;


        Word[] mainMemory =system.getMainMemory();
        Integer start=null;

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
            start=10;

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
            start=25;


        for(int i=start;i<start+3;i++)
        {
            Word word =mainMemory[i];
            if(word!=null && word.data!=null && word.data instanceof Variable){
                Variable variable =(Variable) word.data;
                if(variable.variable.equals(variableName))
                    value=variable.value;

            }
        }

        return system.readFromFile(value+"");
    }
    public static void writeFile(String [] instruction, int processId){
        String fileNameVariableName = instruction[1];
        String valueVariableName = instruction[2];
        String fileName=null;
        Object value=null;


        Word[] mainMemory =system.getMainMemory();
        Integer start=null;

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
            start=10;

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
            start=25;


        for(int i=start;i<start+3;i++)
        {
            Word word =mainMemory[i];
            if(word!=null && word.data!=null && word.data instanceof Variable){
                Variable variable =(Variable) word.data;
                if(variable.variable.equals(valueVariableName))
                    value=variable.value;
                else if(variable.variable.equals(fileNameVariableName))
                    fileName=variable.value.toString();

            }
        }

        system.writeToFile(fileName,value+"");
    }
    public static void print(String []instruction, int processId){
        String variableName = instruction[1];

        Word[] mainMemory =system.getMainMemory();
        Integer start=null;
        Object value=null;

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
            start=10;

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
            start=25;

        for(int i=start;i<start+3;i++)
        {
            Word word =mainMemory[i];
            if(word!=null && word.data!=null && word.data instanceof Variable){
                Variable variable =(Variable) word.data;
                if(variable.variable.equals(variableName))
                    value=variable.value;

            }
        }

        system.print(value.toString());
    }
    public static void addVariableValueToMemory(int processId , String variable , String value) throws Exception {
        Word [] mainMemory = system.getMainMemory();

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
        {
           for(int i =10 ; i <=12 ; i++){
               Word word =mainMemory[i];
               if(word.data==null){
                   {
                       word.data= new Variable(variable,value);
                       return;
                   }
               }
           }
        }

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
        {
            for(int i =25 ; i <=27 ; i++){
                Word word =mainMemory[i];
                if(word.data==null){
                    {
                        word.data= new Variable(variable,value);
                        return;
                    }
                }
            }
            throw new Exception("CAN NOT FIND PLACE TO PUT VARIABLE AND ITS VALUE!!");
        }
    }

    public static int incrementPc(int processId) throws Exception {

        Word [] mainMemory = system.getMainMemory();

        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
        {
           int oldPc= ( (Integer) mainMemory[2].getData()).intValue();
          Variable variable= (Variable) mainMemory[2].data;
          variable.value=oldPc+1;
          return oldPc+1;
        }

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
        {
            int oldPc= ( (Integer) mainMemory[7].getData()).intValue();
            Variable variable= (Variable) mainMemory[7].data;
            variable.value=oldPc+1;
            return oldPc+1;
        }
        throw new Exception("MUST NOT REACH HERE IN PC INCREMENT!!");
    }








}
