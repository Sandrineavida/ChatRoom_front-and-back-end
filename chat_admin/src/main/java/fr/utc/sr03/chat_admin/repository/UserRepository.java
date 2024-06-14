package fr.utc.sr03.chat_admin.repository;

import fr.utc.sr03.chat_admin.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//public interface UserRepository extends JpaRepository<User, Long> {
//}

import fr.utc.sr03.chat_admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    // Requete generee automatiquement par Spring
    User findByMailAndPassword(@Param("mail") String mail, @Param("password") String password);
    Page<User> findByMailContainingIgnoreCase(String mail, Pageable pageable);

    User findByMail(@Param("mail") String mail);

    boolean existsByMail(String mail);
    void deleteByMail(String mail);

    // Requete creee manuellement
    @Query("SELECT u FROM User u WHERE LENGTH(u.lastName) >= :lastNameLength")
    List<User> findByLastNameLength(@Param("lastNameLength") int lastNameLength);
}
