package org.reutils.core.build.executors.base;

import org.reutils.annotations.Group;
import org.reutils.core.build.exceptions.BuildException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBuildExecutor<C> implements BuildExecutor<C> {

    protected final Class<C> clazz;

    protected AbstractBuildExecutor(final Class<C> clazz) {
        this.clazz = clazz;
    }

    protected String getFieldGroup(final Matcher matcher, final Field field) {
        final String groupName = field.getAnnotation(Group.class).group();
        final Matcher groupIndexMatcher = Pattern.compile("[$\\\\](\\d+)").matcher(groupName);
        if (groupIndexMatcher.matches()) {
            return matcher.group(Integer.parseInt(groupIndexMatcher.group(1)));
        } else {
            return matcher.group(groupName.isBlank() ? field.getName() : groupName);
        }
    }

    protected C processGroupFields(final C object, final Matcher matcher) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Group.class)) {
                try {
                    field.set(object, getFieldGroup(matcher, field));
                } catch (final RuntimeException | IllegalAccessException e) {
                    throw new BuildException("Unable to set field '" + field.getName() + "'.", e);
                }
            }
        }

        return object;
    }

    protected C instantiate() {
        final C object;
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

}
