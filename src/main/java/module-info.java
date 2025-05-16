module Lunchify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.jfree.jfreechart;
    requires com.google.gson;
    requires org.apache.pdfbox;


    opens jku.se.Controller to javafx.fxml;
    exports jku.se.Controller;
    exports jku.se.Utilities;

    opens jku.se to javafx.base, org.junit.platform.commons, org.mockito;
    exports jku.se;

    opens jku.se.repository to org.junit.platform.commons, org.mockito;
    exports jku.se.repository;
    opens jku.se.Utilities to javafx.base, org.junit.platform.commons, org.mockito;
}