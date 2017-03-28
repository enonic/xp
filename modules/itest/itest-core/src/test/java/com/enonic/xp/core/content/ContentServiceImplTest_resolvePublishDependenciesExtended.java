package com.enonic.xp.core.content;

import java.time.Instant;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolveContentsToBePublishedCommandResult;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_resolvePublishDependenciesExtended
    extends AbstractContentServiceTest
{
    private final static NodeId ROOT_UUID = NodeId.from( "000-000-000-000" );

    private Content content;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();

        this.content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );
    }

    @Test
    public void resolve_with_offline_child()
        throws Exception
    {
        createChild( true );

        final ResolvePublishDependenciesParams.Builder builder = ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( content.getId() ) );

        refresh();

        final ResolveContentsToBePublishedCommandResult result = this.contentService.
            resolvePublishDependenciesExtended( builder.build() );

        assertTrue( result.getContainsOffline() );
    }

    @Test
    public void resolve_without_offline_child()
        throws Exception
    {
        createChild( false );

        final ResolvePublishDependenciesParams.Builder builder = ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( content.getId() ) );

        refresh();

        final ResolveContentsToBePublishedCommandResult result = this.contentService.
            resolvePublishDependenciesExtended( builder.build() );

        assertFalse( result.getContainsOffline() );
    }

    private Content createChild( final Boolean isOffline )
    {
        return this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 1" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            contentPublishInfo( isOffline ? ContentPublishInfo.create().first( Instant.now() ).build() : null ).
            build() );
    }
}
