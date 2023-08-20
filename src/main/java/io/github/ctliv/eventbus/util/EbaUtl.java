package io.github.ctliv.eventbus.util;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EbaUtl {

    //------- Check Object(s) for Null --------

    public static boolean isNotNull(Object... objects) {
        Objects.requireNonNull(objects);
        if (objects.getClass().isArray()) {
            return Arrays.stream(objects).allMatch(Objects::nonNull);
        } else {
            return true;
        }
    }

    public static void notNull(Object object) {
        Objects.requireNonNull(object);
    }

    public static void allNotNull(Object... objects) {
        EbaUtl.notNull(objects);
        if (objects.getClass().isArray()) {
            Arrays.stream(objects).forEach(EbaUtl::notNull);
        }
    }

    //------- Check String(s) for Empty --------

    public static boolean isEmpty(String str) {
        return Strings.isNullOrEmpty(str);
    }

    public static boolean isNotEmpty(String str) { return !isEmpty(str); }

    public static boolean isNotEmpty(String... strings) {
        Objects.requireNonNull(strings);
        return Arrays.stream(strings).noneMatch(Strings::isNullOrEmpty);
    }

    public static void notEmpty(String string) {
        if (!isNotEmpty(string)) throw new IllegalArgumentException("String is null or empty");
    }

    public static void allNotEmpty(String... strings) {
        if (!isNotEmpty(strings)) throw new IllegalArgumentException("String is null or empty");
    }

    //------- Check Integer(s) for Zero --------

    public static boolean isNullOrZero(Integer integer) {
        return integer == null || integer == 0;
    }

    public static boolean isNotZero(Integer integer) {
        return !isNullOrZero(integer);
    }

    public static boolean isNotZero(Integer... integers) {
        Objects.requireNonNull(integers);
        return Arrays.stream(integers).noneMatch(integer -> integer == null || integer == 0);
    }

    public static void notZero(Integer integer) {
        if (!isNotZero(integer))  throw new IllegalArgumentException("Integer is null or zero");
    }

    public static void allNotZero(Integer... integers) {
        if (!isNotZero(integers)) throw new IllegalArgumentException("Integer is null or zero");
    }

    //------- Conversion --------

    public static <T> T cast(Object obj, Class<T> type) {
        EbaUtl.allNotNull(type, obj);
//        if (!type.isAssignableFrom(obj.getClass())) return null;
        return type.cast(obj);
    }

    //------- Object Utils -------

    @SafeVarargs
    public static <T> boolean in(T obj, T... objects) {
        if (objects == null) return obj == null;
        if (obj == null) return false;
        if (!objects.getClass().isArray()) return objects.equals(obj);
        return Arrays.asList(objects).contains(obj);
    }

    //------- Class Utils -------

    public static boolean exists(String className) {
        return forName(className) != null;
    }
    public static Class<?> forName(String className) {
        if (isEmpty(className)) return null;
        try {
            return Class.forName(trim(className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    //------- Other String Utils -------

/*  Uses Apache Commons Lang3
    public static String left(String str, int len) {
        return StringUtils.left(str, len);
    }
*/

    public static String trim(String str) {
        if (str == null) return null;
        return str.trim();
    }

    public static final String NULL_REP = "(null)";

    public static <T> String nn(T obj) {
        return nn(obj, NULL_REP);
    }

    public static <T> String nn(T obj, String def) {
        return obj == null ? def : obj.toString();
    }

    public static <T> T nn(T obj, T def) {
        return obj == null ? def : obj;
    }

}
