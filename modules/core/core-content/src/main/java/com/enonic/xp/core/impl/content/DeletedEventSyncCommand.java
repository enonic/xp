package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.IdFilter;

final class DeletedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    DeletedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected void doSync()
    {
        doSync( params.getContents() );
    }

    private void doSync( final List<ContentToSync> contents )
    {
        final Set<ContentId> fullIds =
            contents.stream().map( contentToSync -> contentToSync.getTargetContent().getId() ).collect( Collectors.toSet() );

        final Set<ContentToSync> roots = getRoots( contents );

        roots.forEach( content -> content.getTargetContext().runWith( () -> {

            if ( isToSyncDelete( content.getTargetContent() ) )
            {
                if ( needToDelete( content, fullIds ) )
                {
                    final DeleteContentParams deleteParams =
                        DeleteContentParams.create().contentPath( content.getTargetContent().getPath() ).build();

                    contentService.delete( deleteParams );
                }
            }
        } ) );
    }

    private Set<ContentToSync> getRoots( final List<ContentToSync> contents )
    {
        final Set<ContentPath> paths =
            contents.stream().map( contentToSync -> contentToSync.getTargetContent().getPath() ).collect( Collectors.toSet() );

        final Set<ContentPath> rootPaths =
            paths.stream().filter( path -> !paths.contains( path.getParentPath() ) ).collect( Collectors.toSet() );

        return contents.stream()
            .filter( content -> rootPaths.contains( content.getTargetContent().getPath() ) )
            .collect( Collectors.toSet() );
    }

    private boolean isToSyncDelete( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.CONTENT );
    }

    private boolean needToDelete( final ContentToSync content, final Set<ContentId> idsToRemove )
    {
        return content.getSourceContent() == null && removedInSource( content ) && hasNoInboundDependencies( content, idsToRemove ) &&
            hasNoChildren( content, idsToRemove );
    }

    private boolean removedInSource( final ContentToSync contentToSync )
    {
        return contentToSync.getSourceContext().callWith( () -> !contentService.contentExists( contentToSync.getTargetContent().getId() ) );
    }

    private boolean hasNoChildren( final ContentToSync contentToSync, final Set<ContentId> idsToRemove )
    {
        return !contentToSync.getTargetContent().hasChildren() ||
            getAllChildren( contentToSync.getTargetContent().getId() ).stream().allMatch( idsToRemove::contains );
    }

    private boolean hasNoInboundDependencies( final ContentToSync contentToSync, final Set<ContentId> idsToRemove )
    {
        return getInboundDependencies( contentToSync.getTargetContent().getId() ).stream().allMatch( idsToRemove::contains );
    }

    private ContentIds getInboundDependencies( final ContentId contentId )
    {
        return this.contentService.find( ContentQuery.create()
                                             .queryFilter( BooleanFilter.create()
                                                               .must( IdFilter.create()
                                                                          .fieldName( ContentIndexPath.REFERENCES.getPath() )
                                                                          .value( contentId.toString() )
                                                                          .build() )
                                                               .mustNot( IdFilter.create()
                                                                             .fieldName( ContentIndexPath.ID.getPath() )
                                                                             .value( contentId.toString() )
                                                                             .build() )
                                                               .build() )
                                             .size( -1 )
                                             .build() ).getContentIds();
    }

    private ContentIds getAllChildren( final ContentId contentId )
    {
        return this.contentService.findIdsByParent( FindContentByParentParams.create().parentId( contentId ).recursive( true ).build() )
            .getContentIds();
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( params.getContents().stream().allMatch( content -> content.getTargetContent() != null ),
                                         "targetContent must be set." );
        }

        @Override
        public DeletedEventSyncCommand build()
        {
            validate();
            return new DeletedEventSyncCommand( this );
        }
    }
}
