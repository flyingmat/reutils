package org.reutils.core.build.builders;

import org.reutils.annotations.Group;
import org.reutils.core.build.exceptions.BuildException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexBuilder {

    protected final Map<String,Function<String,?>> mappings;

    protected final String input;

    protected RegexBuilder(final Map<String,Function<String,?>> mappings, final String input) {
        this.mappings = mappings;
        this.input = input;
    }

    public abstract <T> T as(final Class<T> clazz);

    public abstract <T> Optional<T> asOptional(final Class<T> clazz);

    public static abstract class Builder {

        protected final Map<String,Function<String,?>> mappings = new HashMap<>();

        public Builder map(final String group, final Function<String,?> mapping) {
            mappings.put(group, mapping);
            return self();
        }

        public abstract RegexBuilder build(String input);

        public abstract Builder self();

    }

    protected static String getGroupName(final Field field) {
        final String groupName = field.getAnnotation(Group.class).value();
        return groupName.isBlank() ? field.getName() : groupName;
    }

    protected static <T> T instantiate(final Class<T> clazz) {
        final T object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (final InstantiationException e) {
            throw new BuildException("Unable to instantiate the provided class '" + clazz.getName() + "' because it is abstract.", e);
        } catch (final IllegalAccessException e) {
            throw new BuildException("Unable to access the provided class '" + clazz.getName() + "'.", e);
        } catch (final RuntimeException | InvocationTargetException e) {
            throw new BuildException("Unable to instantiate the provided class '" + clazz.getName() + "'.", e);
        } catch (final NoSuchMethodException e) {
            throw new BuildException("Unable to find a constructor taking no arguments in the provided class '" + clazz.getName() + "'.", e);
        }

        return object;
    }

    protected <T> T processGroupFields(final T object, final Matcher matcher) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Group.class)) {
                final String groupName = getGroupName(field);
                try {
                    final String group = matcher.group(groupName);
                    field.set(object, mappings.containsKey(groupName) ? mappings.get(groupName).apply(group) : group);
                } catch (final RuntimeException | IllegalAccessException e) {
                    throw new BuildException("Unable to set field '" + field.getName() + "'.", e);
                }
            }
        }

        return object;
    }

    public static SingleRegexBuilder.Builder of(final Pattern pattern) {
        return new SingleRegexBuilder.Builder(pattern);
    }
}
