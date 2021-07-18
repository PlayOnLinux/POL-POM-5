module org.phoenicis.configuration {
    exports org.phoenicis.configuration;
    exports org.phoenicis.configuration.localisation;
    exports org.phoenicis.configuration.security;
    opens org.phoenicis.configuration;
    opens org.phoenicis.configuration.localisation;
    requires com.fasterxml.jackson.databind;
    requires gettext.commons;
    requires jackson.annotations;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}
