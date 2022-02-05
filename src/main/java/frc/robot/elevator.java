package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//FALCON 500 (1)

public class Elevator{
   
    //MOTORS
    private MotorController elevatorMotor;

    //ENCODERS
    private TalonFXSensorCollection elevatorEncoder;

    //SENSORS
    private DigitalInput limitTop;          //4000                    
    private DigitalInput limitBot;          //-1200

    //VALUES
    private double closeTopLimit = 0.50* -2094;                  //close to top limit switch enc. value         
    private double closeBotLimit = -600;                         //close to bottom limit switch enc. value
    private double extendSpeed = -0.40;                          //counter-clockwise to extend (-speed)
    private double slowExtendSpeed = -0.30;
    private double retractSpeed = 0.40;                         //clockwise to retract (+speed)
    private double slowRetractSpeed = 0.30;

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
    
    private elevatorState runState = elevatorState.STOP;        //default state     

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

    //CHECKS
    public boolean topLimitTouched(){                                                       //return true if top limit switch is pressed
        return limitTop.get();
    }

    public boolean bottomLimitTouched(){                                                    //return true if bottom limit switch is pressed
        return limitBot.get(); 
    }
    
    public boolean topEncoderLimitReached(){                                                //return true if past top encoder check
        return elevatorEncoder.getIntegratedSensorPosition() > closeTopLimit;
    }
    
    public boolean botEncoderLimitReached(){                                                //return true if past bottom encoder check
        return elevatorEncoder.getIntegratedSensorAbsolutePosition() < closeBotLimit;
    }


    //STOP
    public void stop(){                                                                     //stop elevator motor
        elevatorMotor.set(0);
    }

    //TESTING
    public void testing(){}

    //MANUALS
    public void elevExtend(){                                                               //automatically set to extend speed value
        elevatorMotor.set(extendSpeed);
    }                
    
    public void elevRetract(){                                                              //automatically set to retract speed value
        elevatorMotor.set(retractSpeed);
    }

    public void elevExtendSlow(){                                                           //automatically set to extend slow speed value
        elevatorMotor.set(slowExtendSpeed);
    }
    
    public void elevRetractSlow(){                                                          //automatically set to retract slow speed value
        elevatorMotor.set(slowRetractSpeed);
    }

    public void manualElev(double speed){                                                   //if not at either limits, move to an inputted speed
        if(!topLimitTouched() || !bottomLimitTouched())
        elevatorMotor.set(speed);
    }

    public void encoderReset(){                                                             //reset elevator encoder value
        elevatorEncoder.setIntegratedSensorPosition(0, 0);
    }
    

    //EXTEND
    public void extendToTopLimit(){
        if(topLimitTouched()){                                                              //if at top limit
            elevatorMotor.set(0);                                                           //stop extending
        }
        else{
            if(topEncoderLimitReached()){                                                   //not at top limit but close to
                elevatorMotor.set(slowExtendSpeed);                                         //extend slow
            }
            else{
                elevatorMotor.set(extendSpeed);                                             //if not close to top limit, extend fast
            }
        }
        }

    //RETRACT
    public void retractToBottomLimit(){
        if(bottomLimitTouched()){                                                           //if at bottom limit
            elevatorMotor.set(0);                                                           //stop retracting
            elevatorEncoder.setIntegratedSensorPosition(0, 0);                              //reset encoder (bottom limit should be 0 position)
        }
        else{
            if(botEncoderLimitReached()){                                                   //if not at bottom limit but close to
                elevatorMotor.set(slowRetractSpeed);
            }
            else{                                                                           //if not at or close to bottom limit
                elevatorMotor.set(retractSpeed);                                            //retract fast
            }
        }
    }

    //RUN
    public void run(){
        SmartDashboard.putNumber("ElevatorEncoder:", elevatorEncoder.getIntegratedSensorPosition());
        SmartDashboard.putBoolean("Elevator Top Limit:", limitTop.get());
        SmartDashboard.putBoolean("Elevator Bottom Limit:", limitBot.get());
        SmartDashboard.putNumber("Elevator Arm Speed:", elevatorMotor.get());
        SmartDashboard.putString("Elevator Run State:", runState.toString());
        switch(runState){
            
            case STOP:
            stop();
            break;

            case EXTEND:
            extendToTopLimit();
            break;

            case RETRACT:
            retractToBottomLimit();
            break;
            
            case TESTING:
            testing();
            break;

            default:
            stop();
            break;
        }
        
    }
    
}