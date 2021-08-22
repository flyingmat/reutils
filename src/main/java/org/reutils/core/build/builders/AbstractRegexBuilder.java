package org.reutils.core.build.builders;

import org.reutils.annotations.Group;
import org.reutils.core.build.exceptions.BuildException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

public abstract class AbstractRegexBuilder implements RegexBuilder {

    protected final Map<String, Function<String, ?>> mappings = new HashMap<>();

    public AbstractRegexBuilder map(final String group, final Function<String, ?> mapping) {
        mappings.put(group, mapping);
        return this;
    }

    protected static <T> T instantiate(final Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (final InstantiationException e) {
            throw new BuildException("Unable to instantiate the provided class '" + clazz.getName() + "' because it is abstract.", e);
        } catch (final IllegalAccessException e) {
            throw new BuildException("Unable to access the provided class '" + clazz.getName() + "'.", e);
        } catch (final InvocationTargetException e) {
            throw new BuildException("Unable to instantiate the provided class '" + clazz.getName() + "'.", e);
        } catch (final NoSuchMethodException e) {
            throw new BuildException("Unable to find a constructor taking no arguments in the provided class '" + clazz.getName() + "'.", e);
        }
    }

    protected <T> T processGroupFields(final T object, final Matcher matcher) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Group.class)) {
                final Group groupAnnotation = field.getAnnotation(Group.class);
                final String groupName = groupAnnotation.value().isEmpty() ? field.getName() : groupAnnotation.value();
                try {
                    final String group = matcher.group(groupName);
                    if (group == null && !groupAnnotation.optional()) {
                        throw new BuildException("Group '" + groupName + "' did not match any input for non-optional field '" + field.getName() + "'.");
                    }
                    field.setAccessible(true);
                    field.set(object, mappings.containsKey(groupName) ? mappings.get(groupName).apply(group) : group);
                } catch (final IllegalAccessException | IllegalArgumentException e) {
                    throw new BuildException("Unable to set field '" + field.getName() + "'.", e);
                }
            }
        }

        return object;
    }

}
