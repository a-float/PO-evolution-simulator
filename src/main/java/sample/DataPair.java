package sample;

import java.util.Objects;

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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DataPair dataPair = (DataPair) o;
//        return Objects.equals(name, dataPair.name) && Objects.equals(value, dataPair.value);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name, value);
//    }

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
