package io.github.buzzxu.spuddy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author xux
 * @date 2022年07月16日 10:33
 */
@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Arrays {
    public static Object[] emptyIfNull(Object[] objects) {
        return objects == null ? new Object[0] : objects;
    }

    public static <E> E[] box(Object value) {
        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException(value + " is not an array");
        }
        Class<?> componentType = value.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            return switch (componentType.getSimpleName()) {
                case "long" -> (E[]) box((long[]) value);
                case "int" -> (E[]) box((int[]) value);
                case "short" -> (E[]) box((short[]) value);
                case "char" -> (E[]) box((char[]) value);
                case "byte" -> (E[]) box((byte[]) value);
                case "boolean" -> (E[]) box((boolean[]) value);
                case "float" -> (E[]) box((float[]) value);
                case "double" -> (E[]) box((double[]) value);
                default -> (E[]) value;
            };
        }
        return (E[]) value;
    }

    public static Long[] box(long[] value) {
        Long[] boxed = new Long[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Integer[] box(int[] value) {
        Integer[] boxed = new Integer[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Short[] box(short[] value) {
        Short[] boxed = new Short[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Character[] box(char[] value) {
        Character[] boxed = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Byte[] box(byte[] value) {
        Byte[] boxed = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Boolean[] box(boolean[] value) {
        Boolean[] boxed = new Boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Float[] box(float[] value) {
        Float[] boxed = new Float[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Double[] box(double[] value) {
        Double[] boxed = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            boxed[i] = value[i];
        }
        return boxed;
    }

    public static Object unbox(Object[] value) {
        Class<?> componentType = value.getClass().getComponentType();
        if (!componentType.isPrimitive()) {
            return switch (componentType.getSimpleName()) {
                case "Long" -> unbox((Long[]) value);
                case "Integer" -> unbox((Integer[]) value);
                case "Short" -> unbox((Short[]) value);
                case "Character" -> unbox((Character[]) value);
                case "Byte" -> unbox((Byte[]) value);
                case "Boolean" -> unbox((Boolean[]) value);
                case "Float" -> unbox((Float[]) value);
                case "Double" -> unbox((Double[]) value);
                default -> value;
            };
        }
        return value;
    }

    public static long[] unbox(Long[] value) {
        long[] unboxed = new long[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static int[] unbox(Integer[] value) {
        int[] unboxed = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static short[] unbox(Short[] value) {
        short[] unboxed = new short[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static char[] unbox(Character[] value) {
        char[] unboxed = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static byte[] unbox(Byte[] value) {
        byte[] unboxed = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static boolean[] unbox(Boolean[] value) {
        boolean[] unboxed = new boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static float[] unbox(Float[] value) {
        float[] unboxed = new float[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static double[] unbox(Double[] value) {
        double[] unboxed = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            unboxed[i] = value[i];
        }
        return unboxed;
    }

    public static Object toArray(Collection<?> collection, Class<?> componentType) {
        Object array = Array.newInstance(componentType, collection.size());
        int i = 0;
        for (Object o : collection) {
            Array.set(array, i++, o);
        }
        return array;
    }

    @SafeVarargs
    public static <E> E[] of(E... values) {
        return values;
    }

    @SafeVarargs
    public static <E> E[] toArray(E first, E... others) {
        Object array = Array.newInstance(others.getClass().getComponentType(), others.length + 1);
        Array.set(array, 0, first);
        for (int i = 0; i < others.length; i++) {
            Array.set(array, i + 1, others[i]);
        }
        return (E[]) array;
    }

    public static <E> Stream<E> toStream(Object value) {
        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException(value + " is not an array");
        }
        if (value.getClass().getComponentType().isPrimitive()) {
            value = Arrays.box(value);
        }
        return Stream.of((E[]) value);
    }

    public static <E> boolean in(E value, E... values) {
        for (E e : values) {
            if (e.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <E> E[] concat(E[] first, E[] second) {
        E[] array = (E[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length);
        System.arraycopy(first, 0, array, 0, first.length);
        System.arraycopy(second, 0, array, first.length, second.length);
        return array;
    }

    public static <T> T firstMatch(Predicate<T> matcher, T... array) {
        int index = matchIndex(matcher, array);
        return index < 0 ? null : array[index];
    }

    public static <T> int matchIndex(Predicate<T> matcher, T... array) {
        return matchIndex(matcher, 0, array);
    }

    public static <T> int matchIndex(Predicate<T> matcher, int beginIndexInclude, T... array) {
        checkNotNull(matcher, "Matcher must be not null !");
        if (isNotEmpty(array)) {
            for(int i = beginIndexInclude; i < array.length; ++i) {
                if (matcher.test(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }
    public static <T> boolean isNotEmpty(T[] array) {
        return null != array && array.length != 0;
    }

    public static boolean contains(int[] array,int value){
        return  java.util.Arrays.stream(array).anyMatch(x ->Objects.equals(x,value));
    }
    public static boolean contains(long[] array,long value){
        return  java.util.Arrays.stream(array).anyMatch(x ->Objects.equals(x,value));
    }
    public static boolean contains(Object[] array,Object value){
        return java.util.Arrays.asList(array).contains(value);
    }

}
