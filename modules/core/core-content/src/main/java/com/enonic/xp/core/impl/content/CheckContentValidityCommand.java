package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
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

    private final boolean checkWorkflow;

    private CheckContentValidityCommand( final Builder builder )
    {
        super( builder );
        contentIds = builder.contentIds;
        checkWorkflow = builder.checkWorkflow;
    }

    public ContentIds execute()
    {
        if ( this.contentIds.getSize() == 0 )
        {
            return ContentIds.empty();
        }

        // valid == false
        final Filter notValid = ValueFilter.create().
            fieldName( ContentPropertyNames.VALID ).
            addValue( ValueFactory.newBoolean( false ) ).
            build();

        Filter notOk;

        if ( !checkWorkflow )
        {
            // Query: valid == false
            notOk = notValid;
        }
        else
        {
            // Query: valid == false OR (workflow != null && workflow != READY)
            notOk = BooleanFilter.create().
                should( notValid ).
                should( notReady() ).
                build();
        }

        final ContentQuery query = ContentQuery.create().
            queryFilter( notOk ).
            queryFilter( IdFilter.create().
                fieldName( ContentIndexPath.ID.getPath() ).
                values( contentIds.asStrings() ).
                build() ).
            size( -1 ).
            build();

        final FindContentByQueryResult result = FindContentByQueryCommand.create().
            params( FindContentByQueryParams.create().
                contentQuery( query ).
                populateChildren( false ).
                build() ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            build().
            execute();

        return ContentIds.from( result.getContents().stream().map( content -> content.getId() ).collect( Collectors.toList() ) );
    }

    private Filter notReady()
    {
        // workflow != null && workflow != READY
        return BooleanFilter.create().
            must( ExistsFilter.create().fieldName( WORKFLOW_STATE_FIELD ).build() ).
            mustNot( ValueFilter.create().fieldName( WORKFLOW_STATE_FIELD ).addValues( WorkflowState.READY.toString() ).build() ).
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

        private boolean checkWorkflow;

        private Builder()
        {
            checkWorkflow = false;
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder checkWorkflow( final boolean checkWorkflow )
        {
            this.checkWorkflow = checkWorkflow;
            return this;
        }

        public CheckContentValidityCommand build()
        {
            return new CheckContentValidityCommand( this );
        }
    }
}
