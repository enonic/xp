package com.enonic.xp.admin.impl.tool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;

public abstract class AdminToolDescriptorBuilderMapper
{
    @JsonCreator
    public static AdminToolDescriptor.Builder create()
    {
        return AdminToolDescriptor.create();
    }

    @JsonProperty("displayName")
    public abstract AdminToolDescriptor.Builder displayName( LocalizedText text );

    @JsonProperty("description")
    public abstract AdminToolDescriptor.Builder description( LocalizedText text );

    @JsonProperty("allow")
    public abstract AdminToolDescriptor.Builder addAllowedPrincipals( PrincipalKeys allowedPrincipals );

    @JsonProperty("apis")
    public abstract AdminToolDescriptor.Builder apiMounts( DescriptorKeys apiDescriptors );

    @JsonProperty("interfaces")
    public abstract AdminToolDescriptor.Builder addInterfaces( Iterable<String> interfaceNames );
}
