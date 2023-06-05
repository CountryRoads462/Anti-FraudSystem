package antifraud;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "stolen_cards")
public class StolenCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "stolen_card_id")
    private long id;

    @NotNull
    @Column(name = "stolen_card_number")
    private String number;

    public StolenCard() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
