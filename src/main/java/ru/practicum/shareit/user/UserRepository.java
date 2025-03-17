package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(*) from User u " +
            "where u.email = :email")
    int getUserCountByEmail(@Param("email") String email);

    boolean findByEmailIgnoreCase(@Email String email);
}