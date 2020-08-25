package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentNameResolverImplTest
{
    private ComponentNameResolverImpl componentNameResolver;

    private ContentService contentService;

    @BeforeEach
    public void init()
    {
        componentNameResolver = new ComponentNameResolverImpl();
        contentService = Mockito.mock( ContentService.class );

        componentNameResolver.setContentService( contentService );
    }

    @Test
    public void testResolveEmptyImageComponent()
        throws Exception
    {
        final ImageComponent imageComponent = ImageComponent.create().build();

        final ComponentName result = componentNameResolver.resolve( imageComponent );

        assertEquals( imageComponent.getName(), result );
    }

    @Test
    public void testResolveImageComponent()
        throws Exception
    {
        final Content imageContent = createContent();
        final ImageComponent imageComponent = ImageComponent.create().image( ContentId.from( "id" ) ).build();

        Mockito.when( contentService.getById( imageComponent.getImage() ) ).thenReturn( imageContent );

        final ComponentName result = componentNameResolver.resolve( imageComponent );

        assertEquals( imageContent.getDisplayName(), result.toString() );
    }

    @Test
    public void testResolveMissingImageComponent()
        throws Exception
    {
        final ContentId imageComponentId = ContentId.from( "imageCompId" );
        final ImageComponent imageComponent = ImageComponent.create().image( imageComponentId ).build();

        Mockito.when( contentService.getById( imageComponent.getImage() ) ).thenThrow(
            new ContentNotFoundException( imageComponentId, null ) );

        final ComponentName result = componentNameResolver.resolve( imageComponent );

        assertEquals( imageComponent.getName(), result );
    }

    @Test
    public void testResolveEmptyFragmentComponent()
        throws Exception
    {
        final FragmentComponent fragmentComponent = FragmentComponent.create().build();

        final ComponentName result = componentNameResolver.resolve( fragmentComponent );

        assertEquals( fragmentComponent.getName(), result );
    }

    @Test
    public void testResolveFragmentComponent()
        throws Exception
    {
        final Content fragmentContent = createContent();
        final FragmentComponent fragmentComponent = FragmentComponent.create().fragment( ContentId.from( "id" ) ).build();

        Mockito.when( contentService.getById( fragmentComponent.getFragment() ) ).thenReturn( fragmentContent );

        final ComponentName result = componentNameResolver.resolve( fragmentComponent );

        assertEquals( fragmentContent.getDisplayName(), result.toString() );
    }

    @Test
    public void testResolveMissingFragmentComponent()
        throws Exception
    {
        final ContentId fragmentComponentId = ContentId.from( "fragmentCompId" );
        final FragmentComponent fragmentComponent = FragmentComponent.create().fragment( fragmentComponentId ).build();

        Mockito.when( contentService.getById( fragmentComponent.getFragment() ) ).thenThrow(
            new ContentNotFoundException( fragmentComponentId, null ) );

        final ComponentName result = componentNameResolver.resolve( fragmentComponent );

        assertEquals( fragmentComponent.getName(), result );
    }

    private Content createContent()
    {
        final Content.Builder builder = Content.create();

        builder.id( ContentId.from( "123456" ) );
        builder.name( "someName" );
        builder.parentPath( ContentPath.ROOT );
        builder.displayName( "displayName" );

        return builder.build();
    }

}