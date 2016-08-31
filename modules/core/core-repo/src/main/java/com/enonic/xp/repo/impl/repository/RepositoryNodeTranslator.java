package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.acl.AccessControlList;

public class RepositoryNodeTranslator
{
    public static CreateNodeParams toCreateNodeParams( final Repository repository )
    {
        final PropertyTree repositoryData = new PropertyTree();

        return CreateNodeParams.create().
            data( repositoryData ).
            name( repository.getId().toString() ).
            parent( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( AccessControlList.empty() ).
            build();
    }

    public static Repository fromNode( final Node node )
    {
        final PropertyTree data = node.data();

        return Repository.create().
            id( RepositoryId.from( data.getString( "repositoryId" ) ) ).
            build();
    }
}
