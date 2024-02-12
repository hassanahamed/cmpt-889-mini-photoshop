module com.example.cmpt820 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.photo_shop.cmpt820.ui to javafx.fxml;
    exports com.photo_shop.cmpt820.algorithm;
    exports com.photo_shop.cmpt820.ui;
    exports com.photo_shop.cmpt820.util;
}