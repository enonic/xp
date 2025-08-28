package com.enonic.xp.core.impl.schema;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeMapper;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeNameMapper;
import com.enonic.xp.core.impl.schema.mapper.FieldSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FormItemDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FormItemSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetOptionMapper;
import com.enonic.xp.core.impl.schema.mapper.InlineMixinMapper;
import com.enonic.xp.core.impl.schema.mapper.MixinNameMapper;
import com.enonic.xp.core.impl.schema.mapper.OccurrencesMapper;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

public final class YmlTypeParser
{
    private static final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

    static
    {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer( Form.class, new FormDeserializer() );
        module.addDeserializer( FormItem.class, new FormItemDeserializer() );
        MAPPER.registerModule( module );

        MAPPER.addMixIn( ContentTypeName.class, ContentTypeNameMapper.class );
        MAPPER.addMixIn( ContentType.Builder.class, ContentTypeMapper.Builder.class );

        MAPPER.addMixIn( Occurrences.class, OccurrencesMapper.class );

        MAPPER.addMixIn( FieldSet.class, FieldSetMapper.class );
        MAPPER.addMixIn( FieldSet.Builder.class, FieldSetMapper.Builder.class );

        MAPPER.addMixIn( InlineMixin.class, InlineMixinMapper.class );
        MAPPER.addMixIn( InlineMixin.Builder.class, InlineMixinMapper.Builder.class );
        MAPPER.addMixIn( MixinName.class, MixinNameMapper.class );

        MAPPER.addMixIn( FormItemSet.class, FormItemSetMapper.class );
        MAPPER.addMixIn( FormItemSet.Builder.class, FormItemSetMapper.Builder.class );

        MAPPER.addMixIn( FormOptionSet.class, FormOptionSetMapper.class );
        MAPPER.addMixIn( FormOptionSet.Builder.class, FormOptionSetMapper.Builder.class );

        MAPPER.addMixIn( FormOptionSetOption.class, FormOptionSetOptionMapper.class );
        MAPPER.addMixIn( FormOptionSetOption.Builder.class, FormOptionSetOptionMapper.Builder.class );
    }

    public <T> T parse( final String yml, final Class<T> clazz )
    {
        return parse( yml, clazz, null );
    }

    public <T> T parse( final String yml, final Class<T> clazz, final InjectableValues injectableValues )
    {
        try
        {
            final ObjectMapper localMapper = MAPPER.copy();
            localMapper.setInjectableValues( injectableValues );
            return localMapper.readValue( yml, clazz );
        }
        catch ( final JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public ContentType.Builder parseContentType( final String yml, final ApplicationKey currentApplication )
    {
        final ApplicationRelativeResolver applicationRelativeResolver = new ApplicationRelativeResolver( currentApplication );

        final ContentType.Builder builder = parse( yml, ContentType.Builder.class,
                                                   new InjectableValues.Std().addValue( "applicationRelativeResolver",
                                                                                        applicationRelativeResolver ) );
        builder.name( "_TEMP_NAME_" );

        final ContentType contentType = builder.build();

        return ContentType.create( contentType )
            .superType( applicationRelativeResolver.toContentTypeName( contentType.getSuperType().toString() ) );
    }
}
