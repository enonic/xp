package com.enonic.xp.core.impl.content;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.validate.ContentNameValidator;
import com.enonic.xp.core.impl.content.validate.MixinValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.content.validate.SiteConfigsValidator;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateContentDataCommandTest
{
    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private CmsService cmsService;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );
        this.cmsService = Mockito.mock( CmsService.class );
    }

    @Test
    void validation_with_errors()
    {
        // setup
        final ContentType contentType = ContentType.create()
            .superType( ContentTypeName.structured() )
            .name( "myapplication:my_type" )
            .addFormItem( FieldSet.create()
                              .label( "My layout" )
                              .addFormItem( FormItemSet.create()
                                                .name( "mySet" )
                                                .required( true )
                                                .addFormItem( Input.create()
                                                                  .name( "myInput" )
                                                                  .label( "Input" )
                                                                  .inputType( InputTypeName.TEXT_LINE )
                                                                  .build() )
                                                .build() )
                              .build() )
            .build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();

        final ValidationErrors result = executeValidation( content.getData(), contentType.getName() );
        // test
        assertTrue( result.hasErrors() );
        assertThat( result.stream() ).hasSize( 1 );
    }

    @Test
    void validation_no_errors()
    {
        // setup
        final FieldSet fieldSet = FieldSet.create()
            .label( "My layout" )
            .addFormItem( FormItemSet.create()
                              .name( "mySet" )
                              .required( true )
                              .addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                              .build() )
            .build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:my_type" ).addFormItem( fieldSet ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result = executeValidation( content.getData(), contentType.getName() );

        assertFalse( result.hasErrors() );
    }

    @Test
    void testSiteConfigTextRegexpFailure()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "textInput-1", "test" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createCmsDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        assertThat( result.stream() ).hasSize( 1 );
    }

    @Test
    void test_empty_displayName()
    {
        // setup
        final FieldSet fieldSet = FieldSet.create()
            .label( "My layout" )
            .addFormItem( FormItemSet.create()
                              .name( "mySet" )
                              .required( true )
                              .addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                              .build() )
            .build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:my_type" ).addFormItem( fieldSet ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).displayName( "" ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result =
            executeValidation( content.getData(), contentType.getName(), content.getName(), content.getDisplayName() );

        assertThat( result.stream() ).hasSize( 1 );
    }

    @Test
    void test_unnamed()
    {
        // setup
        final FieldSet fieldSet = FieldSet.create()
            .label( "My layout" )
            .addFormItem( FormItemSet.create()
                              .name( "mySet" )
                              .required( true )
                              .addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                              .build() )
            .build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:my_type" ).addFormItem( fieldSet ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create()
            .path( "/mycontent" )
            .type( contentType.getName() )
            .name( ContentName.uniqueUnnamed() )
            .displayName( "display-name" )
            .build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result =
            executeValidation( content.getData(), contentType.getName(), content.getName(), content.getDisplayName() );

        assertThat( result.stream() ).hasSize( 1 );
    }

    private CmsDescriptor createCmsDescriptor()
    {
        final Form config = Form.create()
            .addFormItem( Input.create()
                              .inputType( InputTypeName.TEXT_LINE )
                              .label( "some-label" )
                              .name( "textInput-1" )
                              .inputTypeProperty( "regexp", "\\d+" )
                              .build() )
            .build();
        return CmsDescriptor.create().applicationKey( ApplicationKey.from( "myapp" ) ).form( config ).build();
    }

    private CmsDescriptor createNestedSiteDescriptor()
    {
        final Form config = Form.create()
            .addFormItem( FormItemSet.create()
                              .name( "myItemSet" )
                              .addFormItem( Input.create()
                                                .inputType( InputTypeName.TEXT_LINE )
                                                .label( "Nested Input" )
                                                .name( "nestedInput" )
                                                .inputTypeProperty( "regexp", "\\d+"  )
                                                .build() )
                              .build() )
            .build();
        return CmsDescriptor.create().applicationKey( ApplicationKey.from( "myapplication" ) ).form( config ).build();
    }

    private CmsDescriptor createMultiOccurrenceSiteDescriptor()
    {
        final Form config = Form.create().addFormItem( Input.create()
                                                           .inputType( InputTypeName.TEXT_LINE )
                                                           .label( "Multi Input" )
                                                           .name( "multiInput" )
                                                           .inputTypeProperty( "regexp", "\\d+"  )
                                                           .minimumOccurrences( 0 )
                                                           .maximumOccurrences( 5 )
                                                           .build() ).build();
        return CmsDescriptor.create().applicationKey( ApplicationKey.from( "myapplication" ) ).form( config ).build();
    }

    private CmsDescriptor createNestedMultiOccurrenceSiteDescriptor()
    {
        final Form config = Form.create()
            .addFormItem( FormItemSet.create()
                              .name( "multiItemSet" )
                              .occurrences( 0, 5 )
                              .addFormItem( Input.create()
                                                .inputType( InputTypeName.TEXT_LINE )
                                                .label( "Nested Input" )
                                                .name( "nestedInput" )
                                                .inputTypeProperty( "regexp", "\\d+"  )
                                                .build() )
                              .build() )
            .build();
        return CmsDescriptor.create().applicationKey( ApplicationKey.from( "myapplication" ) ).form( config ).build();
    }

    private CmsDescriptor createThirdLevelNestedSiteDescriptor()
    {
        final Form config = Form.create()
            .addFormItem( FormItemSet.create()
                              .name( "outerSet" )
                              .addFormItem( FormItemSet.create()
                                                .name( "innerSet" )
                                                .occurrences( 0, 5 )
                                                .addFormItem( Input.create()
                                                                  .inputType( InputTypeName.TEXT_LINE )
                                                                  .label( "Deep Input" )
                                                                  .name( "deepInput" )
                                                                  .inputTypeProperty( "regexp", "\\d+"  )
                                                                  .build() )
                                                .build() )
                              .build() )
            .build();
        return CmsDescriptor.create().applicationKey( ApplicationKey.from( "myapplication" ) ).form( config ).build();
    }

    @Test
    void testSiteConfigTextRegexpPasses()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "textInput-1", "1234" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createCmsDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        assertFalse( result.hasErrors() );
    }

    @Test
    void testSiteConfigValidationErrorIncludesFieldPath()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "textInput-1", "invalid-text" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createCmsDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        // verify
        assertThat(result.stream()).hasSize( 1 );
        final var error = result.stream().findFirst().orElseThrow();

        // Verify that the error is a DataValidationError with the field path
        assertThat(error).isInstanceOf( com.enonic.xp.content.DataValidationError.class );
        final var dataError = (com.enonic.xp.content.DataValidationError) error;
        assertThat(dataError.getPropertyPath().toString()).isEqualTo( "textInput-1" );
    }

    @Test
    void testSiteConfigValidationErrorIncludesNestedFieldPath()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "myItemSet.nestedInput", "invalid-nested-text" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createNestedSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        // verify
        assertThat(result.stream()).hasSize( 1 );
        final var error = result.stream().findFirst().orElseThrow();

        // Verify that the error is a DataValidationError with the nested field path
        assertThat(error).isInstanceOf( com.enonic.xp.content.DataValidationError.class );
        final var dataError = (com.enonic.xp.content.DataValidationError) error;
        assertThat(dataError.getPropertyPath().toString()).isEqualTo( "myItemSet.nestedInput" );
    }

    @Test
    void testSiteConfigValidationErrorIncludesFieldPathWithMultipleOccurrences()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        // First occurrence is valid, second is invalid
        siteConfigDataSet.setString( "multiInput[0]", "123" );
        siteConfigDataSet.setString( "multiInput[1]", "invalid-text" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createMultiOccurrenceSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        // verify
        assertThat(result.stream()).hasSize( 1 );
        final var error = result.stream().findFirst().orElseThrow();

        // Verify that the error is a DataValidationError with the field path including the array index
        assertThat(error).isInstanceOf( com.enonic.xp.content.DataValidationError.class );
        final var dataError = (com.enonic.xp.content.DataValidationError) error;
        assertThat(dataError.getPropertyPath().toString()).isEqualTo( "multiInput[1]" );
    }

    @Test
    void testSiteConfigValidationErrorIncludesNestedFieldPathWithMultipleOccurrences()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        // First occurrence is valid, second occurrence has invalid nested field
        siteConfigDataSet.setString( "multiItemSet[0].nestedInput", "123" );
        siteConfigDataSet.setString( "multiItemSet[1].nestedInput", "invalid-text" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createNestedMultiOccurrenceSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        // verify
        assertThat(result.stream()).hasSize( 1 );
        final var error = result.stream().findFirst().orElseThrow();

        // Verify that the error is a DataValidationError with the nested field path including the array index
        assertThat(error).isInstanceOf( com.enonic.xp.content.DataValidationError.class );
        final var dataError = (com.enonic.xp.content.DataValidationError) error;
        assertThat(dataError.getPropertyPath().toString()).isEqualTo( "multiItemSet[1].nestedInput" );
    }

    @Test
    void testSiteConfigValidationErrorIncludesThirdLevelNestedFieldPathWithMultipleOccurrences()
    {
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        // Third level: outerSet -> innerSet[occurrence] -> deepInput
        siteConfigDataSet.setString( "outerSet.innerSet[0].deepInput", "123" );
        siteConfigDataSet.setString( "outerSet.innerSet[1].deepInput", "invalid-text" );

        SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "myapp" ) ).config( siteConfigDataSet ).build();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( cmsService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createThirdLevelNestedSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        // verify
        assertThat(result.stream()).hasSize( 1 );
        final var error = result.stream().findFirst().orElseThrow();

        // Verify that the error is a DataValidationError with the third level nested field path including the array index
        assertThat(error).isInstanceOf( com.enonic.xp.content.DataValidationError.class );
        final var dataError = (com.enonic.xp.content.DataValidationError) error;
        assertThat(dataError.getPropertyPath().toString()).isEqualTo( "outerSet.innerSet[1].deepInput" );
    }

    private ValidationErrors executeValidation( final PropertyTree propertyTree, final ContentTypeName contentTypeName )
    {
        return this.executeValidation( propertyTree, contentTypeName, ContentName.from( "name" ), "display-name" );
    }

    private ValidationErrors executeValidation( final PropertyTree propertyTree, final ContentTypeName contentTypeName,
                                                final ContentName name, final String displayName )
    {
        return ValidateContentDataCommand.create()
            .contentTypeName( contentTypeName )
            .data( propertyTree )
            .contentName( name )
            .displayName( displayName )
            .contentTypeService( this.contentTypeService )
            .contentValidators( List.of( new ContentNameValidator(), new SiteConfigsValidator( cmsService ), new OccurrenceValidator(),
                                         new MixinValidator( mixinService ) ) )
            .build()
            .execute();
    }
}
