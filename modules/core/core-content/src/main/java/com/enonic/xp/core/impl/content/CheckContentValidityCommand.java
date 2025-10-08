package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentValidityResult;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class CheckContentValidityCommand
    extends AbstractContentCommand
{
    private static final String WORKFLOW_STATE_FIELD =
        String.join( ".", ContentPropertyNames.WORKFLOW_INFO, ContentPropertyNames.WORKFLOW_INFO_STATE );

    private final ContentIds contentIds;

    private CheckContentValidityCommand( final Builder builder )
    {
        super( builder );
        contentIds = builder.contentIds;
    }

    public ContentValidityResult execute()
    {
        if ( this.contentIds.isEmpty() )
        {
            return ContentValidityResult.create().
            notValidContentIds( ContentIds.empty() ).
            notReadyContentIds( ContentIds.empty() ).
            build();
        }

        // valid == false
        final Filter notValid = ValueFilter.create().
            fieldName( ContentPropertyNames.VALID ).
            addValue( ValueFactory.newBoolean( false ) ).
            build();

        // workflow != null && workflow != READY
        final Filter notReady = BooleanFilter.create().
            must( ExistsFilter.create().fieldName( WORKFLOW_STATE_FIELD ).build() ).
            mustNot( ValueFilter.create().fieldName( WORKFLOW_STATE_FIELD ).addValues( WorkflowState.READY.toString() ).build() ).
            build();

        ContentIds invalidContentIds = FindContentIdsByQueryCommand.create()
            .query( ContentQuery.create()
                        .queryFilter( notValid )
                        .queryFilter( IdFilter.create().fieldName( ContentIndexPath.ID.getPath() ).values( contentIds ).build() )
                        .size( contentIds.getSize() )
                        .build() )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute()
            .getContentIds();

        ContentIds notReadyContentIds = FindContentIdsByQueryCommand.create()
            .query( ContentQuery.create()
                        .queryFilter( notReady )
                        .queryFilter( IdFilter.create().fieldName( ContentIndexPath.ID.getPath() ).values( contentIds ).build() )
                        .size( contentIds.getSize() )
                        .build() )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute()
            .getContentIds();


        return ContentValidityResult.create().
            notValidContentIds( invalidContentIds ).
            notReadyContentIds( notReadyContentIds ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public CheckContentValidityCommand build()
        {
            return new CheckContentValidityCommand( this );
        }
    }
}
