module org.phoenicis.repository {
    exports org.phoenicis.repository;
    exports org.phoenicis.repository.dto;
    exports org.phoenicis.repository.location;
    exports org.phoenicis.repository.types;
    opens org.phoenicis.repository;
    opens org.phoenicis.repository.location;
    opens org.phoenicis.repository.dto;
    requires com.fasterxml.jackson.databind;
    requires commons.lang;
    requires jackson.annotations;
    requires org.apache.commons.compress;
    requires org.eclipse.jgit;
    requires org.phoenicis.configuration;
    requires org.phoenicis.entities;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.tools;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}
