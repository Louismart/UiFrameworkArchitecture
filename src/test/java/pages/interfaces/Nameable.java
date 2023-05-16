package pages.interfaces;

public interface Nameable {

    default String getName() {
        return getClass().getSimpleName();
    }
}
