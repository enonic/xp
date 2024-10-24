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
import com.enonic.xp.portal.UniversalApiHandlerRegistry;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.universalapi.UniversalApiHandler;
import com.enonic.xp.web.universalapi.UniversalApiHandlerWrapper;

@Component(service = UniversalApiHandlerRegistry.class)
public class UniversalApiHandlerRegistryImpl
    implements UniversalApiHandlerRegistry
{
    private final ConcurrentMap<DescriptorKey, UniversalApiHandlerWrapper> dynamicApiHandlers = new ConcurrentHashMap<>();

    @Activate
    public UniversalApiHandlerRegistryImpl()
    {

    }

    @Override
    public UniversalApiHandlerWrapper getApiHandler( final DescriptorKey descriptorKey )
    {
        return dynamicApiHandlers.get( descriptorKey );
    }

    @Override
    public List<ApiDescriptor> getAllApiDescriptors()
    {
        return dynamicApiHandlers.values().stream().map( UniversalApiHandlerWrapper::getApiDescriptor ).collect( Collectors.toList() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApiHandler( final UniversalApiHandler apiHandler, final Map<String, ?> properties )
    {
        final ApiDescriptor apiDescriptor = createDynamicApiDescriptor( properties );
        this.dynamicApiHandlers.put( apiDescriptor.getKey(), new UniversalApiHandlerWrapper( apiHandler, apiDescriptor ) );
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

        final ApiDescriptor.Builder builder =
            ApiDescriptor.create().key( DescriptorKey.from( applicationKey, apiKey ) ).allowedPrincipals( allowedPrincipals );

        if ( properties.containsKey( "description" ) )
        {
            builder.description( (String) properties.get( "description" ) );
        }
        if ( properties.containsKey( "displayName" ) )
        {
            builder.displayName( (String) properties.get( "displayName" ) );
        }

        return builder.build();
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
