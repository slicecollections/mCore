package tk.slicecollections.maxteer.reflection;

import tk.slicecollections.maxteer.reflection.acessors.ConstructorAccessor;
import tk.slicecollections.maxteer.reflection.acessors.FieldAccessor;
import tk.slicecollections.maxteer.reflection.acessors.MethodAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Uma classe criado com o intuito de acessar<br/>
 * métodos, fields e construtores de classes através da<br/>
 * Reflection do Java.
 *
 * @author Maxteer
 */
@SuppressWarnings({"rawtypes"})
public class Accessors {

  private Accessors() {
    // Selando a classe.
  }

  /**
   * Método utilizado para remover o {@link Modifier#FINAL} do field caso ele possua<br>
   * e setar o field para acessível caso ele não seja acessível.
   *
   * @param field O field para modificar.
   */
  public static void setAccessible(Field field) {
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    if (field.getModifiers() != (field.getModifiers() & ~Modifier.FINAL)) {
      getField(Field.class, "modifiers").set(field, field.getModifiers() & ~Modifier.FINAL);
    }
  }

  /**
   * Seta o valor de um field através um {@link FieldAccessor}
   *
   * @param field  O field para modificar.
   * @param target O alvo que contém o field para modificar.
   * @param value  O novo valor do field.
   */
  public static void setFieldValue(Field field, Object target, Object value) {
    new FieldAccessor<>(field, true).set(target, value);
  }

  /**
   * Retorna o valor de um field através de um {@link FieldAccessor}
   *
   * @param field  O field para pegar o valor.
   * @param target O alvo que contém o field para pegar.
   * @return O valor do field.
   */
  public static Object getFieldValue(Field field, Object target) {
    return new FieldAccessor<>(field, true).get(target);
  }

  /**
   * Um encurtador sem tipo para {@link Accessors#getField(Class, int, Class)}
   */
  public static FieldAccessor<Object> getField(Class clazz, int index) {
    return getField(clazz, index, null);
  }

  /**
   * Um encurtador sem tipo para {@link Accessors#getField(Class, String, Class)}
   */
  public static FieldAccessor<Object> getField(Class clazz, String fieldName) {
    return getField(clazz, fieldName, null);
  }

  /**
   * Um encurtador sem nome correto para {@link Accessors#getField(Class, String, int, Class)}
   */
  public static <T> FieldAccessor<T> getField(Class clazz, int index, Class<T> fieldType) {
    return getField(clazz, null, index, fieldType);
  }

  /**
   * Um encurtador com índice 0 para {@link Accessors#getField(Class, String, int, Class)}
   */
  public static <T> FieldAccessor<T> getField(Class clazz, String fieldName, Class<T> fieldType) {
    return getField(clazz, fieldName, 0, fieldType);
  }

  /**
   * Método utilizado para pegar um {@link Field} por índice.
   *
   * @param clazz     A classe para buscar os fields.
   * @param fieldName O nome do field, caso for null irá ignorar o nome.
   * @param index     O índice para pegar o field, caso chegue em 0 irá retornar o field.
   * @param fieldType O tipo do field, caso for null irá ignorar o tipo.
   * @return O {@link Field} representado por um {@link FieldAccessor}
   */
  public static <T> FieldAccessor<T> getField(Class clazz, String fieldName, int index, Class<T> fieldType) {
    int indexCopy = index;
    for (final Field field : clazz.getDeclaredFields()) {
      if ((fieldName == null || fieldName.equals(field.getName())) && (fieldType == null || fieldType.equals(field.getType())) && index-- == 0) {
        return new FieldAccessor<>(field, true);
      }
    }

    String message = " with index " + indexCopy;
    if (fieldName != null) {
      message += " and name " + fieldName;
    }
    if (fieldType != null) {
      message += " and type " + fieldType;
    }

    throw new IllegalArgumentException("Cannot find field " + message);
  }

  /**
   * Um encurtador sem parâmetros para {@link Accessors#getMethod(Class, String, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, String methodName) {
    return getMethod(clazz, null, methodName, (Class[]) null);
  }

  /**
   * Um encurtador sem parâmetros {@link Accessors#getMethod(Class, int, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, int index) {
    return getMethod(clazz, null, index, (Class[]) null);
  }

  /**
   * Um encurtador sem tipo de retorno para {@link Accessors#getMethod(Class, Class, String, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, String methodName, Class... parameters) {
    return getMethod(clazz, null, methodName, parameters);
  }

  /**
   * Um encurtador sem tipo de retorno para {@link Accessors#getMethod(Class, Class, int, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, int index, Class... parameters) {
    return getMethod(clazz, null, index, parameters);
  }

  /**
   * Um encurtador sem índice para {@link Accessors#getMethod(Class, int, Class, String, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, Class returnType, String methodName, Class... parameters) {
    return getMethod(clazz, 0, returnType, methodName, parameters);
  }

  /**
   * Um encurtador sem nome correto para {@link Accessors#getMethod(Class, int, Class, String, Class...)}
   */
  public static MethodAccessor getMethod(Class clazz, Class returnType, int index, Class... parameters) {
    return getMethod(clazz, index, returnType, null, parameters);
  }

  /**
   * Método utilizado para pegar um {@link Method} por índice.
   *
   * @param clazz      A classe para buscar os métodos.
   * @param index      O índice para pegar o Método, caso chegue em 0 retorna o Método.
   * @param returnType O tipo de retorno do método, caso for null irá ignorar.
   * @param methodName O nome do éetodo, caso for null irá ignorar.
   * @param parameters Os parâmetros do método, caso for null irá ignorar.
   * @return O {@link Method} representado por um {@link MethodAccessor}
   */
  public static MethodAccessor getMethod(Class clazz, int index, Class returnType, String methodName, Class... parameters) {
    int indexCopy = index;
    for (final Method method : clazz.getDeclaredMethods()) {
      if ((methodName == null || method.getName().equals(methodName)) && (returnType == null || method.getReturnType().equals(returnType)) && (parameters == null || Arrays
        .equals(method.getParameterTypes(), parameters)) && index-- == 0) {
        return new MethodAccessor(method, true);
      }
    }

    String message = " with index " + indexCopy;
    if (methodName != null) {
      message += " and name " + methodName;
    }
    if (returnType != null) {
      message += " and returntype " + returnType;
    }
    if (parameters != null && parameters.length > 0) {
      message += " and parameters " + Arrays.asList(parameters);
    }
    throw new IllegalArgumentException("Cannot find method " + message);
  }

  /**
   * Um encurtador sem tipo de parâmetros para {@link Accessors#getConstructor(Class, int, Class...)}
   */
  public static <T> ConstructorAccessor<T> getConstructor(Class<T> clazz, int index) {
    return getConstructor(clazz, index, (Class[]) null);
  }

  /**
   * Um encurtador com índice 0 para {@link Accessors#getConstructor(Class, int, Class...)}
   */
  public static <T> ConstructorAccessor<T> getConstructor(Class<T> clazz, Class... parameters) {
    return getConstructor(clazz, 0, parameters);
  }

  /**
   * Método utilizado para pegar um {@link Constructor} por um índice.
   *
   * @param clazz      Classe para pegar o construtor.
   * @param index      O índice para pegar o construtor, caso chegue em 0 irá retornar o construtor.
   * @param parameters Classes dos parâmetros do construtor, caso for null irá ignorar os parâmetros.
   * @return O {@link Constructor} representado por um {@link ConstructorAccessor}.
   * @throws IllegalArgumentException Caso não encontre nenhum construtor.
   */
  @SuppressWarnings("unchecked")
  public static <T> ConstructorAccessor<T> getConstructor(Class<T> clazz, int index, Class... parameters) {
    int indexCopy = index;
    for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if ((parameters == null || Arrays.equals(constructor.getParameterTypes(), parameters)) && index-- == 0) {
        return new ConstructorAccessor<>((Constructor<T>) constructor, true);
      }
    }

    throw new IllegalArgumentException("Cannot find constructor for class " + clazz + " with index " + indexCopy);
  }
}
