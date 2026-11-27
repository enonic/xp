package com.enonic.xp.portal.impl.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationScope;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScopeRegistryTest
{
    private static final ApplicationKey ORDERS = ApplicationKey.from( "com.example.orders" );

    private static final ApplicationKey SHOP = ApplicationKey.from( "com.example.shop" );

    private static final String[] XP = {"admin", "read", "content", "read:content", "repo", "read:repo", "app", "read:app", "idprovider",
        "read:idprovider", "system", "read:system"};

    private final ApplicationService applicationService = mock( ApplicationService.class );

    private final ApplicationDescriptorService applicationDescriptorService = mock( ApplicationDescriptorService.class );

    private final ApiDescriptorService apiDescriptorService = mock( ApiDescriptorService.class );

    private final SiteService siteService = mock( SiteService.class );

    private final WebappService webappService = mock( WebappService.class );

    private final AdminToolDescriptorService adminToolDescriptorService = mock( AdminToolDescriptorService.class );

    private final DynamicUniversalApiHandlerRegistry registry = new DynamicUniversalApiHandlerRegistry();

    private final ScopeRegistry service =
        new ScopeRegistry( applicationService, applicationDescriptorService, apiDescriptorService, registry, siteService, webappService,
                           adminToolDescriptorService );

    private static ApiDescriptor api( final ApplicationKey app, final String name, final String mount, final String... scopes )
    {
        final ApiDescriptor.Builder builder = ApiDescriptor.create()
            .key( DescriptorKey.from( app, name ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.ADMIN ) )
            .scopes( Stream.of( scopes ).map( scope -> scope.contains( ":" ) ? scope : app + ":" + scope ).toArray( String[]::new ) );
        if ( mount != null )
        {
            builder.mount( mount );
        }
        return builder.build();
    }

    private static VirtualHost mapping( final String connector, final String target )
    {
        final VirtualHost mapping = mock( VirtualHost.class );
        when( mapping.getConnector() ).thenReturn( connector );
        when( mapping.getTarget() ).thenReturn( target );
        return mapping;
    }

    private List<String> servedBy( final String connector, final String target )
    {
        return List.copyOf( service.getScopes( List.of( mapping( connector, target ) ), false ).getIds() );
    }

    /**
     * Orders has a web API, a management API, an API its web application mounts and one nothing mounts; shop has an API
     * its site mounts and one an admin tool mounts. Each API accepts scopes of its application; orders' all includes its
     * read and write, and a handler registered with XP accepts XP's repository scopes on the management endpoint.
     */
    @BeforeEach
    void setUp()
    {
        final Application orders = mock( Application.class );
        when( orders.getKey() ).thenReturn( ORDERS );
        final Application shop = mock( Application.class );
        when( shop.getKey() ).thenReturn( SHOP );
        when( applicationService.getInstalledApplications() ).thenReturn( Applications.from( orders, shop ) );
        when( apiDescriptorService.getByApplication( ORDERS ) ).thenReturn(
            ApiDescriptors.from( api( ORDERS, "web", "web", "read", "write" ), api( ORDERS, "ops", "management", "run" ),
                                 api( ORDERS, "app", null, "use" ), api( ORDERS, "loose", null, "x" ), api( ORDERS, "plain", "web" ) ) );
        when( apiDescriptorService.getByApplication( SHOP ) ).thenReturn(
            ApiDescriptors.from( api( SHOP, "cart", null, "edit" ), api( SHOP, "tool", null, "admin" ) ) );

        when( applicationDescriptorService.get( ORDERS ) ).thenReturn( ApplicationDescriptor.create()
                                                                        .key( ORDERS )
                                                                        .scopes( List.of( new ApplicationScope( "all", "read", "write" ),
                                                                                          new ApplicationScope( "read" ),
                                                                                          new ApplicationScope( "write" ),
                                                                                          new ApplicationScope( "run" ),
                                                                                          new ApplicationScope( "use" ),
                                                                                          new ApplicationScope( "x" ) ) )
                                                                        .build() );
        when( applicationDescriptorService.get( SHOP ) ).thenReturn( ApplicationDescriptor.create()
                                                                      .key( SHOP )
                                                                      .scopes( List.of( new ApplicationScope( "edit" ),
                                                                                        new ApplicationScope( "admin" ) ) )
                                                                      .build() );

        final WebappDescriptor webapp = mock( WebappDescriptor.class );
        when( webapp.getApiMounts() ).thenReturn( DescriptorKeys.from( DescriptorKey.from( ORDERS, "app" ) ) );
        when( webappService.getDescriptor( ORDERS ) ).thenReturn( webapp );
        final SiteDescriptor site = mock( SiteDescriptor.class );
        when( site.getApiMounts() ).thenReturn( DescriptorKeys.from( DescriptorKey.from( SHOP, "cart" ) ) );
        when( siteService.getDescriptor( SHOP ) ).thenReturn( site );
        final AdminToolDescriptor tool = mock( AdminToolDescriptor.class );
        when( tool.getApiMounts() ).thenReturn( DescriptorKeys.from( DescriptorKey.from( SHOP, "tool" ) ) );
        when( adminToolDescriptorService.getAll() ).thenReturn( AdminToolDescriptors.from( List.of( tool ) ) );

        registry.addApiHandler( mock( UniversalApiHandler.class ), Map.of( "key", "server:snapshot", "allowedPrincipals", "role:system.admin", "mount", "management", "scopes",
                                        new String[]{"read:repo", "repo"} ) );
    }

    @Test
    void every_defined_scope_xps_first()
    {
        final List<String> all = new java.util.ArrayList<>( List.of( XP ) );
        all.addAll( List.of( "com.example.orders:all", "com.example.orders:read", "com.example.orders:write", "com.example.orders:run",
                             "com.example.orders:use", "com.example.orders:x", "com.example.shop:edit", "com.example.shop:admin" ) );
        assertThat( service.getScopes().getIds() ).containsExactlyElementsOf( all );
    }

    @Test
    void a_mapping_offers_the_scopes_its_apis_accept_and_those_that_include_them()
    {
        final String web = DispatchConstants.WEB_CONNECTOR;
        final String[] ordersWeb = {"com.example.orders:all", "com.example.orders:read", "com.example.orders:write"};
        assertThat( servedBy( web, "/api" ) ).containsExactly( ordersWeb );
        assertThat( servedBy( web, "/api/com.example.orders:web" ) ).containsExactly( ordersWeb );
        assertThat( servedBy( web, "/api/com.example.orders:plain" ) ).isEmpty();
        assertThat( servedBy( web, "/webapp/com.example.orders" ) ).containsExactly( "com.example.orders:use" );
        assertThat( servedBy( web, "/webapp/com.example.shop" ) ).isEmpty();
        assertThat( servedBy( web, "/site/shop/master/store" ) ).containsExactly( "com.example.shop:edit" );
        assertThat( servedBy( web, "/admin" ) ).containsExactly( "com.example.shop:edit", "com.example.shop:admin" );
        assertThat( servedBy( web, "/" ) ).as( "everything mounted somewhere on the web endpoint" )
            .containsExactly( "com.example.orders:all", "com.example.orders:read", "com.example.orders:write", "com.example.orders:use",
                              "com.example.shop:edit", "com.example.shop:admin" );
        assertThat( servedBy( web, "/apis" ) ).isEmpty();
    }

    @Test
    void the_management_and_statistics_endpoints()
    {
        final String[] management = {"admin", "read", "repo", "read:repo", "com.example.orders:run"};
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/" ) ).containsExactly( management );
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/api" ) ).containsExactly( management );
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/server:snapshot" ) ).as( "a mapping onto one API" )
            .containsExactly( "admin", "read", "repo", "read:repo" );
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/server:snapshot/restore" ) ).containsExactly( "admin", "read", "repo",
                                                                                                                  "read:repo" );
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/com.example.orders:ops" ) ).containsExactly( "com.example.orders:run" );
        assertThat( servedBy( DispatchConstants.MANAGEMENT_CONNECTOR, "/server:other" ) ).isEmpty();
        assertThat( servedBy( DispatchConstants.STATISTICS_CONNECTOR, "/" ) ).isEmpty();
        assertThat( service.getScopes( List.of(), true ).getIds() ).as( "the management endpoint without a mapping of its own" )
            .containsExactly( management );
    }

    @Test
    void several_mappings_offer_what_any_of_them_does()
    {
        assertThat( service.getScopes( List.of( mapping( DispatchConstants.WEB_CONNECTOR, "/webapp/com.example.orders" ),
                                                mapping( DispatchConstants.WEB_CONNECTOR, "/site/shop/master" ) ), false ).getIds() ).containsExactly(
            "com.example.orders:use", "com.example.shop:edit" );
        assertThat( service.getScopes( List.of(), false ) ).isEmpty();
    }
}
