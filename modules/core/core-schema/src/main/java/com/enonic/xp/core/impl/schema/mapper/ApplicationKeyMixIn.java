package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.app.ApplicationKey;

public abstract class ApplicationKeyMixIn
{
    @JsonCreator
    static ApplicationKey from( String value )
    {
        return ApplicationKey.from( value );
    }
}
