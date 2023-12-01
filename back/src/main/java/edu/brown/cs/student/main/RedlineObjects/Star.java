package edu.brown.cs.student.main.RedlineObjects;

public class Star {

  // StarID,ProperName,X,Y,Z
  private int ID;
  private String properName;
  private double x;
  private double y;
  private double z;

  public Star(int ID_in, String properName_in, double x_in, double y_in, double z_in) {
    this.ID = ID_in;
    this.properName = properName_in;
    this.x = x_in;
    this.y = y_in;
    this.z = z_in;
  }

  public int getID() {
    return ID;
  }
}
