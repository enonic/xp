package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class CreateContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateContentTypeHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new CreateContentTypeHandler();
        handler.setContext( this.context );
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        CreateContentType command = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        final Node node = Node.newNode().
            name( contentType.getName() ).
            id( EntityId.from( "1" ) ).
            property( "displayName", contentType.getDisplayName() ).
            build();

        Mockito.when( client.execute( Mockito.isA( CreateNode.class ) ) ).thenReturn( new CreateNodeResult( node ) );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        ContentTypeName contentTypeName = command.getResult();
        assertNotNull( contentTypeName );
        assertEquals( "my_content_type", contentTypeName.toString() );
    }

    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        //setup
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.SHORTCUT ) );

        ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "Inheriting a final ContentType" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();

        final Node node = Node.newNode().
            name( contentType.getName() ).
            id( EntityId.from( "1" ) ).
            property( "displayName", "Inheriting a final ContentType" ).
            property( "superType", ContentTypeName.shortcut().toString() ).
            build();

        Mockito.when( client.execute( Mockito.isA( CreateNode.class ) ) ).thenReturn( new CreateNodeResult( node ) );

        // exercise
        final CreateContentType createCommand = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        this.handler.setCommand( createCommand );
        this.handler.handle();
    }

}
