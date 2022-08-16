package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.Errors;
import com.gen.GeneralModule.entities.QErrors;
import com.gen.GeneralModule.repositories.ErrorsRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ErrorsService {
    @Autowired
    ErrorsRepository errorsRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    private static final String filterError = "com.gen.GeneralModule";

    private static final QErrors errors = new QErrors("errors");

    public List<Errors> getAllErrors() {
        return queryFactory.from(errors).select(errors)
                .orderBy(errors.dateTime.desc()).fetch();
    }

    public List<Errors> getArchiveErrors() {
        return queryFactory.from(errors).select(errors)
                .where(errors.verificationError.eq(true))
                .orderBy(errors.dateTime.desc()).fetch();
    }

    public List<Errors> getNotVerifiedErrors() {
        return queryFactory.from(errors).select(errors)
                .where(errors.verificationError.eq(false))
                .orderBy(errors.dateTime.desc()).fetch();
    }

    public void verifyPosition(int id){
        Errors error = errorsRepository.findById(id).orElse(null);
        if(error != null) {
            error.verificationError = true;
        } else {
            log.error("Нет error с таким id");
        }
    }

    public void verifyPosition(List<Errors> errors){
        for(Errors error : errors) {
            error.verificationError = !error.verificationError;
            errorsRepository.saveAndFlush(error);
        }
    }

    public <E> void saveError(E anyException) {
        saveError(anyException, null);
    }

    public <E> void saveError(E anyException, String payload) {
        Exception exception = (Exception) anyException;
        Errors errors = new Errors();
        StackTraceElement[] elements = exception.getStackTrace();
        StackTraceElement element = Arrays.stream(elements).filter(el -> el.getClassName().contains(filterError)).toList().get(0);
        errors.classAndLine = element.getFileName() + ": " + element.getLineNumber();
        errors.descriptionError = exception.getMessage();
        errors.verificationError = false;
        errors.dateTime = new Date();
        errors.payload = payload;
        errorsRepository.save(errors);
    }
}
