package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class ContentConstants
{
    public static final Workspace WORKSPACE_STAGE = Workspace.create().
        name( "stage" ).
        childOrder( ChildOrder.create().
            add( FieldOrderExpr.create( ContentIndexPaths.DISPLAY_NAME_FIELD_NAME, OrderExpr.Direction.ASC ) ).build() ).
        build();

    public static final Workspace WORKSPACE_PROD = Workspace.create().
        name( "prod" ).
        childOrder( ChildOrder.create().
            add( FieldOrderExpr.create( ContentIndexPaths.DISPLAY_NAME_FIELD_NAME, OrderExpr.Direction.ASC ) ).build() ).
        build();

    public static final Repository CONTENT_REPO = Repository.create().
        id( RepositoryId.from( "wem-content-repo" ) ).
        build();

    public static final Context CONTEXT_STAGE = ContextBuilder.create().
        workspace( WORKSPACE_STAGE ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

}
