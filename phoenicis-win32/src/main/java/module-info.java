module org.phoenicis.win32 {
    exports org.phoenicis.win32;
    exports org.phoenicis.win32.pe;
    opens org.phoenicis.win32;
    opens org.phoenicis.win32.registry;
    requires commons.lang;
    requires org.apache.commons.io;
    requires org.phoenicis.configuration;
    requires spring.context;
}
