package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private UserEntity booker;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}