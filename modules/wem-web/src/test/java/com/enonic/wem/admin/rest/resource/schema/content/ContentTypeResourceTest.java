package com.enonic.wem.admin.rest.resource.schema.content;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeConfigRpcJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeResultJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.AbstractFormItem;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemListJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.InputJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.OccurrencesJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static org.junit.Assert.*;

public class ContentTypeResourceTest
{
    private ContentTypeResource resource;

    private Client client;

    @Before
    public void setup()
    {
        resource = new ContentTypeResource();

        client = Mockito.mock( Client.class );
        resource.setClient( client );

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

        ContentTypeJson resultJson = ( ContentTypeJson ) resource.get( "json", "mymodule:my_type", null );

        assertEquals( "http://localhost/admin/rest/schema/image/ContentType:mymodule:my_type", resultJson.getIconUrl() );

        ContentTypeResultJson ctResultJson = resultJson.getContentType();

        assertEquals( "my_type", ctResultJson.getName() );
        assertEquals( "mymodule", ctResultJson.getModule() );
        assertEquals( "mymodule:my_type", ctResultJson.getQualifiedName() );
        assertEquals( null, ctResultJson.getDisplayName() );
        assertEquals( null, ctResultJson.getContentDisplayNameScript() );
        assertEquals( "system:unstructured", ctResultJson.getSuperType() );
        assertFalse( ctResultJson.isAbstract() );
        assertFalse( ctResultJson.isFinal() );
        assertTrue( ctResultJson.isAllowChildren() );

        final FormItemListJson formResult = ctResultJson.getForm();

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

        ContentTypeConfigRpcJson resultJson = ( ContentTypeConfigRpcJson ) resource.get( "xml", "mymodule:my_type", null );

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

        resource.get( "json", "mymodule:my_type", null );
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

        resource.get( "xml", "mymodule:my_type", null );
    }
}
