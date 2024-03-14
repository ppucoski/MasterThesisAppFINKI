package vp.magisterski.model.magister;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class MasterThesisDocument {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MasterThesis thesis;

    @Enumerated(EnumType.STRING)
    private MasterThesisDocumentType type;

    @Lob
    private byte[] document;


    private LocalDate createdDate;
}
