package net.trenkmann.dev.infrastructure.writer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.trenkmann.dev.model.target2.Target2Entity;
import net.trenkmann.dev.repository.target2.Target2Repository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class Target2ItemWriter implements ItemWriter<Target2Entity> {

  @Autowired private Target2Repository target2Repository;

  @Override
  public void write(List<? extends Target2Entity> items) throws Exception {

    if (target2Repository == null) {
      log.error("Target repository is null, no data can be written");
      throw new NullPointerException();
    }
    items.forEach(
        target ->
          target2Repository.saveAndFlush(target)
        );
  }
}
