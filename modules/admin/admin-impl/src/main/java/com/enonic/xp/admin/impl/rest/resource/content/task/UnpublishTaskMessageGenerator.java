package com.enonic.xp.admin.impl.rest.resource.content.task;

class UnpublishTaskMessageGenerator
    extends TaskMessageGenerator<UnpublishRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to unpublish.";
    }

    @Override
    void appendMessageForSingleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" could not be unpublished.", result.getFailed().get( 0 ).getName() ) );
    }

    @Override
    void appendMessageForMultipleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to publish %s items. ", result.getFailureCount() ) );
    }

    @Override
    void appendMessageForSingleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" is unpublished.", result.getSucceeded().get( 0 ).getName() ) );
    }

    @Override
    void appendMessageForMultipleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "%s items are unpublished", result.getSuccessCount() ) );
    }

}
