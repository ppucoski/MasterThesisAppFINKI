package vp.magisterski.web;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.service.MasterThesisDocumentService;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.MasterThesisStatusChangeService;
import vp.magisterski.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(  "/masterThesis")
public class MasterThesisController {
    private final UserService userService;
    private final MasterThesisService masterThesisService;
    private final MasterThesisDocumentService masterThesisDocumentService;
    private final MasterThesisStatusChangeService masterThesisStatusChangeService;

    public MasterThesisController(UserService userService, MasterThesisService masterThesisService, MasterThesisDocumentService masterThesisDocumentService, MasterThesisStatusChangeService masterThesisStatusChangeService) {
        this.userService = userService;
        this.masterThesisService = masterThesisService;
        this.masterThesisDocumentService = masterThesisDocumentService;
        this.masterThesisStatusChangeService = masterThesisStatusChangeService;
    }


    @ModelAttribute
    public void trackUsername(Model model){
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }


    @GetMapping("/masterThesisInfo")
    public String getMasterThesisInfo(Model model){
        List<MasterThesis> thesis = masterThesisService.findByStudentIndex("201163");
        model.addAttribute("thesis", thesis);
        return "masterThesisInfo";
    }

    @GetMapping("/details/{thesisId}")
    public String details(Model model, @PathVariable Long thesisId){
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
        List<MasterThesisDocument> associatedDocuments = masterThesisDocumentService.findAllByThesis(masterThesis);
        Optional<MasterThesisStatusChange> masterThesisStatusChange = masterThesisStatusChangeService.getStatusChange(masterThesis);
        model.addAttribute("thesis", masterThesis);
        model.addAttribute("masterThesisStatusChange", masterThesisStatusChange);
        model.addAttribute("associatedDocuments", associatedDocuments);
        return "masterThesisDetails";
    }

//    @GetMapping("/download/{thesisId}")
//    public ResponseEntity<ByteArrayResource> downloadThesis(@PathVariable Long thesisId) {
//        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", "MasterThesis.pdf");
//        ByteArrayResource resource = new ByteArrayResource(masterThesis.getThesisText());
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(masterThesis.getThesisText().length)
//                .body(resource);
//    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadAssociatedDocument(@PathVariable Long documentId) {
        MasterThesisDocument document = masterThesisDocumentService.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "document_" + documentId + ".pdf");

        ByteArrayResource resource = new ByteArrayResource(document.getDocument());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(document.getDocument().length)
                .body(resource);
    }
}
