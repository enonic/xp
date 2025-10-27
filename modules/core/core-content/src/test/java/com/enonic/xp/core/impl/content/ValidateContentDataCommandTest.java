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
import com.enonic.xp.core.impl.content.validate.ExtraDataValidator;
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
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateContentDataCommandTest
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private CmsService cmsService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.cmsService = Mockito.mock( CmsService.class );
    }

    @Test
    public void validation_with_errors()
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
    public void validation_no_errors()
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
    public void testSiteConfigTextRegexpFailure()
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
    public void test_empty_displayName()
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
    public void test_unnamed()
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

    @Test
    public void testSiteConfigTextRegexpPasses()
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
                                         new ExtraDataValidator( xDataService ) ) )
            .build()
            .execute();
    }
}
