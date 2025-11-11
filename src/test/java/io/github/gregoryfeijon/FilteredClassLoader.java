package io.github.gregoryfeijon;

/**
 * ClassLoader that filters out specific classes for testing conditional configuration.
 */
class FilteredClassLoader extends ClassLoader {
    private final Class<?>[] filteredClasses;

    FilteredClassLoader(Class<?>... filteredClasses) {
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