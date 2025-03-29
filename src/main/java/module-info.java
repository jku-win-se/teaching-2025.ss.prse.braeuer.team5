module Lunchify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens jku.se.Controller to javafx.fxml;
    exports jku.se.Controller;
}