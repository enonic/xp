package com.enonic.xp.core.impl.idprovider;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

class YmlIdProviderDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( IdProviderDescriptorMode.class, IdProviderDescriptorModeMapper.class );
        PARSER.addMixIn( IdProviderDescriptor.Builder.class, IdProviderDescriptorBuilderMapper.class );
    }

    public static IdProviderDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, IdProviderDescriptor.Builder.class, currentApplication );
    }

    private abstract static class IdProviderDescriptorBuilderMapper
    {
        @JsonProperty("mode")
        public abstract IdProviderDescriptor.Builder mode( IdProviderDescriptorMode mode );

        @JsonProperty("form")
        public abstract IdProviderDescriptor.Builder config( Form config );

        @JacksonInject("currentApplication")
        public abstract IdProviderDescriptor.Builder key( ApplicationKey key );
    }

    private static class IdProviderDescriptorModeMapper
    {
        @JsonCreator
        public static IdProviderDescriptorMode map( String value )
        {
            return IdProviderDescriptorMode.valueOf( value );
        }
    }
}
