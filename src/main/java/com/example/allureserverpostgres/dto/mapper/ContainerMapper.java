package com.example.allureserverpostgres.dto.mapper;

import com.example.allureserverpostgres.dto.ContainerDto;
import com.example.allureserverpostgres.persistence.entity.Container;
import com.example.allureserverpostgres.persistence.entity.Result;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContainerMapper {
    public static Container fromDto(ContainerDto containerDto) {
        Container container = Container.builder()
                .name(containerDto.getName())
                .start(containerDto.getStart())
                .stop(containerDto.getStop())
                .build();
        container.setUuid(UUID.fromString(containerDto.getUuid()));
        return container;
    }

    public static ContainerDto toDto(Container container) {
        return ContainerDto.builder()
                .uuid(container.getUuid().toString())
                .name(container.getName())
                .children(container.getResults()
                        .stream()
                        .map(Result::getUuidString)
                        .collect(Collectors.toList()))
                .befores(new ArrayList<>())
                .afters(new ArrayList<>())
                .start(container.getStart())
                .stop(container.getStop())
                .build();
    }
}
