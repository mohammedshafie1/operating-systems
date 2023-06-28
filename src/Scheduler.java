import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler {

    static Queue<Integer> readyQueue = new LinkedList<>();    //queue of the process ids that are ready
    static Queue<Integer> blockedQueue = new LinkedList<>();  //queue of the process ids that are blocked
    static Integer runningProcessId;
    static int quantum ;




    private Scheduler(){

    }


    public static void start(int arrival1 , int arrival2 , int arrival3 ,int timeSlice) throws Exception {
        quantum=timeSlice;
        int currentTime=0;
        Memory.mainMemory=new Word[40];
        int programsFinished=0;


        for(int i=0;i>-1;i++){  //main program loop
            //Thread.sleep(2000);
            System.out.println("CYCLE: "+i);

            if(i==arrival1){
                //parse the program and put in memory

                Parser.parseFile("Program_1.txt",1);
                if(runningProcessId==null)
                {
                    runningProcessId=1;
                    changeProcessState(runningProcessId,2);
                }
                else
                    readyQueue.add(1); // queue this process

            }
            if(i==arrival2){
                Parser.parseFile("Program_2.txt",2);
                if(runningProcessId==null)
                {
                    runningProcessId=2;
                    changeProcessState(runningProcessId,2);

                }
                else
                    readyQueue.add(2);

            }
            if(i==arrival3){
                Parser.parseFile("Program_3.txt",3);
                if(runningProcessId==null)
                {
                    runningProcessId=3;
                    changeProcessState(runningProcessId,2);
                }
                else
                    readyQueue.add(3);

            }
            if(runningProcessId!=null){//execute one line of this process

                 int pc = system.getPc(runningProcessId);
//                System.out.println(pc+" this is the pc!!!"+runningProcessId);

                int tmp1=runningProcessId;
                boolean finished  = Parser.executeLine(runningProcessId, pc);

               int tmp2=runningProcessId;
               if(tmp1!=tmp2) //blocking happened
               {
                   putIMemory(runningProcessId);
                   currentTime=-1;
               }


              if(finished){
                  programsFinished++;
                  if(programsFinished==3)
                      break; //all programs are done

                  currentTime=0;
                  int processFinishedId = runningProcessId;
                  changeProcessState(processFinishedId,4);//change state to finished
                  System.out.println("PROCESS "+processFinishedId+" HAS FINISHED:)");

                  runningProcessId=readyQueue.poll();
                  if(runningProcessId==null)
                      continue;
                  putIMemory(runningProcessId);
                  changeProcessState(runningProcessId,2);//change state to running


              }
              else {
                  currentTime++;
                  if (currentTime == quantum) {
                      currentTime = 0;

                      changeProcessState(runningProcessId,1);//change state to ready
                      readyQueue.add( runningProcessId);
                      System.out.println("PROCESS "+runningProcessId+" IS PREEMPTED");
                      runningProcessId=readyQueue.poll();
                      putIMemory(runningProcessId);
                      changeProcessState(runningProcessId,2);//change state to running
                  }
              }

            }



            System.out.println("READY QUEUE: "+readyQueue+" BLOCKED QUEUE: "+blockedQueue);
            System.out.println("CURRENTLY EXECUTING PROCESS ID: "+runningProcessId);
            system.printMemory();
            System.out.println("-----------------------------------------------------------------------------------------");



        }

        System.out.println("ALL THE PROGRAMS HAVE FINISHED EXECUTION");

    }

    public static void putIMemory(int processId) throws Exception {
        Word [] mainMemory = system.getMainMemory();

        if( (processId != ( (Integer) mainMemory[0].getData()).intValue()) && (processId != ( (Integer) mainMemory[5].getData()).intValue()) )
           system.unloadFromDiskToMemory(processId);

    }

    public static void changeProcessState(int processId, int state) throws Exception {

        Word [] mainMemory = system.getMainMemory();


        //CHANGE THE PROCESS STATE RO FINISHED
        //System.out.println(processId);
        if(processId == ( (Integer) mainMemory[0].getData()).intValue() )
        {
            Variable variable = (Variable) mainMemory[1].data;
            variable.value=state;
            return;
        }

        else if(processId == ( (Integer) mainMemory[5].getData()).intValue() )
        {
            Variable variable = (Variable) mainMemory[6].data;
            variable.value=state;
            return;
        }
        throw new Exception("ERROR IN TERMINATING PROCESS!!");


    }

    public static void blockRunningProcess() throws Exception {

        int processToBeBlocked=runningProcessId;
        System.out.println("PROCESS "+processToBeBlocked+" IS BLOCKED!");
        blockedQueue.add(processToBeBlocked);

        Word [] mainMemory = system.getMainMemory();
        //CHANGE THE PROCESS STATE TO BLOCKED
        if(processToBeBlocked == ( (Integer) mainMemory[0].getData()).intValue() )
        {
            Variable processState = (Variable) mainMemory[1].data;
            processState.value=3;
        }

        else if(processToBeBlocked == ( (Integer) mainMemory[5].getData()).intValue() )
        {
            Variable processState = (Variable) mainMemory[6].data;
            processState.value=3;
        }

        runProcessFromReadyQueue();

    }

    public static void runProcessFromReadyQueue() throws Exception {
        runningProcessId=readyQueue.poll();
        System.out.println("PROCESS "+runningProcessId+" IS NOW RUNNING!");


        if(runningProcessId!=null){

            Word [] mainMemory = system.getMainMemory();
            //CHANGE THE PROCESS STATE TO RUNNING
            if(runningProcessId == ( (Integer) mainMemory[0].getData()).intValue() )
            {
                Variable processState = (Variable) mainMemory[1].data;
                processState.value=2;
            }

            else if(runningProcessId == ( (Integer) mainMemory[5].getData()).intValue() )
            {
                Variable processState = (Variable) mainMemory[6].data;
                processState.value=2;
            }
//            else
//                throw new Exception("PROCESS "+runningProcessId+" IS NOT IN MEMORY!");

        }
    }

    public static void unblockProcess(int processId) throws Exception {
        if(!blockedQueue.contains(processId))
            throw new Exception(processId+ " process id is not found and can not be unblocked!!");

        blockedQueue.remove(processId);
        readyQueue.add(processId);

    }
}
