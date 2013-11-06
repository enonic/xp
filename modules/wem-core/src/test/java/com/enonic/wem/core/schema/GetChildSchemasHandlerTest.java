package com.enonic.wem.core.schema;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.GetChildSchemas;
import com.enonic.wem.api.command.schema.content.GetChildContentTypes;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class GetChildSchemasHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetChildSchemasHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new GetChildSchemasHandler();
        handler.setContext( this.context );
    }

    @Test
    public void getChildSchemas()
        throws Exception
    {
        // setup
        final ContentType unstructuredContentType = newContentType().
            name( ContentTypeName.structured() ).
            builtIn( true ).
            displayName( "Unstructured" ).
            setFinal( false ).
            setAbstract( false ).
            build();

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( unstructuredContentType.getQualifiedName() ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( client.execute( Mockito.isA( GetChildContentTypes.class ) ) ).thenReturn( contentTypes );

        // exercise
        final GetChildSchemas command = Commands.schema().getChildren().parentKey( unstructuredContentType.getSchemaKey() );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Schemas schemas = command.getResult();
        assertEquals( 1, schemas.getSize() );
        assertTrue( schemas.get( 0 ).getSchemaKey().isContentType() );
        assertEquals( "my_content_type", schemas.get( 0 ).getQualifiedName().toString() );
    }

}
