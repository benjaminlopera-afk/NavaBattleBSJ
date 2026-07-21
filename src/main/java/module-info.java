module com.example.navabattlebsj {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.navabattlebsj to javafx.fxml;
    exports com.example.navabattlebsj;
    exports com.example.navabattlebsj.applications;
    opens com.example.navabattlebsj.applications to javafx.fxml;
    exports com.example.navabattlebsj.controllers;
    opens com.example.navabattlebsj.controllers to javafx.fxml;
}