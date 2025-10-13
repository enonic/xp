package com.enonic.xp.core.impl.project.init;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.core.impl.project.CreateProjectRolesCommand;
import com.enonic.xp.core.impl.project.ProjectAccessHelper;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class Xp8DefaultProjectMigrator
{
    public static final ProjectName DEFAULT_PROJECT_NAME = ProjectName.from( RepositoryId.from( "com.enonic.cms.default" ) );

    private static final String DEFAULT_PROJECT_DISPLAY_NAME = "Default";

    private static final PrincipalKey OWNER = ProjectAccessHelper.createRoleKey( DEFAULT_PROJECT_NAME, ProjectRole.OWNER );

    private static final PrincipalKey EDITOR = ProjectAccessHelper.createRoleKey( DEFAULT_PROJECT_NAME, ProjectRole.EDITOR );

    private static final PrincipalKey AUTHOR = ProjectAccessHelper.createRoleKey( DEFAULT_PROJECT_NAME, ProjectRole.AUTHOR );

    private static final PrincipalKey CONTRIBUTOR = ProjectAccessHelper.createRoleKey( DEFAULT_PROJECT_NAME, ProjectRole.CONTRIBUTOR );

    private static final PrincipalKey VIEWER = ProjectAccessHelper.createRoleKey( DEFAULT_PROJECT_NAME, ProjectRole.VIEWER );

    private static final User SUPER_USER =
        User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();

    private static final List<AccessControlEntry> EXTRA_PERMISSIONS_CONTENT_OR_ARCHIVE =
        List.of( AccessControlEntry.create().allowAll().principal( OWNER ).build(),
                 AccessControlEntry.create().allowAll().principal( EDITOR ).build(), AccessControlEntry.create()
                     .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                     .principal( AUTHOR )
                     .build(), AccessControlEntry.create().allow( Permission.READ ).principal( CONTRIBUTOR ).build(),
                 AccessControlEntry.create().allow( Permission.READ ).principal( VIEWER ).build() );

    private final NodeService nodeService;

    private final SecurityService securityService;

    private final IndexService indexService;

    public Xp8DefaultProjectMigrator( final NodeService nodeService, final SecurityService securityService, final IndexService indexService )
    {
        this.nodeService = nodeService;
        this.securityService = securityService;
        this.indexService = indexService;
    }

    public void migrate()
    {
        if ( indexService.isMaster() )
        {
            doMigrate();
        }
    }

    private void doMigrate()
    {
        List.of( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER )
            .forEach( branch -> createAdminContext( branch ).runWith( () -> {
                migrateContentNode();
                migrateIssueNode();
            } ) );

        createAdminContext( ContentConstants.BRANCH_DRAFT ).runWith( this::migrateArchiveNode );

        CreateProjectRolesCommand.create()
            .securityService( securityService )
            .projectName( DEFAULT_PROJECT_NAME )
            .projectDisplayName( DEFAULT_PROJECT_DISPLAY_NAME )
            .build()
            .execute();
    }

    private void migrateArchiveNode()
    {
        applyPermissions( ArchiveConstants.ARCHIVE_ROOT_PATH, EXTRA_PERMISSIONS_CONTENT_OR_ARCHIVE );
    }

    private void migrateIssueNode()
    {
        final List<AccessControlEntry> permissions = new ArrayList<>();
        permissions.add( AccessControlEntry.create()
                             .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                             .principal( OWNER )
                             .build() );
        permissions.add( AccessControlEntry.create()
                             .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                             .principal( EDITOR )
                             .build() );
        permissions.add( AccessControlEntry.create()
                             .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                             .principal( AUTHOR )
                             .build() );
        permissions.add( AccessControlEntry.create()
                             .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                             .principal( CONTRIBUTOR )
                             .build() );
        permissions.add( AccessControlEntry.create().allow( Permission.READ ).principal( VIEWER ).build() );

        applyPermissions( IssueConstants.ISSUE_ROOT_PATH, permissions );
    }

    private void migrateContentNode()
    {
        applyPermissions( ContentConstants.CONTENT_ROOT_PATH, EXTRA_PERMISSIONS_CONTENT_OR_ARCHIVE );
    }

    private void applyPermissions( final NodePath nodePath, final List<AccessControlEntry> extraPermissions )
    {
        final Node rootNode = nodeService.getByPath( nodePath );

        if ( rootNode != null && rootNode.getPermissions().contains( RoleKeys.CONTENT_MANAGER_APP ) )
        {
            nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                              .nodeId( rootNode.id() )
                                              .scope( ApplyPermissionsScope.TREE )
                                              .addPermissions( AccessControlList.create().addAll( extraPermissions ).build() )
                                              .removePermissions( AccessControlList.create()
                                                                      .add( AccessControlEntry.create()
                                                                                .principal( RoleKeys.CONTENT_MANAGER_APP )
                                                                                .build() )
                                                                      .build() )
                                              .build() );
        }
    }

    private Context createAdminContext( Branch branch )
    {
        return RepoDependentInitializer.createAdminContext( branch, DEFAULT_PROJECT_NAME.getRepoId() );
    }
}
