package nl.stokpop.equals;

import java.util.Objects;

public class MyData {

    private final String field1;
    private final String field2;
    private final String field3;

    public MyData(String field1, String field2, String field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MyData that = (MyData) o;
        return field1.equals(that.field1) &&
            field2.equals(that.field2) &&
            field3.equals(that.field3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field1, field2, field3);
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        MyData myData = (MyData) o;
//        return Objects.equals(field1, myData.field1) && Objects.equals(field2, myData.field2) && Objects.equals(field3, myData.field3);
//    }

}


