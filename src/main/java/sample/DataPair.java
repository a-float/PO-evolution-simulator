package sample;

public class DataPair {
    private String name;
    private String value;

    DataPair(String name, String data){
        this.name = name;
        this.value = data;
    }
    public String toString(){
        return "{name: "+name+", value: "+value+"}";
    }

    public String getPair(){
        return String.format("%12s: %5s",name, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
