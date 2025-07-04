module it.unimol.dama {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.unimol.dama to javafx.fxml;
    exports it.unimol.dama;
}