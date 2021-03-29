package com.ep.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @DisplayName("빌더 테스트")
    @Test
    void builder(){
        Event event = Event.builder()
                .name("Keesun Spring REST API")
                .description("REST API developer with Spring")
                .build();

        System.out.println("event : "+event);
        assertThat(event).isNotNull();
    }

    @DisplayName("자바 빈 스펙 준수 여부")
    @Test
    void javaBeanTest(){
        // 기본 생성자가 있어야 한다.
        // @Getter @Setter @NoArgsConstructor @AllArgsConstructor

        // given
        Event event = new Event();
        String name = "Event";

        // when
        event.setName(name);
        String description = "Spring";
        event.setDescription(description);

        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }


}