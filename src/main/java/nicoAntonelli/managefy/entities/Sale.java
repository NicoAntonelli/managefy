package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Data @NoArgsConstructor @AllArgsConstructor
public class Sale {
    // State enum
    public enum SaleState { Cancelled, PendingPayment, PartialPayment, Payed, PayedAndBilled }

    @Id
    @SequenceGenerator(name = "sales_sequence", sequenceName = "sales_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime date;
    @Column(nullable = false)
    private Float totalPrice;
    private Float partialPayment; // Nullable
    @Column(nullable = false)
    private SaleState state;

    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "businesses_sales_fk")
    )
    private Business business;

    @ManyToOne
    @JoinColumn(
            name = "clientID",
            nullable = true,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "clients_sales_fk")
    )
    private Client client;

    @OneToMany(mappedBy = "sale", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleLine> saleLines = new ArrayList<>();

    public Sale(Long id) {
        this.id = id;
    }

    public Sale(Long id, Float partialPayment, SaleState state) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.partialPayment = partialPayment;

        if (state == null) state = SaleState.PendingPayment;
        this.state = state;
    }

    public Sale(Float partialPayment, SaleState state) {
        this.date = LocalDateTime.now();
        this.partialPayment = partialPayment;

        if (state == null) state = SaleState.PendingPayment;
        this.state = state;
    }

    public void setBusinessByID(Long businessID) {
        business = new Business();
        business.setId(businessID);
    }

    public void setClientByID(Long clientID) {
        client = new Client();
        client.setId(clientID);
    }

    public void addSaleLine(SaleLine saleLine) {
        saleLines.add(saleLine);
    }

    public Boolean setStateByText(String state) {
        switch (state.toLowerCase()) {
            case "cancelled" -> setState(Sale.SaleState.Cancelled);
            case "pendingpayment" -> setState(Sale.SaleState.PendingPayment);
            case "partialpayment" -> setState(Sale.SaleState.PartialPayment);
            case "payed" -> setState(Sale.SaleState.Payed);
            case "payedandbilled" -> setState(Sale.SaleState.PayedAndBilled);
            default -> { return false; }
        }

        return true;
    }

    public void calculateAndSetTotalPrice() {
        List<SaleLine> lines = getSaleLines();
        if (lines.isEmpty()) this.setTotalPrice(0f);

        float total = 0;
        for (SaleLine line : lines) {
            total += line.getSubtotal();
        }

        this.setTotalPrice(total);
    }

    public void calculateAndSetTotalPrice(List<SaleLine> lines) {
        if (lines.isEmpty()) this.setTotalPrice(0f);

        float total = 0;
        for (SaleLine line : lines) {
            total += line.getSubtotal();
        }

        this.setTotalPrice(total);
    }
}
