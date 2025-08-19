package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.core.impl.schema.mapper.FieldSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormItemMapper;
import com.enonic.xp.core.impl.schema.mapper.FormItemSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetMapper;
import com.enonic.xp.core.impl.schema.mapper.FormOptionSetOptionMapper;
import com.enonic.xp.core.impl.schema.mapper.InlineMixinMapper;
import com.enonic.xp.core.impl.schema.mapper.InputMapper;
import com.enonic.xp.core.impl.schema.mapper.InputTypeDefaultMapper;
import com.enonic.xp.core.impl.schema.mapper.InputTypeNameMapper;
import com.enonic.xp.core.impl.schema.mapper.InputTypePropertyMapper;
import com.enonic.xp.core.impl.schema.mapper.MixinNameMapper;
import com.enonic.xp.core.impl.schema.mapper.OccurrencesMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.ContentTypeYmlMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.ExtContentTypeMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.FormItemYml;
import com.enonic.xp.core.impl.schema.mapper.sandbox.FormItemYmlMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.FormYml;
import com.enonic.xp.core.impl.schema.mapper.sandbox.FormYmlMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.InputRegistry;
import com.enonic.xp.core.impl.schema.mapper.sandbox.InputYml;
import com.enonic.xp.core.impl.schema.mapper.sandbox.ItemSetYml;
import com.enonic.xp.core.impl.schema.mapper.sandbox.MyContentTypeMapper;
import com.enonic.xp.core.impl.schema.mapper.sandbox.MyContentTypeMapper2;
import com.enonic.xp.core.impl.schema.mapper.sandbox.RadioButtonYml;
import com.enonic.xp.core.impl.schema.mapper.sandbox.TextLineYml;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Sandbox
{
//    @Test
//    void test2()
//        throws IOException
//    {
//        ObjectMapper mapper = new ObjectMapper( new YAMLFactory() );
//
//        mapper.addMixIn( ContentType.class, MyContentTypeMapper.class );
//        mapper.addMixIn( ContentType.Builder.class, MyContentTypeMapper.Builder.class );
//
//        String yaml = new String( Sandbox.class.getResourceAsStream( "/descriptors/content.yml" ).readAllBytes(), StandardCharsets.UTF_8 );
//
//        ContentType.Builder dto = mapper.readValue( yaml, ContentType.Builder.class );
//        ContentType contentType = dto.build();
//
////        assertEquals( "myapp:article", dto.getName() );
//        assertEquals( "Article", contentType.getDisplayName() );
//        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
//        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
//    }

    @Test
    void test3()
        throws IOException
    {
        ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

        MAPPER.addMixIn( Occurrences.class, OccurrencesMapper.class );

        MAPPER.addMixIn( Form.class, FormMapper.class );
        MAPPER.addMixIn( FormItem.class, FormItemMapper.class );

        MAPPER.addMixIn( Input.class, InputMapper.class );
        MAPPER.addMixIn( Input.Builder.class, InputMapper.Builder.class );
        MAPPER.addMixIn( InputTypeName.class, InputTypeNameMapper.class );

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

        MAPPER.registerSubtypes( new NamedType( Input.class, "TextLine" ), new NamedType( Input.class, "Double" ),
                                 new NamedType( Input.class, "RadioButton" ), new NamedType( Input.class, "TextArea" ),
                                 new NamedType( Input.class, "CheckBox" ), new NamedType( Input.class, "ComboBox" ),
                                 new NamedType( Input.class, "ContentSelector" ), new NamedType( Input.class, "CustomSelector" ),
                                 new NamedType( Input.class, "ContentTypeFilter" ), new NamedType( Input.class, "Date" ),
                                 new NamedType( Input.class, "DateTime" ), new NamedType( Input.class, "MediaUploader" ),
                                 new NamedType( Input.class, "AttachmentUploader" ), new NamedType( Input.class, "GeoPoint" ),
                                 new NamedType( Input.class, "HtmlArea" ), new NamedType( Input.class, "ImageSelector" ),
                                 new NamedType( Input.class, "MediaSelector" ), new NamedType( Input.class, "ImageUploader" ),
                                 new NamedType( Input.class, "Long" ), new NamedType( Input.class, "SiteConfigurator" ),
                                 new NamedType( Input.class, "Tag" ), new NamedType( Input.class, "Time" ),
                                 new NamedType( FieldSet.class, "FieldSet" ), new NamedType( InlineMixin.class, "InlineMixin" ),
                                 new NamedType( FormItemSet.class, "ItemSet" ), new NamedType( FormOptionSet.class, "OptionSet" ),
                                 new NamedType( FormOptionSetOption.class, "OptionSetOption" ) );

        String yaml = new String( Sandbox.class.getResourceAsStream( "/descriptors/content2.yml" ).readAllBytes(), StandardCharsets.UTF_8 );

        MyContentTypeMapper2 dto = MAPPER.readValue( yaml, MyContentTypeMapper2.class );

        final ContentType.Builder builder = dto.toContentTypeBuilder();

        builder.name( "myapp:article" );
        builder.superType( ContentTypeName.unstructured() );

        final Instant now = Instant.now();
        builder.createdTime( now );

        final ContentType contentType = builder.build();

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( "Article", contentType.getDisplayName() );
        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
    }

    @Test
    void testRadiButton()
        throws JsonProcessingException
    {
        ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

        MAPPER.registerSubtypes( new NamedType( RadioButtonYml.class, "RadioButton" ) );

        String yamlStr = """
            type: RadioButton
            name: type
            label: Policy type
            options:
              - name: cookie
                value: Cookie
                i18n: i18n.rbg.cookie
              - name: privacy
                value: Privacy
            default: cookie
            """;

        RadioButtonYml dto = MAPPER.readValue( yamlStr, RadioButtonYml.class );
        Input input = InputRegistry.toInput( dto );

        assertEquals( "RadioButton", input.getInputType().toString() );
    }

    @Test
    void testRadiButtonNew()
        throws IOException
    {
        ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

        MAPPER.addMixIn( FormYml.class, FormYmlMapper.class );
        MAPPER.addMixIn( FormItemYml.class, FormItemYmlMapper.class );

        MAPPER.registerSubtypes( new NamedType( RadioButtonYml.class, "RadioButton" ), new NamedType( TextLineYml.class, "TextLine" ),
                                 new NamedType( ItemSetYml.class, "ItemSet" ) );

        String yaml = new String( Sandbox.class.getResourceAsStream( "/descriptors/content-type-new-format.yml" ).readAllBytes(),
                                  StandardCharsets.UTF_8 );

        ContentTypeYmlMapper dto = MAPPER.readValue( yaml, ContentTypeYmlMapper.class );

        final ContentType.Builder builder = dto.toContentTypeBuilder();

        builder.name( "myapp:article" );
        builder.superType( ContentTypeName.unstructured() );

        final Instant now = Instant.now();
        builder.createdTime( now );

        final ContentType contentType = builder.build();

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( "Article", contentType.getDisplayName() );
        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
    }

    @Test
    void testExtContentTypeMapper()
        throws IOException
    {
        ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

        MAPPER.addMixIn( FormYml.class, FormYmlMapper.class );
        MAPPER.addMixIn( FormItemYml.class, FormItemYmlMapper.class );
        MAPPER.addMixIn( ContentType.Builder.class, ExtContentTypeMapper.Builder.class );

        MAPPER.registerSubtypes( new NamedType( RadioButtonYml.class, "RadioButton" ), new NamedType( TextLineYml.class, "TextLine" ),
                                 new NamedType( ItemSetYml.class, "ItemSet" ) );

        String yaml = new String( Sandbox.class.getResourceAsStream( "/descriptors/content-type-new-format.yml" ).readAllBytes(),
                                  StandardCharsets.UTF_8 );

        ContentType.Builder builder = MAPPER.readValue( yaml, ContentType.Builder.class );

        builder.name( "myapp:article" );
        builder.superType( ContentTypeName.unstructured() );

        final Instant now = Instant.now();
        builder.createdTime( now );

        final ContentType contentType = builder.build();

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
//        assertEquals( "Article", contentType.getDisplayName() );
//        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
//        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
    }
}
