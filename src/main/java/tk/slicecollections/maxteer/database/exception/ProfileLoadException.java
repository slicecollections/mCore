package tk.slicecollections.maxteer.database.exception;

public class ProfileLoadException extends Exception {
  static final long serialVersionUID = 3287516992918834123L;

  public ProfileLoadException(String reason) {
    super(reason);
  }
}
