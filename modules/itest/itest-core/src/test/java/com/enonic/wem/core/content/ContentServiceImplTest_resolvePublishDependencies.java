package com.enonic.wem.core.content;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RootNode;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_resolvePublishDependencies
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Ignore
    @Test
    public void resolve_single()
        throws Exception
    {
        nodeService.push( NodeIds.from( RootNode.UUID ), WS_OTHER );

        refresh();

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        refresh();

        final ResolvePublishDependenciesResult result =
            this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
                contentIds( ContentIds.from( content.getId() ) ).
                includeChildren( true ).
                target( WS_OTHER ).
                build() );

        assertEquals( 1, result.contentIds().getSize() );
    }


}
