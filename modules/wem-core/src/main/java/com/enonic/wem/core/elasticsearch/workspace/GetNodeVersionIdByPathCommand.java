package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;

public class GetNodeVersionIdByPathCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final NodePath nodePath;

    private GetNodeVersionIdByPathCommand( Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        nodePath = builder.nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }


    NodeVersionId execute()
    {
        final TermQueryBuilder parentQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.PATH_FIELD_NAME, nodePath.toString() );
        final BoolQueryBuilder workspacedByPathQuery = joinWithWorkspaceQuery( this.workspace.getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( 1, this.repository );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, workspacedByPathQuery );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final Object value = firstHit.getField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).getValue();

        if ( value == null )
        {
            throw new ElasticsearchDataException(
                "Field " + WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME + " not found on node with path " +
                    nodePath + " in workspace " + this.workspace );
        }

        return NodeVersionId.from( value.toString() );
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private NodePath nodePath;

        private Builder()
        {
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public GetNodeVersionIdByPathCommand build()
        {
            return new GetNodeVersionIdByPathCommand( this );
        }
    }
}
