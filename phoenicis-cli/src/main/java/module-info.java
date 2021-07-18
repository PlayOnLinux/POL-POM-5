module org.phoenicis.cli {
    exports org.phoenicis.cli.scriptui;
    requires commandline;
    requires org.jsoup;
    requires org.phoenicis.configuration;
    requires org.phoenicis.engines;
    requires org.phoenicis.library;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.repository;
    requires org.phoenicis.scripts;
    requires org.phoenicis.tools;
    requires org.phoenicis.win32;
    requires spring.context;
}
