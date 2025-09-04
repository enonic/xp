package com.enonic.xp.core.impl.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationDescriptor;

abstract class ApplicationDescriptorBuilderMapper
{
    @JsonCreator
    static ApplicationDescriptor.Builder create()
    {
        return ApplicationDescriptor.create();
    }

    @JsonProperty("description")
    abstract ApplicationDescriptor.Builder description( String description );
}
