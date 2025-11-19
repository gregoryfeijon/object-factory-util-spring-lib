package io.github.gregoryfeijon.object.factory.util.domain;

/**
 * ClassLoader that filters out specific classes for testing conditional configuration.
 */
public class FilteredClassLoader extends ClassLoader {
    private final Class<?>[] filteredClasses;

    public FilteredClassLoader(Class<?>... filteredClasses) {
        this.filteredClasses = filteredClasses;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (Class<?> filteredClass : filteredClasses) {
            if (filteredClass.getName().equals(name)) {
                throw new ClassNotFoundException(name);
            }
        }
        return super.loadClass(name, resolve);
    }
}