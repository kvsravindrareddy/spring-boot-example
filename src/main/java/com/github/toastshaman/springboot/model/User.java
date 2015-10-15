package com.github.toastshaman.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.github.toastshaman.springboot.resources.UserResource;
import com.github.toastshaman.springboot.util.ModelObject;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class User extends ModelObject {

    public User(Map data) {
        super(data);
    }

    @JsonIgnore
    public BigDecimal getDatabaseId() {
        return  dynamic().get("id").convert().intoDecimal();
    }

    public String getFirstname() {
        return get("firstname", String.class);
    }

    public Date getCreated() {
        return DateTime.now().toDate();
    }

    public String getLastname() {
        return get("lastname", String.class);
    }

    public Optional<String> getMiddlename() {
        return getMaybe("middlename", String.class);
    }

    @JsonProperty("_links")
    public Map links() {
        return ImmutableMap.of("self", linkTo(UserResource.class).slash(getDatabaseId()).withSelfRel());
    }
}
