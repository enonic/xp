package com.enonic.xp.admin.impl.rest.resource.content.task;

class ApplyPermissionsTaskMessageGenerator
    extends TaskMessageGenerator<RunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to edit.";
    }

    void appendMessageForSingleFailure( final StringBuilder builder, final RunnableTaskResult result )
    {
        builder.append( String.format( "Permissions \"%s\" could not be applied.", result.getFailed().get( 0 ).getName() ) );
    }

    void appendMessageForMultipleFailure( final StringBuilder builder, final RunnableTaskResult result )
    {
        builder.append( String.format( "Failed to apply permissions for %s items. ", result.getFailureCount() ) );
    }

    void appendMessageForSingleSuccess( final StringBuilder builder, final RunnableTaskResult result )
    {
        if ( result.getSucceeded() != null && result.getSucceeded().size() == 1 )
        {
            builder.append( String.format( "Permissions for \"%s\" are applied.", result.getSucceeded().get( 0 ).getName() ) );
        }
    }

    void appendMessageForMultipleSuccess( final StringBuilder builder, final RunnableTaskResult result )
    {
        builder.append( String.format( "Permissions for %s items are applied.", result.getSuccessCount() ) );
    }

}
