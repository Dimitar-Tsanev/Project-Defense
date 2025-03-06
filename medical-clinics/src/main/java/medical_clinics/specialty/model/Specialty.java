package medical_clinics.specialty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@NoArgsConstructor

@Entity
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private SpecialtyName name;

    public Specialty(SpecialtyName name) {
        this.name = name;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( o == null || getClass ( ) != o.getClass ( ) ) return false;

        Specialty specialty = (Specialty) o;
        return id.equals ( specialty.id ) && name == specialty.name;
    }

    @Override
    public int hashCode () {
        int result = id.hashCode ( );
        result = 31 * result + name.hashCode ( );
        return result;
    }
}
