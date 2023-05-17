package web.tests;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TearDownable {

    @Getter
    private final List<Runnable> methodTearDowns = new ArrayList<>();

    /**
     * Dynamic teardown support.
     *
     */
    public void registerTearDown(Runnable tearDown) {
        getMethodTearDowns().add(tearDown);
    }
}
