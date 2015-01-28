package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.api.query.Direction;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.workspace.Workspace;

public class ContentConstants
{
    public static final String CONTENT_DEFAULT_ANALYZER = "content_default";

    public static final Workspace WORKSPACE_STAGE = Workspace.create().
        name( "stage" ).
        build();

    public static final Workspace WORKSPACE_PROD = Workspace.create().
        name( "prod" ).
        build();

    public static final Repository CONTENT_REPO = Repository.create().
        id( RepositoryId.from( "wem-content-repo" ) ).
        build();

    public static final Context CONTEXT_STAGE = ContextBuilder.create().
        workspace( WORKSPACE_STAGE ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final Context CONTEXT_PROD = ContextBuilder.create().
        workspace( WORKSPACE_PROD ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final String CONTENT_ROOT_NAME = "content";

    public static final NodePath CONTENT_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath CONTENT_ROOT_PATH = NodePath.newNodePath( CONTENT_ROOT_PARENT, CONTENT_ROOT_NAME ).build();

    public static final NodeType CONTENT_NODE_COLLECTION = NodeType.from( "content" );

    public static final AccessControlList CONTENT_ROOT_DEFAULT_ACL = AccessControlList.create().
        add( AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allowAll().
            build() ).
        build();

    public static final ChildOrder CONTENT_DEFAULT_CHILD_ORDER = ChildOrder.from( ContentIndexPath.DISPLAY_NAME + " " + Direction.ASC );

}
