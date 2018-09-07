package com.enonic.xp.core.content;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolveDuplicateDependenciesParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_resolveDuplicateDependencies
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void resolve_inner_dependency()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        final Content child_1 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( content.getPath() ).name( "child1" ).build() );
        final Content child_1_1 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( child_1.getPath() ).name( "child1_1" ).build() );

        final PropertyTree data = new PropertyTree();
        data.setReference( "ref", Reference.from( child_1_1.getId().toString() ) );

        this.contentService.update( new UpdateContentParams().contentId( content.getId() ).editor( edited -> edited.data = data ) );

        refresh();

        Map<ContentId, ContentPath> ids = Maps.newHashMap();
        ids.put( content.getId(), null );

        ContentIds result = this.contentService.resolveDuplicateDependencies( ResolveDuplicateDependenciesParams.create().
            contentIds( ids ).
            excludeChildrenIds( ContentIds.empty() ).
            build() );

        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( child_1_1.getId() ) );
        assertTrue( result.contains( child_1.getId() ) );

        result = this.contentService.resolveDuplicateDependencies( ResolveDuplicateDependenciesParams.create().
            contentIds( ids ).
            excludeChildrenIds( ContentIds.from( content.getId() ) ).
            build() );

        // can't exclude dependencies
        assertEquals( 2, result.getSize() );
    }

    @Test
    public void resolve_inner_dependency_within_path()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        final Content child_1 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( content.getPath() ).name( "child1" ).build() );
        final Content child_1_1 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( child_1.getPath() ).name( "child1_1" ).build() );
        final Content child_2 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( content.getPath() ).name( "child2" ).build() );
        final Content child_2_1 = this.contentService.create(
            CreateContentParams.create( createContentParams ).parent( child_2.getPath() ).name( "child2_1" ).build() );

        final PropertyTree data = new PropertyTree();
        data.setReference( "ref", Reference.from( child_2_1.getId().toString() ) );

        this.contentService.update( new UpdateContentParams().contentId( child_1_1.getId() ).editor( edited -> edited.data = data ) );

        refresh();

        Map<ContentId, ContentPath> ids = Maps.newHashMap();
        ids.put( child_1_1.getId(), child_2_1.getPath() );

        ContentIds result = this.contentService.resolveDuplicateDependencies( ResolveDuplicateDependenciesParams.create().
            contentIds( ids ).
            excludeChildrenIds( ContentIds.empty() ).
            build() );

        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( child_2_1.getId() ) );
        assertTrue( result.contains( child_2.getId() ) );

    }

    @Test
    public void resolve_outer_dependency()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        final Content dependency =
            this.contentService.create( CreateContentParams.create( createContentParams ).name( "reference" ).build() );

        final PropertyTree data = new PropertyTree();
        data.setReference( "ref", Reference.from( dependency.getId().toString() ) );

        this.contentService.update( new UpdateContentParams().contentId( content.getId() ).editor( edited -> edited.data = data ) );

        refresh();

        Map<ContentId, ContentPath> ids = Maps.newHashMap();
        ids.put( content.getId(), null );

        final ContentIds result = this.contentService.resolveDuplicateDependencies( ResolveDuplicateDependenciesParams.create().
            contentIds( ids ).
            excludeChildrenIds( ContentIds.empty() ).
            build() );

        assertEquals( 0, result.getSize() );
    }

}
