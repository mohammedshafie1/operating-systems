public class Word {
    Object data;  //could be an instruction or a variable

    public Word(Object data) {  //if it is an instruction
        this.data = data;
    }

    public Word(String variable , Object data){ // if it is a data
        this.data = new Variable(variable,data);

    }

    public Word(){  //for empty data
        this.data=null;
    }

    public Object getData(){
        if(data ==null)
            return null;  //null
        if(data instanceof Variable){
            Variable variable = (Variable) data;
            return  variable.value; //value of variable
        }else
            return data; //instruction string array
    }
}
