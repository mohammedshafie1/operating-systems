
public class Memory {

    static Word[] mainMemory;
    static int mainMemorySize=40;




    private Memory()
    {

    }



    public static Word[] getMainMemory() {
        return mainMemory;
    }

    public static void setMainMemory(Word[] mainMemory) {
        Memory.mainMemory = mainMemory;
    }

    public static int getMainMemorySize() {
        return mainMemorySize;
    }

    public static void setMainMemorySize(int mainMemorySize) {
        Memory.mainMemorySize = mainMemorySize;
    }


}
