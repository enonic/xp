package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.core.entity.NodePaths;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultField;

public class GetNodeVersionIdsByPathsCommand
    extends AbstractWorkspaceCommand
{
    private final NodePaths nodePaths;

    private final Workspace workspace;

    private GetNodeVersionIdsByPathsCommand( Builder builder )
    {
        super( builder );
        nodePaths = builder.nodePaths;
        workspace = builder.workspace;
    }

    public static Builder create()
    {
        return new Builder();
    }

    NodeVersionIds execute()
    {
        final TermsQueryBuilder parentQuery =
            new TermsQueryBuilder( WorkspaceXContentBuilderFactory.PATH_FIELD_NAME, nodePaths.getAsStrings() );
        final BoolQueryBuilder workspacedByPathsQuery = joinWithWorkspaceQuery( this.workspace.getName(), parentQuery );
        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( nodePaths.getSize(), this.repositoryId );

        final SearchResult searchResult = elasticsearchDao.search( queryMetaData, workspacedByPathsQuery );

        final Set<SearchResultField> fieldValues =
            searchResult.getResults().getFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        return fieldValuesToVersionIds( fieldValues );
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private NodePaths nodePaths;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder nodePaths( NodePaths nodePaths )
        {
            this.nodePaths = nodePaths;
            return this;
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public GetNodeVersionIdsByPathsCommand build()
        {
            return new GetNodeVersionIdsByPathsCommand( this );
        }
    }
}
