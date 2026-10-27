package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.dispatch.DispatchConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualHostMappingTest
{
    private VirtualHostMapping virtualHostMapping;

    @Test
    void testName()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "mymapping", this.virtualHostMapping.getName() );
    }

    @Test
    void testHost()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "foo.no", this.virtualHostMapping.getHost() );
    }

    @Test
    void testSource()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "a/b", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "a/b", this.virtualHostMapping.getSource() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", null, "/", VirtualHostIdProvidersMapping.create().build(), 0 ) );
    }

    @Test
    void testTarget()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/", "a/b", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "a/b", this.virtualHostMapping.getTarget() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().build(), 0 ) );
    }

    @Test
    void testIdProviderKey()
    {
        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().
                          setDefaultIdProvider( IdProviderKey.system() ).
                          build(), 0 ) );

        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().
            setDefaultIdProvider( IdProviderKey.system() ).
            build(), 0 );
        assertEquals( IdProviderKey.system(), this.virtualHostMapping.getDefaultIdProviderKey() );
    }

    private static final String HERE = "https://admin.example.com/admin";

    private static VirtualHostMapping serving( final String... audience )
    {
        return new VirtualHostMapping( "m", "admin.example.com", "/admin", "/", VirtualHostIdProvidersMapping.create().build(), 0, Map.of(),
                                       DispatchConstants.WEB_CONNECTOR, PrincipalKeys.empty(), Set.of( audience ) );
    }

    private static VirtualHostMapping mapping( final String host, final String source )
    {
        return new VirtualHostMapping( "m", host, source, "/", VirtualHostIdProvidersMapping.create().build(), 0 );
    }

    @Test
    void a_host_declaring_no_audience_stands_for_xp_itself_and_for_its_own_identifier()
    {
        assertTrue( serving().acceptsAudience( Set.of( "xp" ), "xp", HERE ) );
        assertTrue( serving().acceptsAudience( Set.of( "xp", "api" ), "xp", HERE ) );
        assertTrue( serving().acceptsAudience( Set.of( HERE ), "xp", HERE ) );
        assertFalse( serving().acceptsAudience( Set.of( "api" ), "xp", HERE ) );
        assertFalse( serving().acceptsAudience( Set.of( "xp" ), "prod-eu", HERE ) );
        assertFalse( serving().acceptsAudience( Set.of( HERE ), "xp", null ) );
        assertFalse( serving().acceptsAudience( Set.of(), "xp", HERE ) );
    }

    @Test
    void a_host_declaring_audiences_matches_its_list_alone()
    {
        assertTrue( serving( "api", "mgmt" ).acceptsAudience( Set.of( "mgmt" ), "xp", HERE ) );
        assertTrue( serving( "api", "mgmt" ).acceptsAudience( Set.of( "other", "api" ), "xp", HERE ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of( "other" ), "xp", HERE ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of( "xp" ), "xp", HERE ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of( HERE ), "xp", HERE ) );
        assertFalse( serving( "api", "mgmt" ).acceptsAudience( Set.of(), "xp", HERE ) );
    }

    @Test
    void the_identifier_is_the_url_the_source_is_served_at()
    {
        assertEquals( "https://admin.example.com/admin", mapping( "admin.example.com", "/admin" ).identifier( "https", "admin.example.com", 443 ) );
        assertEquals( "https://admin.example.com/admin", mapping( "admin.example.com", "/admin" ).identifier( "https", "admin.example.com", -1 ) );
        assertEquals( "http://localhost:8080", mapping( "localhost", "/" ).identifier( "http", "localhost", 8080 ) );
        assertEquals( "http://localhost", mapping( "localhost", "/" ).identifier( "http", "localhost", 80 ) );
        assertEquals( "https://a.example.com/api/mcp", mapping( "~.*\\.example\\.com", "/api/mcp" ).identifier( "https", "a.example.com", 443 ) );
    }

    @Test
    void host_matching_takes_lists_and_patterns()
    {
        assertTrue( mapping( "admin.example.com", "/" ).matchesHost( "ADMIN.example.com" ) );
        assertFalse( mapping( "admin.example.com", "/" ).matchesHost( "www.example.com" ) );
        assertTrue( mapping( "a.example.com b.example.com", "/" ).matchesHost( "b.example.com" ) );
        assertTrue( mapping( "~.*\\.example\\.com", "/" ).matchesHost( "x.example.com" ) );
        assertFalse( mapping( "~.*\\.example\\.com", "/" ).matchesHost( "example.org" ) );
    }
}
