package nl.stokpop.robot.domain;

import nl.stokpop.robot.domain.type.ArmType;
import nl.stokpop.robot.domain.type.EyeType;
import nl.stokpop.robot.domain.type.LegType;

import java.util.ArrayList;
import java.util.List;

public class RobotFactory {
    public static Robot createRobot(String name) {

        // mutable list
        List<Eye> eyes = new ArrayList<>();
        eyes.add(Eye.builder().name("left").type(EyeType.Camera).build());
        eyes.add(Eye.builder().name("right").type(EyeType.Zoom).build());

        // immutable list
        List<Leg> legs = List.of(
                Leg.builder().name("leg1").type(LegType.Human).build(),
                Leg.builder().name("leg2").type(LegType.Spider).build());

        return Robot.builder()
                .name(name)
                .head(Head.builder().name("head").eyes(eyes).build())
                .arm(Arm.builder().name("arm1").type(ArmType.Static).length(100).build())
                .arm(Arm.builder().name("arm2").type(ArmType.Telescopic).length(390).build())
                .legs(legs)
                .build();
    }

    public static Robot createSimpleRobot() {
        return Robot.builder()
                .name("Robby Simple")
                .build();
    }
}
