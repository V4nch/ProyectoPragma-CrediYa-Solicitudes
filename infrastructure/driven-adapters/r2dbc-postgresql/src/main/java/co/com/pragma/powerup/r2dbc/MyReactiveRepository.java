package co.com.pragma.powerup.r2dbc;

import co.com.pragma.powerup.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface MyReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity,
        Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

}
