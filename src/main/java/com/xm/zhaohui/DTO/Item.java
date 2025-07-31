package com.xm.zhaohui.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(name="items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_tags",joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "tag")
    private Set<String> tags;
}
