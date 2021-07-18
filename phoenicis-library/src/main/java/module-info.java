module org.phoenicis.library {
    exports org.phoenicis.library;
    exports org.phoenicis.library.dto;
    opens org.phoenicis.library;
    requires com.fasterxml.jackson.databind;
    requires commons.lang;
    requires jdk.scripting.nashorn;
    requires org.apache.commons.io;
    requires org.phoenicis.configuration;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.scripts;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
}
