package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.command.schema.content.GetChildContentTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static junit.framework.Assert.assertEquals;

public class GetChildContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetChildContentTypesHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new GetChildContentTypesHandler();
        handler.setContext( this.context );
    }

    @Test
    public void getChildContentTypes()
        throws Exception
    {
        // setup

        final Node node1 = Node.newNode().
            id( EntityId.from( "1" ) ).
            name( ContentTypeName.unstructured().toString() ).
            property( "displayName", "Unstructured root content type" ).
            property( "builtIn", "true" ).
            build();
        final Node node2 = Node.newNode().
            id( EntityId.from( "2" ) ).
            name( "my_type" ).
            property( "displayName", "My content type" ).
            property( "superType", node1.name() ).
            build();
        final Node node3 = Node.newNode().
            id( EntityId.from( "3" ) ).
            name( "sub_type_1" ).
            property( "displayName", "My sub-content-1 type" ).
            property( "superType", node2.name() ).
            build();
        final Node node4 = Node.newNode().
            id( EntityId.from( "4" ) ).
            name( "sub_type_2" ).
            property( "displayName", "My sub-content-2 type" ).
            property( "superType", node2.name() ).
            build();
        final Node node5 = Node.newNode().
            id( EntityId.from( "5" ) ).
            name( ContentTypeName.folder().toString() ).
            property( "displayName", "Folder root content type" ).
            property( "builtIn", "true" ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetNodesByParent.class ) ) ).thenReturn(
            Nodes.from( node1, node2, node3, node4, node5 ) );

        // exercise
        GetChildContentTypes command = Commands.contentType().get().children().parentName( ContentTypeName.from( node5.name() ) );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        ContentTypes types = command.getResult();
        assertEquals( 0, types.getSize() );

        // exercise
        command = Commands.contentType().get().children().parentName( ContentTypeName.from( node1.name() ) );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        types = command.getResult();
        assertEquals( 1, types.getSize() );
        assertEquals( "my_type", types.get( 0 ).getName().toString() );

        // exercise
        command = Commands.contentType().get().children().parentName( ContentTypeName.from( node2.name() ) );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        types = command.getResult();
        assertEquals( 2, types.getSize() );
        assertEquals( "sub_type_1", types.get( 0 ).getName().toString() );
        assertEquals( "sub_type_2", types.get( 1 ).getName().toString() );
    }
}
