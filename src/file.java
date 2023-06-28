import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class file extends Mutex {

    static Queue<Integer> blockedQueue = new LinkedList<>();
    static Integer ownerId;


    private file(){
        super();
    }


    public static void semWait(int processId) throws Exception {
        if(ownerId==null)
            ownerId=processId;
        else{
            blockedQueue.add(processId);
            Scheduler.blockRunningProcess();
            //block this process and put this process on the blocked queue of the scheduler

        }

    }

    public static void  semSignal(int processId) throws Exception {
        if(ownerId==processId){

            if(blockedQueue.isEmpty())
                ownerId=null;
            else
            {
                ownerId=blockedQueue.poll();
                Scheduler.unblockProcess(ownerId);
                //unblock this process from the blocked queue of the scheduler
            }
        }


    }

}
