package pt.ipportalegre.estgd.studentmonitoringsystem.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ParticipationCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
