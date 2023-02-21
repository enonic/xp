package com.enonic.xp.impl.server.rest.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.impl.server.rest.model.ReprocessContentResultJson;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;


public class ReprocessRunnableTask
    implements RunnableTask
{
    private static final Logger LOG = LoggerFactory.getLogger( ReprocessRunnableTask.class );

    private final Branch branch;

    private final ContentPath contentPath;

    private int total;

    private int current;

    private final boolean skipChildren;

    private final ContentService contentService;

    private ReprocessRunnableTask( final Builder builder )
    {
        this.branch = builder.branch;
        this.contentPath = builder.contentPath;
        this.skipChildren = builder.skipChildren;

        this.contentService = builder.contentService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        runWithContext( () -> {
            final List<ContentPath> updated = new ArrayList<>();
            final List<String> errors = new ArrayList<>();

            final Content content = this.contentService.getByPath( contentPath );
            try
            {
                if ( !skipChildren )
                {
                    String nodePath = ContentConstants.CONTENT_ROOT_PATH.toString();
                    if ( !ContentPath.ROOT.equals( contentPath ) )
                    {
                        nodePath += content.getPath().asAbsolute().toString();
                    }
                    final ConstraintExpr pathExpr = CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( nodePath + "/*" ) );
                    final ContentQuery countChildren = ContentQuery.create().queryExpr( QueryExpr.from( pathExpr ) ).size( 0 ).build();

                    total = (int) contentService.find( countChildren ).getTotalHits() + 1;
                }

                reprocessContent( content, skipChildren, updated, errors, progressReporter );
            }
            catch ( Exception e )
            {
                errors.add( String.format( "Content '%s' - %s: %s", content.getPath(), e.getClass().getCanonicalName(), e.getMessage() ) );
                LOG.warn( "Error reprocessing content [" + content.getPath() + "]", e );
            }

            progressReporter.info( new ReprocessContentResultJson( ContentPaths.from( updated ), errors ).toString() );
        } );
    }

    private void reprocessContent( final Content content, final boolean skipChildren, final List<ContentPath> updated,
                                   final List<String> errors, final ProgressReporter progressReporter )
    {
        final Content reprocessedContent = this.contentService.reprocess( content.getId() );
        if ( !reprocessedContent.equals( content ) )
        {
            updated.add( content.getPath() );
        }

        progressReporter.progress( ++current, total );
        if ( skipChildren )
        {
            return;
        }

        int from = 0;
        int resultCount;
        do
        {
            final FindContentByParentParams findParams =
                FindContentByParentParams.create().parentId( content.getId() ).from( from ).size( 5 ).build();
            final FindContentByParentResult results = this.contentService.findByParent( findParams );

            for ( Content child : results.getContents() )
            {
                try
                {
                    reprocessContent( child, false, updated, errors, progressReporter );
                }
                catch ( Exception e )
                {
                    errors.add(
                        String.format( "Content '%s' - %s: %s", child.getPath(), e.getClass().getCanonicalName(), e.getMessage() ) );
                    LOG.warn( "Error reprocessing content [" + child.getPath() + "]", e );
                }
            }
            resultCount = Math.toIntExact( results.getHits() );
            from = from + resultCount;
        }
        while ( resultCount > 0 );
    }


    private void runWithContext( Runnable runnable )
    {
        createContext().runWith( runnable );
    }

    private Context createContext()
    {
        return ContextBuilder.create()
            .branch( Branch.from( branch.getValue() ) )
            .repositoryId( ContentConstants.CONTENT_REPO_ID )
            .authInfo( ContextAccessor.current().getAuthInfo() )
            .build();
    }

    public static class Builder
    {
        private Branch branch;

        private ContentPath contentPath;

        private boolean skipChildren;

        private ContentService contentService;

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder skipChildren( final boolean skipChildren )
        {
            this.skipChildren = skipChildren;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public ReprocessRunnableTask build()
        {
            return new ReprocessRunnableTask( this );
        }
    }
}
