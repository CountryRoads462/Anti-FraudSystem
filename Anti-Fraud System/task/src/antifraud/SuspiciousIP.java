package antifraud;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "suspicious_ip_addresses")
public class SuspiciousIP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "suspicious_ip_id")
    private long id;

    @NotNull
    @Column(name = "suspicious_ip_ip")
    private String ip;

    public SuspiciousIP() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
