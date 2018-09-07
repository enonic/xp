package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class MoveTaskMessageGenerator
    extends TaskMessageGenerator<MoveRunnableTaskResult>
{
    @Override
    protected String getNoResultsMessage()
    {
        return "Nothing was moved.";
    }

    protected void appendMessageForSingleFailure( final StringBuilder builder, final MoveRunnableTaskResult result )
    {
        final List<ContentPath> existsFailed = result.getExistsFailed();
        final List<ContentPath> notExistsFailed = result.getNotExistsFailed();
        final List<ContentPath> accessFailed = result.getAccessFailed();
        final List<ContentPath> failed = result.getFailed();

        if ( existsFailed != null && existsFailed.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" already exists at \"%s\".", existsFailed.get( 0 ).getName(),
                                           result.getDestination().toString() ) );
        }
        else if ( notExistsFailed != null && notExistsFailed.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" was not found.", notExistsFailed.get( 0 ).getName() ) );
        }
        else if ( accessFailed != null && accessFailed.size() == 1 )
        {
            builder.append( String.format( "You don't have access to item \"%s\".", result.getDestination().getName() ) );
        }
        else if ( failed != null && failed.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" could not be moved.", failed.get( 0 ).getName() ) );
        }
    }

    protected void appendMessageForMultipleFailure( final StringBuilder builder, final MoveRunnableTaskResult result )
    {
        builder.append( "Failed to move " ).append( result.getFailureCount() ).append( " items" );
        if ( result.getExistsFailed().size() > 0 || result.getAccessFailed().size() > 0 || result.getNotExistsFailed().size() > 0 )
        {
            builder.append( " ( " );
            if ( result.getExistsFailed().size() > 0 )
            {
                builder.append( "Exist at destination: " ).append( getNameOrSize( result.getExistsFailed() ) );
            }

            if ( result.getNotExistsFailed().size() > 0 )
            {
                if ( result.getExistsFailed().size() > 0 )
                {
                    builder.append( ", " );
                }
                builder.append( "Not found: " ).append( getNameOrSize( result.getNotExistsFailed() ) );
            }

            if ( result.getAccessFailed().size() > 0 )
            {
                if ( result.getExistsFailed().size() > 0 || result.getNotExistsFailed().size() > 0 )
                {
                    builder.append( ", " );
                }
                builder.append( "Access denied: " ).append( getNameOrSize( result.getAccessFailed() ) );
            }
            builder.append( " )" );
        }
        builder.append( "." );
    }

    protected void appendMessageForSingleSuccess( final StringBuilder builder, final MoveRunnableTaskResult result )
    {
        final List<ContentPath> alreadyMoved = result.getAlreadyMoved();
        final List<ContentPath> moved = result.getSucceeded();
        if ( alreadyMoved != null && alreadyMoved.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is already moved.", alreadyMoved.get( 0 ).getName() ) );
        }
        else if ( moved != null && moved.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is moved.", moved.get( 0 ).getName() ) );
        }
    }

    protected void appendMessageForMultipleSuccess( final StringBuilder builder, final MoveRunnableTaskResult result )
    {
        builder.append( result.getSuccessCount() ).append( " items were moved" );
        if ( result.getAlreadyMoved().size() > 0 )
        {
            builder.append( " ( Already moved: " ).append( getNameOrSize( result.getAlreadyMoved() ) ).append( " )" );
        }
        builder.append( "." );
    }

}
