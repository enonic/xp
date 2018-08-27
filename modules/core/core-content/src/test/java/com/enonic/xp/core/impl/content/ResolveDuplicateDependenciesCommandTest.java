package com.enonic.xp.core.impl.content;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ResolveDuplicateDependenciesCommandTest
{
    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    private ContentNodeTranslator translator;

    private NodeService nodeService;


    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.nodeService = Mockito.mock( NodeService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.translator = Mockito.mock( ContentNodeTranslator.class );
    }

    @Test
    public void test_valid_changed()
        throws Exception
    {

        final Content content = createContent( true );
        final Node node = createNode();

        Mockito.doReturn( Contents.from( content ) ).when( translator ).fromNodes( Nodes.from( node ), true );

        final Map<ContentId, ContentPath> ids = Maps.newHashMap();
        ids.put( content.getId(), null );

        final Map<NodeId, NodePath> nodeIds = Maps.newHashMap();
        nodeIds.put( NodeId.from( content.getId().toString() ), null );

        Mockito.doReturn( Nodes.create().add( node ).build() ).when( this.nodeService ).findDependenciesWithinPath( nodeIds );
        Mockito.doReturn( Nodes.create().add( node ).build() ).when( this.nodeService ).getByIds( NodeIds.from( node.id() ) );
        Mockito.doReturn(
            FindNodesByQueryResult.create().addNodeHit( NodeHit.create().nodeId( NodeId.from( "dependency-id" ) ).build() ).build() ).when(
            this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        ArgumentCaptor<NodeQuery> queryCaptor = ArgumentCaptor.forClass( NodeQuery.class );

        final ContentIds result = createCommand( ids, ContentIds.empty() ).execute();

        assertEquals( 2, result.getSize() );

        assertTrue( result.contains( content.getId() ) );
        assertTrue( result.contains( ContentId.from( "dependency-id" ) ) );

        Mockito.verify( nodeService ).findByQuery( queryCaptor.capture() );
        assertEquals( "(_path LIKE '/content/test/content/path/*' AND _path NOT IN ('/content/test/content/path')) ORDER BY _path ASC",
                      queryCaptor.getValue().getQuery().toString() );
    }

    private Content createContent( final Boolean valid )
    {
        return Content.create().
            id( ContentId.from( "id" ) ).
            path( ContentPath.from( ContentNodeHelper.translateNodePathToContentPath( ContentConstants.CONTENT_ROOT_PATH ),
                                    ContentPath.from( "test/content/path" ) ) ).
            creator( PrincipalKey.from( "user:system:anonymous" ) ).
            type( ContentTypeName.folder() ).
            data( new PropertyTree() ).
            valid( valid ).
            build();
    }

    private Node createNode()
    {
        return Node.create().
            id( NodeId.from( "id" ) ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            name( NodeName.from( "mynode" ) ).
            data( new PropertyTree() ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();
    }


    private ResolveDuplicateDependenciesCommand createCommand( final Map<ContentId, ContentPath> ids, final ContentIds excludeIds )
    {
        return ResolveDuplicateDependenciesCommand.create().
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            contentIds( ids ).
            excludeChildrenIds( excludeIds ).
            build();
    }


}
