package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentCompareResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class CompareContentCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final Workspace target;

    private CompareContentCommand( final Builder builder )
    {
        super( builder );

        this.contentId = builder.contentId;
        this.target = builder.target;
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
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private Workspace target;


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
