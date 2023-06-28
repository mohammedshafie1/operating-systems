import java.util.ArrayList;

public class Kernel {


    //the kernel is the one who manage the requests to the different resources
    public static void requestResource(String [] instruction , int processId) throws Exception {
        String s =instruction[0];
        String resource = instruction[1];


        if(resource.equals("userInput")){

            if(s.equals("semWait"))
                userInput.semWait(processId);
            else
                userInput.semSignal(processId);

        }
        else if (resource.equals("userOutput")){

            if(s.equals("semWait"))
                userOutput.semWait(processId);
            else
                userOutput.semSignal(processId);

        }
        else if (resource.equals("file")){

            if(s.equals("semWait"))
                file.semWait(processId);
            else
                file.semSignal(processId);

        }
    }





}
