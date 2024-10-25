package com.enonic.xp.portal.impl.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private final CopyOnWriteArrayList<DynamicUniversalApiHandler> dynamicApiHandlers = new CopyOnWriteArrayList<>();

    @Activate
    public DynamicUniversalApiHandlerRegistry()
    {

    }

    public DynamicUniversalApiHandler getApiHandler( final DescriptorKey descriptorKey )
    {
        return dynamicApiHandlers.stream()
            .filter( handler -> handler.getApiDescriptor().getKey().equals( descriptorKey ) )
            .findFirst()
            .orElse( null );
    }

    public List<ApiDescriptor> getAllApiDescriptors()
    {
        return dynamicApiHandlers.stream().map( DynamicUniversalApiHandler::getApiDescriptor ).collect( Collectors.toList() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApiHandler( final UniversalApiHandler apiHandler, final Map<String, ?> properties )
    {
        final ApiDescriptor apiDescriptor = createDynamicApiDescriptor( properties );
        this.dynamicApiHandlers.add( new DynamicUniversalApiHandler( apiHandler, apiDescriptor ) );
    }

    public void removeApiHandler( final UniversalApiHandler apiHandler )
    {
        dynamicApiHandlers.removeIf( handler -> handler.apiHandler == apiHandler );
    }

    private ApiDescriptor createDynamicApiDescriptor( final Map<String, ?> properties )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( (String) properties.get( "applicationKey" ) );
        final String apiKey = Objects.requireNonNull( (String) properties.get( "apiKey" ) );
        final PrincipalKeys allowedPrincipals = resolveDynamicPrincipalKeys( properties.get( "allowedPrincipals" ) );

        final ApiDescriptor.Builder builder =
            ApiDescriptor.create().key( DescriptorKey.from( applicationKey, apiKey ) ).allowedPrincipals( allowedPrincipals );

        if ( properties.get( "description" ) != null )
        {
            builder.description( properties.get( "description" ).toString() );
        }
        if ( properties.get( "displayName" ) != null )
        {
            builder.displayName( properties.get( "displayName" ).toString() );
        }
        if ( properties.get( "documentationUrl" ) != null )
        {
            builder.documentationUrl( properties.get( "documentationUrl" ).toString() );
        }
        if ( properties.get( "slashApi" ) != null )
        {
            builder.slashApi( Boolean.valueOf( properties.get( "slashApi" ).toString() ) );
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
