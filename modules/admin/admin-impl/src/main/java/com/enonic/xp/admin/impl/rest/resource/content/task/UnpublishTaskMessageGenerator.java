package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class UnpublishTaskMessageGenerator
    extends TaskMessageGenerator<UnpublishRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to unpublish.";
    }

    void appendMessageForSingleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" could not be unpublished.", result.getFailed().get( 0 ).getName() ) );
    }

    void appendMessageForMultipleFailure( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to publish %s items. ", result.getFailureCount() ) );
    }

    void appendMessageForSingleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        final List<ContentPath> deleted = result.getDeleted();
        final List<ContentPath> unpublished = result.getSucceeded();
        if ( deleted != null && deleted.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is deleted.", deleted.get( 0 ).getName() ) );
        }
        else if ( unpublished != null && unpublished.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is unpublished.", unpublished.get( 0 ).getName() ) );
        }
    }

    void appendMessageForMultipleSuccess( final StringBuilder builder, final UnpublishRunnableTaskResult result )
    {
        final List<ContentPath> deleted = result.getDeleted();
        builder.append( String.format( "%s items are unpublished", result.getSuccessCount() ) );
        if ( deleted.size() > 0 )
        {
            builder.append( String.format( " ( %s deleted )", getNameOrSize( deleted ) ) );
        }
        builder.append( "." );
    }

}
