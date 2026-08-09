package com.enonic.xp.lib.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.lib.common.JsonToFilterMapper;
import com.enonic.xp.lib.content.mapper.ContentHitsResultMapper;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public final class QueryContentHandler
    extends BaseContextHandler
{
    private Integer start;

    private Integer count;

    private ScriptValue query;

    private ScriptValue sort;

    private Map<String, Object> aggregations;

    private Map<String, Object> highlight;

    private List<String> contentTypes;

    private List<Map<String, Object>> filters;

    private String parent;

    private Boolean recursive;

    private ScriptValue returns;

    @Override
    protected Object doExecute()
    {
        final ContentTypeNames contentTypeNames = getContentTypeNames();

        final QueryExpr queryExpr = QueryExpr.from( buildConstraintExpr(), buildOrderExpr() );

        final Filters filters = JsonToFilterMapper.create( this.filters );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( this.aggregations );

        final HighlightQuery highlight = new QueryHighlightParams().getHighlightQuery( this.highlight );

        final ContentQuery.Builder queryBuilder = ContentQuery.create()
            .aggregationQueries( aggregations )
            .highlight( highlight )
            .addContentTypeNames( contentTypeNames )
            .queryExpr( queryExpr );

        applyParent( queryBuilder );

        if ( start != null )
        {
            queryBuilder.from( start );
        }
        if ( count != null )
        {
            queryBuilder.size( count );
        }

        for ( final Filter filter : filters )
        {
            queryBuilder.queryFilter( filter );
        }

        final ReturnShape shape = resolveReturnShape( queryBuilder );

        final FindContentIdsByQueryResult queryResult = contentService.find( queryBuilder.build() );

        switch ( shape )
        {
            case IDS:
                return new ContentHitsResultMapper( queryResult, ContentHitsResultMapper.Shape.IDS );
            case FIELDS:
                return new ContentHitsResultMapper( queryResult, ContentHitsResultMapper.Shape.FIELDS );
            default:
                return convert( queryResult );
        }
    }

    private enum ReturnShape
    {
        CONTENTS, IDS, FIELDS
    }

    /**
     * The {@code returns} parameter picks the shape of the hits: full contents (the default), bare ids, ids with paths, or ids with the
     * values of the index fields named by an array. Everything but the default skips reading the contents entirely.
     */
    private ReturnShape resolveReturnShape( final ContentQuery.Builder queryBuilder )
    {
        if ( returns == null )
        {
            return ReturnShape.CONTENTS;
        }
        else if ( returns.isValue() )
        {
            final String value = returns.getValue( String.class );
            switch ( value )
            {
                case "contents":
                    return ReturnShape.CONTENTS;
                case "ids":
                    return ReturnShape.IDS;
                default:
                    throw new IllegalArgumentException( "returns must be 'contents', 'ids' or an array of index field names" );
            }
        }
        else if ( returns.isArray() )
        {
            queryBuilder.returnFields( returns.getArray( String.class ).stream().map( IndexPath::from ).toArray( IndexPath[]::new ) );
            return ReturnShape.FIELDS;
        }

        throw new IllegalArgumentException( "returns must be 'contents', 'ids' or an array of index field names" );
    }

    private List<OrderExpr> buildOrderExpr()
    {
        if ( sort == null )
        {
            return List.of();
        }
        else if ( sort.isValue() )
        {
            return QueryParser.parseOrderExpressions( sort.getValue( String.class ) );
        }
        else if ( sort.isObject() )
        {

            return List.of( DslOrderExpr.from( PropertyTree.fromMap( sort.getMap() ) ) );

        }
        else if ( sort.isArray() )
        {
            return sort.getArray()
                .stream()
                .map( expr -> DslOrderExpr.from( PropertyTree.fromMap( expr.getMap() ) ) )
                .collect( Collectors.toList() );
        }

        throw new IllegalArgumentException( "sort must be a String, JSON object or array of JSON objects" );
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
            return DslExpr.from( PropertyTree.fromMap( query.getMap() ) );
        }

        throw new IllegalArgumentException( "query must be a String or JSON object" );
    }

    /**
     * The {@code parent} parameter takes the same key as {@code getChildren}: a value that starts with {@code /} is a content path,
     * anything else is a content id.
     */
    private void applyParent( final ContentQuery.Builder queryBuilder )
    {
        if ( parent != null )
        {
            if ( parent.startsWith( "/" ) )
            {
                queryBuilder.parentPath( ContentPath.from( parent ) );
            }
            else
            {
                queryBuilder.parentId( ContentId.from( parent ) );
            }
            queryBuilder.recursive( Boolean.TRUE.equals( recursive ) );
        }
    }

    private ContentTypeNames getContentTypeNames()
    {
        if ( this.contentTypes == null )
        {
            return ContentTypeNames.empty();
        }
        return this.contentTypes.stream().map( ContentTypeName::from ).collect( ContentTypeNames.collector() );
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
            contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( contentIds ).build() );
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

    public void setParent( final String parent )
    {
        this.parent = parent;
    }

    public void setRecursive( final Boolean recursive )
    {
        this.recursive = recursive;
    }

    public void setReturns( final ScriptValue returns )
    {
        this.returns = returns;
    }

    public void setQuery( final ScriptValue query )
    {
        this.query = query;
    }

    public void setSort( final ScriptValue sort )
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
