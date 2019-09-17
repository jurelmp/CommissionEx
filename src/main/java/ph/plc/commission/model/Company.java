package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Company implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mName = new SimpleStringProperty(this, "name");
    private final StringProperty mDescription = new SimpleStringProperty(this, "description");
    private final SimpleObjectProperty<Set<Commission>> mCommissions = new SimpleObjectProperty<>(this, "commissions");

    public Company() {
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

    @Column(name = "description")
    public String getDescription() {
        return mDescription.get();
    }

    public StringProperty descriptionProperty() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription.set(description);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
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
        return nameProperty().get();
    }

    @Override
    public String toString() {
        return getName();
    }
}
