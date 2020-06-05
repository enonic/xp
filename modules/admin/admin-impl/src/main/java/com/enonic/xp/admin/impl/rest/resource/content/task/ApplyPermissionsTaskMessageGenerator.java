package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.content.ContentPath;

class ApplyPermissionsTaskMessageGenerator
    extends TaskMessageGenerator<RunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to edit.";
    }

    @Override
    void appendMessageForSingleFailure( final StringBuilder builder, final RunnableTaskResult result )
    {
        final ContentPath failedPath = result.getFailed().get( 0 );
        builder.append( String.format( "Permissions for \"%s\" could not be applied.",
                                       !failedPath.isRoot() ? failedPath.getName() : failedPath.toString() ) );
    }

    @Override
    void appendMessageForMultipleFailure( final StringBuilder builder, final RunnableTaskResult result )
    {
        builder.append( String.format( "Failed to apply permissions for %s items. ", result.getFailureCount() ) );
    }

    @Override
    void appendMessageForSingleSuccess( final StringBuilder builder, final RunnableTaskResult result )
    {
        if ( result.getSucceeded() != null && result.getSucceeded().size() == 1 )
        {
            final ContentPath succeedPath = result.getSucceeded().get( 0 );
            builder.append( String.format( "Permissions for \"%s\" are applied.",
                                           !succeedPath.isRoot() ? succeedPath.getName() : succeedPath.toString() ) );
        }
    }

    @Override
    void appendMessageForMultipleSuccess( final StringBuilder builder, final RunnableTaskResult result )
    {
        builder.append( String.format( "Permissions for %s items are applied.", result.getSuccessCount() ) );
    }

}
