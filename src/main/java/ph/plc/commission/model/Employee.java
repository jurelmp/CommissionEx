package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Employee implements GUIRepresentable, Serializable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mCode = new SimpleStringProperty(this, "code");
    private final StringProperty mFirstName = new SimpleStringProperty(this, "firstName");
    private final StringProperty mMiddleInitial = new SimpleStringProperty(this, "middleInitial");
    private final StringProperty mLastName = new SimpleStringProperty(this, "lastName");
    private final ObjectProperty<Additional> mAdditional = new SimpleObjectProperty<>(this, "additional");
    private final ObjectProperty<Allowance> mAllowance = new SimpleObjectProperty<>(this, "allowance");
    private final ObjectProperty<Set<Commission>> mCommissions = new SimpleObjectProperty<>(this, "commissions");

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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    public Additional getAdditional() {
        return mAdditional.get();
    }

    public ObjectProperty<Additional> additionalProperty() {
        return mAdditional;
    }

    public void setAdditional(Additional additional) {
        this.mAdditional.set(additional);
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    public Allowance getAllowance() {
        return mAllowance.get();
    }

    public ObjectProperty<Allowance> allowanceProperty() {
        return mAllowance;
    }

    public void setAllowance(Allowance allowance) {
        this.mAllowance.set(allowance);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Commission> getCommissions() {
        if (mCommissions.get() == null) {
            return new HashSet<>();
        }
        return mCommissions.get();
    }

    public ObjectProperty<Set<Commission>> commissionsProperty() {
        return mCommissions;
    }

    public void setCommissions(Set<Commission> commissions) {
        this.mCommissions.set(commissions);
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
