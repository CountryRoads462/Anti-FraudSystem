package antifraud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmountLimitRepository extends CrudRepository<AmountLimit, Long> {
}
