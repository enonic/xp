package com.enonic.wem.repo.internal.elasticsearch.xcontent;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

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
            addField( builder, VersionIndexPath.TIMESTAMP.getPath(), Instant.now() );
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
