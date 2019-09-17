package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Employee implements GUIRepresentable, Serializable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mCode = new SimpleStringProperty(this, "code");
    private final StringProperty mFirstName = new SimpleStringProperty(this, "firstName");
    private final StringProperty mMiddleInitial = new SimpleStringProperty(this, "middleInitial");
    private final StringProperty mLastName = new SimpleStringProperty(this, "lastName");
    private final ObjectProperty<Set<Additional>> mAdditionals = new SimpleObjectProperty<>(this, "additionals");
    private final ObjectProperty<Set<Allowance>> mAllowances = new SimpleObjectProperty<>(this, "allowances");

    public Employee() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return mId.get();
    }

    public IntegerProperty idProperty() {
        return mId;
    }

    public void setId(int id) {
        this.mId.set(id);
    }

    @Column(name = "code", length = 10, unique = true)
    public String getCode() {
        return mCode.get();
    }

    public StringProperty codeProperty() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode.set(code);
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return mFirstName.get();
    }

    public StringProperty firstNameProperty() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName.set(firstName);
    }

    @Column(name = "middle_initial", length = 1)
    public String getMiddleInitial() {
        return mMiddleInitial.get();
    }

    public StringProperty middleInitialProperty() {
        return mMiddleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.mMiddleInitial.set(middleInitial);
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return mLastName.get();
    }

    public StringProperty lastNameProperty() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName.set(lastName);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    public Set<Additional> getAdditionals() {
        return mAdditionals.get();
    }

    public ObjectProperty<Set<Additional>> additionalsProperty() {
        return mAdditionals;
    }

    public void setAdditionals(Set<Additional> additionals) {
        this.mAdditionals.set(additionals);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    public Set<Allowance> getAllowances() {
        return mAllowances.get();
    }

    public ObjectProperty<Set<Allowance>> allowancesProperty() {
        return mAllowances;
    }

    public void setAllowances(Set<Allowance> allowances) {
        this.mAllowances.set(allowances);
    }

    @Override
    @Transient
    public String getTitle() {
        return codeProperty().getValue();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFirstName()).append(" ");
        if (getMiddleInitial() != null && getMiddleInitial().length() > 0) {
            stringBuilder.append(getMiddleInitial()).append(". ");
        }
        stringBuilder.append(getLastName());
        return stringBuilder.toString();
    }
}
