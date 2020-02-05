package net.trenkmann.dev.repository.target2;

import net.trenkmann.dev.model.target2.Target2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Target2Repository
    extends JpaRepository<Target2Entity, Integer>, JpaSpecificationExecutor<Target2Entity> {

  Target2Entity findFirstByOrderByIdDesc();
}
