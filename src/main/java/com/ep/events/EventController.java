package com.ep.events;

import com.ep.commons.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventUpdateFormValidator eventUpdateFormValidator;

    @GetMapping()
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
       var pagedResources = assembler.toModel(page,e -> new EventResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok().body(pagedResources);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id){
        Optional<Event> result = eventRepository.findById(id);
        if(result.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = result.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok().header("Content-Type",MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8").body(eventResource);
    }

    @PutMapping("/update")
    public ResponseEntity updateEvent(@RequestBody @Valid EventUpdateForm eventUpdateForm, Errors errors){
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        eventUpdateFormValidator.validate(eventUpdateForm,errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        Optional<Event> result = eventRepository.findById(eventUpdateForm.getId());
        if(result.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event newEvent = result.get();

        modelMapper.map(eventUpdateForm,newEvent);
        var selfLinkBuilder = linkTo(methodOn(EventController.class).updateEvent(null,null)).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok().header("Content-Type",MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8").body(eventResource);


    }

    @PostMapping("/create")
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(eventDto,errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto,Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);


        var selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).header("Content-Type",MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8").body(eventResource);
    }

    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
