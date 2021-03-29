package com.ep.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    @ParameterizedTest
    @MethodSource("freeTestParametersProvider")
    void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    static Stream<Arguments> freeTestParametersProvider(){
        return Stream.of(
                arguments(0,0,true),
                arguments(100,0,false),
                arguments(0,100,false)
        );
    }

    @ParameterizedTest
    @MethodSource("offlineTestParametersProvider")
    void testOffline(String location, boolean isOffline){
        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    static Stream<Arguments> offlineTestParametersProvider(){
        return Stream.of(
                arguments("강남역",true),
                arguments("",false),
                arguments(null,false)
        );
    }
}