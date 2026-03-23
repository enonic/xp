package com.enonic.xp.core.dump;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.config.RepoConfigurationDynamic;
import com.enonic.xp.repo.impl.dump.DumpServiceImpl;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.scheduler.SchedulerConstants;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DumpUpgradeIntegrationTest
    extends AbstractNodeTest
{
    private DumpServiceImpl dumpService;

    DumpUpgradeIntegrationTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
        throws Exception
    {
        final RepoConfigurationDynamic repoConfiguration = new RepoConfigurationDynamic();
        repoConfiguration.activate( Map.of( "dumps.dir", temporaryFolder.toString() ) );

        this.dumpService =
            new DumpServiceImpl( eventPublisher, BLOB_STORE, this.nodeService, this.repositoryEntryService,
                                 this.nodeRepositoryService, this.storageService, this.branchService, repoConfiguration );

        copyClasspathResource( "dump-7.zip", temporaryFolder );
    }

    @Test
    void upgradeAndLoadDump7()
    {
        // Upgrade
        final DumpUpgradeResult upgradeResult =
            NodeHelper.runAsAdmin( () -> this.dumpService.upgrade( SystemDumpUpgradeParams.create().dumpName( "dump-7" ).build() ) );

        assertEquals( new Version( 8, 0, 0 ), upgradeResult.getInitialVersion() );
        assertEquals( Version.parseVersion( "9" ), upgradeResult.getUpgradedVersion() );

        final String upgradedDumpName = upgradeResult.getDumpName();
        assertThat( upgradedDumpName ).startsWith( "dump-7-upgraded-to-" );

        // Load upgraded dump
        final SystemLoadResult loadResult = NodeHelper.runAsAdmin(
            () -> this.dumpService.load( SystemLoadParams.create().dumpName( upgradedDumpName ).includeVersions( true ).build() ) );

        assertNotNull( loadResult );

        refresh();

        // Verify AttachmentSha512Upgrader: content-with-attachment should have sha512 restored
        final Node attachmentContent = contentDraftContext().callWith( () -> {
            final var result = nodeService.findByQuery(
                NodeQuery.create().query( QueryParser.parse( "displayName = 'Content With Attachment'" ) ).build() );
            assertThat( result.getNodeIds() ).hasSize( 1 );
            return nodeService.getById( result.getNodeIds().first() );
        } );
        assertNotNull(
            attachmentContent.data().getSet( ContentPropertyNames.ATTACHMENT ).getString( ContentPropertyNames.ATTACHMENT_SHA512 ),
            "Attachment sha512 should be computed by upgrader" );

        // Verify LanguageTagUpgrader: content language should remain en-GB (already hyphenated)
        final Node contentFolder = contentDraftContext().callWith( () -> {
            final var result =
                nodeService.findByQuery( NodeQuery.create().query( QueryParser.parse( "displayName = 'My Content'" ) ).build() );
            assertThat( result.getNodeIds() ).hasSize( 1 );
            return nodeService.getById( result.getNodeIds().first() );
        } );
        assertEquals( "en-GB", contentFolder.data().getString( ContentPropertyNames.LANGUAGE ) );

        // Verify DefaultProjectPermissionsUpgrader: system-repo should have project role nodes
        systemRepoContext().callWith( () -> {
            assertNotNull( nodeService.getByPath( new NodePath( "/identity/roles/cms.project.default.owner" ) ),
                           "default.owner role should exist" );
            assertNotNull( nodeService.getByPath( new NodePath( "/identity/roles/cms.project.default.editor" ) ),
                           "default.editor role should exist" );
            assertNotNull( nodeService.getByPath( new NodePath( "/identity/roles/cms.project.default.author" ) ),
                           "default.author role should exist" );
            assertNotNull( nodeService.getByPath( new NodePath( "/identity/roles/cms.project.default.contributor" ) ),
                           "default.contributor role should exist" );
            assertNotNull( nodeService.getByPath( new NodePath( "/identity/roles/cms.project.default.viewer" ) ),
                           "default.viewer role should exist" );
            return null;
        } );

        // Verify audit log exists
        auditLogContext().callWith( () -> {
            final var result =
                nodeService.findByQuery( NodeQuery.create().query( QueryParser.parse( "type = 'test.generator'" ) ).build() );
            assertThat( result.getNodeIds() ).isNotEmpty();
            return null;
        } );

        // Verify scheduler job exists
        schedulerContext().callWith( () -> {
            final Node jobNode = nodeService.getByPath( new NodePath( "/one-time-job" ) );
            assertNotNull( jobNode, "Scheduled job should be loaded" );
            return null;
        } );

        // Verify RepositoryBranchesRemovalUpgrader: repository nodes should no longer have "branches" property
        systemRepoContext().callWith( () -> {
            final Node repoNode = nodeService.getById( NodeId.from( "com.enonic.cms.default" ) );
            assertNotNull( repoNode, "Repository node should exist" );
            assertFalse( repoNode.data().hasProperty( "branches" ), "branches property should be removed by upgrader" );
            return null;
        } );
    }

    private com.enonic.xp.context.Context contentDraftContext()
    {
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( RepositoryId.from( "com.enonic.cms.default" ) )
            .authInfo( adminAuthInfo() )
            .build();
    }

    private com.enonic.xp.context.Context systemRepoContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( adminAuthInfo() )
            .build();
    }

    private com.enonic.xp.context.Context auditLogContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( RepositoryId.from( "system.auditlog" ) )
            .authInfo( adminAuthInfo() )
            .build();
    }

    private com.enonic.xp.context.Context schedulerContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SchedulerConstants.SCHEDULER_REPO_ID )
            .authInfo( adminAuthInfo() )
            .build();
    }

    private static AuthenticationInfo adminAuthInfo()
    {
        return AuthenticationInfo.create().principals( RoleKeys.ADMIN, RoleKeys.AUTHENTICATED ).user( TEST_DEFAULT_USER ).build();
    }

    private static void copyClasspathResource( final String resourceName, final Path targetDir )
    {
        try (InputStream is = DumpUpgradeIntegrationTest.class.getClassLoader().getResourceAsStream( resourceName ))
        {
            Files.copy( Objects.requireNonNull( is ), targetDir.resolve( resourceName ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
