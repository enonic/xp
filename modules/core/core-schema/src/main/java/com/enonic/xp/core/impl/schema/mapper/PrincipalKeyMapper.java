package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.security.PrincipalKey;

import static com.google.common.base.Strings.nullToEmpty;

public class PrincipalKeyMapper
{
    @JsonCreator
    public static PrincipalKey from( String value )
    {
        return PrincipalKey.from( nullToEmpty( value ).trim() );
    }
}
