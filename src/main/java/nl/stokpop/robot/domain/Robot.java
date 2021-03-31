package nl.stokpop.robot.domain;

import lombok.*;

import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Robot {
    String name;
    Head head;
    @Singular
    List<Arm> arms;
    @Singular
    List<Leg> legs;
}
