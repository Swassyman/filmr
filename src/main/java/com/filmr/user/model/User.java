package com.filmr.user.model;

import com.filmr.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends AuditableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, updatable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passHash;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassHash() {
    return passHash;
  }

  public void setPassHash(String passHash) {
    this.passHash = passHash;
  }
}
