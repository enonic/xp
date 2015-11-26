package com.enonic.xp.admin.impl.app;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.admin.app.AdminApplicationDescriptor;
import com.enonic.xp.admin.app.AdminApplicationDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.xml.XmlException;

public class GetAdminApplicationDescriptorCommand
{
    private static final String PATH = "/admin/app/";

    private static final AdminApplicationDescriptor CONTENT_MANAGER_APP = AdminApplicationDescriptor.create().
        key( DescriptorKey.from( ApplicationKey.SYSTEM, "content-manager" ) ).
        name( "Content Manager" ).
        shortName( "CM" ).
        iconUrl( "database" ).
        addAllowedPrincipal( RoleKeys.ADMIN ).
        addAllowedPrincipal( RoleKeys.CONTENT_MANAGER_ADMIN ).
        addAllowedPrincipal( RoleKeys.CONTENT_MANAGER_APP ).
        build();

    private static final AdminApplicationDescriptor USER_MANAGER_APP = AdminApplicationDescriptor.create().
        key( DescriptorKey.from( ApplicationKey.SYSTEM, "user-manager" ) ).
        name( "Users" ).
        shortName( "UM" ).
        iconUrl( "users" ).
        addAllowedPrincipal( RoleKeys.ADMIN ).
        addAllowedPrincipal( RoleKeys.USER_MANAGER_ADMIN ).
        addAllowedPrincipal( RoleKeys.USER_MANAGER_APP ).
        build();

    private static final AdminApplicationDescriptor APPLICATIONS_APP = AdminApplicationDescriptor.create().
        key( DescriptorKey.from( ApplicationKey.SYSTEM, "applications" ) ).
        name( "Applications" ).
        shortName( "AM" ).
        iconUrl( "puzzle" ).
        addAllowedPrincipal( RoleKeys.ADMIN ).
        build();

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final Predicate<AdminApplicationDescriptor> filter;

    private GetAdminApplicationDescriptorCommand( final Builder builder )
    {
        applicationService = builder.applicationService;
        resourceService = builder.resourceService;
        filter = builder.filter;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public AdminApplicationDescriptors execute()
    {
        final Stream<AdminApplicationDescriptor> systemAdminApplicationDescriptors = getSystemAdminApplicationDescriptors();

        final Stream<AdminApplicationDescriptor> dynamicAdminApplicationDescriptors = applicationService.getAllApplications().
            stream().
            flatMap( this::getAdminApplicationDescriptors );

        final List<AdminApplicationDescriptor> adminApplicationDescriptors =
            Stream.concat( systemAdminApplicationDescriptors, dynamicAdminApplicationDescriptors ).
                filter( adminApplicationDescriptor -> filter == null || filter.test( adminApplicationDescriptor ) ).
                collect( Collectors.toList() );

        return AdminApplicationDescriptors.from( adminApplicationDescriptors );
    }

    private Stream<AdminApplicationDescriptor> getSystemAdminApplicationDescriptors()
    {
        return Stream.of( CONTENT_MANAGER_APP, USER_MANAGER_APP, APPLICATIONS_APP );
    }

    private Stream<AdminApplicationDescriptor> getAdminApplicationDescriptors( Application application )
    {
        return resourceService.findFolders( application.getKey(), PATH ).
            stream().
            map( folderResourceKey -> ResourceKey.from( application.getKey(),
                                                        folderResourceKey.getPath() + "/" + folderResourceKey.getName() + ".xml" ) ).
            map( this::getAdminApplicationDescriptor );
    }

    private AdminApplicationDescriptor getAdminApplicationDescriptor( ResourceKey resourceKey )
    {
        final Resource resource = resourceService.getResource( resourceKey );

        final AdminApplicationDescriptor.Builder builder = AdminApplicationDescriptor.create();

        if ( resource.exists() )
        {
            final String descriptorXml = resource.readString();
            try
            {
                final XmlAdminApplicationDescriptorParser parser = new XmlAdminApplicationDescriptorParser();
                parser.builder( builder );
                parser.currentApplication( resourceKey.getApplicationKey() );
                parser.source( descriptorXml );
                parser.parse();
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load admin application descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
        }
        else
        {
            builder.name( resourceKey.getName() ).
                shortName( resourceKey.getName() ).
                iconUrl( "default" );
        }

        builder.key( DescriptorKey.from( resourceKey.getApplicationKey(), resourceKey.getName() ) );

        return builder.build();
    }


    public static final class Builder
    {
        private ApplicationService applicationService;

        private ResourceService resourceService;

        public Predicate<AdminApplicationDescriptor> filter;

        private Builder()
        {
        }

        public Builder applicationService( final ApplicationService applicationService )
        {
            this.applicationService = applicationService;
            return this;
        }

        public Builder resourceService( final ResourceService resourceService )
        {
            this.resourceService = resourceService;
            return this;
        }

        public Builder filter( final Predicate<AdminApplicationDescriptor> filter )
        {
            this.filter = filter;
            return this;
        }

        public GetAdminApplicationDescriptorCommand build()
        {
            return new GetAdminApplicationDescriptorCommand( this );
        }
    }
}
