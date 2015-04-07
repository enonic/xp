package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.query.ContentQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;


public class ContentChildrenCounter
{
    private ContentService contentService;

    public ContentChildrenCounter( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public long countContentsAndTheirChildren( ContentPaths contentsPaths )
    {
        long total = contentsPaths.getSize() + ( contentsPaths.isEmpty() ? 0 : countChildren( contentsPaths ) );

        return total;
    }

    public long countChildren( ContentPaths contentsPaths )
    {
        FindContentByQueryResult result = this.contentService.find( FindContentByQueryParams.create().
            contentQuery( ContentQuery.newContentQuery().size( 0 ).queryExpr( constructQueryExpr( contentsPaths ) ).build() ).
            build() );

        return result.getTotalHits();
    }

    private QueryExpr constructQueryExpr( ContentPaths contentsPaths )
    {
        ConstraintExpr expr = CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( "/content" + contentsPaths.first() + "/*" ) );

        for ( ContentPath contentPath : contentsPaths.getSet() )
        {
            if ( !contentPath.equals( contentsPaths.first() ) )
            {
                ConstraintExpr likeExpr =
                    CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( "/content" + contentPath + "/*" ) );
                expr = LogicalExpr.or( expr, likeExpr );
            }
        }

        return QueryExpr.from( expr );
    }

}
