package com.enonic.xp.core.impl.idprovider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Form;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

abstract class IdProviderDescriptorBuilderMapper
{
    @JsonCreator
    public static IdProviderDescriptor.Builder create()
    {
        return IdProviderDescriptor.create();
    }

    @JsonProperty("mode")
    public abstract IdProviderDescriptor.Builder mode( IdProviderDescriptorMode mode );

    @JsonProperty("form")
    public abstract IdProviderDescriptor.Builder config( Form config );
}
