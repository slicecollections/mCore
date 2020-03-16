package tk.slicecollections.maxteer.reflection.acessors;

import java.lang.reflect.Constructor;

/**
 * Essa classe representa um {@link Constructor} com métodos seguros de acesso.
 * 
 * @author Maxteer
 */
public class ConstructorAccessor<T> {

  private Constructor<T> handle;

  public ConstructorAccessor(Constructor<T> constructor) {
    this(constructor, false);
  }

  public ConstructorAccessor(Constructor<T> constructor, boolean forceAccess) {
    this.handle = constructor;
    if (forceAccess) {
      constructor.setAccessible(true);
    }
  }

  /**
   * Método utilizado criar uma instância de um {@link Constructor}
   * 
   * @param args Os parâmetros para criar a instância.
   * @return A instância criada.
   */
  public T newInstance(Object... args) {
    try {
      return this.handle.newInstance(args);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException("Cannot invoke constructor.", ex);
    }
  }

  /**
   * Método utilizado para verificar se a classe do Objeto possui o {@link Constructor}.
   * 
   * @param target O alvo para verificar.
   * @return TRUE caso possua o construtor na classe, FALSE caso não.
   */
  public boolean hasConstructor(Object target) {
    return target != null && this.handle.getDeclaringClass().equals(target.getClass());
  }

  @Override
  public String toString() {
    return "ConstructorAccessor[class=" + this.handle.getDeclaringClass().getName() + ", params=" + this.handle.getParameterTypes().toString() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    if (obj instanceof ConstructorAccessor) {
      ConstructorAccessor<?> other = (ConstructorAccessor<?>) obj;
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
