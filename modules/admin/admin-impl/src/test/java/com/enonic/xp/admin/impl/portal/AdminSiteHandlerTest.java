package com.enonic.xp.admin.impl.portal;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminSiteHandlerTest
    extends BaseHandlerTest
{
    private AdminSiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    private ContentService contentService;

    private ProjectService projectService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.contentService = mock( ContentService.class );
        this.projectService = mock( ProjectService.class );

        this.handler =
            new AdminSiteHandler( contentService, projectService, mock( ExceptionMapper.class ), mock( ExceptionRenderer.class ) );
        this.handler.activate( mock( AdminConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );

        this.request = new WebRequest();
        this.request.setRawRequest( rawRequest );

        this.response = WebResponse.create().build();
    }

    @Test
    public void testCanHandle()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCanHandleRootContent()
    {
        this.request.setRawPath( "/admin/site/repo/master" );
        assertTrue( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/admin/site/repo/master/" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCannotHandle()
    {
        this.request.setRawPath( "/admin/repo/master/content/1" );
        assertFalse( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCreatePortalRequestWithoutMode()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertThrows( WebException.class, () -> this.handler.createPortalRequest( this.request, this.response ) );
    }

    @Test
    public void testCreatePortalRequest()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/content/1" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/content/1", portalRequest.getContentPath().toString() );
        assertEquals( "edit", portalRequest.getMode().toString() );
    }

    @Test
    public void testInlineAssetRequest()
    {
        this.request.setRawPath( "/admin/site/inline/repo/draft/_/asset/com.enonic.app.superhero:1622131535374/css/style.css" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/inline", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "draft", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.INLINE, portalRequest.getMode() );
    }

    @Test
    public void testCreatePortalRequestRootContentPath()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    public void testCreatePortalRequestEmptyContentPath()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    void createPortalRequestWithRootContent()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        this.request.setRawPath( "/admin/site/preview/myrepo/master" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( ContentPath.ROOT, portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( project, portalRequest.getProject() );
        assertEquals( RenderMode.PREVIEW, portalRequest.getMode() );
    }

    @Test
    void createPortalRequestWithContentAndNearestSite()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        final Content content = newContent();
        when( contentService.getByPath( eq( ContentPath.from( "/mysite/landing-page" ) ) ) ).thenReturn( content );

        final Site site = newSite();
        when( this.contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );

        this.request.setRawPath( "/admin/site/edit/myrepo/master/mysite/landing-page" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( content.getPath(), portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( content, portalRequest.getContent() );
        assertEquals( site, portalRequest.getSite() );
        assertEquals( project, portalRequest.getProject() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    void createPortalRequestWithContentWhichIsSiteEditMode()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        final Site site = newSite();
        when( contentService.getByPath( eq( ContentPath.from( "/mysite" ) ) ) ).thenReturn( site );

        this.request.setRawPath( "/admin/site/edit/myrepo/master/mysite" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( site.getPath(), portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( site, portalRequest.getContent() );
        assertEquals( site, portalRequest.getSite() );
        assertEquals( project, portalRequest.getProject() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    void createPortalRequestWithContentAndNearestSiteEdit()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        final Content content = newContent();
        when( contentService.getById( eq( ContentId.from( "contentId" ) ) ) ).thenReturn( content );

        final Site site = newSite();
        when( this.contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );

        this.request.setRawPath( "/admin/site/edit/myrepo/master/contentId" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( content.getPath(), portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( content, portalRequest.getContent() );
        assertEquals( site, portalRequest.getSite() );
        assertEquals( project, portalRequest.getProject() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }


    @Test
    void createPortalRequestWithContentAndNearestSiteEditContentNotFound()
    {
        final Branch branch = Branch.from( "master" );
        final Project project = mock( Project.class );
        final ProjectName projectName = ProjectName.from( "myrepo" );

        when( projectService.get( eq( projectName ) ) ).thenReturn( project );

        final ContentNotFoundException contentNotFoundException = ContentNotFoundException.create()
            .contentId( ContentId.from( "contentId" ) )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .contentRoot( NodePath.ROOT )
            .build();

        when( contentService.getById( eq( ContentId.from( "contentId" ) ) ) ).thenThrow( contentNotFoundException );

        this.request.setRawPath( "/admin/site/edit/myrepo/master/contentId" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( "/contentId", portalRequest.getContentPath().toString() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
        assertEquals( project, portalRequest.getProject() );
        assertNull( portalRequest.getContent() );
        assertNull( portalRequest.getSite() );
    }

    @Test
    void createPortalRequestWithContentAndFindNearestSitePreview()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        final Content content = newContent();
        when( contentService.getByPath( eq( ContentPath.from( "/mysite/landing-page" ) ) ) ).thenReturn( content );

        final Site site = newSite();
        when( this.contentService.findNearestSiteByPath( eq( content.getPath() ) ) ).thenReturn( site );

        this.request.setRawPath( "/admin/site/preview/myrepo/master/mysite/landing-page" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( content.getPath(), portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( content, portalRequest.getContent() );
        assertEquals( site, portalRequest.getSite() );
        assertEquals( project, portalRequest.getProject() );
        assertEquals( RenderMode.PREVIEW, portalRequest.getMode() );
    }

    @Test
    void createPortalRequestWithContentWhichIsSite()
    {
        final Project project = mock( Project.class );
        when( projectService.get( eq( ProjectName.from( "myrepo" ) ) ) ).thenReturn( project );

        final Site site = newSite();
        when( contentService.getByPath( eq( ContentPath.from( "/mysite" ) ) ) ).thenReturn( site );

        this.request.setRawPath( "/admin/site/preview/myrepo/master/mysite" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( site.getPath(), portalRequest.getContentPath() );
        assertEquals( ProjectName.from( "myrepo" ).getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( site, portalRequest.getContent() );
        assertEquals( site, portalRequest.getSite() );
        assertEquals( project, portalRequest.getProject() );
    }

    @Test
    void createPortalRequestContentNotFound()
    {
        final Branch branch = Branch.from( "master" );
        final Project project = mock( Project.class );
        final ProjectName projectName = ProjectName.from( "myrepo" );

        when( projectService.get( eq( projectName ) ) ).thenReturn( project );

        final ContentNotFoundException contentNotFoundException = ContentNotFoundException.create()
            .contentId( ContentId.from( "contentId" ) )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .contentRoot( NodePath.ROOT )
            .build();

        when( contentService.getByPath( eq( ContentPath.from( "/mysite/landing-page" ) ) ) ).thenThrow( contentNotFoundException );

        this.request.setRawPath( "/admin/site/preview/myrepo/master/mysite/landing-page" );

        final PortalRequest portalRequest = handler.createPortalRequest( request, response );

        assertEquals( ContentPath.from( "/mysite/landing-page" ), portalRequest.getContentPath() );
        assertEquals( projectName.getRepoId(), portalRequest.getRepositoryId() );
        assertEquals( Branch.from( "master" ), portalRequest.getBranch() );
        assertEquals( project, portalRequest.getProject() );
        assertNull( portalRequest.getContent() );
        assertNull( portalRequest.getSite() );
    }

    private Content newContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) );
        builder.name( "landing-page" );
        builder.displayName( "My Landing Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( ApplicationKey.from( "com.enonic.test.app" ), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                                 .build() );
        return builder.build();
    }

    private Site newSite()
    {

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "site0c10-0002-4b68-b407-87412f3e45c9" ) );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        site.permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );
        return site.build();
    }
}
