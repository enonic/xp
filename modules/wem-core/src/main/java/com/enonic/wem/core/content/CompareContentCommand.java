package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentCompareResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Workspace;

public class CompareContentCommand
{
    private final ContentId contentId;

    private final Workspace target;

    private final Context context;

    private final NodeService nodeService;

    private CompareContentCommand( final Builder builder )
    {
        this.context = builder.context;
        this.contentId = builder.contentId;
        this.target = builder.target;
        this.nodeService = builder.nodeService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentCompareResult execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        final EntityComparison compareResult = this.nodeService.compare( entityId, this.target, this.context );

        return CompareResultTranslator.translate( compareResult );
    }

    public static class Builder
    {
        private ContentId contentId;

        private Workspace target;

        private Context context;

        private NodeService nodeService;

        public Builder id( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public CompareContentCommand build()
        {
            return new CompareContentCommand( this );
        }
    }

}
