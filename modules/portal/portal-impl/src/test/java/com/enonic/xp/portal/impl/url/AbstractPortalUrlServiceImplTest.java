package com.enonic.xp.portal.impl.url;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.impl.macro.MacroServiceImpl;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractPortalUrlServiceImplTest
{
    protected PortalRequest portalRequest;

    protected PortalUrlServiceImpl service;

    protected ContentService contentService;

    protected ProjectService projectService;

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    protected StyleDescriptorService styleDescriptorService;

    protected RedirectChecksumService redirectChecksumService;

    HttpServletRequest req;

    @BeforeEach
    public void setup()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );

        req = mock( HttpServletRequest.class );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.portalRequest.setApplicationKey( applicationKey );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/context/path" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
        this.portalRequest.setRawRequest( req );

        this.contentService = mock( ContentService.class );
        this.projectService = mock( ProjectService.class );
        this.resourceService = mock( ResourceService.class );
        this.styleDescriptorService = mock( StyleDescriptorService.class );
        when( this.styleDescriptorService.getByApplications( any() ) ).thenReturn( StyleDescriptors.empty() );

        this.applicationService = mock( ApplicationService.class );
        when( this.applicationService.getInstalledApplication( applicationKey ) ).thenReturn( application );

        this.redirectChecksumService = mock( RedirectChecksumService.class );

        UrlGeneratorParamsAdapter urlStrategyFacade = new UrlGeneratorParamsAdapter( this.contentService, this.projectService );

        this.service =
            new PortalUrlServiceImpl( this.contentService, this.resourceService, new MacroServiceImpl(), this.styleDescriptorService,
                                      this.redirectChecksumService, urlStrategyFacade );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( true );
        when( portalConfig.asset_legacyContextPath() ).thenReturn( true );
        when( portalConfig.idprovider_legacyContextPath() ).thenReturn( true );

        PortalRequestAccessor.set( this.portalRequest );

        this.service.activate( portalConfig );
    }

    @AfterEach
    public void destroy()
    {
        PortalRequestAccessor.remove();
    }
}
