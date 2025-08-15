package com.enonic.xp.core.impl.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.core.impl.schema.mapper.ContentTypeMapper;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeNameMapper;
import com.enonic.xp.core.impl.schema.mapper.FieldSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormItemMapper;
import com.enonic.xp.core.impl.schema.mapper.FormItemSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetOptionMapper;
import com.enonic.xp.core.impl.schema.mapper.InlineMixinMapper;
import com.enonic.xp.core.impl.schema.mapper.InputMapper;
import com.enonic.xp.core.impl.schema.mapper.InputTypeDefaultMapper;
import com.enonic.xp.core.impl.schema.mapper.InputTypePropertyMapper;
import com.enonic.xp.core.impl.schema.mapper.MixinNameMapper;
import com.enonic.xp.core.impl.schema.mapper.OccurrencesMapper;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

public final class YmlTypeParser
{
    private static final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

    static
    {
        MAPPER.addMixIn( ContentType.class, ContentTypeMapper.class );
        MAPPER.addMixIn( ContentType.Builder.class, ContentTypeMapper.Builder.class );
        MAPPER.addMixIn( ContentTypeName.class, ContentTypeNameMapper.class );

        MAPPER.addMixIn( Occurrences.class, OccurrencesMapper.class );

        // FormItem and implementations

        MAPPER.addMixIn( FormItem.class, FormItemMapper.class );

        MAPPER.addMixIn( Input.class, InputMapper.class );
        MAPPER.addMixIn( Input.Builder.class, InputMapper.Builder.class );

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

        MAPPER.addMixIn( InputTypeProperty.class, InputTypePropertyMapper.class );

        MAPPER.addMixIn( InputTypeDefault.class, InputTypeDefaultMapper.class );
        MAPPER.addMixIn( InputTypeDefault.Builder.class, InputTypeDefaultMapper.Builder.class );

        MAPPER.registerSubtypes(
            new NamedType( Input.class, "TextLine" ),
            new NamedType( Input.class, "Double" ),
            new NamedType( Input.class, "RadioButton" ),
            new NamedType( Input.class, "TextArea" ),
            new NamedType( Input.class, "CheckBox" ),
            new NamedType( Input.class, "ComboBox" ),
            new NamedType( Input.class, "ContentSelector" ),
            new NamedType( Input.class, "CustomSelector" ),
            new NamedType( Input.class, "ContentTypeFilter" ),
            new NamedType( Input.class, "Date" ),
            new NamedType( Input.class, "DateTime" ),
            new NamedType( Input.class, "MediaUploader" ),
            new NamedType( Input.class, "AttachmentUploader" ),
            new NamedType( Input.class, "GeoPoint" ),
            new NamedType( Input.class, "HtmlArea" ),
            new NamedType( Input.class, "ImageSelector" ),
            new NamedType( Input.class, "MediaSelector" ),
            new NamedType( Input.class, "ImageUploader" ),
            new NamedType( Input.class, "Long" ),
            new NamedType( Input.class, "SiteConfigurator" ),
            new NamedType( Input.class, "Tag" ),
            new NamedType( Input.class, "Time" ),
            new NamedType( FieldSet.class, "FieldSet" ),
            new NamedType( InlineMixin.class, "InlineMixin" ),
            new NamedType( FormItemSet.class, "ItemSet" ),
            new NamedType( FormOptionSet.class, "OptionSet" ),
            new NamedType( FormOptionSetOption.class, "OptionSetOption" ) );
    }

    public ContentType parseContentType( final String contentTypeAsYml )
        throws Exception
    {
        return MAPPER.readValue( contentTypeAsYml, ContentType.class );
    }

    public <T> T parse( final String yml, final Class<T> clazz )
        throws Exception
    {
        return MAPPER.readValue( yml, clazz );
    }

}
