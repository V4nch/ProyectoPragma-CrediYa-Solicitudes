package co.com.pragma.powerup.r2dbc;


import co.com.pragma.powerup.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity,
        Long>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
}
