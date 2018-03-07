package com.enonic.xp.admin.impl.tool;

import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Component(immediate = true)
public final class AdminToolDescriptorServiceImpl
    implements AdminToolDescriptorService
{
    private static final String ADMIN_TOOLS_URI_PREFIX = "/admin/tool";

    private final static String PATH = "/admin/tools";

    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public AdminToolDescriptors getAllowedAdminToolDescriptors( final PrincipalKeys principalKeys )
    {
        return new GetAdminToolDescriptorsCommand().
            applicationService( this.applicationService ).
            resourceService( this.resourceService ).
            filter( adminToolDescriptor -> adminToolDescriptor.isAccessAllowed( principalKeys ) ).
            execute();
    }

    @Override
    public AdminToolDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        return AdminToolDescriptors.from(
            findDescriptorKeys( applicationKey ).stream().map( this::getByKey ).collect( Collectors.toList() ) );
    }

    @Override
    public AdminToolDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return new GetAdminToolDescriptorCommand().
            resourceService( this.resourceService ).
            descriptorKey( descriptorKey ).
            execute();
    }


    @Override
    public String getIconByKey( final DescriptorKey descriptorKey )
    {
        return new GetAdminToolIconCommand().
            resourceService( this.resourceService ).
            descriptorKey( descriptorKey ).
            execute();
    }

    private static String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.createUri( uri );
    }

    @Override
    public String getHomeToolUri()
    {
        return rewriteUri( ADMIN_TOOLS_URI_PREFIX );
    }

    @Override
    public String generateAdminToolUri( String application, String adminTool )
    {
        String uri = ADMIN_TOOLS_URI_PREFIX + "/" + application;
        if ( adminTool != null )
        {
            uri += "/" + adminTool;
        }
        return rewriteUri( uri );
    }

    private Set<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, PATH, true ).findKeys( key );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
