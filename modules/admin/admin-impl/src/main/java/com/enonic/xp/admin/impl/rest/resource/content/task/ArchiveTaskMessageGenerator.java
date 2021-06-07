package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class ArchiveTaskMessageGenerator
    extends TaskMessageGenerator<ArchiveRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing was archived.";
    }

    @Override
    void appendMessageForSingleFailure( final StringBuilder builder, final ArchiveRunnableTaskResult result )
    {
        final List<ContentPath> failed = result.getFailed();

        if ( failed != null && failed.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" could not be archived.", failed.get( 0 ).getName() ) );
        }
    }

    @Override
    void appendMessageForMultipleFailure( final StringBuilder builder, final ArchiveRunnableTaskResult result )
    {
        builder.append( "Failed to archive " ).append( result.getFailureCount() ).append( " items." );
    }

    @Override
    void appendMessageForSingleSuccess( final StringBuilder builder, final ArchiveRunnableTaskResult result )
    {
        final List<ContentPath> archived = result.getSucceeded();
        if ( archived != null && archived.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is archived.", archived.get( 0 ).getName() ) );
        }
    }

    @Override
    void appendMessageForMultipleSuccess( final StringBuilder builder, final ArchiveRunnableTaskResult result )
    {
        builder.append( result.getSuccessCount() ).append( " items were archived." );
    }

}
