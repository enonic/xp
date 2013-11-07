package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;
import static org.junit.Assert.*;

public class UpdateContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateContentTypeHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new UpdateContentTypeHandler();
        handler.setContext( this.context );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType existingContentType = newContentType().
            id( new SchemaId( "1" ) ).
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn( existingContentType );

        UpdateContentType command = Commands.contentType().update().contentTypeName( ContentTypeName.from( "my_content_type" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        assertEquals( UpdateContentTypeResult.SUCCESS, command.getResult() );
    }


    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType existingContentType = newContentType().
            id( new SchemaId( "1" ) ).
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            setFinal( true ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn( existingContentType );

        UpdateContentType command = Commands.contentType().update().contentTypeName( ContentTypeName.from( "my_content_type" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();
    }

}
