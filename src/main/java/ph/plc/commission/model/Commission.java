package ph.plc.commission.model;

import javafx.beans.property.*;
import ph.plc.commission.gui.GUIRepresentable;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Commission implements GUIRepresentable {
    private final IntegerProperty mId = new SimpleIntegerProperty(this, "id");
    private final StringProperty mInvoice = new SimpleStringProperty(this, "invoice");
    private final ObjectProperty<Date> mTransactionDate = new SimpleObjectProperty<>(this, "transactionDate");
    private final IntegerProperty mVolume = new SimpleIntegerProperty(this, "volume");
    private final DoubleProperty mRate = new SimpleDoubleProperty(this, "rate");
    private final IntegerProperty mPumping = new SimpleIntegerProperty(this, "pumping");
    private final IntegerProperty mSuction = new SimpleIntegerProperty(this, "suction");
    private final StringProperty mTruckNo = new SimpleStringProperty(this, "truckNo");
    private final ObjectProperty<Location> mLocation = new SimpleObjectProperty<>(this, "location");
    private final ObjectProperty<Company> mCompany = new SimpleObjectProperty<>(this, "company");
    private final ObjectProperty<Employee> mEmployee = new SimpleObjectProperty<>(this, "employee");

    public Commission() {
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

    @Column(name = "invoice", nullable = false, unique = true)
    public String getInvoice() {
        return mInvoice.get();
    }

    public StringProperty invoiceProperty() {
        return mInvoice;
    }

    public void setInvoice(String invoice) {
        this.mInvoice.set(invoice);
    }

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    public Date getTransactionDate() {
        return mTransactionDate.get();
    }

    public ObjectProperty<Date> transactionDateProperty() {
        return mTransactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.mTransactionDate.set(transactionDate);
    }

    @Column(name = "volume", columnDefinition = "INTEGER DEFAULT 0", nullable = false)
    public int getVolume() {
        return mVolume.get();
    }

    public IntegerProperty volumeProperty() {
        return mVolume;
    }

    public void setVolume(int volume) {
        this.mVolume.set(volume);
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

    @Column(name = "pumping", columnDefinition = "INTEGER DEFAULT 0", nullable = false)
    public int getPumping() {
        return mPumping.get();
    }

    public IntegerProperty pumpingProperty() {
        return mPumping;
    }

    public void setPumping(int pumping) {
        this.mPumping.set(pumping);
    }

    @Column(name = "suction", columnDefinition = "INTEGER DEFAULT 0", nullable = false)
    public int getSuction() {
        return mSuction.get();
    }

    public IntegerProperty suctionProperty() {
        return mSuction;
    }

    public void setSuction(int suction) {
        this.mSuction.set(suction);
    }

    @Column(name = "truck_no", length = 20)
    public String getTruckNo() {
        return mTruckNo.get();
    }

    public StringProperty truckNoProperty() {
        return mTruckNo;
    }

    public void setTruckNo(String truckNo) {
        this.mTruckNo.set(truckNo);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    public Location getLocation() {
        return mLocation.get();
    }

    public ObjectProperty<Location> locationProperty() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation.set(location);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    public Company getCompany() {
        return mCompany.get();
    }

    public ObjectProperty<Company> companyProperty() {
        return mCompany;
    }

    public void setCompany(Company company) {
        this.mCompany.set(company);
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
