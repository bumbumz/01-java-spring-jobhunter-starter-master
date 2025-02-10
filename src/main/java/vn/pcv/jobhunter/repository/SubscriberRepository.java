package vn.pcv.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.pcv.jobhunter.domain.Subscriber;
import java.util.List;
import java.util.Optional;


@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>,
        JpaSpecificationExecutor<Subscriber> {
     Optional<Subscriber> findByEmail(String  email);
}
