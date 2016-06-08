package com.enonic.xp.admin.impl.rest.resource.content;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentResultJson;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UnableToDeleteContentException;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.util.BinaryReferences;

import static com.enonic.xp.security.acl.Permission.READ;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

public class ContentResourceTest
    extends AdminResourceTestSupport
{

    private final LocalDate currentDate = LocalDate.of( 2013, 8, 23 );

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private SecurityService securityService;

    private static final UserStoreKey SYSTEM = UserStoreKey.system();

    @Override
    protected Object getResourceInstance()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );

        final ContentResource resource = new ContentResource();

        contentService = Mockito.mock( ContentService.class );
        resource.setContentService( contentService );
        resource.setContentTypeService( contentTypeService );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentType( "myapplication:my_type" ) );

        securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );
        return resource;
    }

    @Test
    public void get_content_by_path()
        throws Exception
    {
        final Content content = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree data = content.getData();

        data.setLong( "myArray[0]", 1L );
        data.setLong( "myArray[1]", 2L );

        data.setDouble( "mySetWithArray.myArray[0]", 3.14159 );
        data.setDouble( "mySetWithArray.myArray[1]", 1.333 );

        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( content );

        String jsonString = request().
            path( "content/bypath" ).
            queryParam( "path", "/my_a_content" ).
            get().getAsString();

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_content_summary_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();
        aContentData.setLocalDate( "myProperty", currentDate );

        aContentData.setLong( "mySet.setProperty1", 1L );
        aContentData.setLong( "mySet.setProperty2", 2L );

        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent );

        String jsonString = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).
            queryParam( "expand", "summary" ).get().getAsString();

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_permissions_by_id()
        throws Exception
    {
        final User admin = User.create().displayName( "Admin" ).key( PrincipalKey.from( "user:system:admin" ) ).login( "admin" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn(
            Optional.of( admin ) );
        final User anon = User.create().displayName( "Anonymous" ).key( PrincipalKey.ofAnonymous() ).login( "anonymous" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn(
            Optional.of( anon ) );

        final AccessControlList permissions = getTestPermissions();

        Mockito.when( contentService.getPermissionsById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( permissions );

        String jsonString = request().path( "content/contentPermissions" ).queryParam( "id", "/my_a_content" ).get().getAsString();

        assertJson( "get_content_permissions_success.json", jsonString );
    }

    @Test
    public void get_content_by_path_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).
            thenReturn( Contents.empty() );

        final MockRestResponse response = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get();

        assertEquals( response.getStatus(), 404 );
    }

    @Test
    public void get_content_id_by_path_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();
        aContentData.setLocalDate( "myProperty", this.currentDate );

        aContentData.setLong( "mySet.setProperty1", 1L );
        aContentData.setLong( "mySet.setProperty2", 2L );

        Mockito.when( contentService.getByPath( Mockito.eq( ContentPath.from( "/my_a_content" ) ) ) ).thenReturn( aContent );

        String jsonString = request().
            path( "content/bypath" ).
            queryParam( "path", "/my_a_content" ).
            queryParam( "expand", "none" ).
            get().getAsString();

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_path_and_version_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByPath( Mockito.eq( ContentPath.from( "/my_a_content" ) ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get();
        assertEquals( response.getStatus(), 404 );
    }

    @Test
    public void get_content_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();

        aContentData.setLong( "myArray[0]", 1L );
        aContentData.setLong( "myArray[1]", 2L );

        aContentData.setDouble( "mySetWithArray.myArray[0]", 3.14159 );
        aContentData.setDouble( "mySetWithArray.myArray[1]", 1.333 );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_site_content_by_id()
        throws Exception
    {
        PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "A", 1L );
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( siteConfigConfig ).
            build();

        Site content = createSite( "aaa", "my_a_content", "myapplication:my_type", SiteConfigs.from( siteConfig ) );

        PropertyTree contentData = content.getData();
        contentData.setString( "myProperty", "myValue" );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( content );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_with_site.json", jsonString );
    }

    @Test
    public void get_page_content_by_id()
        throws Exception
    {
        PropertyTree componentConfig = new PropertyTree();
        componentConfig.setString( "my-prop", "value" );

        PartComponent component = PartComponent.create().
            name( "my-component" ).
            descriptor( DescriptorKey.from( "mainapplication:partTemplateName" ) ).
            config( componentConfig ).
            build();

        Region region = Region.create().
            name( "my-region" ).
            add( component ).
            build();

        PageRegions regions = PageRegions.create().
            add( region ).
            build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.setString( "background-color", "blue" );
        Page page = Page.create().
            template( PageTemplateKey.from( "mypagetemplate" ) ).
            regions( regions ).
            config( pageConfig ).
            build();

        Content content = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        content = Content.create( content ).page( page ).build();

        PropertyTree contentData = content.getData();
        contentData.setString( "myProperty", "myValue" );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( content );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_with_page.json", jsonString );
    }

    @Test
    public void get_content_summary_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();
        aContentData.setLocalDate( "myProperty", this.currentDate );

        aContentData.setLong( "mySet.setProperty1", 1L );
        aContentData.setLong( "mySet.setProperty2", 2L );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).
            queryParam( "expand", "summary" ).get().getAsString();

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_by_id_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.empty() );

        final MockRestResponse response = request().path( "content" ).queryParam( "id", "aaa" ).get();
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void get_content_id_by_id_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();

        aContentData.setString( "myArray[0]", "arrayValue1" );
        aContentData.setString( "myArray[1]", "arrayValue2" );

        aContentData.setDouble( "mySetWithArray.myArray[0]", 3.14159 );
        aContentData.setDouble( "mySetWithArray.myArray[1]", 1.333 );

        Mockito.when( contentService.getById( Mockito.eq( ContentId.from( "aaa" ) ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_id_and_version_not_found()
        throws Exception
    {
        Mockito.when( contentService.getById( Mockito.eq( ContentId.from( "aaa" ) ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "content" ).queryParam( "id", "aaa" ).get();
        assertEquals( response.getStatus(), 404 );
    }

    @Test
    public void list_content_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get().getAsString();

        assertJson( "list_content_summary_byPath.json", jsonString );
    }

    @Test
    public void list_content_full_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).
            queryParam( "expand", "full" ).get().getAsString();

        assertJson( "list_content_full_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_path_not_found()
        throws Exception
    {
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.empty() ).
                hits( 0 ).
                totalHits( 0 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get().getAsString();

        assertJson( "list_content_empty_byPath.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "list_content_id_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "myapplication:my_type" );
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( cContent );

        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "parentId", "ccc" ).get().getAsString();

        assertJson( "list_content_summary.json", jsonString );
    }

    @Test
    public void list_content_full_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "myapplication:my_type" );
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( cContent );

        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "parentId", "ccc" ).
            queryParam( "expand", "full" ).get().getAsString();

        assertJson( "list_content_full.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "list_content_id.json", jsonString );
    }

    @Test
    public void batch_content()
        throws Exception
    {

        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "myapplication:my_type" );

        Mockito.when( contentService.getByPaths( Mockito.isA( ContentPaths.class ) ) ).
            thenReturn( Contents.from( aContent, bContent ) );

        // Request 3 contents and receive 2 (1 should not be found)
        String jsonString = request().path( "content/batch" ).
            entity( readFromFile( "batch_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "batch_content_summary.json", jsonString );
    }

    @Test
    public void delete_content_success()
        throws Exception
    {

        Content content = Content.create().
            id( ContentId.from( "123" ) ).
            parentPath( ContentPath.ROOT ).
            name( "one" ).
            displayName( "one" ).
            type( ContentTypeName.folder() ).
            build();
        Mockito.when( contentService.delete( Mockito.isA( DeleteContentParams.class ) ) ).thenReturn( Contents.from( content ) );

        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_success.json", jsonString );
    }

    @Test
    public void delete_content_failure()
        throws Exception
    {
        Mockito.when( contentService.delete( Mockito.eq( DeleteContentParams.create().
            contentPath( ContentPath.from( "/one" ) ).
            build() ) ) ).
            thenThrow( new ContentNotFoundException( ContentPath.from( "/one" ), ContentConstants.BRANCH_DRAFT ) );

        final Content aContent = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent );
        Mockito.when( contentService.delete( Mockito.eq( DeleteContentParams.create().
            contentPath( ContentPath.from( "/two" ) ).
            build() ) ) ).
            thenThrow( new UnableToDeleteContentException( ContentPath.from( "/two" ), "Some reason" ) );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_failure.json", jsonString );
    }

    @Test
    public void delete_content_both()
        throws Exception
    {
        final Content aContent1 = createContent( "aaa", "my_a_content1", "myapplication:my_type" );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent1 );

        final Content aContent2 = Content.create().
            id( ContentId.from( "123" ) ).
            parentPath( ContentPath.ROOT ).
            name( "one" ).
            displayName( "one" ).
            build();
        Mockito.when( contentService.delete( Mockito.eq( DeleteContentParams.create().
            contentPath( ContentPath.from( "/one" ) ).
            build() ) ) ).
            thenReturn( Contents.from( aContent2 ) );

        final Content aContent3 = createContent( "aaa", "my_a_content2", "myapplication:my_type" );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent3 );

        Mockito.when( contentService.delete( DeleteContentParams.create().
            contentPath( ContentPath.from( "/two" ) ).
            build() ) ).
            thenThrow( new UnableToDeleteContentException( ContentPath.from( "/two" ), "Some reason" ) );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_both.json", jsonString );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_exception()
        throws Exception
    {
        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( contentService.create( Mockito.isA( CreateContentParams.class ) ) ).thenThrow( e );

        request().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

    }

    @Test
    public void create_content_success()
        throws Exception
    {
        Content content = createContent( "content-id", "content-path", "myapplication:content-type" );
        Mockito.when( contentService.create( Mockito.isA( CreateContentParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_content_success.json", jsonString );
    }


    @Test(expected = ContentNotFoundException.class)
    public void update_content_failure()
        throws Exception
    {
        Exception e = new ContentNotFoundException( ContentId.from( "content-id" ), ContentConstants.BRANCH_DRAFT );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );

        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenThrow( e );

        request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void update_content_nothing_updated()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_nothing_updated.json", jsonString );
    }

    @Test
    public void update_content_success()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Test
    public void duplicate()
        throws Exception
    {
        final String contentIdString = "1";

        final Content aContent = createContent( contentIdString, "my_a_content", "myapplication:my_type" );

        final PropertyTree aContentData = aContent.getData();

        aContentData.setString( "myArray[0]", "arrayValue1" );
        aContentData.setString( "myArray[1]", "arrayValue2" );

        aContentData.setDouble( "mySetWithArray.myArray[0]", 3.14159 );
        aContentData.setDouble( "mySetWithArray.myArray[1]", 1.333 );

        final DuplicateContentParams duplicateContentParams = new DuplicateContentParams( ContentId.from( contentIdString ) );

        Mockito.when( contentService.duplicate( duplicateContentParams ) ).thenReturn( aContent );

        String jsonString = request().path( "content/duplicate" ).
            entity( readFromFile( "duplicate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "duplicate_content_success.json", jsonString );
    }


    @Test(expected = ContentNotFoundException.class)
    public void duplicate_not_found()
        throws Exception
    {

        final Exception e = new ContentNotFoundException( ContentId.from( "content-id" ), ContentConstants.BRANCH_DRAFT );

        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenThrow( e );

        request().path( "content/duplicate" ).
            entity( readFromFile( "duplicate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

    }

    @Test
    public void applyPermissions()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        final User admin = User.create().displayName( "Admin" ).key( PrincipalKey.from( "user:system:admin" ) ).login( "admin" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn(
            Optional.of( admin ) );
        final User anon = User.create().displayName( "Anonymous" ).key( PrincipalKey.ofAnonymous() ).login( "anonymous" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn(
            Optional.of( anon ) );

        final AccessControlList permissions = getTestPermissions();
        content = Content.create( content ).permissions( permissions ).inheritPermissions( true ).build();
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/applyPermissions" ).
            entity( readFromFile( "apply_content_permissions_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 1 ) ).applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) );

        assertJson( "apply_content_permissions_success.json", jsonString );
    }

    @Test
    public void getPermissions()
        throws Exception
    {
        final User admin = User.create().displayName( "Admin" ).key( PrincipalKey.from( "user:system:admin" ) ).login( "admin" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn(
            Optional.of( admin ) );
        final User anon = User.create().displayName( "Anonymous" ).key( PrincipalKey.ofAnonymous() ).login( "anonymous" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn(
            Optional.of( anon ) );

        final AccessControlList permissions = getTestPermissions();
        Mockito.when( contentService.getRootPermissions() ).thenReturn( permissions );

        String jsonString = request().path( "content/rootPermissions" ).get().getAsString();

        Mockito.verify( contentService, Mockito.times( 1 ) ).getRootPermissions();

        assertJson( "get_content_root_permissions_success.json", jsonString );
    }

    @Test
    public void setChildOrder()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/setChildOrder" ).
            entity( readFromFile( "set_order_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 1 ) ).setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) );

        assertJson( "set_order_success.json", jsonString );
    }

    @Test
    public void reorderChildrenContents()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        content = Content.create( content ).childOrder( ChildOrder.defaultOrder() ).build();
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( content );
        Mockito.when( contentService.setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) ) ).thenReturn( content );

        final ReorderChildContentsParams reorderChildren = ReorderChildContentsParams.create().
            add( ReorderChildParams.create().contentToMove( ContentId.from( "content-id-1" ) ).contentToMoveBefore(
                ContentId.from( "content-id-2" ) ).build() ).
            add( ReorderChildParams.create().contentToMove( ContentId.from( "content-id-3" ) ).build() ).
            build();
        final ReorderChildContentsResult result = new ReorderChildContentsResult( 2 );
        Mockito.when( contentService.reorderChildren( Mockito.eq( reorderChildren ) ) ).thenReturn( result );

        String jsonString = request().path( "content/reorderChildren" ).
            entity( readFromFile( "reorder_children_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 1 ) ).setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) );

        Mockito.verify( contentService, Mockito.times( 1 ) ).reorderChildren( Mockito.isA( ReorderChildContentsParams.class ) );

        assertJson( "reorder_children_success.json", jsonString );
    }

    @Test
    public void resortReorderChildrenContents()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        content = Content.create( content ).childOrder( ChildOrder.defaultOrder() ).build();
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( content );
        Mockito.when( contentService.setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) ) ).thenReturn( content );

        final ReorderChildContentsParams reorderChildren = ReorderChildContentsParams.create().
            add( ReorderChildParams.create().contentToMove( ContentId.from( "content-id-1" ) ).contentToMoveBefore(
                ContentId.from( "content-id-2" ) ).build() ).
            add( ReorderChildParams.create().contentToMove( ContentId.from( "content-id-3" ) ).build() ).
            build();
        final ReorderChildContentsResult result = new ReorderChildContentsResult( 2 );
        Mockito.when( contentService.reorderChildren( Mockito.eq( reorderChildren ) ) ).thenReturn( result );

        String jsonString = request().path( "content/reorderChildren" ).
            entity( readFromFile( "resort_reorder_children_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 2 ) ).setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) );

        Mockito.verify( contentService, Mockito.times( 1 ) ).reorderChildren( Mockito.isA( ReorderChildContentsParams.class ) );

        assertJson( "reorder_children_success.json", jsonString );
    }

    @Test
    public void move_with_moveContentException()
    {
        MoveContentJson json = new MoveContentJson();
        json.setContentIds( asList( "id1", "id2", "id3", "id4" ) );
        json.setParentContentPath( "/root" );

        ContentResource contentResource = ( (ContentResource) getResourceInstance() );
        Mockito.when( contentService.move( Mockito.any() ) ).thenThrow( new MoveContentException( "" ) ).thenReturn( null );

        MoveContentResultJson resultJson = contentResource.move( json );

        assertEquals( 3, resultJson.getSuccesses().size() );
        assertEquals( 1, resultJson.getFailures().size() );
    }

    @Test
    public void getEffectivePermissions()
        throws Exception
    {
        final User user1 = createUser( "User 1" );
        final User user2 = createUser( "User 2" );
        final User user3 = createUser( "User 3" );
        final User user4 = createUser( "User 4" );
        final PrincipalKey groupA = PrincipalKey.ofGroup( SYSTEM, "groupA" );
        final PrincipalKey groupB = PrincipalKey.ofGroup( SYSTEM, "groupB" );
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( user1.getKey() ) ).thenReturn( Optional.of( user1 ) );
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( user2.getKey() ) ).thenReturn( Optional.of( user2 ) );
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( user3.getKey() ) ).thenReturn( Optional.of( user3 ) );
        Mockito.<Optional<? extends Principal>>when( securityService.getUser( user4.getKey() ) ).thenReturn( Optional.of( user4 ) );

        final PrincipalRelationships group1Memberships =
            PrincipalRelationships.from( PrincipalRelationship.from( groupA ).to( user1.getKey() ),
                                         PrincipalRelationship.from( groupA ).to( user2.getKey() ) );
        Mockito.when( this.securityService.getRelationships( eq( groupA ) ) ).thenReturn( group1Memberships );

        final PrincipalRelationships group2Memberships =
            PrincipalRelationships.from( PrincipalRelationship.from( groupB ).to( user3.getKey() ),
                                         PrincipalRelationship.from( groupA ).to( user4.getKey() ) );
        Mockito.when( this.securityService.getRelationships( eq( groupB ) ) ).thenReturn( group2Memberships );

        final Permission[] ACCESS_WRITE = {Permission.READ, Permission.CREATE, Permission.DELETE, Permission.MODIFY};
        final Permission[] ACCESS_PUBLISH = {Permission.READ, Permission.CREATE, Permission.DELETE, Permission.MODIFY, Permission.PUBLISH};
        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( user1.getKey() ).allowAll().build(),
                                  AccessControlEntry.create().principal( groupA ).allow( ACCESS_WRITE ).build(),
                                  AccessControlEntry.create().principal( groupB ).allow( Permission.READ ).build(),
                                  AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( ACCESS_PUBLISH ).build() );

        final PrincipalQueryResult totalUsers = PrincipalQueryResult.create().
            totalSize( 200 ).
            addPrincipals( asList( user1, user2, user3, user4 ) ).
            build();
        Mockito.when( this.securityService.query( any( PrincipalQuery.class ) ) ).thenReturn( totalUsers );

        Mockito.when( contentService.getPermissionsById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( permissions );

        String jsonString = request().path( "content/effectivePermissions" ).queryParam( "id", "/my_content" ).get().getAsString();

        assertJson( "get_effective_permissions_success.json", jsonString );
    }

    @Test
    public void deleteAttachment()
        throws Exception
    {
        Content content = Content.create().
            id( ContentId.from( "123" ) ).
            parentPath( ContentPath.ROOT ).
            name( "one" ).
            displayName( "one" ).
            type( ContentTypeName.folder() ).
            build();

        final BinaryReferences attachmentNames = BinaryReferences.from( "file1.jpg", "file2.txt" );
        class UpdateContentParamsMatcher
            extends ArgumentMatcher<UpdateContentParams>
        {
            public boolean matches( Object param )
            {
                final UpdateContentParams upc = (UpdateContentParams) param;
                return upc.getContentId().equals( content.getId() ) && upc.getRemoveAttachments().equals( attachmentNames );
            }
        }
        Mockito.when( contentService.update( argThat( new UpdateContentParamsMatcher() ) ) ).thenReturn( content );

        String jsonString = request().path( "content/deleteAttachment" ).
            entity( readFromFile( "delete_attachments_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_attachments_success.json", jsonString );
    }

    private User createUser( final String displayName )
    {
        final String userId = displayName.replace( " ", "" ).toLowerCase();
        return User.create().displayName( userId ).key( PrincipalKey.ofUser( SYSTEM, userId ) ).login( userId ).build();
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create().
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
            build();
    }

    private Site createSite( final String id, final String name, final String contentTypeName, SiteConfigs siteConfigs )
    {
        return Site.create().
            siteConfigs( siteConfigs ).
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.create().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( name ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            build();
    }

    private AccessControlList getTestPermissions()
    {
        return AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.from( "user:system:admin" ) ).allowAll().build(),
                                     AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build() );
    }
}
