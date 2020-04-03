package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Locale;

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
        this.language = Locale.forLanguageTag( language );
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
