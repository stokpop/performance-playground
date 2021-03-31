package nl.stokpop.money;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class Bill {
    private Amount amount;
    private String serialNumber;
    private Date publishDate;
    @EqualsAndHashCode.Exclude private Date lastSeen;
}
