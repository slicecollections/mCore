package tk.slicecollections.maxteer.reflection.acessors;

import java.lang.reflect.Method;

/**
 * Essa classe representa um {@link Method} com métodos seguros de acesso.
 * 
 * @author Maxteer
 */
public class MethodAccessor {

  private Method handle;

  public MethodAccessor(Method method) {
    this(method, false);
  }

  public MethodAccessor(Method method, boolean forceAccess) {
    this.handle = method;
    if (forceAccess) {
      method.setAccessible(true);
    }
  }

  /**
   * Método utilizado para invocar um {@link Method}
   * 
   * @param target A instância para utilizar o método.
   * @param args Os parâmetros para invocar o método.
   * @return Resultado do método.
   */
  public Object invoke(Object target, Object... args) {
    try {
      return handle.invoke(target, args);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException("Cannot invoke method.", ex);
    }
  }

  /**
   * Método utilizado para verificar se a classe do Objeto possui o {@link Method}.
   * 
   * @param target O alvo para verificar.
   * @return TRUE caso possua o método na classe, FALSE caso não.
   */
  public boolean hasMethod(Object target) {
    return target != null && this.handle.getDeclaringClass().equals(target.getClass());
  }

  @Override
  public String toString() {
    return "MethodAccessor[class=" + this.handle.getDeclaringClass().getName() + ", name=" + this.handle.getName() + ", params=" + this.handle.getParameterTypes().toString() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    if (obj instanceof MethodAccessor) {
      MethodAccessor other = (MethodAccessor) obj;
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
