package com.customers.oriontek.application.mapper;

import com.customers.oriontek.application.command.AssignLocationToCustomerCommand;
import com.customers.oriontek.application.dto.LocationResponse;
import com.customers.oriontek.domain.entity.CustomerLocation;
import com.customers.oriontek.domain.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "customerLocations", ignore = true)
    Location toEntity(AssignLocationToCustomerCommand command);

    @Mapping(target = "id", source = "location.id")
    @Mapping(target = "streetAddress", source = "location.streetAddress")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "stateProvince", source = "location.stateProvince")
    @Mapping(target = "country", source = "location.country")
    @Mapping(target = "postalCode", source = "location.postalCode")
    @Mapping(target = "isPrimary", source = "primary")
    @Mapping(target = "assignedAt", source = "assignedAt")
    LocationResponse toResponse(CustomerLocation customerLocation);
}
