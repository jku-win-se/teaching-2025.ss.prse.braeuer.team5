module Lunchify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens jku.se.Controller to javafx.fxml;
    exports jku.se.Controller;
    opens jku.se to javafx.base;
}