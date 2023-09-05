package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.service.MasterThesisService;

@Service
public class MasterThesisServiceImpl implements MasterThesisService {

    @Override
    public MasterThesis save(String student, String title, String area, String description, String mentor, String firstMember, String secondMember) {
        return null;
    }
}
