package tk.slicecollections.maxteer.reflection.acessors;

import java.lang.reflect.Field;
import tk.slicecollections.maxteer.reflection.Accessors;

/**
 * Essa classe representa um {@link Field} com métodos seguros de acesso.
 * 
 * @author Maxteer
 */
@SuppressWarnings("unchecked")
public class FieldAccessor<TField> {

  private Field handle;

  public FieldAccessor(Field field) {
    this(field, false);
  }

  public FieldAccessor(Field field, boolean forceAccess) {
    this.handle = field;
    if (forceAccess) {
      Accessors.setAccessible(field);
    }
  }

  /**
   * Método utilizado para pegar o valor de um {@link Field}
   * 
   * @param target O alvo para pegar o valor do field.
   * @return O valor do field.
   */
  public TField get(Object target) {
    try {
      return (TField) handle.get(target);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException("Cannot access field.", ex);
    }
  }

  /**
   * Método utilizado para setar o valor de um {@link Field}
   * 
   * @param target O alvo para setar o valor do field.
   * @param value O novo valor do field.
   */
  public void set(Object target, TField value) {
    try {
      handle.set(target, value);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException("Cannot access field.", ex);
    }
  }

  /**
   * Método utilizado para verificar se a classe do Objeto possui o {@link Field}.
   * 
   * @param target O alvo para verificar.
   * @return TRUE caso possua o field na classe, FALSE caso não.
   */
  public boolean hasField(Object target) {
    return target != null && this.handle.getDeclaringClass().equals(target.getClass());
  }

  /**
   * @return O {@link Field} representado nesse Accessor.
   */
  public Field getHandle() {
    return handle;
  }

  @Override
  public String toString() {
    return "FieldAccessor[class=" + this.handle.getDeclaringClass().getName() + ", name=" + this.handle.getName() + ", type=" + this.handle.getType() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    if (obj instanceof FieldAccessor) {
      FieldAccessor<?> other = (FieldAccessor<?>) obj;
      if (other.handle.equals(handle)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.handle.hashCode();
  }
}
