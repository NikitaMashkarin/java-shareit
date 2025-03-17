package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Getter;
import ru.practicum.shareit.user.UserEntity;

@Entity
@Getter
@Table(name = "requests")
public class ItemRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private UserEntity requestor;
}
