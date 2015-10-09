package com.lv.springboot.model;

import alexh.weak.Dynamic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.lv.springboot.resources.UserResource;
import org.joda.time.DateTime;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class User extends ResourceSupport {

    private final Dynamic data;

    public User(Map data) {
        this.data = Dynamic.from(data);
    }

    @JsonIgnore
    public BigDecimal getDatabaseId() {
        return data.get("id").convert().intoDecimal();
    }

    public String getFirstname() {
        return data.get("firstname").asString();
    }

    public Date getCreated() {
        return DateTime.now().toDate();
    }

    public String getLastname() {
        return data.get("lastname").asString();
    }

    public Optional<String> getMiddlename() {
        return data.get("middlename").maybe().asString();
    }

    @JsonProperty("_links")
    public Map links() {
        return ImmutableMap.of("self", linkTo(UserResource.class).slash(getDatabaseId()).withSelfRel());
    }
}
