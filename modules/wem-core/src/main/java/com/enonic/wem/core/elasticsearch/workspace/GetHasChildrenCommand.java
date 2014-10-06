package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;

public class GetHasChildrenCommand
    extends AbstractWorkspaceCommand
{
    private final NodePath parentPath;

    private final Workspace workspace;

    private GetHasChildrenCommand( Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
        workspace = builder.workspace;
    }

    public static Builder create()
    {
        return new Builder();
    }

    boolean execute()
    {
        final QueryMetaData queryMetaData = QueryMetaData.create( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            from( 0 ).
            size( 0 ).
            build();

        final TermQueryBuilder findWithParentQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.PARENT_PATH_FIELD_NAME, this.parentPath.toString() );

        final BoolQueryBuilder joinWithWorkspaceQuery = joinWithWorkspaceQuery( this.workspace.getName(), findWithParentQuery );

        final long count = elasticsearchDao.count( queryMetaData, joinWithWorkspaceQuery );

        return count > 0;
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public GetHasChildrenCommand build()
        {
            return new GetHasChildrenCommand( this );
        }
    }
}
