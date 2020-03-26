package tk.slicecollections.maxteer.nms.v1_8_R3.utils;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;

/**
 * @author Maxter
 */
public class NullBoundingBox extends AxisAlignedBB {

  public NullBoundingBox() {
    super(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
  }

  public double a() {
    return 0.0D;
  }

  public double a(AxisAlignedBB arg0, double arg1) {
    return 0.0D;
  }

  public AxisAlignedBB a(AxisAlignedBB arg0) {
    return this;
  }

  public AxisAlignedBB a(double arg0, double arg1, double arg2) {
    return this;
  }

  public MovingObjectPosition a(Vec3D arg0, Vec3D arg1) {
    return super.a(arg0, arg1);
  }

  public boolean a(Vec3D arg0) {
    return false;
  }

  public double b(AxisAlignedBB arg0, double arg1) {
    return 0.0D;
  }

  public boolean b(AxisAlignedBB arg0) {
    return false;
  }

  public double c(AxisAlignedBB arg0, double arg1) {
    return 0.0D;
  }

  public AxisAlignedBB c(double arg0, double arg1, double arg2) {
    return this;
  }

  public AxisAlignedBB grow(double arg0, double arg1, double arg2) {
    return this;
  }

  public AxisAlignedBB shrink(double arg0, double arg1, double arg2) {
    return this;
  }
}
