// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Joystick.AxisType;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

WPI_TalonFX elevatorMotor;
TalonFXSensorCollection elevEncoder;
Elevator Elevator;
DigitalInput top;
DigitalInput bottom;


Joystick joy;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    elevatorMotor = new WPI_TalonFX(2);                                    //elevator port change!!!
    elevEncoder = new TalonFXSensorCollection(elevatorMotor);
    top = new DigitalInput(2);
    bottom = new DigitalInput(1);
    //constructor + initialize motors here
    Elevator = new Elevator(elevatorMotor, top, bottom, elevEncoder);     //left is top limit switch, right is bottom
    joy = new Joystick(0);
    elevatorMotor.setNeutralMode(NeutralMode.Brake);                    //sets motor to brake mode

  }

  //fx 1 : shooter
  //fx 2: elevator hang
  //srx 3: intake
  //srx 4: pivot hang
  //digital input 2: top elevator limit switch              pressed is t
  //digital input 1: bottom elevator limit switch           pressed is t
  //digital input 3: inward pivot limit switch              pressed is t/f    reversed/not
  //digital input 0: outward pivot limit switch             pressed is t/f    reversed/not
  //brake mode for pivot arm, to keep it in its position


  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break; 
      }
  }
  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
   
    if(joy.getRawAxis(3) == -1){            //get raw axis 3
      if(joy.getRawButton(4)){            //btn 4 + drive elevator with joystick (Y)
        Elevator.setElevatorTest();
        Elevator.testing(joy.getY());
      }
      else if(joy.getRawButton(5)){         //button 4: reset enc at bottom switch pressed
        Elevator.encoderReset();
      }
      else{
        Elevator.setElevatorStop();
      }          
    }

    else if(joy.getRawAxis(3) == 1){       //if axis is positive, not testing
     if(joy.getRawButton(5)){               //btn 5 = extend enum
        Elevator.setElevatorExtend();
      }

      else if(joy.getRawButton(6)){        //btn 6 = retract enum
        Elevator.setElevatorRetract();
      }

      else if(joy.getRawButton(4)){         //button 4: reset enc at bottom switch pressed
        Elevator.encoderReset();
      }
      
      else{
        Elevator.setElevatorStop();
      }  
      
    }
    //SmartDashboard.putString("Elevator Neutral Mode:", elevatorMotor.neutralOutput())
    SmartDashboard.putNumber("Joystick Axis", joy.getRawAxis(3));
    Elevator.run();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
 