package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Additional implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final ObjectProperty<Date> mOperationDate = new SimpleObjectProperty<>(this, "operationDate");
    private final StringProperty mSubject = new SimpleStringProperty(this, "subject");
    private final DoubleProperty mAmount = new SimpleDoubleProperty(this, "amount");
    private final ObjectProperty<Employee> mEmployee = new SimpleObjectProperty<>(this, "employee");

    public Additional() {
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

    @Column(name = "operation_date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getOperationDate() {
        return mOperationDate.get();
    }

    public ObjectProperty<Date> operationDateProperty() {
        return mOperationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.mOperationDate.set(operationDate);
    }

    @Column(name = "subject")
    public String getSubject() {
        return mSubject.get();
    }

    public StringProperty subjectProperty() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject.set(subject);
    }

    @Column(name = "amount", columnDefinition = "DOUBLE DEFAULT 0.00", nullable = false)
    public double getAmount() {
        return mAmount.get();
    }

    public DoubleProperty amountProperty() {
        return mAmount;
    }

    public void setAmount(double amount) {
        this.mAmount.set(amount);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    public Employee getEmployee() {
        return mEmployee.get();
    }

    public ObjectProperty<Employee> employeeProperty() {
        return mEmployee;
    }

    public void setEmployee(Employee employee) {
        this.mEmployee.set(employee);
    }

    @Override
    @Transient
    public String getTitle() {
        return null;
    }
}
