package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.command.schema.content.GetRootContentTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetRootContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetRootContentTypesHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        super.initialize();
        handler = new GetRootContentTypesHandler();
        handler.setContext( this.context );
    }

    @Test
    public void getRootContentTypes()
        throws Exception
    {
        // setup
        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                id( EntityId.from( "1" ) ).
                name( "my_content_type1" ).
                property( "displayName", "Display Name 1" ).
                property( "builtIn", Boolean.toString( true ) ).
                build() ).
            add( Node.newNode().
                id( EntityId.from( "2" ) ).
                name( "my_content_type2" ).
                property( "displayName", "Display Name 2" ).
                property( "superType", "my_content_type1" ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetNodesByParent.class ) ) ).thenReturn( nodes );

        // exercise
        final GetRootContentTypes command = Commands.contentType().get().getRoots();
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( client, atLeastOnce() ).execute( Mockito.isA( GetNodesByParent.class ) );
        final ContentTypes types = command.getResult();
        assertEquals( 1, types.getSize() );
        assertEquals( "my_content_type1", types.get( 0 ).getContentTypeName().toString() );

    }
}
