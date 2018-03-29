package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class PublishTaskMessageGenerator
    extends TaskMessageGenerator<PublishRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing to publish.";
    }

    void appendMessageForSingleFailure( final StringBuilder builder, final PublishRunnableTaskResult result )
    {
        builder.append( String.format( "Item \"%s\" could not be published.", result.getFailed().get( 0 ).toString() ) );
    }

    void appendMessageForMultipleFailure( final StringBuilder builder, final PublishRunnableTaskResult result )
    {
        builder.append( String.format( "Failed to publish %s items. ", result.getFailureCount() ) );
    }

    void appendMessageForSingleSuccess( final StringBuilder builder, final PublishRunnableTaskResult result )
    {
        final List<ContentPath> deleted = result.getDeleted();
        final List<ContentPath> published = result.getSucceeded();
        if ( deleted != null && deleted.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" was deleted.", deleted.get( 0 ).toString() ) );
        }
        else if ( published != null && published.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" was published.", published.get( 0 ) ) );
        }
    }

    void appendMessageForMultipleSuccess( final StringBuilder builder, final PublishRunnableTaskResult result )
    {
        final List<ContentPath> deleted = result.getDeleted();
        builder.append( String.format( "Published %s items", result.getSuccessCount() ) );
        if ( deleted.size() > 0 )
        {
            builder.append( String.format( " ( Deleted: %s )", getNameOrSize( deleted ) ) );
        }
        builder.append( "." );
    }

}
