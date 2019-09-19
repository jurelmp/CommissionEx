package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Location implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mArea = new SimpleStringProperty(this, "area");
    private final StringProperty mLocation = new SimpleStringProperty(this, "location");
    private final SimpleObjectProperty<Set<Commission>> mCommissions = new SimpleObjectProperty<>(this, "commissions");

    public Location() {
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

    @Column(name = "area", nullable = false)
    public String getArea() {
        return mArea.get();
    }

    public StringProperty areaProperty() {
        return mArea;
    }

    public void setArea(String area) {
        this.mArea.set(area);
    }

    @Column(name = "location", nullable = false)
    public String getLocation() {
        return mLocation.get();
    }

    public StringProperty locationProperty() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation.set(location);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    public Set<Commission> getCommissions() {
        return mCommissions.get();
    }

    public SimpleObjectProperty<Set<Commission>> commissionsProperty() {
        return mCommissions;
    }

    public void setCommissions(Set<Commission> commissions) {
        this.mCommissions.set(commissions);
    }

    @Override
    @Transient
    public String getTitle() {
        return areaProperty().get() + ", " + locationProperty().get();
    }

    @Override
    public String toString() {
        return getLocation() + ", " + getArea();
    }
}
