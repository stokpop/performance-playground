package nl.stokpop.robot.domain;

import lombok.*;
import nl.stokpop.robot.domain.type.EyeType;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Eye {
    String name;
    EyeType type;
}
