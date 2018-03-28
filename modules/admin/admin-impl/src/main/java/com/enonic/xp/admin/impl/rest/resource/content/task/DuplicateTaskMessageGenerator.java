package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class DuplicateTaskMessageGenerator
    extends TaskMessageGenerator<DuplicateRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to duplicate duplicated.";
    }

    void appendMessageForSingleFailure( final StringBuilder builder, final DuplicateRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" failed to be duplicated.", result.getFailed().get( 0 ).getName() ) );
    }

    void appendMessageForMultipleFailure( final StringBuilder builder, final DuplicateRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to duplicate %s items.", result.getFailureCount() ) );
    }

    void appendMessageForSingleSuccess( final StringBuilder builder, final DuplicateRunnableTaskResult result )
    {
        final List<ContentPath> alreadyDuplicated = result.getAlreadyDuplicated();
        final List<ContentPath> succeeded = result.getSucceeded();
        if ( alreadyDuplicated != null && alreadyDuplicated.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is already duplicated.", alreadyDuplicated.get( 0 ).getName() ) );
        }
        else if ( succeeded != null && succeeded.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" was duplicated.", succeeded.get( 0 ) ) );
        }
    }

    void appendMessageForMultipleSuccess( final StringBuilder builder, final DuplicateRunnableTaskResult result )
    {
        final List<ContentPath> alreadyDuplicated = result.getAlreadyDuplicated();
        builder.append( String.format( "Duplicated %s items", result.getSuccessCount() ) );
        if ( alreadyDuplicated.size() > 0 )
        {
            builder.append( String.format( " ( Already duplicated: %s )", getNameOrSize( alreadyDuplicated ) ) );
        }
        builder.append( "." );
    }

}
