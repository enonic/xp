package com.enonic.xp.impl.server.rest.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.impl.server.rest.model.ReprocessContentRequestJson;
import com.enonic.xp.impl.server.rest.model.ReprocessContentResultJson;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH;

public class ReprocessRunnableTask
    extends AbstractRunnableTask
{
    private final ReprocessContentRequestJson params;

    private int total;

    private int current;

    private final static Logger LOG = LoggerFactory.getLogger( ReprocessRunnableTask.class );

    private ReprocessRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final List<ContentPath> updated = new ArrayList<>();
        final List<String> errors = new ArrayList<>();

        final Content content = this.contentService.getByPath( params.getSourceBranchPath().getContentPath() );
        try
        {
            if ( !params.isSkipChildren() )
            {
                String nodePath = CONTENT_ROOT_PATH.asAbsolute().toString();
                if ( !ContentPath.ROOT.equals( params.getSourceBranchPath().getContentPath() ) )
                {
                    nodePath += content.getPath().
                        asAbsolute().
                        toString();
                }
                ConstraintExpr pathExpr = CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( nodePath + "/*" ) );
                ContentQuery countChildren = ContentQuery.create().queryExpr( QueryExpr.from( pathExpr ) ).size( 0 ).build();

                total = (int) contentService.find( countChildren ).getTotalHits() + 1;
            }

            reprocessContent( content, params.isSkipChildren(), updated, errors, progressReporter );
        }
        catch ( Exception e )
        {
            errors.add(
                String.format( "Content '%s' - %s: %s", content.getPath().toString(), e.getClass().getCanonicalName(), e.getMessage() ) );
            LOG.warn( "Error reprocessing content [" + content.getPath() + "]", e );
        }

        progressReporter.info( new ReprocessContentResultJson( ContentPaths.from( updated ), errors ).toString() );
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
            final FindContentByParentParams findParams = FindContentByParentParams.create().parentId( content.getId() ).
                from( from ).size( 5 ).build();
            final FindContentByParentResult results = this.contentService.findByParent( findParams );

            for ( Content child : results.getContents() )
            {
                try
                {
                    reprocessContent( child, false, updated, errors, progressReporter );
                }
                catch ( Exception e )
                {
                    errors.add( String.format( "Content '%s' - %s: %s", child.getPath().toString(), e.getClass().getCanonicalName(),
                                               e.getMessage() ) );
                    LOG.warn( "Error reprocessing content [" + child.getPath() + "]", e );
                }
            }
            resultCount = Math.toIntExact( results.getHits() );
            from = from + resultCount;
        }
        while ( resultCount > 0 );
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private ReprocessContentRequestJson params;

        public Builder params( ReprocessContentRequestJson params )
        {
            this.params = params;
            return this;
        }

        public ReprocessRunnableTask build()
        {
            return new ReprocessRunnableTask( this );
        }
    }
}
