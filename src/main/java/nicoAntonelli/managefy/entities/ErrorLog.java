package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "errorLogs")
@Data @NoArgsConstructor @AllArgsConstructor
public class ErrorLog {
    @Id
    @SequenceGenerator(name = "errorLogs_sequence", sequenceName = "errorLogs_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "errorLogs_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private Date date;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String origin;
    private String browser; // Nullable
    private Date userIPAddress; // Nullable

    public ErrorLog(Date date, String description, String origin) {
        this.date = date;
        this.description = description;
        this.origin = origin;
    }

    public ErrorLog(Date date, String description, String origin, String browser, Date userIPAddress) {
        this.date = date;
        this.description = description;
        this.origin = origin;
        this.browser = browser;
        this.userIPAddress = userIPAddress;
    }
}