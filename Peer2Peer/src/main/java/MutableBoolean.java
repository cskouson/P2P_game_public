public class MutableBoolean{

    boolean bool;

    public MutableBoolean(boolean bool){
        this.bool = bool;
    }

    public void t(){
        bool = true;
    }

    public void f(){
        bool = false;
    }

    public boolean getVal(){
        return bool;
    }
}
 