package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishDependenciesResultJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class ResolvePublishDependenciesResultJsonFactoryTest
{
    private ContentTypeService contentTypeService;

    @Before
    public void before()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
    }


    @Test
    public void testGeneratedJsonWithChildren()
    {

        final Contents resolved = createContents();

        final CompareContentResults compareResults = createCompareContentResults();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addRequested( ContentId.from( "s1" ), ContentId.from( "s1" ) ).
            addChildOf( ContentId.from( "s2" ), ContentId.from( "s1" ), ContentId.from( "s1" ) ).
            addChildOf( ContentId.from( "s3" ), ContentId.from( "s2" ), ContentId.from( "s1" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        final ResolvePublishDependenciesResultJson result = createDependantsResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 2, result.getDependenciesContents().size() );

        assertEquals( result.getDependenciesContents().get( 0 ).isChild(), true );

        assertNotNull( result.getDependenciesContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getId() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getPath() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getType() );
    }

    @Test
    public void testGeneratedJsonWithDependents()
    {

        final Contents resolved = createContents();

        final CompareContentResults compareResults = createCompareContentResults();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addParentOf( ContentId.from( "s1" ), ContentId.from( "s2" ), ContentId.from( "s3" ) ).
            addParentOf( ContentId.from( "s2" ), ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addRequested( ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        final ResolvePublishDependenciesResultJson result = createDependantsResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 2, result.getDependenciesContents().size() );

        assertEquals( result.getDependenciesContents().get( 0 ).isChild(), false );

        assertNotNull( result.getDependenciesContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getId() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getPath() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getType() );
    }

    @Test
    public void testGeneratedJsonWithDependentsAndChildren()
    {

        final Contents resolved = createContents();

        final CompareContentResults compareResults = createCompareContentResults();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addParentOf( ContentId.from( "s1" ), ContentId.from( "s2" ), ContentId.from( "s3" ) ).
            addParentOf( ContentId.from( "s2" ), ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addRequested( ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addChildOf( ContentId.from( "s4" ), ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addChildOf( ContentId.from( "s5" ), ContentId.from( "s4" ), ContentId.from( "s3" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        final ResolvePublishDependenciesResultJson result = createDependantsResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 4, result.getDependenciesContents().size() );

        assertNotNull( result.getDependenciesContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getId() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getPath() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getType() );
    }

    @Test
    public void testGeneratedJsonOrderedByPath()
    {

        final Content content1 = createContent( "s1", "s1Name", ContentPath.from( "b" ), true );
        final Content content2 = createContent( "s2", "s1Name", ContentPath.from( "a" ), true );
        final Content content3 = createContent( "s3", "s1Name", ContentPath.from( "c" ), true );
        final Content content4 = createContent( "s4", "s1Name", ContentPath.from( "s4" ), true );

        final Contents resolved = Contents.from( content1, content2, content3, content4 );

        final CompareContentResults compareResults = createCompareContentResults();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addParentOf( ContentId.from( "s1" ), ContentId.from( "s2" ), ContentId.from( "s4" ) ).
            addParentOf( ContentId.from( "s2" ), ContentId.from( "s3" ), ContentId.from( "s4" ) ).
            addParentOf( ContentId.from( "s3" ), ContentId.from( "s4" ), ContentId.from( "s4" ) ).
            addRequested( ContentId.from( "s4" ), ContentId.from( "s4" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        final ResolvePublishDependenciesResultJson result = createDependantsResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 3, result.getDependenciesContents().size() );

        assertEquals( result.getDependenciesContents().get( 0 ).getId(), "s2" );
        assertEquals( result.getDependenciesContents().get( 1 ).getId(), "s1" );
        assertEquals( result.getDependenciesContents().get( 2 ).getId(), "s3" );

        assertNotNull( result.getDependenciesContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getId() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getPath() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getType() );
    }

    @Test
    public void testGeneratedJsonWithEmptyCompareResult()
    {

        final Contents resolved = createContents();

        final CompareContentResults compareResults = CompareContentResults.create().build();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addParentOf( ContentId.from( "s1" ), ContentId.from( "s2" ), ContentId.from( "s3" ) ).
            addParentOf( ContentId.from( "s2" ), ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addRequested( ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        final ResolvePublishDependenciesResultJson result = createDependantsResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 2, result.getDependenciesContents().size() );

        assertNull( result.getDependenciesContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getId() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getPath() );
        assertNotNull( result.getDependenciesContents().get( 0 ).getType() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedJsonWithMissingResolvedContent()
    {

        final Contents resolved = Contents.empty();

        final CompareContentResults compareResults = createCompareContentResults();

        final PushContentRequests pushRequests = PushContentRequests.create().
            addParentOf( ContentId.from( "s1" ), ContentId.from( "s2" ), ContentId.from( "s3" ) ).
            addParentOf( ContentId.from( "s2" ), ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            addRequested( ContentId.from( "s3" ), ContentId.from( "s3" ) ).
            build();

        final ResolvePublishDependenciesResult resolvedDependencies =
            ResolvePublishDependenciesResult.create().pushContentRequests( pushRequests ).build();

        createDependantsResultJson( resolvedDependencies, resolved, compareResults );
    }

    private ResolvePublishDependenciesResultJson createDependantsResultJson( final ResolvePublishDependenciesResult resolvedDependencies,
                                                                             final Contents resolved,
                                                                             final CompareContentResults compareResults )
    {
        return ResolvePublishDependenciesResultJsonFactory.create().
            resolvedPublishDependencies( resolvedDependencies ).
            iconUrlResolver( new ContentIconUrlResolver( this.contentTypeService ) ).
            resolvedContents( resolved ).
            compareContentResults( compareResults ).
            build().
            createJson();
    }

    private CompareContentResults createCompareContentResults()
    {
        return CompareContentResults.create().
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "s1" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "s2" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "s3" ) ) ).
            build();
    }

    private Contents createContents()
    {
        final Content content1 = createContent( "s1", "s1Name", ContentPath.ROOT, true );
        final Content content2 = createContent( "s2", "s2Name", content1.getPath(), true );
        final Content content3 = createContent( "s3", "s3Name", content2.getPath(), true );
        final Content content4 = createContent( "s4", "s4Name", content3.getPath(), true );
        final Content content5 = createContent( "s5", "s5Name", content4.getPath(), true );

        return Contents.from( content1, content2, content3, content4, content5 );
    }

    private Content createContent( final String id, final String name, final ContentPath path, boolean valid )
    {

        return Content.create().
            id( ContentId.from( id ) ).
            name( name ).
            parentPath( path ).
            valid( valid ).
            type( ContentTypeName.folder() ).
            displayName( name ).
            build();
    }
}
