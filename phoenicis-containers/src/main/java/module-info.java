module org.phoenicis.containers {
    exports org.phoenicis.containers;
    exports org.phoenicis.containers.dto;
    opens org.phoenicis.containers;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jdk.scripting.nashorn;
    requires org.phoenicis.configuration;
    requires org.phoenicis.engines;
    requires org.phoenicis.library;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.scripts;
    requires org.phoenicis.tools;
    requires org.phoenicis.win32;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}
