package net.trenkmann.dev.infrastructure.writer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.trenkmann.dev.model.target1.Target1Entity;
import net.trenkmann.dev.repository.target1.Target1Repository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class Target1ItemWriter implements ItemWriter<Target1Entity> {

  @Autowired private Target1Repository target1Repository;

  @Override
  public void write(List<? extends Target1Entity> items) throws Exception {

    if (target1Repository == null) {
      log.error("Target repository is null, no data can be written");
      throw new NullPointerException();
    }
    items.forEach(
        target ->  target1Repository.saveAndFlush(target)
    );
  }
}
