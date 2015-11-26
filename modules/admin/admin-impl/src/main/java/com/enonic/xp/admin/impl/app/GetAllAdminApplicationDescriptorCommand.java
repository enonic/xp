package com.enonic.xp.admin.impl.app;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.admin.app.AdminApplicationDescriptor;
import com.enonic.xp.admin.app.AdminApplicationDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

public class GetAllAdminApplicationDescriptorCommand
{
    private static final String PATH = "/admin/app/";

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private GetAllAdminApplicationDescriptorCommand( final Builder builder )
    {
        applicationService = builder.applicationService;
        resourceService = builder.resourceService;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public AdminApplicationDescriptors execute()
    {
        final List<AdminApplicationDescriptor> adminApplicationDescriptors = applicationService.getAllApplications().
            stream().
            flatMap( this::getAdminApplicationDescriptors ).
            collect( Collectors.toList() );

        return AdminApplicationDescriptors.from( adminApplicationDescriptors );
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

        public GetAllAdminApplicationDescriptorCommand build()
        {
            return new GetAllAdminApplicationDescriptorCommand( this );
        }
    }
}
