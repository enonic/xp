package com.enonic.xp.portal.impl.api;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.token.ScopeService;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.token.XpScopes;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

/**
 * The scopes defined here, XP's own and those the installed applications define in their
 * application descriptors, as a whole or for what a vhost mapping serves: the scopes that let a
 * token into an API the mapping serves, which are those the API accepts and those that include them.
 * <p>
 * A mapping serves the APIs its target reaches. On the management endpoint those are the APIs
 * mounted on {@code management}, or the one the target names, {@code /<application>:<api>}. On the web endpoint: under {@code /api}, the APIs mounted on
 * {@code web}, or the one the target names; under {@code /webapp/<app>}, the APIs that web
 * application mounts; under {@code /site}, the APIs any installed application mounts on sites, as
 * which sites lie under a target is content, not configuration; under {@code /admin}, the APIs
 * mounted on admin tools and on sites; at the root, all of these. Any other target serves none.
 */
@Component(service = {ScopeRegistry.class, ScopeService.class})
@NullMarked
public class ScopeRegistry
    implements ScopeService
{
    private static final String API_BASE = "/api";

    private static final String WEBAPP_PREFIX = "/webapp/";

    private static final String SITE_BASE = "/site";

    private static final String ADMIN_BASE = "/admin";

    private final ApplicationService applicationService;

    private final ApplicationDescriptorService applicationDescriptorService;

    private final ApiDescriptorService apiDescriptorService;

    private final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private final SiteService siteService;

    private final WebappService webappService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    @Activate
    public ScopeRegistry( @Reference final ApplicationService applicationService,
                          @Reference final ApplicationDescriptorService applicationDescriptorService,
                          @Reference final ApiDescriptorService apiDescriptorService,
                          @Reference final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry,
                          @Reference final SiteService siteService, @Reference final WebappService webappService,
                          @Reference final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.applicationService = applicationService;
        this.applicationDescriptorService = applicationDescriptorService;
        this.apiDescriptorService = apiDescriptorService;
        this.universalApiHandlerRegistry = universalApiHandlerRegistry;
        this.siteService = siteService;
        this.webappService = webappService;
        this.adminToolDescriptorService = adminToolDescriptorService;
    }

    @Override
    public Scopes getScopes()
    {
        return Stream.concat( XpScopes.all().stream(), applications().map( applicationDescriptorService::get )
            .filter( Objects::nonNull )
            .map( ApplicationDescriptor::getScopes )
            .flatMap( Scopes::stream ) ).collect( Scopes.collector() );
    }

    /**
     * The scopes that let a token into an API any of the mappings serves.
     *
     * @param managementByDefault whether the management endpoint is served too, as it is, without a
     *                            mapping of its own, when no mapping is configured for it
     */
    public Scopes getScopes( final Iterable<VirtualHost> mappings, final boolean managementByDefault )
    {
        Predicate<ApiDescriptor> served = managementByDefault ? api -> api.getMount().contains( "management" ) : api -> false;
        for ( final VirtualHost mapping : mappings )
        {
            served = served.or( servedBy( mapping ) );
        }
        final Set<String> accepted =
            descriptors().filter( served ).map( ApiDescriptor::getScopes ).flatMap( Set::stream ).collect( Collectors.toSet() );
        final Scopes defined = getScopes();
        return defined.stream()
            .filter( scope -> defined.expand( Set.of( scope.getId() ) ).stream().anyMatch( accepted::contains ) )
            .collect( Scopes.collector() );
    }

    private Stream<ApiDescriptor> descriptors()
    {
        return Stream.concat( universalApiHandlerRegistry.getAllApiDescriptors().stream(), applications().flatMap(
            application -> apiDescriptorService.getByApplication( application ).stream() ) );
    }

    private Stream<ApplicationKey> applications()
    {
        return applicationService.getInstalledApplications().stream().map( Application::getKey );
    }

    private Predicate<ApiDescriptor> servedBy( final VirtualHost mapping )
    {
        final String target = mapping.getTarget();
        if ( DispatchConstants.MANAGEMENT_CONNECTOR.equals( mapping.getConnector() ) )
        {
            return management( target );
        }
        if ( !DispatchConstants.WEB_CONNECTOR.equals( mapping.getConnector() ) )
        {
            return api -> false;
        }
        if ( "/".equals( target ) )
        {
            final Set<DescriptorKey> mounted = new HashSet<>( siteMounts() );
            mounted.addAll( adminToolMounts() );
            applications().forEach( application -> mounted.addAll( webappMounts( application ) ) );
            return api -> api.getMount().contains( "web" ) || mounted.contains( api.getKey() );
        }
        if ( under( target, API_BASE ) )
        {
            return underApi( target, "web" );
        }
        if ( target.startsWith( WEBAPP_PREFIX ) )
        {
            final String application = target.substring( WEBAPP_PREFIX.length() ).split( "/", 2 )[0];
            final Set<DescriptorKey> mounted = application.isEmpty() ? Set.of() : webappMounts( applicationKey( application ) );
            return api -> mounted.contains( api.getKey() );
        }
        if ( under( target, SITE_BASE ) )
        {
            final Set<DescriptorKey> mounted = siteMounts();
            return api -> mounted.contains( api.getKey() );
        }
        if ( under( target, ADMIN_BASE ) )
        {
            final Set<DescriptorKey> mounted = new HashSet<>( siteMounts() );
            mounted.addAll( adminToolMounts() );
            return api -> mounted.contains( api.getKey() );
        }
        return api -> false;
    }

    /**
     * The APIs a management target reaches: all mounted on the endpoint at the root, or the one the target names, which
     * the endpoint serves at {@code /<application>:<api>}.
     */
    private static Predicate<ApiDescriptor> management( final String target )
    {
        if ( "/".equals( target ) )
        {
            return api -> api.getMount().contains( "management" );
        }
        if ( under( target, API_BASE ) )
        {
            return underApi( target, "management" );
        }
        final String named = target.substring( 1 ).split( "/", 2 )[0];
        return api -> api.getMount().contains( "management" ) && named.equals( api.getKey().toString() );
    }

    /**
     * The APIs under an {@code /api} target: the one it names, or all mounted on the endpoint.
     */
    private static Predicate<ApiDescriptor> underApi( final String target, final String mount )
    {
        if ( !under( target, API_BASE ) )
        {
            return api -> false;
        }
        final String named = target.length() > API_BASE.length() ? target.substring( API_BASE.length() + 1 ).split( "/", 2 )[0] : "";
        return api -> api.getMount().contains( mount ) && ( named.isEmpty() || named.equals( api.getKey().toString() ) );
    }

    private static boolean under( final String target, final String base )
    {
        return target.equals( base ) || target.startsWith( base + "/" );
    }

    private static @Nullable ApplicationKey applicationKey( final String name )
    {
        try
        {
            return ApplicationKey.from( name );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

    private Set<DescriptorKey> siteMounts()
    {
        final Set<DescriptorKey> mounted = new HashSet<>();
        applications().map( siteService::getDescriptor )
            .filter( descriptor -> descriptor != null && descriptor.getApiMounts() != null )
            .map( SiteDescriptor::getApiMounts )
            .forEach( mounts -> mounts.forEach( mounted::add ) );
        return mounted;
    }

    private Set<DescriptorKey> webappMounts( final @Nullable ApplicationKey application )
    {
        final WebappDescriptor descriptor = application == null ? null : webappService.getDescriptor( application );
        final Set<DescriptorKey> mounted = new HashSet<>();
        if ( descriptor != null && descriptor.getApiMounts() != null )
        {
            descriptor.getApiMounts().forEach( mounted::add );
        }
        return mounted;
    }

    private Set<DescriptorKey> adminToolMounts()
    {
        final Set<DescriptorKey> mounted = new HashSet<>();
        adminToolDescriptorService.getAll().forEach( tool -> {
            if ( tool.getApiMounts() != null )
            {
                tool.getApiMounts().forEach( mounted::add );
            }
        } );
        return mounted;
    }
}
