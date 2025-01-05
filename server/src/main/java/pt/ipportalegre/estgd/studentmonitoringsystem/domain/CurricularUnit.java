package pt.ipportalegre.estgd.studentmonitoringsystem.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "curricular_units")
public class CurricularUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private MyUser teacher;

    @ManyToMany(mappedBy = "curricularUnits")
    private Set<MyUser> students;

    @OneToMany(mappedBy = "curricularUnit")
    private Set<ClassSession> classes;
}
