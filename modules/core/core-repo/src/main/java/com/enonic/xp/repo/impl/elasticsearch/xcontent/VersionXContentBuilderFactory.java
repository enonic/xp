package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repo.impl.version.NodeVersionDocument;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

public class VersionXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static XContentBuilder create( final NodeVersionDocument nodeVersionDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, VersionIndexPath.VERSION_ID.getPath(), nodeVersionDocument.getNodeVersionId().toString() );
            addField( builder, VersionIndexPath.NODE_ID.getPath(), nodeVersionDocument.getNodeId().toString() );
            addField( builder, VersionIndexPath.TIMESTAMP.getPath(),
                      nodeVersionDocument.getTimestamp() != null ? nodeVersionDocument.getTimestamp() : Instant.now() );
            addField( builder, VersionIndexPath.NODE_PATH.getPath(), nodeVersionDocument.getNodePath().toString() );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for BranchDocument", e );
        }

    }

}
