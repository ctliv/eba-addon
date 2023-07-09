package io.github.ctliv.eventbus.util;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Utl {

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
        Utl.notNull(objects);
        if (objects.getClass().isArray()) {
            Arrays.stream(objects).forEach(Utl::notNull);
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

    //------- Check Object(s) for Null, String(s) for Empty, Integer(s) for Zero --------

    public static void allPresent(Object... objects) {
        allNotNull(objects);
        allNotEmpty(Arrays.stream(objects).filter(String.class::isInstance).toArray(String[]::new));
        allNotZero(Arrays.stream(objects).filter(Integer.class::isInstance).toArray(Integer[]::new));
    }

    //------- Optional helpers --------

    public static <T> Optional<T> opt(T obj) {
        return Optional.ofNullable(obj);
    }

    //------- Supplier with default --------

    public static <T> T get(Supplier<T> supplier) {
        return get(supplier, null, false);
    }

    public static <T> T get(Supplier<T> supplier, T defaultValue) {
        return get(supplier, defaultValue, false);
    }

    public static <T> T get(Supplier<T> supplier, T defaultValue, boolean trapAllExceptions) {
        T value = defaultValue;
        try {
            value = supplier.get();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            if (!trapAllExceptions) throw e;
        }
        return value;
    }

    //------- Protected Exec ----------

    public static void exec(Runnable runnable) {
        exec(runnable, false);
    }

    public static void exec(Runnable runnable, boolean trapAllExceptions) {
        try {
            runnable.run();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            if (!trapAllExceptions) throw e;
        }
    }

    //------- Hide Null Values --------

    public static String orElse(String string) {
        return string == null ? "" : string;
    }

    public static <T extends R,R> R orElse(T obj, R defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    //------- Conditional value --------

    public static <T> T check(boolean condition, T caseTrue, T caseFalse) {
        return condition ? caseTrue : caseFalse;
    }

    //------- Equality check --------

    public static <T> boolean eq(T first, T second) {
        return Objects.equals(first, second);
    }

    public static <T> boolean eq(T first, T second, boolean resultIfBothNull) {
        return eq(first, second, false, resultIfBothNull);
    }

    public static <T> boolean eq(T first, T second, boolean resultIfOneNull, boolean resultIfBothNull) {
        if (first == null && second == null) return resultIfBothNull;
        if (first == null || second == null) return resultIfOneNull;
        return first.equals(second);
    }

    public static <T> boolean eq(Supplier<T> first, Supplier<T> second) {
        T firstValue = get(first);
        T secondValue = get(second);
        return eq(firstValue, secondValue);
    }

    public static <T> boolean eq(Supplier<T> first, Supplier<T> second, boolean trapAllExceptions) {
        T firstValue = get(first, null, trapAllExceptions);
        T secondValue = get(second, null, trapAllExceptions);
        return eq(firstValue, secondValue);
    }

    //------- In --------

    @SafeVarargs
    public static <T> boolean in(T obj, T... values) {
        if (values == null) return obj == null;
        if (obj == null) return false;
        return Arrays.asList(values).contains(obj);
    }

    //------- Conversion --------

    public static <T> T cast(Object obj, Class<T> type) {
        Utl.allNotNull(type, obj);
//        if (!type.isAssignableFrom(obj.getClass())) return null;
        return type.cast(obj);
    }

    public static <P,N,R> R ifIsApply(N obj, Class<P> type,
                                      Function<P,R> positive) {
       return ifIsApply(obj, type, positive, null);
    }

    public static <P,N,R> R ifIsApply(N obj, Class<P> type,
                                      Function<P,R> positive, Function<N,R> negative) {
        allNotNull(type, obj);
        if (type.isAssignableFrom(obj.getClass())) {
            return positive == null ? null : positive.apply(type.cast(obj));
        } else {
            return negative == null ? null : negative.apply(obj);
        }
    }

    public static <P,N> void ifIsConsume(N obj, Class<P> type,
                                         Consumer<P> positive) {
        ifIsConsume(obj, type, positive, null);
    }

    public static <P,N> void ifIsConsume(N obj, Class<P> type,
                                         Consumer<P> positive, Consumer<N> negative) {
        allNotNull(type, obj);
        if (type.isAssignableFrom(obj.getClass())) {
            if (positive != null) positive.accept(type.cast(obj));
        } else {
            if (negative != null) negative.accept(obj);
        }
    }

    public static boolean ifIsRun(Object obj, Class<?> type,
                                  Runnable positive) {
        return ifIsRun(obj, type, positive, null);
    }

    public static boolean ifIsRun(Object obj, Class<?> type,
                               Runnable positive, Runnable negative) {
        allNotNull(obj, type);
        if (type.isAssignableFrom(obj.getClass())) {
            if (positive != null) positive.run();
            return true;
        } else {
            if (negative != null) negative.run();
            return false;
        }
    }

/*
    //See: https://dzone.com/articles/tap-that-assignment-with-java
    public static <T> T tap(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    public record Tapper<T>(T object) {
        public Tapper<T> exec(Consumer<T> consumer) {
            consumer.accept(object());
            return new Tapper<>(object());
        }
    }

    public static <T> Tapper<T> with(T object) { return new Tapper<>(object, t -> t); }
*/

    //------- Collection utils --------

    public static <T> Stream<T> stream(Collection<T>... collections) {
        if (collections == null) return Stream.of();
        return Arrays.stream(collections)
                .flatMap(Collection::stream);
    }

    //------- Other String Utils -------

/*  Ues Apache Commons Lang3
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
