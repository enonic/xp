package com.enonic.wem.repo.internal.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.xp.core.branch.Branch;
import com.enonic.wem.repo.internal.elasticsearch.branch.BranchIndexPath;
import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;

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
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for BranchDocument", e );
        }

    }

}
