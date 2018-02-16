package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.content.MoveContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class MoveRunnableTask
    extends AbstractRunnableTask
{
    private final MoveContentJson params;

    private MoveRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToMoveList = ContentIds.from( params.getContentIds() );
        progressReporter.info( "Moving content" );

        final MoveContentProgressListener listener = new MoveContentProgressListener( progressReporter );

        final long childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( contentToMoveList ).
            build().
            find().
            getTotalHits();
        final int contentIds = contentToMoveList.getSize();

        listener.setTotal( Math.toIntExact( childrenIds + contentIds ) );

        List<String> moved = Lists.newArrayList();
        List<ContentPath> alreadyMoved = Lists.newArrayList();
        List<ContentPath> existsFailed = Lists.newArrayList();
        List<ContentPath> notExistsFailed = Lists.newArrayList();
        List<ContentPath> accessFailed = Lists.newArrayList();
        List<ContentPath> failed = Lists.newArrayList();
        final ContentPath destination = params.getParentContentPath();

        for ( ContentId contentId : contentToMoveList )
        {
            final MoveContentParams moveContentParams = MoveContentParams.create().
                contentId( contentId ).
                parentContentPath( params.getParentContentPath() ).
                moveContentListener( listener ).
                build();
            try
            {
                final MoveContentsResult result = contentService.move( moveContentParams );
                moved.add( result.getContentName() );
            }
            catch ( ContentAlreadyMovedException e )
            {
                alreadyMoved.add( e.getPath() );
            }
            catch ( ContentAlreadyExistsException e )
            {
                existsFailed.add( e.getContentPath() );
            }
            catch ( ContentNotFoundException e )
            {
                notExistsFailed.add( e.getPath() );
            }
            catch ( ContentAccessException e )
            {
                accessFailed.add( e.getContentPath() );
            }
            catch ( MoveContentException e )
            {
                failed.add( e.getPath() );
            }
        }

        progressReporter.info( getMessage( moved, alreadyMoved, existsFailed, notExistsFailed, accessFailed, failed, destination ) );
    }

    private String getMessage( final List<String> moved, final List<ContentPath> alreadyMoved, final List<ContentPath> existsFailed,
                               final List<ContentPath> notExistsFailed, final List<ContentPath> accessFailed,
                               final List<ContentPath> failed, final ContentPath destination )
    {
        final int total =
            moved.size() + alreadyMoved.size() + existsFailed.size() + notExistsFailed.size() + accessFailed.size() + failed.size();
        if ( total == 0 )
        {
            return "Nothing was moved.";
        }

        final int totalMoved = moved.size() + alreadyMoved.size();
        final int totalFailed = total - totalMoved;
        final StringBuilder builder = new StringBuilder();

        if ( totalMoved == 1 )
        {
            builder.append( getMessageForSingleItem( moved, alreadyMoved, null, null, null, null, destination ) );
        }
        else if ( totalMoved > 1 )
        {
            builder.append( totalMoved ).append( " items were moved" );
            if ( alreadyMoved.size() > 0 )
            {
                builder.append( " ( Already moved: " ).append( getNameOrSize( alreadyMoved ) ).append( " )" );
            }
            builder.append( "." );
        }

        if ( totalFailed > 0 )
        {
            builder.append( " " );
            if ( totalFailed == 1 )
            {
                builder.append( getMessageForSingleItem( null, null, existsFailed, notExistsFailed, accessFailed, failed, destination ) );
            }
            else
            {
                builder.append( "Failed to move " ).append( totalFailed ).append( " items" );
                if ( existsFailed.size() > 0 || accessFailed.size() > 0 || notExistsFailed.size() > 0 )
                {
                    builder.append( " ( " );
                    if ( existsFailed.size() > 0 )
                    {
                        builder.append( "Exist at destination: " ).append( getNameOrSize( existsFailed ) );
                    }

                    if ( notExistsFailed.size() > 0 )
                    {
                        if ( existsFailed.size() > 0 )
                        {
                            builder.append( ", " );
                        }
                        builder.append( "Not found: " ).append( getNameOrSize( notExistsFailed ) );
                    }

                    if ( accessFailed.size() > 0 )
                    {
                        if ( existsFailed.size() > 0 || notExistsFailed.size() > 0 )
                        {
                            builder.append( ", " );
                        }
                        builder.append( "Access denied: " ).append( getNameOrSize( accessFailed ) );
                    }
                    builder.append( " )" );
                }
                builder.append( "." );
            }
        }

        return builder.toString();
    }

    private String getMessageForSingleItem( final List<String> moved, final List<ContentPath> alreadyMoved,
                                            final List<ContentPath> existsFailed, final List<ContentPath> notExistsFailed,
                                            final List<ContentPath> accessFailed, final List<ContentPath> failed,
                                            final ContentPath destination )
    {
        if ( alreadyMoved != null && alreadyMoved.size() == 1 )
        {
            return String.format( "\"%s\" is already moved.", alreadyMoved.get( 0 ).getName() );
        }
        if ( moved != null && moved.size() == 1 )
        {
            return String.format( "\"%s\" was moved.", moved.get( 0 ) );
        }
        else if ( existsFailed != null && existsFailed.size() == 1 )
        {
            return String.format( "\"%s\" already exists at \"%s\".", existsFailed.get( 0 ).getName(), destination );
        }
        else if ( notExistsFailed != null && notExistsFailed.size() == 1 )
        {
            return String.format( "\"%s\" was not found.", notExistsFailed.get( 0 ).getName() );
        }
        else if ( accessFailed != null && accessFailed.size() == 1 )
        {
            return String.format( "You don't have to access \"%s\".", destination );
        }
        else if ( failed != null && failed.size() == 1 )
        {
            return String.format( "\"%s\" could not be moved.", failed.get( 0 ).getName() );
        }
        else
        {
            return "Nothing was moved.";
        }
    }

    private String getNameOrSize( final List<ContentPath> items )
    {
        return items.size() != 1 ? String.valueOf( items.size() ) : "\"" + items.get( 0 ).getName() + "\"";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder
    {
        private MoveContentJson params;

        public Builder params( MoveContentJson params )
        {
            this.params = params;
            return this;
        }

        @Override
        public Builder description( String description )
        {
            super.description( description );
            return this;
        }

        @Override
        public Builder taskService( TaskService taskService )
        {
            super.taskService( taskService );
            return this;
        }

        @Override
        public Builder contentService( ContentService contentService )
        {
            super.contentService( contentService );
            return this;
        }

        public MoveRunnableTask build()
        {
            return new MoveRunnableTask( this );
        }
    }
}
