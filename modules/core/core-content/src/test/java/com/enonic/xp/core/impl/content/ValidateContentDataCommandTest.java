package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.core.impl.content.validate.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class ValidateContentDataCommandTest
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.siteService = Mockito.mock( SiteService.class );
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( FieldSet.create().
                label( "My layout" ).
                name( "myLayout" ).
                addFormItem( FormItemSet.create().name( "mySet" ).required( true ).
                    addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();

        final ValidationErrors result = executeValidation( content.getData(), contentType.getName() );
        // test
        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );

    }

    @Test
    public void validation_no_errors()
        throws Exception
    {
        // setup
        final FieldSet fieldSet = FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result = executeValidation( content.getData(), contentType.getName() );

        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );
    }

    @Test
    public void testSiteConfigTextRegexpFailure()
    {
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( ContentTypeName.site() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "textInput-1", "test" );

        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapp" ) ).
            config( siteConfigDataSet ).build();
        new SiteConfigsDataSerializer().toProperties( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );
    }

    @Test
    public void test_empty_displayName()
        throws Exception
    {
        // setup
        final FieldSet fieldSet = FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).displayName( "" ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result =
            executeValidation( content.getData(), contentType.getName(), content.getName(), content.getDisplayName() );

        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );
    }

    @Test
    public void test_unnamed()
        throws Exception
    {
        // setup
        final FieldSet fieldSet = FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).
            name( ContentName.unnamed() ).displayName( "display-name" ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidationErrors result =
            executeValidation( content.getData(), contentType.getName(), content.getName(), content.getDisplayName() );

        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );
    }

    private SiteDescriptor createSiteDescriptor()
    {
        final Form config = Form.create().
            addFormItem( createTextLineInput( "textInput-1", "some-label" ).build() ).
            build();
        return SiteDescriptor.create().form( config ).build();
    }

    private Input.Builder createTextLineInput( final String name, final String label )
    {
        return Input.create().
            inputType( InputTypeName.TEXT_LINE ).
            label( label ).
            name( name ).
            inputTypeProperty( InputTypeProperty.create( "regexp", "\\d+" ).build() ).
            immutable( true );
    }

    @Test
    public void testSiteConfigTextRegexpPasses()
    {
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( ContentTypeName.site() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        PropertyTree rootDataSet = new PropertyTree();

        PropertyTree siteConfigDataSet = new PropertyTree();
        siteConfigDataSet.setString( "textInput-1", "1234" );

        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapp" ) ).
            config( siteConfigDataSet ).build();
        new SiteConfigsDataSerializer().toProperties( SiteConfigs.from( siteConfig ), rootDataSet.getRoot() );

        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( createSiteDescriptor() );

        // exercise
        final ValidationErrors result = executeValidation( rootDataSet, ContentTypeName.site() );

        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );
    }

    private ValidationErrors executeValidation( final PropertyTree propertyTree, final ContentTypeName contentTypeName )
    {
        return this.executeValidation( propertyTree, contentTypeName, ContentName.from( "name" ), "display-name" );
    }

    private ValidationErrors executeValidation( final PropertyTree propertyTree, final ContentTypeName contentTypeName,
                                                final ContentName name, final String displayName )
    {
        return ValidateContentDataCommand.create().
            contentData( propertyTree ).
            contentType( contentTypeName ).
            name( name ).
            displayName( displayName ).
            contentTypeService( this.contentTypeService ).
            xDataService( this.xDataService ).
            siteService( this.siteService ).
            build().
            execute();
    }
}
