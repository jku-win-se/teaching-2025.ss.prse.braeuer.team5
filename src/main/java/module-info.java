module Lunchify {
    requires javafx.controls;
    requires javafx.fxml;


    opens jku.se.Controller to javafx.fxml;
    exports jku.se.Controller;
}