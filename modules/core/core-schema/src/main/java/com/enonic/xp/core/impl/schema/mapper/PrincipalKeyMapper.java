package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.security.PrincipalKey;

public class PrincipalKeyMapper
{
    @JsonCreator
    public static PrincipalKey from( String value )
    {
        return PrincipalKey.from( value );
    }
}
