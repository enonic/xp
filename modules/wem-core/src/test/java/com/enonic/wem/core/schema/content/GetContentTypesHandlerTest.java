package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class GetContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetContentTypesHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new GetContentTypesHandler();
        handler.setContext( this.context );
    }

    @Ignore // Does not work atm because of rewriting of client to instanticate handler
    @Test
    public void handle()
        throws Exception
    {
        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                name( NodeName.from( "content_type_1" ) ).
                id( EntityId.from( "1" ) ).
                property( "displayName", "DisplayName" ).
                property( "description", "Description" ).
                build() ).
            add( Node.newNode().
                name( NodeName.from( "content_type_2" ) ).
                id( EntityId.from( "2" ) ).
                property( "displayName", "DisplayName2" ).
                property( "description", "Description2" ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetNodesByParent.class ) ) ).thenReturn( nodes );
        Mockito.when( client.execute( Mockito.isA( GetNodesByPaths.class ) ) ).thenReturn( nodes );

        // Exercise:
        GetContentTypes command =
            Commands.contentType().get().byNames().contentTypeNames( ContentTypeNames.from( "content_type_1", "content_type_2" ) );
        command.validate();
        this.handler.setCommand( command );
        this.handler.handle();

        // Verify
        final ContentTypes result = command.getResult();
        assertEquals( 2, result.getSize() );
        verifyContentType( "content_type_1", "DisplayName", "Description", result );
        verifyContentType( "content_type_2", "DisplayName2", "Description2", result );
    }

    private void verifyContentType( final String contentTypeName, final String displayName, final String description, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName().toString() );
        assertEquals( displayName, contentType.getDisplayName() );
        assertEquals( description, contentType.getDescription() );
    }

}
