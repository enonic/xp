package com.enonic.wem.admin.rest.resource.schema.content;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeConfigJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeConfigListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeTreeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeTreeNodeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeValidationErrorJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ValidateContentTypeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.AbstractFormItem;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.InputJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.OccurrencesJson;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.command.schema.content.GetContentTypeTree;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.command.schema.content.ValidateContentType;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationError;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ContentTypeResourceTest
{
    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private UploadService uploadService;

    private ContentTypeResource resource;

    private Client client;

    @Before
    public void setup()
    {
        resource = new ContentTypeResource();

        client = Mockito.mock( Client.class );
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        mockCurrentContextHttpRequest();
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void testRequestGetContentTypeJson_existing()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_type" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:my_type" ) );
        Mockito.when( client.execute( Commands.contentType().get().qualifiedNames( names ) ) ).thenReturn( contentTypes );

        ContentTypeListJson listJson = (ContentTypeListJson) resource.get( "json", Arrays.asList( "mymodule:my_type" ), null );

        checkListResult( listJson.getContentTypes().get( 0 ) );
    }

    private void checkListResult( final ContentTypeJson resultJson )
    {
        assertEquals( "http://localhost/admin/rest/schema/image/ContentType:mymodule:my_type", resultJson.getIconUrl() );

        assertEquals( "my_type", resultJson.getName() );
        assertEquals( "mymodule", resultJson.getModule() );
        assertEquals( "mymodule:my_type", resultJson.getQualifiedName() );
        assertEquals( null, resultJson.getDisplayName() );
        assertEquals( null, resultJson.getContentDisplayNameScript() );
        assertEquals( "system:unstructured", resultJson.getSuperType() );
        assertFalse( resultJson.isAbstract() );
        assertFalse( resultJson.isFinal() );
        assertTrue( resultJson.isAllowChildren() );

        final FormItemListJson formResult = resultJson.getForm();

        assertEquals( 3, formResult.getTotal() );

        InputJson itemResult = (InputJson) getItemByName( formResult, "inputText1" );

        assertEquals( "Line Text 1", itemResult.getLabel() );
        assertEquals( false, itemResult.isImmutable() );

        OccurrencesJson occurrences = itemResult.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text line 1", itemResult.getHelpText() );
        assertEquals( "TextLine", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );

        itemResult = (InputJson) getItemByName( formResult, "inputText2" );

        assertEquals( "Line Text 2", itemResult.getLabel() );
        assertEquals( true, itemResult.isImmutable() );

        occurrences = itemResult.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text line 2", itemResult.getHelpText() );
        assertEquals( "TextLine", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );

        itemResult = (InputJson) getItemByName( formResult, "textArea1" );

        assertEquals( "Text Area", itemResult.getLabel() );
        assertEquals( false, itemResult.isImmutable() );

        occurrences = itemResult.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text area", itemResult.getHelpText() );
        assertEquals( "TextArea", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );
    }

    private AbstractFormItem getItemByName( final FormItemListJson formResult, String name )
    {
        for ( FormItemJson item : formResult.get() )
        {
            AbstractFormItem formItem = item.get();

            if ( formItem.getName().equals( name ) )
            {
                return formItem;
            }
        }

        return null;
    }

    @Test
    public void testRequestGetContentTypeXml_existing()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_type" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:my_type" ) );
        Mockito.when( client.execute( Commands.contentType().get().qualifiedNames( names ) ) ).thenReturn( contentTypes );

        ContentTypeConfigListJson listJson = (ContentTypeConfigListJson) resource.get( "xml", Arrays.asList( "mymodule:my_type" ), null );
        ContentTypeConfigJson resultJson = listJson.getContentTypeXmls().get( 0 );
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<type>\r\n  <name>my_type</name>\r\n  <module>mymodule</module>\r\n  <display-name />\r\n  <content-display-name-script />\r\n  <super-type>system:unstructured</super-type>\r\n  <is-abstract>false</is-abstract>\r\n  <is-final>false</is-final>\r\n  <allow-children>true</allow-children>\r\n  <form>\r\n    <input type=\"TextLine\" built-in=\"true\" name=\"inputText1\">\r\n      <label>Line Text 1</label>\r\n      <immutable>false</immutable>\r\n      <indexed>false</indexed>\r\n      <custom-text />\r\n      <help-text>Help text line 1</help-text>\r\n      <occurrences minimum=\"1\" maximum=\"1\" />\r\n    </input>\r\n    <input type=\"TextLine\" built-in=\"true\" name=\"inputText2\">\r\n      <label>Line Text 2</label>\r\n      <immutable>true</immutable>\r\n      <indexed>false</indexed>\r\n      <custom-text />\r\n      <help-text>Help text line 2</help-text>\r\n      <occurrences minimum=\"0\" maximum=\"1\" />\r\n    </input>\r\n    <input type=\"TextArea\" built-in=\"true\" name=\"textArea1\">\r\n      <label>Text Area</label>\r\n      <immutable>false</immutable>\r\n      <indexed>false</indexed>\r\n      <custom-text />\r\n      <help-text>Help text area</help-text>\r\n      <occurrences minimum=\"1\" maximum=\"1\" />\r\n    </input>\r\n  </form>\r\n</type>\r\n\r\n",
            resultJson.getContentTypeXml() );
    }

    @Test(expected = NotFoundException.class)
    public void testRequestGetContentTypeJson_not_found()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_type" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:my_type" ) );

        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        resource.get( "json", Arrays.asList( "mymodule:my_type" ), null );
    }

    @Test(expected = NotFoundException.class)
    public void testRequestGetContentTypeXml_not_found()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_type" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:my_type" ) );
        Mockito.when( client.execute( Commands.contentType().get().qualifiedNames( names ) ) ).thenReturn( contentTypes );

        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        resource.get( "xml", Arrays.asList( "mymodule:my_type" ), null );
    }

    @Test
    public void testList()
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType1 = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_type" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final Input inputTextCty2 =
            newInput().name( "inputText_1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build();
        final ContentType contentType2 = newContentType().
            module( ModuleName.from( "othermodule" ) ).
            name( "the_content_type" ).
            addFormItem( inputTextCty2 ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2 );
        Mockito.when( client.execute( Commands.contentType().get().all() ) ).thenReturn( contentTypes );

        ContentTypeListJson listJson = resource.list();

        checkListResult( listJson.getContentTypes().get( 0 ) );
    }

    @Test
    public void deleteSingleContentType()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existing_content_type" );

        Mockito.when( client.execute( Mockito.any( contentType().delete().getClass() ) ) ).thenReturn( DeleteContentTypeResult.SUCCESS );

        resource.delete( Arrays.asList( "my:existing_content_type" ) );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.any( DeleteContentType.class ) );
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFoundContentTypes()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existing_content_type" );
        final QualifiedContentTypeName notFoundName = new QualifiedContentTypeName( "my:not_found_content_type" );

        Mockito.when( client.execute( eq( contentType().delete().name( existingName ) ) ) ).thenReturn( DeleteContentTypeResult.SUCCESS );
        Mockito.when( client.execute( eq( contentType().delete().name( notFoundName ) ) ) ).thenReturn( DeleteContentTypeResult.NOT_FOUND );

        resource.delete( Arrays.asList( "my:existing_content_type", "my:not_found_content_type" ) );
    }

    @Test(expected = NotFoundException.class)
    public void deleteUnableToDeleteContentTypes()
        throws Exception
    {
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existing_content_type" );
        final QualifiedContentTypeName beingUsedName = new QualifiedContentTypeName( "my:being_used_content_type" );

        Mockito.when( client.execute( eq( contentType().delete().name( existingName ) ) ) ).thenReturn( DeleteContentTypeResult.SUCCESS );
        Mockito.when( client.execute( eq( contentType().delete().name( beingUsedName ) ) ) ).thenReturn( DeleteContentTypeResult.UNABLE_TO_DELETE );

        resource.delete( Arrays.asList( "my:existing_content_type", "my:being_used_content_type" ) );
    }

    @Test
    public void create_ContentType()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        resource.create(
            "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n  <qualifiedName>mymodule:all_types</qualifiedName>\n  <displayName>All the Types</displayName>\n  <superType>system:content</superType>\n  <isAbstract>false</isAbstract>\n  <isFinal>true</isFinal>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <customText /><helpText /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>",
            null );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
    }

    @Test
    public void update_ContentType()
        throws Exception
    {
        ContentType existingContentType = ContentType.newContentType().name( "a_type" ).module( Module.SYSTEM.getName() ).build();
        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );
        Mockito.when( client.execute( isA( UpdateContentType.class ) ) ).thenReturn( UpdateContentTypeResult.SUCCESS );

        resource.update(
            "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n  <qualifiedName>mymodule:all_types</qualifiedName>\n  <displayName>All the Types</displayName>\n  <superType>system:content</superType>\n  <isAbstract>false</isAbstract>\n  <isFinal>true</isFinal>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <customText /><helpText /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>",
            null );

        verify( client, times( 1 ) ).execute( isA( UpdateContentType.class ) );
    }

    @Test (expected = WebApplicationException.class)
    public void update_ContentType_with_failure()
        throws Exception
    {
        ContentType existingContentType = ContentType.newContentType().name( "a_type" ).module( Module.SYSTEM.getName() ).build();
        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );
        Mockito.when( client.execute( isA( UpdateContentType.class ) ) ).thenThrow( new InvalidContentTypeException( existingContentType ) );

        resource.update(
            "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n  <qualifiedName>mymodule:all_types</qualifiedName>\n  <displayName>All the Types</displayName>\n  <superType>system:content</superType>\n  <isAbstract>false</isAbstract>\n  <isFinal>true</isFinal>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <customText /><helpText /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>",
            null );

        verify( client, times( 1 ) ).execute( isA( UpdateContentType.class ) );
    }

    @Test
    public void create_ContentType_with_Icon()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "photo.png", IMAGE_DATA, "image/png" );

        resource.create(
            "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n  <qualifiedName>mymodule:all_types</qualifiedName>\n  <displayName>All the Types</displayName>\n  <superType>system:content</superType>\n  <isAbstract>false</isAbstract>\n  <isFinal>true</isFinal>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <customText /><helpText /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>",
            "edc1af66-ecb4-4f8a-8df4-0738418f84fc" );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
    }

    private void uploadFile( String id, String name, byte[] data, String type )
        throws Exception
    {
        File file = createTempFile( data );
        UploadItem item = Mockito.mock( UploadItem.class );
        Mockito.when( item.getId() ).thenReturn( id );
        Mockito.when( item.getMimeType() ).thenReturn( type );
        Mockito.when( item.getUploadTime() ).thenReturn( 0L );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getSize() ).thenReturn( (long) data.length );
        Mockito.when( item.getFile() ).thenReturn( file );
        Mockito.when( this.uploadService.getItem( Mockito.<String>any() ) ).thenReturn( item );
    }

    private File createTempFile( byte[] data )
        throws IOException
    {
        String id = UUID.randomUUID().toString();
        File file = File.createTempFile( id, "" );
        Files.write( data, file );
        return file;
    }

    @Test
    public void testRequestGetContentTypeTree()
        throws Exception
    {
        // setup
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType1 = newContentType().
            name( "root" ).
            module( Module.SYSTEM.getName() ).
            displayName( "Some root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "my_type" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            superType( new QualifiedContentTypeName( "system:root" ) ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();
        final ContentType contentType3 = newContentType().
            name( "sub_type" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My sub-content type" ).
            superType( new QualifiedContentTypeName( "mymodule:my_type" ) ).
            build();

        final Tree<ContentType> contentTypeTree = new Tree<ContentType>( Lists.newArrayList( contentType1 ) );
        final TreeNode<ContentType> contentTypeNode2 = contentTypeTree.getRootNode( 0 ).addChild( contentType2 );
        contentTypeNode2.addChild( contentType3 );
        Mockito.when( client.execute( isA( GetContentTypeTree.class ) ) ).thenReturn( contentTypeTree );

        ContentTypeTreeJson resultJson = resource.getTree();

        assertEquals( 3, resultJson.getTotal() );

        List<ContentTypeTreeNodeJson> list = resultJson.getContentTypes();

        ContentTypeTreeNodeJson rootNode = list.get( 0 );
        assertEquals( "root", rootNode.getName() );
        assertEquals( "system", rootNode.getModule() );
        assertEquals( "system:root", rootNode.getQualifiedName() );
        assertEquals( "Some root content type", rootNode.getDisplayName() );
        assertEquals( "http://localhost/admin/rest/schema/image/ContentType:system:root", rootNode.getIconUrl() );

        ContentTypeTreeNodeJson subNode = rootNode.getContentTypes().get( 0 );
        checkTreeResult( subNode );
        assertTrue( subNode.isHasChildren() );

        subNode = subNode.getContentTypes().get( 0 );
        assertEquals( "sub_type", subNode.getName() );
        assertEquals( "mymodule", subNode.getModule() );
        assertEquals( "My sub-content type", subNode.getDisplayName() );
        assertEquals( "mymodule:my_type", subNode.getSuperType() );
        assertEquals( "http://localhost/admin/rest/schema/image/ContentType:mymodule:sub_type", subNode.getIconUrl() );
        assertFalse( subNode.isHasChildren() );
    }

    private void checkTreeResult( final ContentTypeJson resultJson )
    {
        assertEquals( "http://localhost/admin/rest/schema/image/ContentType:mymodule:my_type", resultJson.getIconUrl() );

        assertEquals( "my_type", resultJson.getName() );
        assertEquals( "mymodule", resultJson.getModule() );
        assertEquals( "mymodule:my_type", resultJson.getQualifiedName() );
        assertEquals( "My content type", resultJson.getDisplayName() );
        assertEquals( null, resultJson.getContentDisplayNameScript() );
        assertEquals( "system:root", resultJson.getSuperType() );
        assertFalse( resultJson.isAbstract() );
        assertFalse( resultJson.isFinal() );
        assertTrue( resultJson.isAllowChildren() );

        final FormItemListJson formResult = resultJson.getForm();

        assertEquals( 3, formResult.getTotal() );

        InputJson itemResult = (InputJson) getItemByName( formResult, "inputText1" );

        assertEquals( "Line Text 1", itemResult.getLabel() );
        assertEquals( false, itemResult.isImmutable() );

        OccurrencesJson occurrences = itemResult.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text line 1", itemResult.getHelpText() );
        assertEquals( "TextLine", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );

        itemResult = (InputJson) getItemByName( formResult, "inputText2" );

        assertEquals( "Line Text 2", itemResult.getLabel() );
        assertEquals( true, itemResult.isImmutable() );

        occurrences = itemResult.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text line 2", itemResult.getHelpText() );
        assertEquals( "TextLine", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );

        itemResult = (InputJson) getItemByName( formResult, "textArea1" );

        assertEquals( "Text Area", itemResult.getLabel() );
        assertEquals( false, itemResult.isImmutable() );

        occurrences = itemResult.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( null, itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "Help text area", itemResult.getHelpText() );
        assertEquals( "TextArea", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );
    }

    @Test (expected = WebApplicationException.class)
    public void validate_invalid_xml()
        throws Exception
    {
        final ContentTypeValidationResult contentTypeValidationResult = null;
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( contentTypeValidationResult );

        ValidateContentTypeJson result = resource.validate( "<invalid XML>" );
    }

    @Test
    public void validate_without_errors()
        throws Exception
    {
        final ContentTypeValidationResult noErrors = ContentTypeValidationResult.empty();
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( noErrors );

        ValidateContentTypeJson result = resource.validate( "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n   <display-name>All the Types</display-name>\n  <super-type>system:content</super-type>\n  <is-abstract>false</is-abstract>\n  <is-final>true</is-final>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <custom-text /><help-text /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>" );

        ContentTypeJson contentType = result.getContentType();

        assertEquals( "all_types", contentType.getName() );
        assertEquals( "mymodule", contentType.getModule() );
        assertEquals( "mymodule:all_types", contentType.getQualifiedName() );
        assertEquals( "All the Types", contentType.getDisplayName() );
        assertEquals( "system:content", contentType.getSuperType() );

        final FormItemListJson formResult = contentType.getForm();

        assertEquals( 1, formResult.getTotal() );

        InputJson itemResult = (InputJson) getItemByName( formResult, "myTextLine" );

        assertEquals( "", itemResult.getLabel() );
        assertEquals( false, itemResult.isImmutable() );

        OccurrencesJson occurrences = itemResult.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        assertEquals( false, itemResult.isIndexed() );
        assertEquals( "", itemResult.getCustomText() );
        assertEquals( null, itemResult.getValidationRegexp() );
        assertEquals( "", itemResult.getHelpText() );
        assertEquals( "TextLine", itemResult.getType().getName() );
        assertEquals( true, itemResult.getType().isBuiltIn() );
    }

    @Test
    public void validate_with_errors()
        throws Exception
    {
        final ContentType contentType1 =
            ContentType.newContentType().module( ModuleName.from( "mymodule" ) ).name( "content_type" ).build();
        final ContentType contentType2 = ContentType.newContentType( contentType1 ).name( "my_type2" ).build();
        final ContentTypeValidationError error1 = new ContentTypeValidationError( "Validation error message 1", contentType1 );
        final ContentTypeValidationError error2 = new ContentTypeValidationError( "Validation error message 2", contentType2 );
        final ContentTypeValidationResult validationErrors = ContentTypeValidationResult.from( error1, error2 );
        Mockito.when( client.execute( isA( ValidateContentType.class ) ) ).thenReturn( validationErrors );

        ValidateContentTypeJson result = resource.validate( "<type>\n  <name>all_types</name>\n  <module>mymodule</module>\n   <display-name>All the Types</display-name>\n  <super-type>system:content</super-type>\n  <is-abstract>false</is-abstract>\n  <is-final>true</is-final>\n  <form><input type=\"TextLine\" built-in=\"true\" name=\"myTextLine\"> <label /> <immutable>false</immutable> <indexed>false</indexed> <custom-text /><help-text /><occurrences minimum=\"0\" maximum=\"1\" /></input></form>\n</type>" );

        List<ContentTypeValidationErrorJson> errors = result.getErrors();

        assertEquals( 2, errors.size() );
        assertEquals( "Invalid content type: [mymodule:content_type]: Validation error message 1", errors.get( 0 ).getMessage() );
        assertEquals( "Invalid content type: [mymodule:my_type2]: Validation error message 2", errors.get( 1 ).getMessage() );
    }
}
