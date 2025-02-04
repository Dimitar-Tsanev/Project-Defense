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
import java.util.Collections;
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

    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinTable(
            name = "clinics_work_days",
            joinColumns = @JoinColumn(name = "clinic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_day_id", referencedColumnName = "id")
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
    @JoinTable(
            name = "clinics_specialties",
            joinColumns = @JoinColumn(name = "clinic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    )
    private Collection<Specialty> specialtyList;

    @OneToMany(mappedBy = "workplace", targetEntity = Physician.class, fetch = FetchType.EAGER)
    private Collection<Physician> physicians;

    public Collection<Specialty> getSpecialtyList () {
        if (specialtyList == null) {
            specialtyList = new ArrayList<>();
        }
        return Collections.unmodifiableCollection (specialtyList);
    }

    public void setSpecialtyList (Collection<Specialty> specialtyList) {
        for (Specialty s : specialtyList) {
            addSpeciality ( s );
        }
    }

    public void addSpeciality( Specialty specialty) {
        if ( specialtyList == null ) {
            specialtyList = new ArrayList<> ();
        }
        specialtyList.add(specialty);
    }

    public void removeSpeciality( Specialty specialty) {
        if ( specialtyList == null ) {
            return;
        }
        specialtyList.remove(specialty);
    }

    public Collection<Physician> getPhysicians() {
        if ( physicians == null ) {
            physicians = new ArrayList<>();
        }
        return Collections.unmodifiableCollection (physicians);
    }

    public void setPhysicians(Collection<Physician> physicians) {
        for (Physician p : physicians) {
            addPhysician ( p );
        }
    }

    public void addPhysician( Physician physician) {
        if ( physicians == null ) {
            physicians = new ArrayList<> ();
        }
        physicians.add(physician);
    }
    public void removePhysician( Physician physician) {
        if ( physicians == null ) {
            return;
        }
        physicians.remove(physician);
    }
}
