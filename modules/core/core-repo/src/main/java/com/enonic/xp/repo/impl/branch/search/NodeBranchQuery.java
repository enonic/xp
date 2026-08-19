package com.enonic.xp.repo.impl.branch.search;

import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;

public class NodeBranchQuery
    extends AbstractQuery
{
    private final ReturnFields returnFields;

    private NodeBranchQuery( final Builder builder )
    {
        super( builder );
        this.returnFields = builder.returnFields;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private ReturnFields returnFields = ReturnFields.from( BranchIndexPath.entryFields() );

        private Builder()
        {
            super();
        }

        /**
         * Narrows what each hit carries, for a caller that reads less than a whole branch entry. Defaults to every field an entry holds.
         */
        public Builder returnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public NodeBranchQuery build()
        {
            return new NodeBranchQuery( this );
        }
    }
}
