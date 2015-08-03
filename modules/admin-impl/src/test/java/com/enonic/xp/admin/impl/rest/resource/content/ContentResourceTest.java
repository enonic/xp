package com.enonic.xp.admin.impl.rest.resource.content;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.admin.impl.rest.resource.MockRestResponse;
import com.enonic.xp.admin.impl.rest.resource.content.json.CountItemsWithChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentResultJson;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
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
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UnableToDeleteContentException;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyIdProviderAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.GetContentTypesParams;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static com.enonic.xp.security.acl.Permission.READ;

public class ContentResourceTest
    extends AbstractResourceTest
{
    private final LocalDate currentDate = LocalDate.of( 2013, 8, 23 );

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private SecurityService securityService;

    @Before
    public void before()
    {
        PropertyIdProviderAccessor.instance().set( new PropertyTree.PredictivePropertyIdProvider() );
    }

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
        assertEquals( response.getAsString(), "Content [/my_a_content] was not found" );
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
        assertEquals( response.getAsString(), "Content [/my_a_content] was not found" );
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
        assertEquals( response.getStatus(), 404 );
        assertEquals( response.getAsString(), "Content [aaa] was not found" );
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
        assertEquals( response.getAsString(), "Content [aaa] was not found" );
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
    public void generate_name()
        throws Exception
    {
        Mockito.when( contentService.generateContentName( "Some rea11y we!rd name..." ) ).thenReturn( "some-rea11y-werd-name" );

        String jsonString =
            request().path( "content/generateName" ).queryParam( "displayName", "Some rea11y we!rd name..." ).get().getAsString();

        assertJson( "generate_content_name.json", jsonString );
    }

    @Test
    public void delete_content_success()
        throws Exception
    {

        Content content =  Content.create().
            id( ContentId.from( "123" ) ).
            parentPath( ContentPath.ROOT ).
            name( "one" ).
            displayName( "one" ).
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

        final Content aContent2 =  Content.create().
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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

        Exception e = new ContentNotFoundException( ContentId.from( "content-id" ), ContentConstants.BRANCH_DRAFT );

        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenThrow( e );

        request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void update_content_nothing_updated()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Test
    public void publish_content_success()
        throws Exception
    {
        Mockito.when( contentService.push( Mockito.isA( PushContentParams.class ) ) ).thenReturn( PushContentsResult.create().
            addPushedContent( Contents.from(  Content.create().
                id( ContentId.from( "my-content" ) ).
                parentPath( ContentPath.ROOT ).
                name( "content" ).
                displayName( "My Content" ).
                build() ) ).
            addFailed(  Content.create().
                id( ContentId.from( "my-content2" ) ).
                parentPath( ContentPath.ROOT ).
                name( "content" ).
                displayName( "My Content" ).
                build(), PushContentsResult.FailedReason.PARENT_NOT_EXISTS ).
            build() );

        String jsonString = request().path( "content/publish" ).
            entity( readFromFile( "publish_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "publish_content_success.json", jsonString );
    }

    @Test
    public void publish_content_deleted()
        throws Exception
    {
        Mockito.when( contentService.push( Mockito.isA( PushContentParams.class ) ) ).thenReturn( PushContentsResult.create().
            addPushedContent( Contents.from(  Content.create().
                id( ContentId.from( "my-content" ) ).
                parentPath( ContentPath.ROOT ).
                name( "content" ).
                displayName( "My Content" ).
                build() ) ).
            addDeleted( Contents.from(  Content.create().
                id( ContentId.from( "myContentId" ) ).
                parentPath( ContentPath.ROOT ).
                name( "content" ).
                displayName( "My deleted content" ).
                build() ) ).
            build() );

        String jsonString = request().path( "content/publish" ).
            entity( readFromFile( "publish_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "publish_content_deleted.json", jsonString );
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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

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
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "myapplication:my-type" ) ) );

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
    public void countContentsWithDescendants_check_children_filtered()
    {
        Set<String> contentPaths = new HashSet<String>( Arrays.asList( "/root/a", "/root/a/b", "/root/c", "root/a/b/c" ) );

        CountItemsWithChildrenJson json = new CountItemsWithChildrenJson();
        json.setContentPaths( contentPaths );

        ContentResource contentResource = ( (ContentResource) getResourceInstance() );
        Mockito.when( this.contentService.find( Mockito.any() ) ).thenReturn( FindContentByQueryResult.create().totalHits( 0L ).build() );

        assertEquals( 2L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void countContentsWithDescendants_empty_json()
    {
        CountItemsWithChildrenJson json = new CountItemsWithChildrenJson();
        json.setContentPaths( new HashSet<String>() );

        ContentResource contentResource = ( (ContentResource) getResourceInstance() );

        assertEquals( 0L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void countContentsWithDescendants_no_children()
    {
        Set<String> contentPaths = new HashSet<String>( Arrays.asList( "/root/a", "/root/b", "/root/c" ) );

        CountItemsWithChildrenJson json = new CountItemsWithChildrenJson();
        json.setContentPaths( contentPaths );

        ContentResource contentResource = ( (ContentResource) getResourceInstance() );
        Mockito.when( this.contentService.find( Mockito.any() ) ).thenReturn( FindContentByQueryResult.create().totalHits( 0L ).build() );

        assertEquals( 3L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void move_with_moveContentException()
    {
        MoveContentJson json = new MoveContentJson();
        json.setContentIds( Arrays.asList( "id1", "id2", "id3", "id4" ) );
        json.setParentContentPath( "/root" );

        ContentResource contentResource = ( (ContentResource) getResourceInstance() );
        Mockito.when( contentService.move( Mockito.any() ) ).thenThrow( new MoveContentException( "" ) ).thenReturn( null );

        MoveContentResultJson resultJson = contentResource.move( json );

        assertEquals( 3, resultJson.getSuccesses().size() );
        assertEquals( 1, resultJson.getFailures().size() );
    }

    @Test
    public void resolve_publish_dependencies()
        throws Exception
    {
        Mockito.when( contentService.resolvePublishDependencies( Mockito.isA( ResolvePublishDependenciesParams.class ) ) ).thenReturn(
            ResolvePublishDependenciesResult.create().
                pushContentRequests( PushContentRequests.create().
                    addRequested( ContentId.from( "node1_1" ), ContentId.from( "node1_1" ) ).
                    addChildOf( ContentId.from( "node1_1_1" ), ContentId.from( "node1_1" ), ContentId.from( "node1_1" ) ).
                    addParentOf( ContentId.from( "node1" ), ContentId.from( "node1_1" ), ContentId.from( "node1_1" ) ).
                    build() ).
                build() );

        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.create().
            add( createContent( "node1", "node1_content", "myapplication:my_type" ) ).
            add( createContent( "node1_1", "node1_1_content", "myapplication:my_type" ) ).
            add( createContent( "node1_1_1", "node1_1_1_content", "myapplication:my_type" ) ).build() );

        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).thenReturn( CompareContentResults.create().
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "node1_1" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "node1" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "node1_1_1" ) ) ).
            build() );

        String jsonString = request().path( "content/resolvePublishDependencies" ).
            entity( readFromFile( "resolve_publish_dependencies_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "resolve_publish_dependencies.json", jsonString );
    }


    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return  Content.create().
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
