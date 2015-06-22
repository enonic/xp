package com.enonic.wem.modules.xslt;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

@Component(immediate = true)
public final class Initializer
{
    private final static Logger LOG = LoggerFactory.getLogger( Initializer.class );

    public static final ModuleKey THIS_MODULE = ModuleKey.from( Initializer.class );

    private static final AccessControlList PERMISSIONS =
        AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() );

    private ContentService contentService;

    private PageTemplateService pageTemplateService;

    @Activate
    public void initialize()
        throws Exception
    {
        runAs( RoleKeys.ADMIN, () -> {
            doInitialize();
            return null;
        } );
    }

    private void doInitialize()
    {
        final ContentPath path = ContentPath.from( ContentPath.ROOT, "xslt" );
        if ( hasContent( path ) )
        {
            LOG.info( "Already initialized with data. Skipping." );
            return;
        }

        LOG.info( "Initializing data...." );

        final SiteConfig siteConfig = SiteConfig.newSiteConfig().
            module( THIS_MODULE ).
            config( new PropertyTree() ).
            build();
        final SiteConfigs siteConfigs = SiteConfigs.from( siteConfig );

        final Site site = contentService.create( createSiteContent( "Xslt", "Xslt demo site.", siteConfigs ) );
        final UpdateContentParams setSitePermissions = new UpdateContentParams().
            contentId( site.getId() ).
            editor( ( content ) -> {
                content.permissions = PERMISSIONS;
                content.inheritPermissions = false;
            } );
        this.contentService.update( setSitePermissions );

        createRssTemplate( site.getPath() );

        this.contentService.applyPermissions(
            ApplyContentPermissionsParams.create().contentId( site.getId() ).modifier( PrincipalKey.ofAnonymous() ).build() );
    }

    private <T> T runAs( final PrincipalKey role, final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( role ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( runnable );
    }

    private CreateSiteParams createSiteContent( final String displayName, final String description, final SiteConfigs siteConfigs )
    {
        return new CreateSiteParams().
            siteConfigs( siteConfigs ).
            description( description ).
            displayName( displayName ).
            parent( ContentPath.ROOT );
    }

    private Content createRssTemplate( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.site() );

        return this.pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "rss-page" ).
            displayName( "Rss page" ).
            controller( DescriptorKey.from( THIS_MODULE, "rss" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
                build() ) );
    }

    private boolean hasContent( final ContentPath path )
    {
        try
        {
            return this.contentService.getByPath( path ) != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }
}
