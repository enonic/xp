package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.branch.BranchInfos;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Beta
public class ContentConstants
{
    private static final PrincipalKey CONTENT_SUPER_USER_KEY = PrincipalKey.ofUser( IdProviderKey.system(), "content-su" );

    private static final User CONTENT_SUPER_USER = User.create().key( CONTENT_SUPER_USER_KEY ).login( "content" ).build();

    public static final AuthenticationInfo CONTENT_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( CONTENT_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( CONTENT_SUPER_USER ).
        build();

    public static final String DOCUMENT_INDEX_DEFAULT_ANALYZER = "document_index_default";

    public static final Branch BRANCH_DRAFT = Branch.from( "draft" );

    public static final Branch BRANCH_MASTER = Branch.from( "master" );

    public static final BranchInfo BRANCH_INFO_DRAFT = BranchInfo.from( BRANCH_DRAFT );

    public static final BranchInfo BRANCH_INFO_MASTER = BranchInfo.from( BRANCH_MASTER );

    public static final String CONTENT_REPO_ID_PREFIX = "com.enonic.cms.";

    public static final RepositoryId CONTENT_REPO_ID = RepositoryId.from( CONTENT_REPO_ID_PREFIX + "default" );

    public static final Repository CONTENT_REPO = Repository.create().
        id( CONTENT_REPO_ID ).
        branchInfos( BranchInfos.from( BRANCH_INFO_DRAFT, BRANCH_INFO_MASTER ) ).
        build();

    public static final Context CONTEXT_DRAFT = ContextBuilder.create().
        branch( BRANCH_DRAFT ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final Context CONTEXT_MASTER = ContextBuilder.create().
        branch( BRANCH_MASTER ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final String CONTENT_ROOT_NAME = "content";

    public static final NodePath CONTENT_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath CONTENT_ROOT_PATH = NodePath.create( CONTENT_ROOT_PARENT, CONTENT_ROOT_NAME ).build();

    public static final NodeType CONTENT_NODE_COLLECTION = NodeType.from( "content" );

    public static final ChildOrder DEFAULT_CONTENT_REPO_ROOT_ORDER = ChildOrder.from( "_name ASC" );

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();

}
