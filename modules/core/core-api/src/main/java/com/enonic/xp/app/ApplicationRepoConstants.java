package com.enonic.xp.app;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

/**
 * The repository the installed applications are persisted in: the node of an application is a child of the root, named by the
 * application key, with the application descriptor and the schema the application owns below it.
 */
public final class ApplicationRepoConstants
{
    public static final RepositoryId APP_REPO_ID = RepositoryId.from( "system.app" );

    public static final Branch APP_REPO_BRANCH = Branch.create().value( "master" ).build();

    public static final AccessControlList APP_REPO_DEFAULT_ACL =
        AccessControlList.create().add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ).build();

    private ApplicationRepoConstants()
    {
    }
}
