package nl.stokpop.money;

import java.math.BigDecimal;
import java.util.*;

public class Money {

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(0.105);
        System.out.println("value = " + bigDecimal);
        System.out.println("result = " + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));

        Set<Amount> set = new HashSet<>();
        set.add(new Amount("0.10"));
        set.add(new Amount("0.10"));
        System.out.println("result = " + set);

        Map<Amount, Amount> map = new HashMap<>();
        map.put(new Amount("0.10"), new Amount("0.10"));
        System.out.println("result = " + map.containsKey(new Amount("0.10")));

        Set<Bill> bills = new HashSet<>();
        Bill myBill = new Bill();
        myBill.setAmount(new Amount("100"));
        myBill.setPublishDate(new Date(2020,1,1));
        myBill.setLastSeen(new Date(2020,2,2));
        myBill.setSerialNumber("123456789");
        bills.add(myBill);
        System.out.println("contains: " + bills.contains(myBill));
        myBill.setLastSeen(new Date(2020,3,3));
        System.out.println("still contains: " + bills.contains(myBill));
    }
}
