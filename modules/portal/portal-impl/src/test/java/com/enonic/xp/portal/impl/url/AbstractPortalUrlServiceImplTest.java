package com.enonic.xp.portal.impl.url;

import java.time.Instant;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.impl.macro.MacroServiceImpl;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

public abstract class AbstractPortalUrlServiceImplTest
{
    protected PortalRequest portalRequest;

    protected PortalUrlServiceImpl service;

    protected ContentService contentService;

    protected ApplicationService applicationService;

    protected StyleDescriptorService styleDescriptorService;

    @Before
    public void setup()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( applicationKey );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.MAX );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( applicationKey );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        this.service = new PortalUrlServiceImpl();
        this.service.setMacroService( new MacroServiceImpl() );

        this.contentService = Mockito.mock( ContentService.class );
        this.service.setContentService( this.contentService );

        this.styleDescriptorService = Mockito.mock( StyleDescriptorService.class );
        Mockito.when( this.styleDescriptorService.getByApplications( Mockito.any() ) ).thenReturn( StyleDescriptors.empty() );
        this.service.setStyleDescriptorService( this.styleDescriptorService );

        this.applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( this.applicationService.getInstalledApplication( applicationKey ) ).thenReturn( application );
        this.service.setApplicationService( this.applicationService );
    }
}
