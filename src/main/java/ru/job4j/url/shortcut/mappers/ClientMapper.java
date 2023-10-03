package ru.job4j.url.shortcut.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.dto.CredentialsDto;
import ru.job4j.url.shortcut.model.dto.RegistrationDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "site", source = "site")
    Client clientFromRegistrationDto(RegistrationDto registrationDto);

    @Mapping(target = "login", source = "login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "registration", source = "registration")
    CredentialsDto clientToCredentialsDto(Client client);
}
