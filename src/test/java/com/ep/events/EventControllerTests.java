package com.ep.events;

import com.ep.accounts.Account;
import com.ep.accounts.AccountRepository;
import com.ep.accounts.AccountRole;
import com.ep.accounts.AccountService;
import com.ep.common.BaseControllerTest;
import com.ep.common.RestDocsConfiguration;
import com.ep.commons.AppProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.apache.tomcat.Jar;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EventControllerTests  extends BaseControllerTest {

    @Autowired EventRepository eventRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired AppProperties appProperties;

    @DisplayName("????????? ?????? ?????????")
    @Test
    void createEvent_correct() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();
                // ???????????? ?????? ??????, ???????????? ?????? ???
                //.free(true)
                //.offline(false)
                //.eventStatus(EventStatus.PUBLISHED)

        mockMvc.perform(post("/api/events/create")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Content-Type","application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("create-event").description("link to update an existing"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event"),
                                fieldWithPath("free").description("it tells is if this event is free or not"),
                                fieldWithPath("offline").description("it tells is if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing evnets"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
                ;
    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception{

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                .param("username",appProperties.getUserUsername())
                .param("password",appProperties.getUserPassword())
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(responseBody).get("access_token").toString();
    }

    @DisplayName("????????? ?????? ????????? - Event.update()")
    @Test
    void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/create")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @DisplayName("????????? ?????? ????????? - ?????? ?????? ????????? ??????")
    @Test
    void createEvent_error() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                // ???????????? ?????? ??????, ???????????? ?????? ???
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        mockMvc.perform(post("/api/events/create")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("????????? ?????? - ????????? empty ??????")
    @Test
    void createEvent_Bad_Request_empty_input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("dsada")
                .build();

        mockMvc.perform(post("/api/events/create")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());


    }

    @DisplayName("????????? ?????? ????????? - ?????? ?????? ????????? ?????? ??????")
    @Test
    void createEvent_error_date() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(-1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(1))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();


        mockMvc.perform(post("/api/events/create")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @DisplayName("30?????? ????????? 10?????? ????????? ????????? ??????")
    @Test
    void getEvents() throws Exception{
        // Given
        // ????????? 30?????? ????????? ??????.
        IntStream.range(0,30).forEach((i)->{
            this.generateEvent(i);
        });

        mockMvc.perform(get("/api/events")
                    .param("page","1")
                    .param("size","10")
                    .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @DisplayName("30?????? ????????? 10?????? ????????? ????????? ?????? - ???????????? ?????????")
    @Test
    void getEventsWithAuthentication() throws Exception{
        // Given
        // ????????? 30?????? ????????? ??????.
        IntStream.range(0,30).forEach((i)->{
            this.generateEvent(i);
        });

        mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page","1")
                .param("size","10")
                .param("sort","name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    @DisplayName("????????? ??????")
    @Test
    void getEvent() throws Exception{
        // given
        Event event = generateEvent(100);

        // when & then
        mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @DisplayName("????????? ?????? - ?????? ?????????")
    @Test
    void getEvent_error() throws Exception{
        // given
        Event event = generateEvent(100);

        // when & then
        mockMvc.perform(get("/api/event/1333"))
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName(("????????? ?????? - ??????"))
    @Test
    void updateEvent() throws Exception{
        Event event = generateEvent(1);
        EventUpdateForm eventUpdateForm = EventUpdateForm.builder()
                .id(event.getId())
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();

        mockMvc.perform(put("/api/events/update")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventUpdateForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
//                .andExpect(header().exists("Location"))
                .andExpect(header().string("Content-Type","application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(eventUpdateForm.getId()))
                .andExpect(jsonPath("name").value("Spring"))
                .andExpect(jsonPath("description").value("REST API Development with Spring"))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;





    }

    @DisplayName(("????????? ?????? - ???????????? ?????? ??????"))
    @Test
    void updateEvent_error_not_found() throws Exception{
        Event event = generateEvent(1);
        EventUpdateForm eventUpdateForm = EventUpdateForm.builder()
                .id(20L)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();

        mockMvc.perform(put("/api/events/update")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventUpdateForm)))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @DisplayName(("????????? ?????? - ????????? empty"))
    @Test
    void updateEvent_error_input_empty() throws Exception{
        Event event = generateEvent(1);
        EventUpdateForm eventUpdateForm = EventUpdateForm.builder()
                .build();

        mockMvc.perform(put("/api/events/update")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventUpdateForm)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                //.andExpect(jsonPath("content[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;

    }

    @DisplayName(("????????? ?????? - ????????? ??????"))
    @Test
    void updateEvent_error_input_data() throws Exception{
        Event event = generateEvent(1);
        EventUpdateForm eventUpdateForm = EventUpdateForm.builder()
                .id(5L)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.now())
                .closeEnrollmentDateTime(LocalDateTime.now().plusDays(-1))
                .beginEventDateTime(LocalDateTime.now().plusDays(2))
                .endEventDateTime(LocalDateTime.now().plusDays(-3))
                .basePrice(-1)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ???????????? ?????????")
                .build();

        mockMvc.perform(put("/api/events/update")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventUpdateForm)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;

    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .eventStatus(EventStatus.DRAFT)
                .build();

        return eventRepository.save(event);
    }

}