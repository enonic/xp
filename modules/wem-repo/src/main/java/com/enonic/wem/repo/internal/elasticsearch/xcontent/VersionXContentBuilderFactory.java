package com.enonic.wem.repo.internal.elasticsearch.xcontent;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;

public class VersionXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static final String NODE_VERSION_ID_FIELD_NAME = "versionid";

    public static final String NODE_ID_FIELD_NAME = "nodeid";

    public static final String TIMESTAMP_ID_FIELD_NAME = "timestamp";

    public static XContentBuilder create( final NodeVersionDocument nodeVersionDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, NODE_VERSION_ID_FIELD_NAME, nodeVersionDocument.getNodeVersionId().toString() );
            addField( builder, NODE_ID_FIELD_NAME, nodeVersionDocument.getNodeId().toString() );
            addField( builder, TIMESTAMP_ID_FIELD_NAME, Instant.now() );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for WorkspaceDocument", e );
        }

    }

}
