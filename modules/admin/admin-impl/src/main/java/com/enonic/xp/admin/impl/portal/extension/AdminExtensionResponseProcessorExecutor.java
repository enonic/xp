package com.enonic.xp.admin.impl.portal.extension;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.admin.extension.AdminExtensionResponseProcessor;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.PrincipalKeys;

@Component(service = AdminExtensionResponseProcessorExecutor.class)
public class AdminExtensionResponseProcessorExecutor
{
    private final List<AdminExtensionResponseProcessor> processors = new CopyOnWriteArrayList<>();

    private final AdminExtensionDescriptorService descriptorService;

    @Activate
    public AdminExtensionResponseProcessorExecutor( @Reference final AdminExtensionDescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    public PortalResponse execute( final AdminToolDescriptor tool, final PortalRequest request, final PortalResponse response )
    {
        if ( this.processors.isEmpty() )
        {
            return response;
        }

        // registration order is non-deterministic, sort to make the chain stable across restarts
        final List<AdminExtensionResponseProcessor> chain = this.processors.stream()
            .sorted( Comparator.comparing( processor -> processor.getExtensionKey().toString() ) )
            .toList();

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();

        PortalResponse result = response;
        for ( final AdminExtensionResponseProcessor processor : chain )
        {
            final AdminExtensionDescriptor descriptor = descriptorService.getByKey( processor.getExtensionKey() );
            if ( descriptor == null || !isMounted( descriptor, tool ) || !descriptor.isAccessAllowed( principals ) )
            {
                continue;
            }
            result = Objects.requireNonNull( processor.process( request, result ), () -> String.format(
                "Response processor for extension [%s] returned null", processor.getExtensionKey() ) );
        }
        return result;
    }

    private static boolean isMounted( final AdminExtensionDescriptor descriptor, final AdminToolDescriptor tool )
    {
        return descriptor.hasInterface( AdminExtensionDescriptor.GENERIC_INTERFACE ) ||
            descriptor.getInterfaces().stream().anyMatch( tool::hasInterface );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addProcessor( final AdminExtensionResponseProcessor processor )
    {
        this.processors.add( processor );
    }

    public void removeProcessor( final AdminExtensionResponseProcessor processor )
    {
        this.processors.remove( processor );
    }
}
