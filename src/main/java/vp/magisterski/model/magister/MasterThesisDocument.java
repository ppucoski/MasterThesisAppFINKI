package vp.magisterski.model.magister;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
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

    public MasterThesisDocument(MasterThesis thesis, MasterThesisDocumentType type, byte[] document, LocalDate createdDate) {
        this.thesis = thesis;
        this.type = type;
        this.document = document;
        this.createdDate = createdDate;
    }

}
