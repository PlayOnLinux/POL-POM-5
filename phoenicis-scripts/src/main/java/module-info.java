module org.phoenicis.scripts {
    exports org.phoenicis.scripts;
    exports org.phoenicis.scripts.interpreter;
    exports org.phoenicis.scripts.ui;
    exports org.phoenicis.scripts.wizard;
    opens org.phoenicis.scripts;
    opens org.phoenicis.scripts.wizard;
    requires java.scripting;
    requires jdk.scripting.nashorn;
    requires org.apache.commons.io;
    requires org.phoenicis.configuration;
    requires org.phoenicis.entities;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.repository;
    requires spring.beans;
    requires spring.context;
}
