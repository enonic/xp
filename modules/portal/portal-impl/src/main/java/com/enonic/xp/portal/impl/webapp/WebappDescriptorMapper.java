package com.enonic.xp.portal.impl.webapp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.webapp.WebappDescriptor;

abstract class WebappDescriptorMapper
{
    @JsonCreator
    static WebappDescriptor.Builder create()
    {
        return WebappDescriptor.create();
    }

    @JsonProperty("apis")
    abstract WebappDescriptor.Builder apiMounts( DescriptorKeys apiMounts );
}
