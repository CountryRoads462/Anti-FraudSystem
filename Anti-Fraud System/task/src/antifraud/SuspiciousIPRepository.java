package antifraud;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SuspiciousIPRepository extends CrudRepository<SuspiciousIP, Long> {

    boolean existsByIp(String ip);

    Optional<SuspiciousIP> findByIp(String ip);

    void deleteByIp(String ip);
}
