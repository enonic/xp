package com.enonic.nodb.server.tools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;

/**
 * One-off CLI to provision a tenant schema (DESIGN.md §4/§7.2) against a running Postgres,
 * standing in for whatever control-plane action normally calls {@link TenantProvisioner}
 * (today: only {@code nodb/bench}'s {@code BenchEnvironment} and the XP itest fixture
 * {@code NodbTestCluster} do this, both in-process). {@link
 * com.enonic.nodb.server.NodbServer} never provisions tenants itself -- {@code
 * RepositoryAdminService.createRepository} runs DDL INSIDE an already-provisioned tenant
 * schema (see {@code Tx#inTenantSchema}), it does not create the schema/role/migrations
 * that {@link TenantProvisioner#provision} sets up. Added in Phase 1 Gate D
 * (nodb/BUILD-PHASE-1.md) so a standalone {@code NodbServer} boot (e.g. behind a real XP
 * instance) has a tenant to point XP's config at, without hand-writing the provisioning
 * SQL (schema/role/grants/migrations) by hand.
 *
 * <pre>
 *   java -cp build/install/server/lib/* com.enonic.nodb.server.tools.TenantBootstrapTool \
 *       --tenant acme --pg-url jdbc:postgresql://localhost:5432/nodb --pg-user nodb --pg-password nodb
 * </pre>
 *
 * Idempotent (delegates straight to {@link TenantProvisioner#provision}): safe to re-run
 * against an already-provisioned tenant.
 */
public final class TenantBootstrapTool
{
    private TenantBootstrapTool()
    {
    }

    public static void main( String[] args )
        throws Exception
    {
        String tenant = null;
        String pgUrl = null;
        String pgUser = null;
        String pgPassword = "";
        String serviceRole = null;

        for ( int i = 0; i < args.length; i++ )
        {
            switch ( args[i] )
            {
                case "--tenant" -> tenant = args[++i];
                case "--pg-url" -> pgUrl = args[++i];
                case "--pg-user" -> pgUser = args[++i];
                case "--pg-password" -> pgPassword = args[++i];
                case "--service-role" -> serviceRole = args[++i];
                default -> throw new IllegalArgumentException( "Unknown argument: " + args[i] );
            }
        }

        if ( tenant == null || pgUrl == null || pgUser == null )
        {
            System.err.println(
                "Usage: TenantBootstrapTool --tenant <t> --pg-url <jdbc-url> --pg-user <u> --pg-password <p> [--service-role <role>]" );
            System.exit( 2 );
            return;
        }
        // The connecting Postgres role IS the service role TenantProvisioner grants
        // membership to (see Tx#inTenantTx's SET LOCAL ROLE), unless overridden.
        if ( serviceRole == null )
        {
            serviceRole = pgUser;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( pgUrl );
        config.setUsername( pgUser );
        config.setPassword( pgPassword );
        config.setMaximumPoolSize( 4 );

        try (HikariDataSource dataSource = new HikariDataSource( config ))
        {
            new TenantProvisioner( dataSource, serviceRole ).provision( new TenantContext( tenant ) );
        }

        System.out.println( "Provisioned tenant [" + tenant + "]" );
    }
}
