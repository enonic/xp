package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class SortContentCommandTest
{
    private NodeService nodeService;

    private ContentTypeService contentTypeService;

    private ContentNodeTranslator contentNodeTranslator;

    private EventPublisher eventPublisher;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentNodeTranslator = Mockito.mock( ContentNodeTranslator.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void no_outside_requested()
        throws Exception
    {

        Mockito.when( nodeService.getById( Mockito.isA( NodeId.class ) ) ).
            thenReturn( createNode( "s1", "s1Name", NodePath.ROOT.toString() ) );

        Mockito.when( contentNodeTranslator.fromNode( Mockito.isA( Node.class ) ) ).
            thenReturn(  createContent( "s1", "s1Name", ContentPath.ROOT, true )  );

        SortContentParams params = new SortContentParams().contentId( ContentId.from( "s1" ) );

        final Content resultContent = SortContentCommand.create( params ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( "s1", resultContent.getId().toString() );
        SortContentParams params1 = new SortContentParams().contentId( resultContent.getId() );

        assertEquals(params, params1);
        assertEquals(params.hashCode(), params1.hashCode());
    }


    private Node createNode( final String id, final String name, final String path )
    {
        return Node.newNode().
            id( NodeId.from( id ) ).
            name( name ).
            parentPath( NodePath.newPath( path ).build() ).
            build();
    }

    private Content createContent( final String id, final String name, final ContentPath path, boolean valid )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            name( name ).
            parentPath( path ).
            valid( valid ).
            build();
    }

}