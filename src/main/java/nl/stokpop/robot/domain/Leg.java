package nl.stokpop.robot.domain;

import lombok.*;
import nl.stokpop.robot.domain.type.LegType;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Leg {
    String name;
    LegType type;
}
