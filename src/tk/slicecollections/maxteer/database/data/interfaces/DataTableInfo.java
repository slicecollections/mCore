package tk.slicecollections.maxteer.database.data.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataTableInfo {
  String name();
  String create();
  String select();
  String insert();
  String update();
}
