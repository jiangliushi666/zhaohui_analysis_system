package com.xm.zhaohui.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_preferred_tags",joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private Set<String> preferredTags;
}
