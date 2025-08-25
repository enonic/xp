package com.enonic.xp.core.impl.schema;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.mapper.ContentSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeMapper;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeNameMapper;
import com.enonic.xp.core.impl.schema.mapper.DoubleYml;
import com.enonic.xp.core.impl.schema.mapper.FieldSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormDeserializer;
import com.enonic.xp.core.impl.schema.mapper.FormItemMapper;
import com.enonic.xp.core.impl.schema.mapper.FormItemSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetOptionMapper;
import com.enonic.xp.core.impl.schema.mapper.InlineMixinMapper;
import com.enonic.xp.core.impl.schema.mapper.MixinNameMapper;
import com.enonic.xp.core.impl.schema.mapper.OccurrencesMapper;
import com.enonic.xp.core.impl.schema.mapper.RadioButtonYml;
import com.enonic.xp.core.impl.schema.mapper.TextLineYml;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
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
        MAPPER.registerModule( module );

        MAPPER.addMixIn( ContentTypeName.class, ContentTypeNameMapper.class );
        MAPPER.addMixIn( ContentType.Builder.class, ContentTypeMapper.Builder.class );

        MAPPER.addMixIn( Occurrences.class, OccurrencesMapper.class );

        MAPPER.addMixIn( FormItem.class, FormItemMapper.class );

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

        MAPPER.registerSubtypes( new NamedType( TextLineYml.class, "TextLine" ), new NamedType( RadioButtonYml.class, "RadioButton" ),
                                 new NamedType( DoubleYml.class, "Double" ), new NamedType( Input.class, "TextArea" ),
                                 new NamedType( Input.class, "CheckBox" ), new NamedType( Input.class, "ComboBox" ),
                                 new NamedType( ContentSelectorYml.class, "ContentSelector" ), new NamedType( Input.class, "CustomSelector" ),
                                 new NamedType( Input.class, "ContentTypeFilter" ), new NamedType( Input.class, "Date" ),
                                 new NamedType( Input.class, "DateTime" ), new NamedType( Input.class, "MediaUploader" ),
                                 new NamedType( Input.class, "AttachmentUploader" ), new NamedType( Input.class, "GeoPoint" ),
                                 new NamedType( Input.class, "HtmlArea" ), new NamedType( Input.class, "ImageSelector" ),
                                 new NamedType( Input.class, "MediaSelector" ), new NamedType( Input.class, "ImageUploader" ),
                                 new NamedType( Input.class, "Long" ), new NamedType( Input.class, "Tag" ),
                                 new NamedType( Input.class, "Time" ), new NamedType( FieldSet.class, "FieldSet" ),
                                 new NamedType( InlineMixin.class, "InlineMixin" ), new NamedType( FormItemSet.class, "ItemSet" ),
                                 new NamedType( FormOptionSet.class, "OptionSet" ),
                                 new NamedType( FormOptionSetOption.class, "OptionSetOption" ) );
    }

    public <T> T parse( final String yml, final Class<T> clazz )
    {
        try
        {
            return MAPPER.readValue( yml, clazz );
        }
        catch ( final JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public ContentType.Builder parseContentType( final String yml, final ApplicationKey currentApplication )
    {
        final ContentType.Builder builder = parse( yml, ContentType.Builder.class );
        builder.name( "_TEMP_NAME_" );

        final ContentType contentType = builder.build();

        return ContentType.create( contentType )
            .superType( new ApplicationRelativeResolver( currentApplication ).toContentTypeName( contentType.getSuperType().toString() ) );
    }

}
