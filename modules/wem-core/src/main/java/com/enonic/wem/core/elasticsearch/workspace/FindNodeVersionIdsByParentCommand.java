package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.QueryProperties;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultField;

public class FindNodeVersionIdsByParentCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final NodePath parentPath;

    private FindNodeVersionIdsByParentCommand( Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        parentPath = builder.parentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }


    NodeVersionIds execute()
    {
        final TermQueryBuilder getByParentQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.PARENT_PATH_FIELD_NAME, this.parentPath );
        final TermQueryBuilder workspaceQuery = createWorkspaceQuery( this.workspace );
        final BoolQueryBuilder query = join( workspaceQuery, getByParentQuery );

        final QueryProperties queryProperties = createGetBlobKeyQueryMetaData( DEFAULT_UNKNOWN_SIZE, this.repositoryId );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, query );

        if ( searchResult.getResults().getSize() == 0 )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues =
            searchResult.getResults().getFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        return fieldValuesToVersionIds( fieldValues );
    }


    public static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private NodePath parentPath;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder parentPath( NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public FindNodeVersionIdsByParentCommand build()
        {
            return new FindNodeVersionIdsByParentCommand( this );
        }
    }
}
