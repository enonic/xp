package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.json.content.ActiveContentVersionEntryJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultJson;
import com.enonic.xp.admin.impl.json.content.CompareContentResultsJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.json.content.ContentSummaryListJson;
import com.enonic.xp.admin.impl.json.content.ContentTreeSelectorListJson;
import com.enonic.xp.admin.impl.json.content.ContentVersionJson;
import com.enonic.xp.admin.impl.json.content.ContentVersionViewJson;
import com.enonic.xp.admin.impl.json.content.ContentsExistByPathJson;
import com.enonic.xp.admin.impl.json.content.ContentsExistJson;
import com.enonic.xp.admin.impl.json.content.GetActiveContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsForViewResultJson;
import com.enonic.xp.admin.impl.json.content.GetContentVersionsResultJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.CompareContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentIdsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentPathsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentTreeSelectorQueryJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetContentVersionsJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetDependenciesResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.GetDescendantsOfContents;
import com.enonic.xp.admin.impl.rest.resource.content.json.HasUnpublishedChildrenResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.LocaleListJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ReorderChildrenJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.SetActiveVersionJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.TaskResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UndoPendingDeleteContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UndoPendingDeleteContentResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.UnpublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.task.DeleteRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.DuplicateRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.MoveRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.PublishRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.UnpublishRunnableTask;
import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesParams;
import com.enonic.xp.content.GetPublishStatusesResult;
import com.enonic.xp.content.HasUnpublishedChildrenParams;
import com.enonic.xp.content.PublishStatus;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.ReorderChildParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;
import com.enonic.xp.content.SetActiveContentVersionResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
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
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.xdata.XDataName;
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
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.BinaryReferences;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.READ;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

public class ContentResourceTest
    extends AdminResourceTestSupport
{

    private static final UserStoreKey SYSTEM = UserStoreKey.system();

    private final LocalDate currentDate = LocalDate.of( 2013, 8, 23 );

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private SecurityService securityService;

    private TaskService taskService;

    private RelationshipTypeService relationshipTypeService;

    private BinaryExtractor binaryExtractor;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Override
    protected ContentResource getResourceInstance()
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

        binaryExtractor = Mockito.mock( BinaryExtractor.class );
        resource.setExtractor( binaryExtractor );

        taskService = Mockito.mock( TaskService.class );
        resource.setTaskService( taskService );

        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        resource.setRelationshipTypeService( relationshipTypeService );

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
    public void get_content_permissions_by_ids()
        throws Exception
    {
        final User admin = User.create().displayName( "Admin" ).key( PrincipalKey.from( "user:system:admin" ) ).login( "admin" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn(
            Optional.of( admin ) );
        final User anon = User.create().displayName( "Anonymous" ).key( PrincipalKey.ofAnonymous() ).login( "anonymous" ).build();
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn(
            Optional.of( anon ) );

        final Content content1 = createContent( "aaa", "my_a_content", "myapplication:my_type" );
        final Content content2 = createContent( "bbb", "my_b_content", "myapplication:my_type" );

        final AccessControlList permissions = getTestPermissions();

        Mockito.when( contentService.getPermissionsById( content1.getId() ) ).
            thenReturn( permissions );

        Mockito.when( contentService.getPermissionsById( content2.getId() ) ).
            thenReturn( AccessControlList.create().build() );

        String jsonString = request().path( "content/contentPermissionsByIds" ).entity( readFromFile( "get_permissions_by_ids.json" ),
                                                                                        MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "get_content_permissions_by_ids_success.json", jsonString );
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

    @Test
    public void update_content_new_name_occurred()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );

        MockRestResponse response = request().path( "content/update" ).
            entity( readFromFile( "update_content_params_new_name_occured.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post();

        assertEquals( HttpStatus.CONFLICT.value(), response.getStatus() );
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
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
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
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Test
    public void update_content_renamed_to_unnamed()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.rename( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_renamed_to_unnamed.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        ArgumentCaptor<RenameContentParams> argumentCaptor = ArgumentCaptor.forClass( RenameContentParams.class );

        Mockito.verify( contentService, Mockito.times( 1 ) ).rename( argumentCaptor.capture() );
        assertTrue( argumentCaptor.getValue().getNewName().hasUniqueness() );
    }

    @Test
    public void update_content_renamed()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.rename( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_renamed.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        ArgumentCaptor<RenameContentParams> argumentCaptor = ArgumentCaptor.forClass( RenameContentParams.class );

        Mockito.verify( contentService, Mockito.times( 1 ) ).rename( argumentCaptor.capture() );
        assertTrue( argumentCaptor.getValue().getNewName().toString().equals( "new-name" ) );
    }


    @Test
    public void update_content_success_publish_dates_are_updated()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params_with_publish_dates.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Test
    public void update_content_with_new_permissions()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.getPermissionsById( content.getId() ) ).
            thenReturn( AccessControlList.of( AccessControlEntry.create().
                allow( Permission.WRITE_PERMISSIONS ).
                principal( PrincipalKey.from( "user:store:user" ) ).
                build() ) );

        request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 1 ) ).applyPermissions( Mockito.any() );
    }

    @Test
    public void update_content_without_publish_from()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        final int status = request().path( "content/update" ).
            entity( readFromFile( "update_content_params_without_publish_from.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().
            getStatus();
        assertEquals( 422, status );
    }

    @Test
    public void update_content_with_invalid_publish_info()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );
        final int status = request().path( "content/update" ).
            entity( readFromFile( "update_content_params_with_invalid_publish_info.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().
            getStatus();
        assertEquals( 422, status );
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
    public void update_content_already_exists()
        throws Exception
    {
        Content content = createContent( "content-id", "new-content-name", "myapplication:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );

        Mockito.when( contentService.rename( Mockito.any() ) ).thenThrow(
            new ContentAlreadyExistsException( ContentPath.from( "/path" ) ) );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );

        Mockito.when( contentService.getPermissionsById( content.getId() ) ).thenReturn( AccessControlList.empty() );
        MockRestResponse response = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post();
        assertEquals( HttpStatus.CONFLICT.value(), response.getStatus() );
    }

    @Test
    public void reorderChildrenContents_not_allowed()
        throws Exception
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        content = Content.create( content ).childOrder( ChildOrder.defaultOrder() ).build();
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( content );
        Mockito.when( contentService.setChildOrder( Mockito.isA( SetContentChildOrderParams.class ) ) ).thenReturn( content );

        expectedEx.expect( WebApplicationException.class );
        expectedEx.expectMessage( "Not allowed to reorder children manually, current parentOrder = [_timestamp DESC]" );

        contentResource.reorderChildContents(
            new ReorderChildrenJson( false, false, content.getId().toString(), null, Lists.newArrayList() ) );
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
    public void countContentsWithDescendants_check_children_filtered()
    {
        Set<String> contentPaths = new HashSet<>( asList( "/root/a", "/root/a/b", "/root/c", "root/a/b/c" ) );

        GetDescendantsOfContents json = new GetDescendantsOfContents();
        json.setContentPaths( contentPaths );

        ContentResource contentResource = getResourceInstance();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 0L ).build() );

        assertEquals( 2L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void countContentsWithDescendants_empty_json()
    {
        GetDescendantsOfContents json = new GetDescendantsOfContents();
        json.setContentPaths( new HashSet<String>() );

        ContentResource contentResource = getResourceInstance();

        assertEquals( 0L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void countContentsWithDescendants_no_children()
    {
        Set<String> contentPaths = new HashSet<String>( asList( "/root/a", "/root/b", "/root/c" ) );

        GetDescendantsOfContents json = new GetDescendantsOfContents();
        json.setContentPaths( contentPaths );

        ContentResource contentResource = getResourceInstance();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 0L ).build() );

        assertEquals( 3L, contentResource.countContentsWithDescendants( json ) );
    }

    @Test
    public void has_unpublished_children()
        throws Exception
    {
        final Content contentA = Mockito.mock( Content.class );
        final Content contentB = Mockito.mock( Content.class );

        Mockito.when( contentA.getId() ).thenReturn( ContentId.from( "aaa" ) );
        Mockito.when( contentB.getId() ).thenReturn( ContentId.from( "bbb" ) );

        ContentResource contentResource = getResourceInstance();

        Mockito.when( contentService.hasUnpublishedChildren(
            new HasUnpublishedChildrenParams( contentA.getId(), ContentConstants.BRANCH_MASTER ) ) ).thenReturn( true );
        Mockito.when( contentService.hasUnpublishedChildren(
            new HasUnpublishedChildrenParams( contentB.getId(), ContentConstants.BRANCH_MASTER ) ) ).thenReturn( false );

        final HasUnpublishedChildrenResultJson result = contentResource.hasUnpublishedChildren(
            new ContentIdsJson( Arrays.asList( contentA.getId().toString(), contentB.getId().toString() ) ) );

        assertEquals(
            result.getContents().contains( new HasUnpublishedChildrenResultJson.HasUnpublishedChildrenJson( contentA.getId(), true ) ),
            true );
        assertEquals(
            result.getContents().contains( new HasUnpublishedChildrenResultJson.HasUnpublishedChildrenJson( contentB.getId(), false ) ),
            true );
    }

    @Test
    public void resolve_publish_contents()
        throws Exception
    {
        final ContentId requestedId = ContentId.from( "requested-contentId" );
        final ContentId dependantId = ContentId.from( "dependant-contentId" );
        final ContentId requiredId = ContentId.from( "required-contentId" );

        final CompareContentResult requested = new CompareContentResult( CompareStatus.NEW, requestedId );
        final CompareContentResult dependant = new CompareContentResult( CompareStatus.NEW, dependantId );
        final CompareContentResults results = CompareContentResults.create().
            add( requested ).
            add( dependant ).
            build();

        Mockito.when( contentService.resolvePublishDependencies( Mockito.isA( ResolvePublishDependenciesParams.class ) ) ).thenReturn(
            results );

        Mockito.when( contentService.resolveRequiredDependencies( Mockito.isA( ResolveRequiredDependenciesParams.class ) ) ).thenReturn(
            ContentIds.from( requiredId ) );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).thenReturn( results );
        Mockito.when( contentService.getPermissionsById( Mockito.isA( ContentId.class ) ) ).thenReturn( AccessControlList.empty() );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( dependantId ) ).totalHits( 1L ).build() );

        Mockito.doReturn( ContentIds.from( dependantId, requiredId ) ).when( this.contentService ).getInvalidContent(
            Mockito.isA( ContentIds.class ) );

        String jsonString = request().path( "content/resolvePublishContent" ).
            entity( readFromFile( "resolve_publish_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "resolve_publish_content.json", jsonString );
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

    @Test
    public void get_permitted_actions_for_admin()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SimpleSession( SessionKey.generate() ) );

        //checking that admin has all requested permissions
        String jsonString = request().
            path( "content/allowedActions" ).
            entity( readFromFile( "get_permitted_actions_params_root.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "[\"CREATE\",\"PUBLISH\",\"DELETE\"]", jsonString );

        //checking that admin has all permissions when no permissions set in request
        jsonString = request().
            path( "content/allowedActions" ).
            entity( readFromFile( "get_permitted_actions_params_root_all_permissions.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "get_permitted_actions_admin_allowed_all.json", jsonString );

    }

    @Test
    public void get_permitted_actions_single_content()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SimpleSession( SessionKey.generate() ) );

        final AccessControlList nodePermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( CREATE ).build() ).
            add( AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( DELETE ).build() ).
            build();

        Content content = Content.create().id( ContentId.from( "id" ) ).path( "/myroot/mysub" ).permissions( nodePermissions ).build();

        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( content ) );

        //["CREATE", "PUBLISH", "DELETE", "MODIFY"] permissions requested, checking  that only create and delete allowed on provided content
        String jsonString = request().
            path( "content/allowedActions" ).
            entity( readFromFile( "get_permitted_actions_params_single_content.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "[\"CREATE\",\"DELETE\"]", jsonString );

        //all root permissions requested for user, root allows only 'CREATE' and 'DELETE', checking that only 'CREATE' and 'DELETE' returned
        final AccessControlList rootPermissions = AccessControlList.create().
            add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( CREATE ).build() ).
            build();

        Mockito.when( contentService.getRootPermissions() ).thenReturn( rootPermissions );

        jsonString = request().
            path( "content/allowedActions" ).
            entity( readFromFile( "get_permitted_actions_params_root_all_permissions.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "[\"READ\",\"CREATE\"]", jsonString );
    }

    @Test
    public void get_permitted_actions_multiple_contents()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SimpleSession( SessionKey.generate() ) );

        final AccessControlList nodePermissions1 = AccessControlList.create().
            add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( READ ).build() ).
            add( AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( READ ).build() ).
            build();

        final AccessControlList nodePermissions2 = AccessControlList.create().
            add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( READ, CREATE ).build() ).
            add( AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( READ, CREATE, MODIFY, DELETE ).build() ).
            build();

        Content content1 = Content.create().id( ContentId.from( "id0" ) ).path( "/myroot/mysub" ).permissions( nodePermissions1 ).build();
        Content content2 = Content.create().id( ContentId.from( "id1" ) ).path( "/myroot/mysub2" ).permissions( nodePermissions2 ).build();

        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( content1, content2 ) );

        //requesting ["CREATE", "PUBLISH", "DELETE", "MODIFY"] on 2 contents, checking that nothing allowed because all contents must have required permissions
        String jsonString = request().
            path( "content/allowedActions" ).
            entity( readFromFile( "get_permitted_actions_params_multiple_contents.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "[]", jsonString );
    }

    @Test
    public void treeSelectorQuery_empty()
    {
        ContentResource contentResource = getResourceInstance();

        Mockito.when( this.contentService.findContentPaths( Mockito.isA( ContentQuery.class ) ) ).thenReturn( ContentPaths.empty() );

        ContentTreeSelectorQueryJson json = initContentTreeSelectorQueryJson( null );
        ContentTreeSelectorListJson result = contentResource.treeSelectorQuery( json );
        assertEquals( ContentTreeSelectorListJson.empty(), result );
    }

    @Test
    public void treeSelectorQuery()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", content1.getPath(), "content-name2", "myapplication:content-type" );
        Content content3 = createContent( "content-id3", content2.getPath(), "content-name3", "myapplication:content-type" );
        Content content4 = createContent( "content-id4", content3.getPath(), "content-name4", "myapplication:content-type" );

        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content1.getId() ) ) ) ).thenReturn(
            Contents.from( content1 ) );
        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content2.getId() ) ) ) ).thenReturn(
            Contents.from( content2 ) );
        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content3.getId() ) ) ) ).thenReturn(
            Contents.from( content3 ) );
        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content4.getId() ) ) ) ).thenReturn(
            Contents.from( content4 ) );

        Mockito.when( this.contentService.findContentPaths( Mockito.isA( ContentQuery.class ) ) ).
            thenReturn( ContentPaths.from( content4.getPath() ) );

        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).
            thenReturn( FindContentIdsByQueryResult.create().totalHits( 1L ).contents( ContentIds.from( content1.getId() ) ).build() );

        ContentTreeSelectorQueryJson json = initContentTreeSelectorQueryJson( null );
        ContentTreeSelectorListJson result = contentResource.treeSelectorQuery( json );
        assertEquals( result.getItems().get( 0 ).getContent().getId(), content1.getId().toString() );

        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).
            thenReturn( FindContentIdsByQueryResult.create().totalHits( 1L ).contents( ContentIds.from( content2.getId() ) ).build() );

        json = initContentTreeSelectorQueryJson( content1.getPath() );
        result = contentResource.treeSelectorQuery( json );
        assertEquals( result.getItems().get( 0 ).getContent().getId(), content2.getId().toString() );

        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).
            thenReturn( FindContentIdsByQueryResult.create().totalHits( 1L ).contents( ContentIds.from( content3.getId() ) ).build() );

        json = initContentTreeSelectorQueryJson( content2.getPath() );
        result = contentResource.treeSelectorQuery( json );
        assertEquals( result.getItems().get( 0 ).getContent().getId(), content3.getId().toString() );
    }

    @Test
    public void createMedia()
        throws Exception
    {
        ContentResource contentResource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );
        Mockito.when( multipartItem.getContentType() ).thenReturn( com.google.common.net.MediaType.JPEG );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );
        Mockito.when( multipartForm.getAsString( "content" ) ).thenReturn( "content-id" );
        Mockito.when( multipartForm.getAsString( "name" ) ).thenReturn( "name" );
        Mockito.when( multipartForm.getAsString( "parent" ) ).thenReturn( "/parentPath" );
        Mockito.when( multipartForm.getAsString( "focalX" ) ).thenReturn( "2" );
        Mockito.when( multipartForm.getAsString( "focalY" ) ).thenReturn( "1" );
        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        CreateMediaParams paramsss =
            new CreateMediaParams().name( "name" ).mimeType( "image/jpeg" ).byteSource( byteSource ).focalX( 2.0 ).focalY( 1.0 ).parent(
                ContentPath.from( "/parentPath" ) );

        Mockito.when( this.contentService.create( paramsss ) ).thenReturn( content );

        contentResource.createMedia( multipartForm );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).create( paramsss );

    }

    @Test
    public void updateMedia()
    {
        ContentResource contentResource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );
        Mockito.when( multipartItem.getContentType() ).thenReturn( com.google.common.net.MediaType.JPEG );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );
        Mockito.when( multipartForm.getAsString( "content" ) ).thenReturn( "content-id" );
        Mockito.when( multipartForm.getAsString( "name" ) ).thenReturn( "name" );
        Mockito.when( multipartForm.getAsString( "focalX" ) ).thenReturn( "2" );
        Mockito.when( multipartForm.getAsString( "focalY" ) ).thenReturn( "1" );
        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        UpdateMediaParams params =
            new UpdateMediaParams().content( ContentId.from( "content-id" ) ).name( "name" ).mimeType( "image/jpeg" ).byteSource(
                byteSource ).focalX( 2.0 ).focalY( 1.0 );

        Mockito.when( this.contentService.update( params ) ).thenReturn( content );

        contentResource.updateMedia( multipartForm );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).update( params );

    }

    @Test
    public void undoPendingDelete()
    {
        ContentResource contentResource = getResourceInstance();
        Content content = createContent( "content-id1", "content-name1", "myapplication:content-type" );

        Mockito.when( this.contentService.undoPendingDelete(
            UndoPendingDeleteContentParams.create().contentIds( ContentIds.from( content.getId() ) ).target(
                ContentConstants.BRANCH_MASTER ).build() ) ).thenReturn( 1 );

        UndoPendingDeleteContentJson params = new UndoPendingDeleteContentJson();
        params.setContentIds( Lists.newArrayList( ContentIds.from( content.getId() ).asStrings() ) );

        UndoPendingDeleteContentResultJson result = contentResource.undoPendingDelete( params );

        assertEquals( 1, result.getSuccess() );
    }

    @Test
    public void getDependencies()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        Mockito.when( contentService.getDependencies( content1.getId() ) ).thenReturn( ContentDependencies.create().inboundDependencies(
            Collections.singleton( new ContentDependenciesAggregation( ContentTypeName.folder(), 2L ) ) ).outboundDependencies(
            Collections.singleton( new ContentDependenciesAggregation( ContentTypeName.media(), 1L ) ) ).build() );

        Mockito.when( contentService.getDependencies( content2.getId() ) ).thenReturn(
            ContentDependencies.create().inboundDependencies( Sets.newHashSet() ).outboundDependencies( Sets.newHashSet() ).build() );

        GetDependenciesResultJson result = contentResource.getDependencies(
            new ContentIdsJson( Lists.asList( content1.getId().toString(), content2.getId().toString(), new String[]{} ) ) );

        assertEquals( 1, result.getDependencies().get( content1.getId().toString() ).getInbound().size() );
        assertEquals( 2L, result.getDependencies().get( content1.getId().toString() ).getInbound().get( 0 ).getCount() );
        assertEquals( ContentTypeName.folder().toString(),
                      result.getDependencies().get( content1.getId().toString() ).getInbound().get( 0 ).getType() );

        assertEquals( 1, result.getDependencies().get( content1.getId().toString() ).getOutbound().size() );
        assertEquals( 1L, result.getDependencies().get( content1.getId().toString() ).getOutbound().get( 0 ).getCount() );
        assertEquals( ContentTypeName.media().toString(),
                      result.getDependencies().get( content1.getId().toString() ).getOutbound().get( 0 ).getType() );
    }

    @Test
    public void duplicate()
    {
        ContentResource contentResource = getResourceInstance();
        Mockito.when( taskService.submitTask( Mockito.isA( DuplicateRunnableTask.class ), eq( "Duplicate content" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        TaskResultJson result = contentResource.duplicate( new DuplicateContentsJson( Lists.newArrayList() ) );

        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void move()
    {
        ContentResource contentResource = getResourceInstance();
        Mockito.when( taskService.submitTask( Mockito.isA( MoveRunnableTask.class ), eq( "Move content" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        TaskResultJson result = contentResource.move( new MoveContentJson() );

        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void delete()
    {
        ContentResource contentResource = getResourceInstance();
        Mockito.when( taskService.submitTask( Mockito.isA( DeleteRunnableTask.class ), eq( "Delete content" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        TaskResultJson result = contentResource.delete( new DeleteContentJson() );

        assertEquals( "task-id", result.getTaskId() );
    }


    @Test
    public void publish()
    {
        ContentResource contentResource = getResourceInstance();
        Mockito.when( taskService.submitTask( Mockito.isA( PublishRunnableTask.class ), eq( "Publish content" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        TaskResultJson result = contentResource.publish( new PublishContentJson() );

        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void unpublish()
    {
        ContentResource contentResource = getResourceInstance();
        Mockito.when( taskService.submitTask( Mockito.isA( UnpublishRunnableTask.class ), eq( "Unpublish content" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        TaskResultJson result = contentResource.unpublish( new UnpublishContentJson() );

        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void getByIds_null()
        throws Exception
    {
        expectedEx.expect( WebApplicationException.class );
        expectedEx.expectMessage( "Contents [[content-id1, content-id2]] was not found" );

        ContentResource contentResource = getResourceInstance();

        ContentSummaryListJson result =
            contentResource.getByIds( new ContentIdsJson( Lists.asList( "content-id1", "content-id2", new String[]{} ) ) );

    }

    @Test
    public void getByIds()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );

        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        Mockito.when( contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content1.getId(), content2.getId() ) ) ) ).
            thenReturn( Contents.from( content1, content2 ) );

        ContentSummaryListJson result = contentResource.getByIds(
            new ContentIdsJson( Lists.asList( content1.getId().toString(), content2.getId().toString(), new String[]{} ) ) );

        assertEquals( 2L, result.getMetadata().getHits() );
        assertEquals( 2L, result.getMetadata().getTotalHits() );

        assertEquals( 2, result.getContents().size() );
        assertEquals( new ContentSummaryJson( content1, new ContentIconUrlResolver( contentTypeService ) ), result.getContents().get( 0 ) );
        assertEquals( new ContentSummaryJson( content2, new ContentIconUrlResolver( contentTypeService ) ), result.getContents().get( 1 ) );
    }

    @Test
    public void checkContentsReadOnly()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = Content.create( createContent( "content-id1", "content-name1", "myapplication:content-type" ) ).permissions(
            AccessControlList.create().add(
                AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allowAll().build() ).build() ).build();

        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        Mockito.when( contentService.getByIds( new GetContentByIdsParams( ContentIds.from( content1.getId(), content2.getId() ) ) ) ).
            thenReturn( Contents.from( content1, content2 ) );

        List<String> result = contentResource.checkContentsReadOnly(
            new ContentIdsJson( Lists.asList( content1.getId().toString(), content2.getId().toString(), new String[]{} ) ) );

        assertEquals( 1, result.size() );
        assertEquals( content2.getId().toString(), result.get( 0 ) );
    }

    @Test
    public void contentsExists()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        Mockito.when( contentService.contentExists( content2.getId() ) ).thenReturn( true );

        ContentsExistJson result = contentResource.contentsExist(
            new ContentIdsJson( Lists.asList( content1.getId().toString(), content2.getId().toString(), new String[]{} ) ) );

        assertFalse( result.getContentsExistJson().get( 0 ).exists() );
        assertTrue( result.getContentsExistJson().get( 1 ).exists() );
    }

    @Test
    public void contentsExistByPath()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        Mockito.when( contentService.contentExists( content2.getPath() ) ).thenReturn( true );

        ContentsExistByPathJson result = contentResource.contentsExistByPath(
            new ContentPathsJson( Lists.asList( content1.getPath().toString(), content2.getPath().toString(), new String[]{} ) ) );

        assertFalse( result.getContentsExistJson().get( 0 ).exists() );
        assertTrue( result.getContentsExistJson().get( 1 ).exists() );
    }

    @Test
    public void getNearest_null()
    {
        ContentResource contentResource = getResourceInstance();

        ContentJson result = contentResource.getNearest( new GetNearestSiteJson( "non-existed-content-id" ) );

        assertEquals( null, result );
    }

    @Test
    public void getNearest()
    {
        ContentResource contentResource = getResourceInstance();

        PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "A", 1L );
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( siteConfigConfig ).
            build();

        Site site = createSite( "aaa", "my_a_content", "myapplication:my_type", SiteConfigs.from( siteConfig ) );

        Mockito.when( contentService.getNearestSite( site.getId() ) ).thenReturn( site );

        ContentJson result = contentResource.getNearest( new GetNearestSiteJson( site.getId().toString() ) );

        assertEquals(
            new ContentJson( site, new ContentIconUrlResolver( contentTypeService ), new ContentPrincipalsResolver( securityService ) ),
            result );
    }

    @Test
    public void getDescendantsOfContents()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        GetDescendantsOfContents params = new GetDescendantsOfContents();
        params.setContentPaths( Sets.newHashSet( content1.getPath().toString(), content2.getPath().toString() ) );

        final ArgumentCaptor<ContentQuery> argumentCaptor = ArgumentCaptor.forClass( ContentQuery.class );

        FindContentIdsByQueryResult findResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.empty() ).hits( 1L ).totalHits( 10L ).contents(
                ContentIds.from( content1.getId(), content2.getId() ) ).build();

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );

        List<ContentIdJson> result = contentResource.getDescendantsOfContents( params );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).find( argumentCaptor.capture() );

        assertEquals(
            "((_path LIKE '/content/content-name2/*' OR _path LIKE '/content/content-name1/*') AND _path NOT IN ('/content/content-name2', '/content/content-name1')) ORDER BY _path ASC",
            argumentCaptor.getValue().getQueryExpr().toString() );

        assertTrue( result.contains( new ContentIdJson( content1.getId() ) ) );
        assertTrue( result.contains( new ContentIdJson( content2.getId() ) ) );
    }

    @Test
    public void getDescendantsOfContents_filtered()
    {
        ContentResource contentResource = getResourceInstance();

        Content content1 = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content2 = createContent( "content-id2", "content-name2", "myapplication:content-type" );

        GetDescendantsOfContents params = new GetDescendantsOfContents();
        params.setContentPaths( Sets.newHashSet( content1.getPath().toString(), content2.getPath().toString() ) );
        params.setFilterStatuses( Collections.singleton( CompareStatus.NEW ) );

        final ArgumentCaptor<ContentQuery> argumentCaptor = ArgumentCaptor.forClass( ContentQuery.class );

        FindContentIdsByQueryResult findResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.empty() ).hits( 1L ).totalHits( 10L ).contents(
                ContentIds.from( content1.getId(), content2.getId() ) ).build();

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );

        Mockito.when( contentService.compare( new CompareContentsParams( ContentIds.from( content1.getId(), content2.getId() ),
                                                                         ContentConstants.BRANCH_MASTER ) ) ).thenReturn(
            CompareContentResults.create().add( new CompareContentResult( CompareStatus.NEW, content1.getId() ) ).build() );

        List<ContentIdJson> result = contentResource.getDescendantsOfContents( params );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).find( argumentCaptor.capture() );

        assertEquals(
            "((_path LIKE '/content/content-name2/*' OR _path LIKE '/content/content-name1/*') AND _path NOT IN ('/content/content-name2', '/content/content-name1')) ORDER BY _path ASC",
            argumentCaptor.getValue().getQueryExpr().toString() );

        assertTrue( result.contains( new ContentIdJson( content1.getId() ) ) );
        assertEquals( 1, result.size() );
    }

    @Test
    public void query()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id1", "content-name1", "myapplication:content-type" );

        Aggregations aggregations = Aggregations.from( Aggregation.bucketAggregation( "aggregation" ).buckets(
            Buckets.create().add( Bucket.create().key( "bucketKey" ).docCount( 1 ).build() ).build() ).build() );

        FindContentIdsByQueryResult findResult =
            FindContentIdsByQueryResult.create().aggregations( aggregations ).hits( 1L ).totalHits( 10L ).contents(
                ContentIds.from( content.getId() ) ).build();

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );

        Mockito.when( contentService.getByIds( new GetContentByIdsParams( findResult.getContentIds() ) ) ).thenReturn(
            Contents.from( content ) );

        AbstractContentQueryResultJson result =
            contentResource.query( new ContentQueryJson( "", 0, 10, Lists.newArrayList(), null, null, null, null ) );

        assertEquals( 1, result.getContents().size() );
        assertTrue( result.getContents().contains( new ContentIdJson( content.getId() ) ) );

        assertEquals( 10L, result.getMetadata().getTotalHits() );
        assertEquals( 1L, result.getMetadata().getHits() );
        assertEquals( new BucketAggregationJson( (BucketAggregation) aggregations.get( "aggregation" ) ),
                      result.getAggregations().toArray()[0] );
    }


    @Test
    public void selectorQuery()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id1", "content-name1", "myapplication:content-type" );

        Aggregations aggregations = Aggregations.from( Aggregation.bucketAggregation( "aggregation" ).buckets(
            Buckets.create().add( Bucket.create().key( "bucketKey" ).docCount( 1 ).build() ).build() ).build() );

        FindContentIdsByQueryResult findResult =
            FindContentIdsByQueryResult.create().aggregations( aggregations ).hits( 1L ).totalHits( 10L ).contents(
                ContentIds.from( content.getId() ) ).build();

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );

        Mockito.when( contentService.getByIds( new GetContentByIdsParams( findResult.getContentIds() ) ) ).thenReturn(
            Contents.from( content ) );

        AbstractContentQueryResultJson result = contentResource.selectorQuery(
            new ContentTreeSelectorQueryJson( "", 0, 10, null, null, null, Lists.newArrayList(), Lists.newArrayList(), null, null, null ) );

        assertEquals( 1, result.getContents().size() );
        assertTrue( result.getContents().contains( new ContentIdJson( content.getId() ) ) );

        assertEquals( 10L, result.getMetadata().getTotalHits() );
        assertEquals( 1L, result.getMetadata().getHits() );
        assertEquals( new BucketAggregationJson( (BucketAggregation) aggregations.get( "aggregation" ) ),
                      result.getAggregations().toArray()[0] );
    }

    @Test
    public void compare()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id1", "content-name1", "myapplication:content-type" );

        CompareContentsJson params = new CompareContentsJson();
        params.ids = Collections.singleton( content.getId().toString() );

        CompareContentResult compareContentResult = new CompareContentResult( CompareStatus.NEW, content.getId() );

        Mockito.when( contentService.compare(
            new CompareContentsParams( ContentIds.from( params.ids ), ContentConstants.BRANCH_MASTER ) ) ).thenReturn(
            CompareContentResults.create().add( new CompareContentResult( CompareStatus.NEW, content.getId() ) ).build() );

        GetPublishStatusResult getPublishStatusResult = new GetPublishStatusResult( content.getId(), PublishStatus.ONLINE );

        Mockito.when( contentService.getPublishStatuses(
            new GetPublishStatusesParams( ContentIds.from( params.ids ), ContentConstants.BRANCH_DRAFT ) ) ).thenReturn(
            GetPublishStatusesResult.create().add( getPublishStatusResult ).build() );

        CompareContentResultsJson result = contentResource.compare( params );

        assertTrue(
            result.getCompareContentResults().contains( new CompareContentResultJson( compareContentResult, getPublishStatusResult ) ) );

    }

    @Test
    public void updateThumbnail()
        throws IOException
    {
        ContentResource contentResource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );
        Mockito.when( multipartItem.getContentType() ).thenReturn( com.google.common.net.MediaType.JPEG );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );
        Mockito.when( multipartForm.getAsString( "name" ) ).thenReturn( "_thumbnail" );
        Mockito.when( multipartForm.getAsString( "id" ) ).thenReturn( "id" );
        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        Map<String, List<String>> data = Maps.newHashMap();
        data.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( com.google.common.net.MediaType.JPEG.toString() ) );

        ExtractedData extractedData = ExtractedData.create().
            metadata( data ).
            text( "myTextValue" ).
            imageOrientation( "1" ).
            build();

        Mockito.when( this.binaryExtractor.extract( Mockito.any() ) ).thenReturn( extractedData );

        final UpdateContentParams params = new UpdateContentParams().
            contentId( ContentId.from( multipartForm.getAsString( "id" ) ) ).
            createAttachments( CreateAttachments.from(
                CreateAttachment.create().mimeType( "image/jpeg" ).name( "_thumbnail" ).byteSource( byteSource ).text(
                    "myTextValue" ).build() ) );

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        Mockito.when( this.contentService.update( params ) ).thenReturn( content );

        contentResource.updateThumbnail( multipartForm );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).update( params );
    }


    @Test
    public void createAttachment()
        throws IOException
    {
        ContentResource contentResource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );
        Mockito.when( multipartItem.getContentType() ).thenReturn( com.google.common.net.MediaType.JPEG );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );
        Mockito.when( multipartForm.getAsString( "name" ) ).thenReturn( "name" );
        Mockito.when( multipartForm.getAsString( "id" ) ).thenReturn( "id" );
        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        Map<String, List<String>> data = Maps.newHashMap();
        data.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( com.google.common.net.MediaType.JPEG.toString() ) );

        ExtractedData extractedData = ExtractedData.create().
            metadata( data ).
            text( "myTextValue" ).
            imageOrientation( "1" ).
            build();

        Mockito.when( this.binaryExtractor.extract( Mockito.any() ) ).thenReturn( extractedData );

        final UpdateContentParams params = new UpdateContentParams().
            contentId( ContentId.from( multipartForm.getAsString( "id" ) ) ).
            createAttachments( CreateAttachments.from(
                CreateAttachment.create().mimeType( "image/jpeg" ).name( "name" ).byteSource( byteSource ).text(
                    "myTextValue" ).build() ) );

        Content content = Mockito.mock( Content.class );
        Attachment attachment = Attachment.create().
            name( "name" ).
            mimeType( "image/jpeg" ).
            size( 666 ).
            build();

        Mockito.when( content.getAttachments() ).thenReturn( Attachments.create().add( attachment ).build() );

        Mockito.when( this.contentService.update( params ) ).thenReturn( content );

        contentResource.createAttachment( multipartForm );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).update( params );
    }

    @Test
    public void getContentVersions()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        ContentVersion contentVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( Instant.now() ).
            modifier( PrincipalKey.ofAnonymous() ).
            build();

        Mockito.when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn( (Optional) Optional.of( User.ANONYMOUS ) );

        FindContentVersionsParams params = FindContentVersionsParams.create().contentId( content.getId() ).from( 0 ).size( 10 ).build();
        FindContentVersionsResult getVersionsResult = FindContentVersionsResult.create().contentVersions(
            ContentVersions.create().contentId( content.getId() ).add( contentVersion ).build() ).build();

        Mockito.when( contentService.getVersions( params ) ).thenReturn( getVersionsResult );

        GetContentVersionsResultJson result =
            contentResource.getContentVersions( new GetContentVersionsJson( 0, 10, content.getId().toString() ) );

        assertEquals( new ContentVersionJson( contentVersion, User.ANONYMOUS ), result.getContentVersions().toArray()[0] );

    }

    @Test
    public void getActiveVersions()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );

        ContentVersion contentVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( Instant.now() ).
            modifier( PrincipalKey.ofAnonymous() ).
            build();

        Mockito.when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn( (Optional) Optional.of( User.ANONYMOUS ) );

        Mockito.when( contentService.getActiveVersions( GetActiveContentVersionsParams.create().
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            contentId( content.getId() ).
            build() ) ).thenReturn( GetActiveContentVersionsResult.create().add(
            ActiveContentVersionEntry.from( ContentConstants.BRANCH_DRAFT, contentVersion ) ).build() );

        GetActiveContentVersionsResultJson result = contentResource.getActiveVersions( content.getId().toString() );

        assertEquals( new ContentVersionJson( contentVersion, User.ANONYMOUS ),
                      ( (ActiveContentVersionEntryJson) result.getActiveContentVersions().toArray()[0] ).getContentVersion() );
    }

    @Test
    public void getContentVersionsForView()
    {
        ContentResource contentResource = getResourceInstance();

        Content content = createContent( "content-id", "content-name", "myapplication:content-type" );
        ContentVersion contentVersion = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( Instant.now() ).
            modifier( PrincipalKey.ofAnonymous() ).
            build();

        Mockito.when( securityService.getPrincipal( PrincipalKey.ofAnonymous() ) ).thenReturn( (Optional) Optional.of( User.ANONYMOUS ) );

        FindContentVersionsParams params = FindContentVersionsParams.create().contentId( content.getId() ).from( 0 ).size( 10 ).build();
        FindContentVersionsResult getVersionsResult = FindContentVersionsResult.create().contentVersions(
            ContentVersions.create().contentId( content.getId() ).add( contentVersion ).build() ).build();

        Mockito.when( contentService.getVersions( params ) ).thenReturn( getVersionsResult );

        Mockito.when( contentService.getActiveVersions( GetActiveContentVersionsParams.create().
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            contentId( content.getId() ).
            build() ) ).thenReturn( GetActiveContentVersionsResult.create().add(
            ActiveContentVersionEntry.from( ContentConstants.BRANCH_DRAFT, contentVersion ) ).build() );

        GetContentVersionsForViewResultJson result =
            contentResource.getContentVersionsForView( new GetContentVersionsJson( 0, 10, content.getId().toString() ) );

        assertEquals( new ContentVersionJson( contentVersion, User.ANONYMOUS ), result.getActiveVersion().getContentVersion() );
        assertEquals( new ContentVersionViewJson( contentVersion, User.ANONYMOUS,
                                                  Collections.singletonList( ContentConstants.BRANCH_DRAFT.toString() ) ),
                      result.getContentVersions().toArray()[0] );
    }

    @Test
    public void getAttachments()
    {
        ContentResource contentResource = getResourceInstance();

        Attachment attachment = Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            size( 6789 ).
            build();

        Content content = Content.create().
            id( ContentId.from( "123" ) ).
            parentPath( ContentPath.ROOT ).
            name( "one" ).
            displayName( "one" ).
            type( ContentTypeName.folder() ).attachments( Attachments.create().add( attachment ).build() ).
            build();

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        List<AttachmentJson> result = contentResource.getAttachments( content.getId().toString() );

        assertEquals( new AttachmentJson( attachment ), result.get( 0 ) );


    }

    @Test
    public void setActiveVersion()
    {
        ContentResource contentResource = getResourceInstance();

        ContentId contentId = ContentId.from( "content-id" );
        ContentVersionId contentVersionId = ContentVersionId.from( "version-1.0" );

        Mockito.when( this.contentService.setActiveContentVersion( contentId, contentVersionId ) ).thenReturn(
            new SetActiveContentVersionResult( contentId, contentVersionId ) );

        ContentIdJson result = contentResource.setActiveVersion( new SetActiveVersionJson( contentId, contentVersionId ) );

        assertEquals( contentId.toString(), result.getId() );
    }

    @Test
    public void getLocales()
    {
        ContentResource contentResource = getResourceInstance();

        Locale[] availableLocales = Stream.of( Locale.getAvailableLocales() ).filter(
            locale -> StringUtils.isNotEmpty( locale.toLanguageTag() ) && StringUtils.isNotEmpty( locale.getDisplayName() ) ).toArray(
            Locale[]::new );

        if ( availableLocales.length > 0 )
        {
            Locale locale = availableLocales[0];

            assertTrue( contentResource.getLocales( locale.toLanguageTag() ).getLocales().contains( new LocaleJson( locale ) ) );
            if ( StringUtils.isNotEmpty( ( locale.getDisplayName( locale ) ) ) )
            {
                assertTrue(
                    contentResource.getLocales( locale.getDisplayName( locale ) ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getLanguage() ) ) )
            {
                assertTrue( contentResource.getLocales( locale.getLanguage() ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getDisplayLanguage( locale ) ) ) )
            {
                assertTrue(
                    contentResource.getLocales( locale.getDisplayLanguage( locale ) ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getVariant() ) ) )
            {
                assertTrue( contentResource.getLocales( locale.getVariant() ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getDisplayVariant( locale ) ) ) )
            {
                assertTrue(
                    contentResource.getLocales( locale.getDisplayVariant( locale ) ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getCountry() ) ) )
            {
                assertTrue( contentResource.getLocales( locale.getCountry() ).getLocales().contains( new LocaleJson( locale ) ) );
            }
            if ( StringUtils.isNotEmpty( ( locale.getDisplayCountry( locale ) ) ) )
            {
                assertTrue(
                    contentResource.getLocales( locale.getDisplayCountry( locale ) ).getLocales().contains( new LocaleJson( locale ) ) );
            }
        }

    }

    @Test
    public void getLocales_all()
    {
        ContentResource contentResource = getResourceInstance();

        Locale[] expectedLocales = Arrays.stream( Locale.getAvailableLocales() ).
            filter( ( locale ) -> StringUtils.isNotEmpty( locale.toLanguageTag() ) && StringUtils.isNotEmpty( locale.getDisplayName() ) ).
            toArray( Locale[]::new );

        LocaleListJson result = contentResource.getLocales( "" );
        assertEquals( expectedLocales.length, result.getLocales().size() );
    }


    @Test
    public void listChildrenIds()
    {
        ContentResource contentResource = getResourceInstance();

        Content parentContent = createContent( "content-id1", "content-name1", "myapplication:content-type" );
        Content content1 = createContent( "content-id2", parentContent.getPath(), "content-name2", "myapplication:content-type" );
        Content content2 = createContent( "content-id3", parentContent.getPath(), "content-name3", "myapplication:content-type" );
        Content content3 = createContent( "content-id4", parentContent.getPath(), "content-name4", "myapplication:content-type" );

        FindContentByParentParams params = FindContentByParentParams.create().
            parentId( parentContent.getId() ).
            childOrder( ChildOrder.defaultOrder() ).
            build();

        Mockito.when( this.contentService.findIdsByParent( params ) ).thenReturn( FindContentIdsByParentResult.create().contentIds(
            ContentIds.from( content1.getId(), content2.getId(), content3.getId() ) ).build() );

        List<ContentIdJson> result =
            contentResource.listChildrenIds( parentContent.getId().toString(), ChildOrder.defaultOrder().toString() );

        assertTrue( result.contains( new ContentIdJson( content1.getId() ) ) );
        assertTrue( result.contains( new ContentIdJson( content2.getId() ) ) );
        assertTrue( result.contains( new ContentIdJson( content3.getId() ) ) );
    }

    private ContentTreeSelectorQueryJson initContentTreeSelectorQueryJson( final ContentPath parentPath )
    {
        final ContentTreeSelectorQueryJson json = Mockito.mock( ContentTreeSelectorQueryJson.class );

        Mockito.when( json.getFrom() ).thenReturn( 0 );
        Mockito.when( json.getSize() ).thenReturn( -1 );
        Mockito.when( json.getQueryExprString() ).thenReturn( "" );
        Mockito.when( json.getContentTypeNames() ).thenReturn( Collections.emptyList() );
        Mockito.when( json.getParentPath() ).thenReturn( parentPath );

        return json;
    }

    private User createUser( final String displayName )
    {
        final String userId = displayName.replace( " ", "" ).toLowerCase();
        return User.create().displayName( userId ).key( PrincipalKey.ofUser( SYSTEM, userId ) ).login( userId ).build();
    }

    private Content createContent( final String id, final ContentPath parentPath, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create().
            id( ContentId.from( id ) ).
            parentPath( parentPath ).
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
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                first( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build() ).
            build();
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return this.createContent( id, ContentPath.ROOT, name, contentTypeName );
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
