package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectName;

public final class ModifyLanguageParamsJson
{
    private final ProjectName name;

    private final Locale language;

    @JsonCreator
    ModifyLanguageParamsJson( @JsonProperty("name") final String name, @JsonProperty("language") final String language )
    {
        this.name = ProjectName.from( name );
        this.language = Optional.ofNullable( language ).
            filter( lang -> !lang.isBlank() ).
            map( Locale::forLanguageTag ).
            orElse( null );
    }

    public ProjectName getName()
    {
        return name;
    }

    public Locale getLanguage()
    {
        return language;
    }
}
