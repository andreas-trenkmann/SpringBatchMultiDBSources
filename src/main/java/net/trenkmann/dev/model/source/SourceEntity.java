package net.trenkmann.dev.model.source;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import net.trenkmann.dev.model.jpa.converter.LocalDateTimeConverter;

@Entity
@Table(name = "source_table")
@Access(AccessType.PROPERTY)
public class SourceEntity implements Serializable {

  private Integer id;
  private String text;
  private int var1;
  private LocalDateTime creationDate;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "int(10) unsigned auto_increment")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Column(name = "text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Column(name = "var1")
  public int getVar1() {
    return var1;
  }

  public void setVar1(int var1) {
    this.var1 = var1;
  }

  @Column(name = "create_date")
  @Convert(converter = LocalDateTimeConverter.class)
  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }
}
