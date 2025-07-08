module com.topcard {

    // --- JavaFX Modules ---
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Often implicitly required, but good to be explicit
    requires javafx.base;    // Often implicitly required, but good to be explicit
    requires javafx.swing;   // Only if you actually use javafx-swing in your project



    // --- Other Dependencies ---
    // For Log4j
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    exports com.topcard.app;
    opens com.topcard.app to org.apache.logging.log4j;

    // For BCrypt
    //requires org.mindrot.jbcrypt;

    // For MySQL JDBC Driver
    // This is also an "automatic module"
    requires mysql.connector.j; // The automatic module name for mysql-connector-java

    // For JDBC API (java.sql package)
    requires java.sql;

    // --- Opening Packages for JavaFX FXML Reflection ---
    // JavaFX uses reflection to inject FXML elements into your controllers
    // and to call event handlers. You need to "open" the packages that
    // contain your controllers and other classes that are accessed by FXML.
    opens com.topcard.presentation.controller to javafx.fxml;
    opens com.topcard.presentation.view to javafx.fxml; // If you have FXML elements in your views that need access

    // --- Exporting Packages ---
    // If other *modules* were going to depend on your 'com.topcard' module,
    // you would 'export' the packages that contain public APIs they can use.
    // Even if you don't have other modules, it's good practice for clarity.
    exports com.topcard.presentation.controller;
    exports com.topcard.presentation.view;
    exports com.topcard.service.player;
    exports com.topcard.dao.player;
    exports com.topcard.domain;
    exports com.topcard.exceptions;
    exports com.topcard.util;

    uses org.apache.logging.log4j.core.util.ContextDataProvider;
    uses org.apache.logging.log4j.core.util.WatchEventService;
}