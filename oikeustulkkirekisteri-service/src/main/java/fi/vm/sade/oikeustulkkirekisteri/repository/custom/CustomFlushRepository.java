package fi.vm.sade.oikeustulkkirekisteri.repository.custom;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 19.17
 */
@Repository
public class CustomFlushRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    public void flush() {
        entityManager.flush();
    }
}
