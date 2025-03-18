package medical_clinics.user_account.repository;

import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail ( String email );

    boolean existsByRole ( Role role );
}
