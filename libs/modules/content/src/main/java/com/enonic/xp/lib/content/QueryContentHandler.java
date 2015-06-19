package com.enonic.xp.lib.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeNames;

public final class QueryContentHandler
    extends BaseContextHandler
{
    private final ContentService contentService;

    private Integer start;

    private Integer count;

    private String query;

    private String sort;

    private Map<String, Object> aggregations;

    private String[] contentTypes;

    public QueryContentHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    protected Object doExecute()
    {
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, GetChildContentHandler.DEFAULT_COUNT );
        final String query = valueOrDefault( this.query, "" ).trim();
        final String sort = valueOrDefault( this.sort, "" ).trim();
        final ContentTypeNames contentTypeNames = getContentTypeNames();

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( query );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( this.aggregations );

        final ContentQuery contentQuery = ContentQuery.newContentQuery().
            from( start ).
            size( count ).
            aggregationQueries( aggregations ).
            addContentTypeNames( contentTypeNames ).
            queryExpr( queryExpr ).
            build();

        final FindContentByQueryParams queryParams = FindContentByQueryParams.create().
            contentQuery( contentQuery ).build();

        final FindContentByQueryResult queryResult = contentService.find( queryParams );

        return convert( queryResult );
    }

    private ContentTypeNames getContentTypeNames()
    {
        if ( this.contentTypes == null )
        {
            return ContentTypeNames.empty();
        }
        return ContentTypeNames.from( this.contentTypes );
    }

    private ContentsResultMapper convert( final FindContentByQueryResult findQueryResult )
    {
        return new ContentsResultMapper( findQueryResult.getContents(), findQueryResult.getTotalHits(), findQueryResult.getAggregations() );
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setQuery( final String query )
    {
        this.query = query;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public void setAggregations( final Map<String, Object> aggregations )
    {
        this.aggregations = aggregations;
    }

    public void setContentTypes( final String[] contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    public void setContentTypes( final List<String> contentTypes )
    {
        this.contentTypes = contentTypes == null ? null : contentTypes.stream().toArray( String[]::new );
    }

    public void setContentType( final String contentType )
    {
        this.contentTypes = new String[]{contentType};
    }
}
