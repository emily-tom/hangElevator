package frc.robot;

import java.util.Enumeration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//FALCON 500 (1)

public class Elevator {
    //MOTORS
    private MotorController elevatorMotor;

    //ENCODERS
    private TalonFXSensorCollection elevatorEncoder;

    //SENSORS
    private DigitalInput limitTop;                              
    private DigitalInput limitBot;

    //VALUES
    private double closeTopLimit;                   //encoder value, when close to the top limit switch, start to slow down         
    private double closeBotLimit;                   // -- bottom switch --


    //CONSTRUCTOR
    public Elevator(MotorController elevMotor, DigitalInput limitSwitchTop, DigitalInput limitSwitchBottom, TalonFXSensorCollection elevEncoder){
        elevatorMotor = elevMotor;
        limitTop = limitSwitchTop;
        limitBot = limitSwitchBottom;
        elevatorEncoder = elevEncoder;
    }
    
    //ENUMERATIONS/STATES
    private enum elevatorState{
        EXTEND, RETRACT, STOP, TESTING;
    }
    
    private elevatorState runState = elevatorState.STOP;        

    public void elevatorExtend(){
        runState = elevatorState.EXTEND;
    }

    public void elevatorRetract(){
        runState = elevatorState.RETRACT;
    }

    public void elevatorStop(){
        runState = elevatorState.STOP;
    }

    public void elevatorTest(){
        runState = elevatorState.TESTING;
    }

    //STOP
    private void stop(){
        elevatorMotor.set(0);
    }

    //TESTING
    public void test(double JoystickY){
        elevatorMotor.set(JoystickY);
    }

    private void dashboard(){
        SmartDashboard.putNumber("ElevatorEncoder:", elevatorEncoder.getIntegratedSensorAbsolutePosition());
        SmartDashboard.putBoolean("Elevator Top Limit:", limitTop.get());
        SmartDashboard.putBoolean("Elevator Bottom Limit:", limitBot.get());
        SmartDashboard.putNumber("Elevator Arm Speed:", elevatorMotor.get());
        SmartDashboard.putString("Elevator Run State:", runState.toString());
    }
    //EXTEND
    private void extend(){
        if(!limitTop.get()){                                                            //if not at top limit
            if(elevatorEncoder.getIntegratedSensorPosition() < closeTopLimit){              //and not close to limit
                elevatorMotor.set(80);                                                          //extend fast
            }
            else{                                                                           //if close to limit
                elevatorMotor.set(10);                                                          //extend slow
            }
        }
        else{                                                                           //until at top limit
            elevatorStop();                                                                 //stop extension
        }
    }

    //RETRACT
    private void retract(){
        if(!limitBot.get()){
            if(elevatorEncoder.getIntegratedSensorPosition() > closeBotLimit){
                elevatorMotor.set(-80);
            }
            else{
                elevatorMotor.set(-10);
            }
        }
        else{
            elevatorStop();
        }
    }

    //RUN
    public void run(){
        switch(runState){
            case STOP:
            stop();
            break;

            case EXTEND:
            extend();
            break;

            case RETRACT:
            retract();
            break;

            case TESTING:
            break;
        }
    }
    
}
