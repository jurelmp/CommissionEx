package ph.plc.commission.model;


import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;

@Entity
public class Rate implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mName = new SimpleStringProperty(this, "name");
    private final DoubleProperty mValue = new SimpleDoubleProperty(this, "value");

    public Rate() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return mId.get();
    }

    public IntegerProperty idProperty() {
        return mId;
    }

    public void setId(int id) {
        this.mId.set(id);
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return mName.get();
    }

    public StringProperty nameProperty() {
        return mName;
    }

    public void setName(String name) {
        this.mName.set(name);
    }

    @Column(name = "value", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    public double getValue() {
        return mValue.get();
    }

    public DoubleProperty valueProperty() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue.set(value);
    }

    @Override
    @Transient
    public String getTitle() {
        return null;
    }
}
