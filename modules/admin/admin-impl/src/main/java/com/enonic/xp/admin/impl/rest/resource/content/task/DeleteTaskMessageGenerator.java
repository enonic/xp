package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;

class DeleteTaskMessageGenerator
    extends TaskMessageGenerator<DeleteRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to delete.";
    }

    void appendMessageForSingleFailure( final StringBuilder builder, final DeleteRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" could not be deleted.", result.getFailed().get( 0 ).getName() ) );
    }

    void appendMessageForMultipleFailure( final StringBuilder builder, final DeleteRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to delete %s items. ", result.getFailureCount() ) );
    }

    void appendMessageForSingleSuccess( final StringBuilder builder, final DeleteRunnableTaskResult result )
    {
        final List<ContentPath> pending = result.getPending();
        final List<ContentPath> deleted = result.getSucceeded();
        if ( pending != null && pending.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is marked for deletion.", pending.get( 0 ).getName() ) );
        }
        else if ( deleted != null && deleted.size() == 1 )
        {
            ContentName name = ContentName.from( deleted.get( 0 ).getName() );
            if ( name.isUnnamed() )
            {
                builder.append( "Item is deleted" );
            }
            else
            {
                builder.append( String.format( "Item \"%s\" is deleted.", name ) );
            }
        }
    }

    void appendMessageForMultipleSuccess( final StringBuilder builder, final DeleteRunnableTaskResult result )
    {
        final List<ContentPath> pending = result.getPending();
        builder.append( String.format( "%s items are deleted", result.getSuccessCount() ) );
        if ( pending.size() > 0 )
        {
            final String isOrAre = pending.size() > 1 ? "are" : "is";
            builder.append( String.format( " ( %s %s marked for deletion )", getNameOrSize( pending ), isOrAre ) );
        }
        builder.append( "." );
    }

}
