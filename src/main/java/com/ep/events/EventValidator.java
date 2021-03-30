package com.ep.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {

    @Override
    public void validate(Object dto, Errors errors){
        EventDto eventDto = (EventDto) dto;

        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            // 필드 에러
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong");
            errors.rejectValue("maxPrice","wrongValue","MaxPrice is wrong");

            // 글로벌 에러
            errors.reject("wrongPrices","Values of prices are wrong");
        }
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime())||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong");
        }

        // TODO beginEventDateTime
        // TODO closeEnrollmentDateTime

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return EventDto.class.isAssignableFrom(clazz);
    }
}
