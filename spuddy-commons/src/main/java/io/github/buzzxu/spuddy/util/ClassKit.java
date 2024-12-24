package io.github.buzzxu.spuddy.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.ServiceLoader;

public abstract class ClassKit {

    /**
     * 获取类加载器
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
        }
        if (cl == null) {
            cl = ClassKit.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                }
            }
        }
        return cl;
    }

    /**
     *  META-INF/service
     * @param classLoader
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> serviceLoader(ClassLoader classLoader,Class<T> clazz){
        List<T> array = Lists.newArrayList();
        ServiceLoader<T> loader = secureGetServiceLoader(clazz, classLoader);
        for (T clz : loader) {
            array.add(clz);
        }
        return array;
    }


    private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        return (classLoader == null) ?
                ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
//        final SecurityManager sm = System.getSecurityManager();
//        if (sm == null) {
//
//        }
//        return AccessController.doPrivileged((PrivilegedAction<ServiceLoader<T>>) () -> (classLoader == null) ?
//                ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader));
    }


    public static Object[] getDefaultValues(Class<?>... classes) {
        Object[] values = new Object[classes.length];

        for(int i = 0; i < classes.length; ++i) {
            values[i] = getDefaultValue(classes[i]);
        }

        return values;
    }

    public static Object getDefaultValue(Class<?> clazz) {
        return clazz.isPrimitive() ? getPrimitiveDefaultValue(clazz) : null;
    }

    public static Object getPrimitiveDefaultValue(Class<?> clazz) {
        if (Long.TYPE == clazz) {
            return 0L;
        } else if (Integer.TYPE == clazz) {
            return 0;
        } else if (Short.TYPE == clazz) {
            return Short.valueOf((short)0);
        } else if (Character.TYPE == clazz) {
            return '\u0000';
        } else if (Byte.TYPE == clazz) {
            return 0;
        } else if (Double.TYPE == clazz) {
            return 0.0;
        } else if (Float.TYPE == clazz) {
            return 0.0F;
        } else {
            return Boolean.TYPE == clazz ? false : null;
        }
    }
}
