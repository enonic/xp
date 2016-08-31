package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

public class RepositoryConstants
{
    public static final NodePath REPOSITORY_STORAGE_PARENT_PATH = NodePath.create( NodePath.ROOT, "repository" ).build();

    public static final RepositoryId REPOSITORY_STORAGE_REPO_ID = SystemConstants.SYSTEM_REPO.getId();

    public static final Branch REPOSITORY_STORAGE_BRANCH = SystemConstants.BRANCH_SYSTEM;
}
