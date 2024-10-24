package com.enonic.xp.portal.impl.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

@Component(service = DynamicUniversalApiHandlerRegistry.class)
public class DynamicUniversalApiHandlerRegistry
{
    private final ConcurrentMap<DescriptorKey, DynamicUniversalApiHandler> dynamicApiHandlers = new ConcurrentHashMap<>();

    @Activate
    public DynamicUniversalApiHandlerRegistry()
    {

    }

    public DynamicUniversalApiHandler getApiHandler( final DescriptorKey descriptorKey )
    {
        return dynamicApiHandlers.get( descriptorKey );
    }

    public List<ApiDescriptor> getAllApiDescriptors()
    {
        return dynamicApiHandlers.values().stream().map( DynamicUniversalApiHandler::getApiDescriptor ).collect( Collectors.toList() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApiHandler( final UniversalApiHandler apiHandler, final Map<String, ?> properties )
    {
        final ApiDescriptor apiDescriptor = createDynamicApiDescriptor( properties );
        this.dynamicApiHandlers.put( apiDescriptor.getKey(), new DynamicUniversalApiHandler( apiHandler, apiDescriptor ) );
    }

    public void removeApiHandler( final UniversalApiHandler apiHandler )
    {
        dynamicApiHandlers.values()
            .stream()
            .filter( wrapper -> wrapper.getApiHandler().equals( apiHandler ) )
            .findFirst()
            .ifPresent( apiHandlerWrapper -> this.dynamicApiHandlers.remove( apiHandlerWrapper.getApiDescriptor().getKey() ) );
    }

    private ApiDescriptor createDynamicApiDescriptor( final Map<String, ?> properties )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( (String) properties.get( "applicationKey" ) );
        final String apiKey = Objects.requireNonNull( (String) properties.get( "apiKey" ) );
        final PrincipalKeys allowedPrincipals = resolveDynamicPrincipalKeys( properties.get( "allowedPrincipals" ) );

        return ApiDescriptor.create().key( DescriptorKey.from( applicationKey, apiKey ) ).allowedPrincipals( allowedPrincipals ).build();
    }

    private PrincipalKeys resolveDynamicPrincipalKeys( final Object allowedPrincipals )
    {
        return switch ( allowedPrincipals )
        {
            case null -> null;
            case String s -> PrincipalKeys.from( s );
            case String[] strings ->
                PrincipalKeys.from( Arrays.stream( strings ).map( PrincipalKey::from ).collect( Collectors.toList() ) );
            default -> throw new IllegalArgumentException( "Invalid allowedPrincipals. Value must be string or string array." );
        };
    }

}
