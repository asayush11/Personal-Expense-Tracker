package com.personal.expensetracker.users;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserBasedRepository<T, ID> {
    List<T> findAllByUser_Email(String email);
    Optional<T> findByIdAndUser_Email(ID id, String email);
    void deleteByIdAndUser_Email(ID id, String email);
    boolean existsByIdAndUser_Email(ID id, String email);

}
