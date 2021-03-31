package nl.stokpop.robot.domain;

import lombok.*;

import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Head {
    String name;
    //@Singular
    List<Eye> eyes;
}
