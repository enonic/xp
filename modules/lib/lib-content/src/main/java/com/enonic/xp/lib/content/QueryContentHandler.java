package com.enonic.xp.lib.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.lib.common.JsonToFilterMapper;
import com.enonic.xp.lib.common.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.JsonHelper;

@SuppressWarnings("unused")
public final class QueryContentHandler
    extends BaseContextHandler
{
    private Integer start;

    private Integer count;

    private ScriptValue query;

    private String sort;

    private Map<String, Object> aggregations;

    private Map<String, Object> highlight;

    private List<String> contentTypes;

    private List<Map<String, Object>> filters;

    @Override
    protected Object doExecute()
    {
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, GetChildContentHandler.DEFAULT_COUNT );
        final String sort = valueOrDefault( this.sort, "" ).trim();
        final ContentTypeNames contentTypeNames = getContentTypeNames();

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );

        final QueryExpr queryExpr = QueryExpr.from( buildConstraintExpr(), orderExpressions );

        final Filters filters = JsonToFilterMapper.create( this.filters );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( this.aggregations );

        final HighlightQuery highlight = new QueryHighlightParams().getHighlightQuery( this.highlight );

        final ContentQuery.Builder queryBuilder = ContentQuery.create()
            .from( start )
            .size( count )
            .aggregationQueries( aggregations )
            .highlight( highlight )
            .addContentTypeNames( contentTypeNames )
            .queryExpr( queryExpr );

        for ( final Filter filter : filters )
        {
            queryBuilder.queryFilter( filter );
        }

        final FindContentIdsByQueryResult queryResult = contentService.find( queryBuilder.build() );

        return convert( queryResult );
    }

    private ConstraintExpr buildConstraintExpr()
    {
        if ( query == null )
        {
            return QueryParser.parseCostraintExpression( "" );
        }
        else if ( query.isValue() )
        {
            return QueryParser.parseCostraintExpression( query.getValue( String.class ) );
        }
        else if ( query.isObject() )
        {
            return DslExpr.from( JsonToPropertyTreeTranslator.translate( JsonHelper.from( query.getMap() ) ) );
        }
        throw new IllegalArgumentException( "query must be a String or JSON object" );
    }

    private ContentTypeNames getContentTypeNames()
    {
        if ( this.contentTypes == null )
        {
            return ContentTypeNames.empty();
        }
        return ContentTypeNames.from( this.contentTypes );
    }

    private ContentsResultMapper convert( final FindContentIdsByQueryResult findQueryResult )
    {
        final ContentIds contentIds = findQueryResult.getContentIds();
        final Contents contents;
        if ( contentIds.isEmpty() )
        {
            contents = Contents.empty();
        }
        else
        {
            contents = this.contentService.getByIds( new GetContentByIdsParams( contentIds ) );
        }

        return new ContentsResultMapper( contents, findQueryResult.getTotalHits(), findQueryResult.getAggregations(),
                                         findQueryResult.getHighlight(), findQueryResult.getSort(), findQueryResult.getScore() );
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setQuery( final ScriptValue query )
    {
        this.query = query;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public void setAggregations( final ScriptValue value )
    {
        this.aggregations = value != null ? value.getMap() : null;
    }

    public void setContentTypes( final ScriptValue value )
    {
        this.contentTypes = value != null ? value.getArray( String.class ) : null;
    }

    public void setFilters( final ScriptValue filters )
    {
        this.filters = doSetFilters( filters );
    }

    public void setHighlight( final ScriptValue value )
    {
        this.highlight = value != null ? value.getMap() : null;
    }

    private List<Map<String, Object>> doSetFilters( final ScriptValue filters )
    {
        List<Map<String, Object>> filterList = new ArrayList<>();

        if ( filters == null )
        {
            return filterList;
        }

        if ( filters.isObject() )
        {
            filterList.add( filters.getMap() );
        }
        else if ( filters.isArray() )
        {
            filters.getArray().forEach( sv -> {

                if ( !sv.isObject() )
                {
                    throw new IllegalArgumentException( "Array elements not of type objects" );
                }

                filterList.add( sv.getMap() );
            } );
        }

        return filterList;
    }

}
