package ai.dame.app;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/** Quarkus application entry point for dame-ai. */
@QuarkusMain
public class Application {

    /** Launches the Quarkus application. */
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
