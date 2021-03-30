package com.ep.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventUpdateFormValidator  implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return EventUpdateForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object eventForm, Errors errors) {
        EventUpdateForm eventUpdateForm = (EventUpdateForm) eventForm;

        if(eventUpdateForm.getBasePrice() > eventUpdateForm.getMaxPrice() && eventUpdateForm.getMaxPrice() > 0){
            // 필드 에러
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong");
            errors.rejectValue("maxPrice","wrongValue","MaxPrice is wrong");

            // 글로벌 에러
            errors.reject("wrongPrices","Values of prices are wrong");
        }
        LocalDateTime endEventDateTime = eventUpdateForm.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventUpdateForm.getBeginEventDateTime())||
                endEventDateTime.isBefore(eventUpdateForm.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventUpdateForm.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong");
        }

        // TODO beginEventDateTime
        // TODO closeEnrollmentDateTime


    }
}
