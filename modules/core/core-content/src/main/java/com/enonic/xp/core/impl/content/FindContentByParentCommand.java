package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.GetContentByIdsParams;

final class FindContentByParentCommand
    extends AbstractContentCommand
{
    private final FindContentByParentParams params;

    private FindContentByParentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final FindContentByParentParams params )
    {
        return new Builder( params );
    }

    FindContentByParentResult execute()
    {
        final FindContentIdsByParentResult idsResult = FindContentIdsByParentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        final Contents contents =
            GetContentByIdsCommand.create( GetContentByIdsParams.create().contentIds( idsResult.getContentIds() ).build() )
                .nodeService( this.nodeService )
                .contentTypeService( this.contentTypeService )
                .eventPublisher( this.eventPublisher )
                .build()
                .execute();

        return FindContentByParentResult.create().contents( contents ).totalHits( idsResult.getTotalHits() ).build();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final FindContentByParentParams params;

        Builder( final FindContentByParentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public FindContentByParentCommand build()
        {
            validate();
            return new FindContentByParentCommand( this );
        }
    }

}
