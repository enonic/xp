package com.enonic.xp.core.content;

import java.util.Collection;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolveDependenciesAggregationResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;


public class ContentServiceImplTest_getDependencies
    extends AbstractContentServiceTest
{

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void resolve_inbound_dependencies()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        PropertyTree data = new PropertyTree();
        data.addReference( "myRef1", Reference.from( content.getId().toString() ) );

        final Content folderRefContent = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 1" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content siteRefContent1 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.site() ).
            build() );

        final Content siteRefContent2 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 3" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.site() ).
            build() );

        Collection<ResolveDependenciesAggregationResult> result =
            this.contentService.resolveInboundDependenciesAggregation( content.getId() );
        assertEquals( result.size(), 2 );

        final ResolveDependenciesAggregationResult siteAggregation = (ResolveDependenciesAggregationResult) result.toArray()[0];
        assertEquals( siteAggregation.getType(), ContentTypeName.site().toString() );
        assertEquals( siteAggregation.getCount(), 2 );

        final ResolveDependenciesAggregationResult folderAggregation = (ResolveDependenciesAggregationResult) result.toArray()[1];
        assertEquals( folderAggregation.getType(), ContentTypeName.folder().toString() );
        assertEquals( folderAggregation.getCount(), 1 );


    }

    @Test
    public void resolve_outbound_dependencies()
        throws Exception
    {
        final Content folderRefContent1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content folderRefContent2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 1" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content siteRefContent1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.site() ).
            build() );

        PropertyTree data = new PropertyTree();
        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( siteRefContent1.getId().toString() ) );

        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 3" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.site() ).
            build() );

        refresh();

        Collection<ResolveDependenciesAggregationResult> result =
            this.contentService.resolveOutboundDependenciesAggregation( content.getId() );
        assertEquals( result.size(), 2 );

        final ResolveDependenciesAggregationResult siteAggregation = (ResolveDependenciesAggregationResult) result.toArray()[0];
        assertEquals( siteAggregation.getType(), ContentTypeName.site().toString() );
        assertEquals( siteAggregation.getCount(), 1 );

        final ResolveDependenciesAggregationResult folderAggregation = (ResolveDependenciesAggregationResult) result.toArray()[1];
        assertEquals( folderAggregation.getType(), ContentTypeName.folder().toString() );
        assertEquals( folderAggregation.getCount(), 2 );
        return;

    }


}
