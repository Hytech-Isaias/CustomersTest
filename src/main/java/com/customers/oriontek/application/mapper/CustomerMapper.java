package com.customers.oriontek.application.mapper;

import com.customers.oriontek.application.command.CreateCustomerCommand;
import com.customers.oriontek.application.dto.CustomerResponse;
import com.customers.oriontek.domain.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { LocationMapper.class })
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "customerLocations", ignore = true)
    Customer toEntity(CreateCustomerCommand command);

    @Mapping(target = "locations", source = "customerLocations")
    CustomerResponse toResponse(Customer customer);
}
