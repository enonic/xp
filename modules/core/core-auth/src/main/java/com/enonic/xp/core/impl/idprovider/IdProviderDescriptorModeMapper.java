package com.enonic.xp.core.impl.idprovider;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.idprovider.IdProviderDescriptorMode;

import static com.google.common.base.Strings.nullToEmpty;

class IdProviderDescriptorModeMapper
{
    @JsonCreator
    public static IdProviderDescriptorMode map( String value )
    {
        return IdProviderDescriptorMode.valueOf( nullToEmpty( value ).trim() );
    }
}
