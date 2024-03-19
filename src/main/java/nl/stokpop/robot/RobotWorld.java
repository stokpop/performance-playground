package nl.stokpop.robot;

import com.fasterxml.jackson.databind.json.JsonMapper;
import nl.stokpop.kotlin.robot.RobotConverter;
import nl.stokpop.robot.domain.*;

import java.io.IOException;
import java.util.List;

public class RobotWorld {

    private static JsonMapper jsonMapper = JsonMapper.builder().build();

    public static void main(String[] args) throws IOException {
        Robot robby = RobotFactory.createRobot("Robby");
        List<Arm> arms = robby.getArms();
        // fails, immutable: arms.add(Arm.builder().name("fake-arm").build());

        List<Eye> eyes = robby.getHead().getEyes();
        eyes.add(Eye.builder().name("fake-eye").build());

        // make robot blind due to @Data
        //robby.getHead().setEyes(null);

        Robot blinky = RobotFactory.createRobot("Blinky");

        String robotJson = RobotConverter.convertRobot(robby);

        System.out.println(robotJson);

        Robot cloneRobot = jsonMapper.readValue(robotJson, Robot.class);

        // still no immutable eyes
        cloneRobot.getHead().getEyes().add(Eye.builder().name("blink-eye").build());

        System.out.println("Clone is the same: " + cloneRobot.equals(robby));
        System.out.println("Clone is the same as Blinky: " + cloneRobot.equals(blinky));

    }

}
