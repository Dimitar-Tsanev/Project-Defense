package medical_clinics.clinic.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.physician.model.Physician;
import medical_clinics.specialty.model.Specialty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "UniqueCityAndAddress",
        columnNames = {"city", "address"}
))
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @OneToMany(
            mappedBy = "clinic",
            targetEntity = WorkDay.class,
            fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}
    )
    private Collection<WorkDay> workingDays;

    @Basic(optional = false)
    private String description;

    @Basic(optional = false)
    private String phoneNumber;

    @Basic(optional = false)
    private String identificationNumber;

    @Basic(optional = false)
    private String pictureUrl;

    @ManyToMany
    private Collection<Specialty> specialties;

    @OneToMany(mappedBy = "workplace", targetEntity = Physician.class)
    private Collection<Physician> physicians;

    public void addSpeciality( Specialty specialty) {
        if ( specialties == null ) {
            specialties = new ArrayList<> ();
        }
        specialties.add(specialty);
    }
}
