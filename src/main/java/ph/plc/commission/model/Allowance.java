package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Allowance implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final ObjectProperty<Date> mOperationDate = new SimpleObjectProperty<>(this, "operationDate");
    private final StringProperty mSubject = new SimpleStringProperty(this, "subject");
    private final DoubleProperty mDays = new SimpleDoubleProperty(this, "days");
    private final DoubleProperty mRate = new SimpleDoubleProperty(this, "rate");
    private final ObjectProperty<Employee> mEmployee = new SimpleObjectProperty<>(this, "employee");

    public Allowance() {
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

    @Column(name = "days", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    public double getDays() {
        return mDays.get();
    }

    public DoubleProperty daysProperty() {
        return mDays;
    }

    public void setDays(double days) {
        this.mDays.set(days);
    }

    @Column(name = "rate", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    public double getRate() {
        return mRate.get();
    }

    public DoubleProperty rateProperty() {
        return mRate;
    }

    public void setRate(double rate) {
        this.mRate.set(rate);
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
