module org.phoenicis.settings {
    exports org.phoenicis.settings;
    opens org.phoenicis.settings;
    requires org.phoenicis.repository;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}
