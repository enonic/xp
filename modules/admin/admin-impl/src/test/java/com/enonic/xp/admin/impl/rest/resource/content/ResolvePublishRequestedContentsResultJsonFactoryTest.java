package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishRequestedContentsResultJson;
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

public class ResolvePublishRequestedContentsResultJsonFactoryTest
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

        final ResolvePublishRequestedContentsResultJson result =
            createPublishDependenciesResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 1, result.getPushRequestedContents().size() );
        assertEquals( 2, result.getPushRequestedContents().get( 0 ).getChildrenCount() );
        assertEquals( 0, result.getPushRequestedContents().get( 0 ).getDependantsCount() );

        assertNotNull( result.getPushRequestedContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getId() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getPath() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getType() );
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

        final ResolvePublishRequestedContentsResultJson result =
            createPublishDependenciesResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 1, result.getPushRequestedContents().size() );
        assertEquals( 0, result.getPushRequestedContents().get( 0 ).getChildrenCount() );
        assertEquals( 2, result.getPushRequestedContents().get( 0 ).getDependantsCount() );

        assertNotNull( result.getPushRequestedContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getId() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getPath() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getType() );
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

        final ResolvePublishRequestedContentsResultJson result =
            createPublishDependenciesResultJson( resolvedDependencies, resolved, compareResults );

        assertEquals( 1, result.getPushRequestedContents().size() );
        assertEquals( 0, result.getPushRequestedContents().get( 0 ).getChildrenCount() );
        assertEquals( 2, result.getPushRequestedContents().get( 0 ).getDependantsCount() );

        assertNull( result.getPushRequestedContents().get( 0 ).getCompareStatus() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getDisplayName() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getIconUrl() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getId() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getPath() );
        assertNotNull( result.getPushRequestedContents().get( 0 ).getType() );
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

        createPublishDependenciesResultJson( resolvedDependencies, resolved, compareResults );
    }

    private ResolvePublishRequestedContentsResultJson createPublishDependenciesResultJson(
        final ResolvePublishDependenciesResult resolvedDependencies, final Contents resolved, final CompareContentResults compareResults )
    {
        return ResolvePublishRequestedContentsResultJsonFactory.create().
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

        return Contents.from( content1, content2, content3 );
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
