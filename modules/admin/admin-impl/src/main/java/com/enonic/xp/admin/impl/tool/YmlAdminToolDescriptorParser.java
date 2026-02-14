package com.enonic.xp.admin.impl.tool;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;

public final class YmlAdminToolDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( AdminToolDescriptor.Builder.class, AdminToolDescriptorBuilderMapper.class );
    }

    public static AdminToolDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, AdminToolDescriptor.Builder.class, currentApplication );
    }

    private abstract static class AdminToolDescriptorBuilderMapper
    {
        @JsonProperty("displayName")
        public abstract AdminToolDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        public abstract AdminToolDescriptor.Builder description( LocalizedText text );

        @JsonProperty("allow")
        public abstract AdminToolDescriptor.Builder addAllowedPrincipals( PrincipalKeys allowedPrincipals );

        @JsonProperty("apis")
        public abstract AdminToolDescriptor.Builder apiMounts( DescriptorKeys apiDescriptors );

        @JsonProperty("interfaces")
        public abstract AdminToolDescriptor.Builder interfaces( String... interfaces );
    }
}
