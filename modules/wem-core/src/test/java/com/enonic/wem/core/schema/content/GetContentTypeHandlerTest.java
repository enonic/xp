package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetNodeByPathParams;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class GetContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetContentTypeHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        handler = new GetContentTypeHandler();
        handler.setContext( this.context );
    }

    @Ignore // Does not work atm because of rewriting of client to instanticate handler
    @Test
    public void handle()
        throws Exception
    {
        final Node node = Node.newNode().
            name( NodeName.from( "content_type_1" ) ).
            id( EntityId.from( "1" ) ).
            property( "displayName", "DisplayName" ).
            property( "description", "Description" ).
            build();

        Mockito.when( nodeService.getByParent( Mockito.isA( GetNodesByParentParams.class ) ) ).thenReturn( Nodes.from( node ) );
        Mockito.when( nodeService.getByPath( Mockito.isA( GetNodeByPathParams.class ) ) ).thenReturn( node );

        // Exercise:
        GetContentType command = Commands.contentType().get().byName().contentTypeName( ContentTypeName.from( "content_type_1" ) );
        command.validate();
        this.handler.setCommand( command );
        this.handler.handle();

        // Verify
        final ContentType contentType = command.getResult();

        assertEquals( "DisplayName", contentType.getDisplayName() );
        assertEquals( "Description", contentType.getDescription() );
    }
}
