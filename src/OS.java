
public class OS {


    public static void main(String[] args) throws Exception {
        int arrival1=0;
        int arrival2=2;
        int arrival3=4;
        int quantum=2;

        Scheduler.start(arrival1,arrival2,arrival3,quantum);

    }


}
