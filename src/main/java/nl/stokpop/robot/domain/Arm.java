package nl.stokpop.robot.domain;

import lombok.*;
import nl.stokpop.robot.domain.type.ArmType;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Arm {
    String name;
    ArmType type;
    long length;
}
