module org.phoenicis.multithreading {
    exports org.phoenicis.multithreading;
    exports org.phoenicis.multithreading.functional;
    opens org.phoenicis.multithreading;
    requires commons.lang;
    requires java.annotation;
    requires slf4j.api;
    requires spring.context;
}
