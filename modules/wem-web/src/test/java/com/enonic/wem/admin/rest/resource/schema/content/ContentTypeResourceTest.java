package com.enonic.wem.admin.rest.resource.schema.content;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeListJson;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertTrue;

public class ContentTypeResourceTest
    extends JsonTestHelper
{
    private UploadService uploadService;

    private ContentTypeResource resource = new ContentTypeResource();

    private Client client;

    private static final QualifiedContentTypeName MY_CTY_QUALIFIED_NAME = QualifiedContentTypeName.from( "mymodule:my_cty" );

    public ContentTypeResourceTest()
    {
        super();
    }

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        mockCurrentContextHttpRequest();
    }

    @Test
    public void get()
    {
        // setup
        final ContentType contentType = newContentType().
            module( MY_CTY_QUALIFIED_NAME.getModuleName() ).
            name( MY_CTY_QUALIFIED_NAME.getLocalName() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                helpText( "Help my text line" ).
                build() ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( client.execute(
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( MY_CTY_QUALIFIED_NAME ) ) ) ).thenReturn(
            contentTypes );

        // execute
        Object resultObject = resource.get( Lists.newArrayList( MY_CTY_QUALIFIED_NAME.toString() ), "JSON", true );

        // verify
        assertTrue( resultObject instanceof ContentTypeListJson );
        JsonNode actualJson = objectToJson( resultObject );
        assertJsonEquals2( loadTestJson( "get-result.json" ), actualJson );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }
}
