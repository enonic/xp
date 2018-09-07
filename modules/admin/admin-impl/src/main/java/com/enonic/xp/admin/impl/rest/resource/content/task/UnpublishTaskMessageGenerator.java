package com.enonic.xp.admin.impl.rest.resource.content.task;

class UnpublishTaskMessageGenerator
    extends TaskMessageGenerator<UnpublishRunnableTaskResult>
{
    @Override
    protected String getNoResultsMessage()
    {
        return "Nothing to unpublish.";
    }

    protected void appendMessageForSingleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" could not be unpublished.", result.getFailed().get( 0 ).getName() ) );
    }

    protected void appendMessageForMultipleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to publish %s items. ", result.getFailureCount() ) );
    }

    protected void appendMessageForSingleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" is unpublished.", result.getSucceeded().get( 0 ).getName() ) );
    }

    protected void appendMessageForMultipleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "%s items are unpublished", result.getSuccessCount() ) );
    }

}
