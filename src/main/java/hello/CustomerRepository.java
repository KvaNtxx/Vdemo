package hello;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);

    List<Customer> findByFirstNameStartsWithIgnoreCase(String firstName);

    List<Customer> findByFirstNameIgnoreCaseContaining(String firstName);
}