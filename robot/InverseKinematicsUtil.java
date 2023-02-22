package robot;
import robot.Constants.ArmConstants;

public final class InverseKinematicsUtil {
    private static double x_pos, y_pos, z_pos;

    private InverseKinematicsUtil() {
        throw new UnsupportedOperationException("InverseKinematicsUtil is a utility class and cannot be instantiated");
    }

    /**
     * calculate arm angles relative to limb that it's attached to
     */
    public static double[] getAnglesFromCoordinates(double x, double y, double z, boolean flipped) {
        double a1, a2, turretAngle;
        double adjusted_y = y - ArmConstants.ORIGIN_HEIGHT;   // calculate height relative to the origin (at the tip of the non-moving rod which holds the arm)
        double adjusted_x = x;
        if (x < 0){
            adjusted_x = 0;
        }
        double adjusted_z = z;
        double dist3d = MathUtil.distance(0, adjusted_x, 0, adjusted_y, 0, z);     // calc distance in 3d from top pivot point
        double totalLimbLength = ArmConstants.LIMB1_LENGTH + ArmConstants.LIMB2_LENGTH;
        if(dist3d > totalLimbLength) {
            adjusted_x *= (totalLimbLength / dist3d); 
            adjusted_y *= (totalLimbLength / dist3d);
            adjusted_z *= (totalLimbLength / dist3d); 
            x_pos = adjusted_x;
            y_pos = adjusted_y;
            z_pos = adjusted_z;
        }

        if (dist3d == 0) { //zero, zero on coordinate -> prevent divide by 0 exception
            return new double[] {0,0,0};
        }           
        a2 = MathUtil.lawOfCosinesForAngle(ArmConstants.LIMB1_LENGTH, ArmConstants.LIMB2_LENGTH, dist3d); // a2 is angle between 1st arm segment to 2nd arm segment
        a1 = MathUtil.angleBetweenLines(0, -1, 0, adjusted_x, adjusted_y, adjusted_z) - MathUtil.lawOfSinesForAngle(a2, dist3d, ArmConstants.LIMB2_LENGTH);   // a1 is angle between verticle to 1st arm segment
       
        //if flipped is true, return angles that are "flipped" 
        if(flipped){
            double angleCalc = Math.toDegrees(Math.atan2(x, -y + ArmConstants.ORIGIN_HEIGHT));
            double lineAngle = angleCalc < 0 ? 360 + angleCalc : angleCalc;
            a1 = lineAngle*2 - a1;
            a2 = 360 - a2;
        }

        //turret angle calculations
        double angleCalc = Math.toDegrees(Math.atan2(z, x));
        turretAngle = angleCalc < 0 ? 360 + angleCalc : angleCalc;
        
        if (a1 < 10){
            a1 = 10;
        }
        if(a2 < 15){
            a2 = 15;
        }

        return new double[] {a1, a2, turretAngle};
    }

    public static double[] getCurrentCoordinates() {
        return new double[] {x_pos, y_pos, z_pos};
    }
}