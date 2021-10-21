public class Winner{
    String name = "";
    boolean bool = false;


    public void makeWinner(String winner){
        bool = true;
        this.name = winner;
    }

    public boolean doWeHaveWinner(){
        return bool;
    }

    public String getWinner(){
        return name;
    }

    public void reset(){
        name = "";
        bool = false;
    }
}