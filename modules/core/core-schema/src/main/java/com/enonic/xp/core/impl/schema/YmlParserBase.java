package com.enonic.xp.core.impl.schema;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.mapper.DescriptorKeyDeserializer;
import com.enonic.xp.core.impl.schema.mapper.DescriptorKeysDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FieldSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FormItemDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FormItemSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetOptionMapper;
import com.enonic.xp.core.impl.schema.mapper.InlineMixinMapper;
import com.enonic.xp.core.impl.schema.mapper.MixinNameMapper;
import com.enonic.xp.core.impl.schema.mapper.OccurrencesMapper;
import com.enonic.xp.core.impl.schema.mapper.PrincipalKeyMapper;
import com.enonic.xp.core.impl.schema.mapper.PrincipalKeysDeserializer;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class YmlParserBase
{
    private final ObjectMapper mapper = new ObjectMapper( new YAMLFactory() );

    public YmlParserBase()
    {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer( Form.class, new FormDeserializer() );
        module.addDeserializer( FormItem.class, new FormItemDeserializer() );
        module.addDeserializer( PrincipalKeys.class, new PrincipalKeysDeserializer() );
        module.addDeserializer( DescriptorKeys.class, new DescriptorKeysDeserializer() );
        module.addDeserializer( DescriptorKey.class, new DescriptorKeyDeserializer() );

        mapper.registerModule( module );

        mapper.addMixIn( PrincipalKey.class, PrincipalKeyMapper.class );
        mapper.addMixIn( Occurrences.class, OccurrencesMapper.class );

        mapper.addMixIn( FieldSet.class, FieldSetMapper.class );
        mapper.addMixIn( FieldSet.Builder.class, FieldSetMapper.Builder.class );

        mapper.addMixIn( InlineMixin.class, InlineMixinMapper.class );
        mapper.addMixIn( InlineMixin.Builder.class, InlineMixinMapper.Builder.class );
        mapper.addMixIn( MixinName.class, MixinNameMapper.class );

        mapper.addMixIn( FormItemSet.class, FormItemSetMapper.class );
        mapper.addMixIn( FormItemSet.Builder.class, FormItemSetMapper.Builder.class );

        mapper.addMixIn( FormOptionSet.class, FormOptionSetMapper.class );
        mapper.addMixIn( FormOptionSet.Builder.class, FormOptionSetMapper.Builder.class );

        mapper.addMixIn( FormOptionSetOption.class, FormOptionSetOptionMapper.class );
        mapper.addMixIn( FormOptionSetOption.Builder.class, FormOptionSetOptionMapper.Builder.class );
    }

    public void addMixIn( final Class<?> target, final Class<?> mixinSource )
    {
        mapper.addMixIn( target, mixinSource );
    }

    public <T> T parse( final String resource, final Class<T> clazz, final ApplicationKey currentApplication )
    {
        try
        {
            final ObjectMapper localMapper = mapper.copy();
            localMapper.setInjectableValues( new InjectableValues.Std().addValue( "currentApplication", currentApplication ) );
            return localMapper.readValue( resource, clazz );
        }
        catch ( final JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
