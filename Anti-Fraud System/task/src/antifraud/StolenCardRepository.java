package antifraud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {

    boolean existsByNumber(String number);

    void deleteByNumber(String number);
}
