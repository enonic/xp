package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.index.query.NodeQueryResult;

final class FindContentCommand
    extends AbstractFindContentCommand
{
    private ContentQuery contentQuery;

    private FindContentCommand( final Builder builder )
    {
        super( builder );
        contentQuery = builder.contentQuery;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.contentQuery );

        final NodeQueryResult queryResult = queryService.find( nodeQuery, context.getWorkspace() );

        return translateToContentQueryResult( queryResult );
    }


    public static final class Builder
        extends AbstractFindContentCommand.Builder<Builder>
    {
        private ContentQuery contentQuery;

        private Builder()
        {
        }

        public Builder contentQuery( ContentQuery contentQuery )
        {
            this.contentQuery = contentQuery;
            return this;
        }

        public FindContentCommand build()
        {
            validate();
            return new FindContentCommand( this );
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentQuery );
        }

    }
}
