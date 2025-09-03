package com.enonic.xp.admin.impl.widget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;

public abstract class WidgetDescriptorBuilderMapper
{
    @JsonCreator
    public static WidgetDescriptor.Builder create()
    {
        return WidgetDescriptor.create();
    }

    @JsonProperty("displayName")
    public abstract WidgetDescriptor.Builder displayName( LocalizedText text );

    @JsonProperty("description")
    public abstract WidgetDescriptor.Builder description( LocalizedText text );

    @JsonProperty("allow")
    public abstract WidgetDescriptor.Builder allowedPrincipals( PrincipalKeys allowedPrincipals );

    @JsonProperty("interfaces")
    public abstract WidgetDescriptor.Builder addInterfaces( Iterable<String> interfaceNames );

    @JsonProperty("config")
    public abstract WidgetDescriptor.Builder addProperty( String key, String value );
}
