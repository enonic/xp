package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.branch.StoreBranchDocument;
import com.enonic.xp.repo.impl.elasticsearch.branch.BranchIndexPath;
import com.enonic.xp.repo.impl.index.IndexException;

public class BranchXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static XContentBuilder create( final StoreBranchDocument doc, final Branch branch )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addField( builder, BranchIndexPath.VERSION_ID.getPath(), doc.getNodeVersionId().toString() );
            addField( builder, BranchIndexPath.BRANCH_NAME.getPath(), branch.getName() );
            addField( builder, BranchIndexPath.NODE_ID.getPath(), doc.getNode().id().toString() );
            addField( builder, BranchIndexPath.STATE.getPath(), doc.getNode().getNodeState().value() );
            addField( builder, BranchIndexPath.PATH.getPath(), doc.getNode().path().toString() );
            addField( builder, BranchIndexPath.TIMESTAMP.getPath(), doc.getNode().getTimestamp() != null ? doc.getNode().getTimestamp() : Instant.now() );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for BranchDocument", e );
        }

    }

}
