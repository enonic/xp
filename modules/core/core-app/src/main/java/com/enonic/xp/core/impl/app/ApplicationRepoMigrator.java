package com.enonic.xp.core.impl.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRepoConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.BinaryReference;

/**
 * Moves the applications installed before the application repository existed, persisted as {@code /applications/<name>} in
 * system-repo with nothing but the bundle, to {@link ApplicationRepoConstants#APP_REPO_ID}. Each is persisted there the way a global
 * installation persists it: the node built from the bundle's manifest, the bundle attached, the descriptor and the schema the bundle
 * ships below it. Only the started state is carried over as it was.
 * <p>
 * An application whose bundle cannot be prepared, or whose nodes cannot be written, is logged and left in system-repo for the next
 * run; nothing of it stays in the application repository. The {@code /applications} node is removed once it has no children left.
 */
public final class ApplicationRepoMigrator
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationRepoMigrator.class );

    public static final NodePath LEGACY_APPLICATIONS_PATH = new NodePath( NodePath.ROOT, NodeName.from( "applications" ) );

    private final NodeService nodeService;

    private final ApplicationRepoService repoService;

    private final EventPublisher eventPublisher;

    public ApplicationRepoMigrator( final NodeService nodeService, final ApplicationRepoService repoService, final EventPublisher eventPublisher )
    {
        this.nodeService = nodeService;
        this.repoService = repoService;
        this.eventPublisher = eventPublisher;
    }

    public void migrate()
    {
        final Nodes legacyNodes = legacyContext().callWith( this::findLegacyApplicationNodes );
        if ( legacyNodes == null )
        {
            return;
        }

        LOG.info( "Migrating [{}] applications from {} in {} to {}", legacyNodes.getSize(), LEGACY_APPLICATIONS_PATH,
                  SystemConstants.SYSTEM_REPO_ID, ApplicationRepoConstants.APP_REPO_ID );

        final List<ApplicationKey> failed = new ArrayList<>();
        for ( final Node legacyNode : legacyNodes )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( legacyNode.name().toString() );
            try
            {
                migrate( legacyNode, applicationKey );
                LOG.info( "Application [{}] migrated to {}", applicationKey, ApplicationRepoConstants.APP_REPO_ID );
            }
            catch ( Exception e )
            {
                failed.add( applicationKey );
                LOG.error( "Cannot migrate application [{}], it is left in {}", applicationKey, SystemConstants.SYSTEM_REPO_ID, e );
            }
        }

        if ( failed.isEmpty() )
        {
            legacyContext().runWith( () -> nodeService.delete(
                DeleteNodeParams.create().nodePath( LEGACY_APPLICATIONS_PATH ).refresh( RefreshMode.ALL ).build() ) );
            LOG.info( "Removed {} from {}", LEGACY_APPLICATIONS_PATH, SystemConstants.SYSTEM_REPO_ID );
        }
        else
        {
            LOG.error( "[{}] applications were not migrated and are left in {}: {}", failed.size(), SystemConstants.SYSTEM_REPO_ID, failed );
        }
    }

    /**
     * @return the application nodes below the legacy folder, {@code null} when there is no such folder
     */
    private Nodes findLegacyApplicationNodes()
    {
        if ( !nodeService.nodeExists( LEGACY_APPLICATIONS_PATH ) )
        {
            return null;
        }
        final NodeIds nodeIds = nodeService.list( ListNodesParams.create().parentPath( LEGACY_APPLICATIONS_PATH ).build() )
            .filter( entry -> LEGACY_APPLICATIONS_PATH.equals( entry.nodePath().getParentPath() ) )
            .map( NodeListEntry::nodeId )
            .collect( NodeIds.collector() );
        return nodeService.getByIds( nodeIds );
    }

    private void migrate( final Node legacyNode, final ApplicationKey applicationKey )
    {
        final ByteSource bundle = legacyContext().callWith(
            () -> nodeService.getBinary( legacyNode.id(), BinaryReference.from( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) ) );
        if ( bundle == null )
        {
            throw new ApplicationBundleException( "Application node has no bundle" );
        }

        // the bundle is read and validated as on installation: nothing is written for an application that would not install
        final PreparedApplication prepared = PreparedApplication.prepare( bundle );
        if ( !applicationKey.equals( prepared.key() ) )
        {
            throw new ApplicationBundleException( String.format( "Application node holds the bundle of [%s]", prepared.key() ) );
        }

        final boolean started = Boolean.TRUE.equals( legacyNode.data().getBoolean( ApplicationPropertyNames.STARTED ) );

        ApplicationHelper.runAsAdmin( () -> {
            try
            {
                // left by a run that failed before the legacy node was removed: replaced as a whole
                if ( repoService.getApplicationNode( applicationKey ) != null )
                {
                    repoService.deleteApplicationNode( applicationKey );
                }
                repoService.upsertApplicationNode( prepared.appInfo(), bundle );
                repoService.persistApplicationSchema( applicationKey, prepared.schemaResources() );
                if ( started )
                {
                    repoService.updateStartedState( applicationKey, true );
                }
            }
            catch ( RuntimeException e )
            {
                try
                {
                    if ( repoService.getApplicationNode( applicationKey ) != null )
                    {
                        repoService.deleteApplicationNode( applicationKey );
                    }
                }
                catch ( Exception cleanupFailure )
                {
                    e.addSuppressed( cleanupFailure );
                }
                throw e;
            }
        } );

        legacyContext().runWith(
            () -> nodeService.delete( DeleteNodeParams.create().nodePath( legacyNode.path() ).refresh( RefreshMode.ALL ).build() ) );

        // the other cluster nodes install the application from its new nodes, as they do on a global installation
        eventPublisher.publish( ApplicationClusterEvents.install( applicationKey ) );
        if ( started )
        {
            eventPublisher.publish( ApplicationClusterEvents.start( applicationKey ) );
        }
    }

    // the legacy nodes are in system-repo, read and removed as the application super user
    private static Context legacyContext()
    {
        return ContextBuilder.create()
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .authInfo( ApplicationConstants.APPLICATION_SU_AUTH_INFO )
            .build();
    }
}
