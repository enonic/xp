package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagementApiPolicyTest
{
    @Test
    void unrestrictedWhenAbsent()
    {
        final ManagementApiPolicy policy = withVirtualHostContext( Map.of(), () -> ManagementApiPolicy.of( "server:snapshot" ) );

        assertTrue( policy.allows( "list" ) );
        assertTrue( policy.allows( "restore" ) );
    }

    @Test
    void restrictedToListedVerbs()
    {
        final ManagementApiPolicy policy = withVirtualHostContext( Map.of( "api.server:snapshot.verbs", " list , create" ),
                                                                   () -> ManagementApiPolicy.of( "server:snapshot" ) );

        assertTrue( policy.allows( "list" ) );
        assertTrue( policy.allows( "create" ) );
        assertFalse( policy.allows( "restore" ) );
        assertFalse( policy.allows( "prune" ) );
    }

    @Test
    void emptyVerbsMeansUnrestricted()
    {
        // a blank value is indistinguishable from an absent key in a properties file, so it must not lock the API
        final ManagementApiPolicy policy =
            withVirtualHostContext( Map.of( "api.server:snapshot.verbs", "  " ), () -> ManagementApiPolicy.of( "server:snapshot" ) );

        assertTrue( policy.allows( "restore" ) );
    }

    @Test
    void wildcard()
    {
        final ManagementApiPolicy policy =
            withVirtualHostContext( Map.of( "api.server:snapshot.verbs", "*" ), () -> ManagementApiPolicy.of( "server:snapshot" ) );

        assertTrue( policy.allows( "restore" ) );
    }

    @Test
    void policyIsPerApi()
    {
        final ManagementApiPolicy policy = withVirtualHostContext( Map.of( "api.server:snapshot.verbs", "list" ),
                                                                   () -> ManagementApiPolicy.of( "server:dump" ) );

        assertTrue( policy.allows( "load" ) );
    }

    @Test
    void setting()
    {
        final Optional<String> setting = withVirtualHostContext( Map.of( "api.server:index.repositories", "com.enonic.cms.default" ),
                                                                 () -> ManagementApiPolicy.setting( "server:index", "repositories" ) );

        assertEquals( Optional.of( "com.enonic.cms.default" ), setting );
        assertEquals( Optional.empty(), withVirtualHostContext( Map.of(), () -> ManagementApiPolicy.setting( "server:index", "repositories" ) ) );
    }

    @Test
    void nonStringAttributeValue()
    {
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( "api.server:snapshot.verbs", List.of( "list" ) );

        final ManagementApiPolicy policy = context.callWith( () -> ManagementApiPolicy.of( "server:snapshot" ) );

        assertEquals( "server:snapshot", policy.getDescriptorKey() );
        assertTrue( policy.allows( "list" ) );
        assertFalse( policy.allows( "restore" ) );
    }
}
