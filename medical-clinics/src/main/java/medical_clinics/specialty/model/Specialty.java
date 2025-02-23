package medical_clinics.specialty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import medical_clinics.physician.model.Physician;

import java.util.*;

@Entity
public class Specialty {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private SpecialtyName name;

    @OneToMany(mappedBy = "specialty", targetEntity = Physician.class)
    private Collection<Physician> specialists;

    public Specialty() {
        specialists = new ArrayList<> ();
    }

    public Specialty(SpecialtyName name) {
        this.name = name;
        specialists = new ArrayList<> ();
    }

    public Collection<Physician> getSpecialists () {
        return Collections.unmodifiableCollection (specialists);
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
