package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssuePropertyNames;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class DefaultProjectPermissionsUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( DefaultProjectPermissionsUpgrader.class );

    private static final RepositoryId DEFAULT_PROJECT_REPO_ID = RepositoryId.from( "com.enonic.cms.default" );

    private static final PrincipalKey OWNER =
        PrincipalKey.ofRole( ProjectConstants.PROJECT_NAME_PREFIX + "default.owner" );

    private static final PrincipalKey EDITOR =
        PrincipalKey.ofRole( ProjectConstants.PROJECT_NAME_PREFIX + "default.editor" );

    private static final PrincipalKey AUTHOR =
        PrincipalKey.ofRole( ProjectConstants.PROJECT_NAME_PREFIX + "default.author" );

    private static final PrincipalKey CONTRIBUTOR =
        PrincipalKey.ofRole( ProjectConstants.PROJECT_NAME_PREFIX + "default.contributor" );

    private static final PrincipalKey VIEWER =
        PrincipalKey.ofRole( ProjectConstants.PROJECT_NAME_PREFIX + "default.viewer" );

    private static final List<AccessControlEntry> EXTRA_CONTENT_PERMISSIONS =
        List.of( AccessControlEntry.create().allowAll().principal( OWNER ).build(),
                 AccessControlEntry.create().allowAll().principal( EDITOR ).build(),
                 AccessControlEntry.create()
                     .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                     .principal( AUTHOR )
                     .build(),
                 AccessControlEntry.create().allow( Permission.READ ).principal( CONTRIBUTOR ).build(),
                 AccessControlEntry.create().allow( Permission.READ ).principal( VIEWER ).build() );

    public static final List<AccessControlEntry> EXTRA_ISSUES_PERMISSIONS = List.of( AccessControlEntry.create()
                                                                                         .allow( Permission.READ, Permission.CREATE,
                                                                                                 Permission.MODIFY, Permission.DELETE )
                                                                                         .principal( OWNER )
                                                                                         .build(), AccessControlEntry.create()
                                                                                         .allow( Permission.READ, Permission.CREATE,
                                                                                                 Permission.MODIFY, Permission.DELETE )
                                                                                         .principal( EDITOR )
                                                                                         .build(), AccessControlEntry.create()
                                                                                         .allow( Permission.READ, Permission.CREATE,
                                                                                                 Permission.MODIFY, Permission.DELETE )
                                                                                         .principal( AUTHOR )
                                                                                         .build(), AccessControlEntry.create()
                                                                                         .allow( Permission.READ, Permission.CREATE,
                                                                                                 Permission.MODIFY, Permission.DELETE )
                                                                                         .principal( CONTRIBUTOR )
                                                                                         .build(), AccessControlEntry.create()
                                                                                         .allow( Permission.READ )
                                                                                         .principal( VIEWER )
                                                                                         .build() );


    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !DEFAULT_PROJECT_REPO_ID.equals( repositoryId ) )
        {
            return null;
        }

        final AccessControlList permissions = nodeVersion.permissions();
        if ( !permissions.contains( RoleKeys.CONTENT_MANAGER_APP ) )
        {
            return null;
        }

        final AccessControlList.Builder aclBuilder = AccessControlList.create( permissions ).remove( RoleKeys.CONTENT_MANAGER_APP );

        final List<AccessControlEntry> extraPermissions = isIssueNode( nodeVersion ) ? EXTRA_ISSUES_PERMISSIONS : EXTRA_CONTENT_PERMISSIONS;
        extraPermissions.forEach( aclBuilder::add );

        LOG.info( "Upgraded permissions for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );

        return NodeStoreVersion.create( nodeVersion ).permissions( aclBuilder.build() ).build();
    }

    private static boolean isIssueNode( final NodeStoreVersion nodeVersion )
    {
        final NodeType nodeType = nodeVersion.nodeType();
        if ( IssueConstants.ISSUE_NODE_COLLECTION.equals( nodeType ) )
        {
            return true;
        }
        return "Root issue".equals( nodeVersion.data().getString( IssuePropertyNames.TITLE ) );
    }
}
