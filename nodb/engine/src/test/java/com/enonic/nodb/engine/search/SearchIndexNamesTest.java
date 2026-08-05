package com.enonic.nodb.engine.search;

import org.junit.jupiter.api.Test;

import com.enonic.nodb.engine.TenantContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchIndexNamesTest
{
    private static final TenantContext ACME = new TenantContext( "acme" );

    @Test
    void aliasAndPhysicalNamesFollowTheDesignedLayout()
    {
        assertEquals( "acme-com.enonic.cms.default", SearchIndexNames.alias( ACME, "com.enonic.cms.default" ) );
        assertEquals( "acme-com.enonic.cms.default+g1", SearchIndexNames.physical( ACME, "com.enonic.cms.default", 1 ) );
        assertEquals( "acme-com.enonic.cms.default+g7", SearchIndexNames.physical( ACME, "com.enonic.cms.default", 7 ) );
    }

    /**
     * The delimiter argument from DESIGN §5, as a test: tenant ids contain no dash, so the FIRST
     * dash is always the tenant/repo boundary even for a repo id full of dashes.
     */
    @Test
    void repoIdsMayContainDashesWithoutAmbiguity()
    {
        assertEquals( "acme-my--weird-repo", SearchIndexNames.alias( ACME, "my--weird-repo" ) );
        assertEquals( "acme-", SearchIndexNames.tenantPrefix( ACME ) );
        assertTrue( SearchIndexNames.alias( ACME, "my--weird-repo" ).startsWith( SearchIndexNames.tenantPrefix( ACME ) ) );
    }

    @Test
    void invalidRepoIdsAreRejectedBeforeTheyReachAUrl()
    {
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, "Upper" ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, "has space" ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, "has+plus" ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, "" ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, null ) );
    }

    @Test
    void generationsStartAtOneAndAreNeverZeroOrNegative()
    {
        assertEquals( 1, SearchIndexNames.FIRST_GENERATION );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.physical( ACME, "repo", 0 ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.physical( ACME, "repo", -1 ) );
    }

    /** Gate 0(d) decision 7: the bundled plugins' own indices must never be enumerated as tenant data. */
    @Test
    void bundledPluginSystemIndicesAreRecognised()
    {
        assertTrue( SearchIndexNames.isSystemIndex( ".plugins-ml-config" ) );
        assertTrue( SearchIndexNames.isSystemIndex( ".opendistro_security" ) );
        assertTrue( SearchIndexNames.isSystemIndex( "top_queries-2026.08.06-12345" ) );
        assertTrue( SearchIndexNames.isSystemIndex( "security-auditlog-2026.08.06" ) );
        assertFalse( SearchIndexNames.isSystemIndex( "acme-com.enonic.cms.default+g1" ) );
    }

    /**
     * Guards the rule that makes physical names and aliases non-colliding in OpenSearch's shared
     * name space: {@code +} is illegal in a repo id, so nothing a caller supplies can forge a
     * generation suffix.
     */
    @Test
    void physicalNamesCanNeverCollideWithAnAlias()
    {
        String alias = SearchIndexNames.alias( ACME, "repo" );
        String physical = SearchIndexNames.physicalFromAlias( alias, 2 );
        assertFalse( alias.equals( physical ) );
        assertTrue( physical.startsWith( alias + "+g" ) );
        assertThrows( IllegalArgumentException.class, () -> SearchIndexNames.alias( ACME, "repo+g2" ) );
    }
}
