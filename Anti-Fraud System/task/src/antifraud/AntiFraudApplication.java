package antifraud;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AntiFraudApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntiFraudApplication.class, args);
    }

    @Component
    public class Runner implements CommandLineRunner {
        private final AmountLimitRepository amountLimitRepository;

        public Runner(AmountLimitRepository repository) {
            this.amountLimitRepository = repository;
        }

        @Override
        public void run(String... args) {
            if (amountLimitRepository.count() == 0) {
                AmountLimit amountLimit = new AmountLimit(1);
                amountLimitRepository.save(amountLimit);
            }
        }
    }
}

