package com.enonic.xp.portal.impl.jslib.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.query.ContentQuery;
import com.enonic.xp.portal.impl.jslib.base.BaseContextHandler;
import com.enonic.xp.portal.impl.jslib.mapper.ContentsResultMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeNames;

@Component(immediate = true, service = CommandHandler.class)
public final class QueryContentHandler
    extends BaseContextHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.query";
    }

    @Override
    protected Object doExecute( final CommandRequest req )
    {
        final int start = req.param( "start" ).value( Integer.class, 0 );
        final int count = req.param( "count" ).value( Integer.class, GetChildContentHandler.DEFAULT_COUNT );
        final String query = req.param( "query" ).value( String.class, "" ).trim();
        final String sort = req.param( "sort" ).value( String.class, "" ).trim();
        final Map<String, Object> aggregationsMap = req.param( "aggregations" ).map();
        final ContentTypeNames contentTypeNames = getContentTypeNames( req.getParams().get( "contentTypes" ) );

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( query );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( aggregationsMap );

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

    private ContentTypeNames getContentTypeNames( final Object contentTypeParam )
    {
        if ( contentTypeParam instanceof String )
        {
            return ContentTypeNames.from( (String) contentTypeParam );
        }
        else if ( contentTypeParam instanceof List )
        {
            return ContentTypeNames.from( (List<String>) contentTypeParam );
        }
        return ContentTypeNames.empty();
    }

    private Object convert( final FindContentByQueryResult findQueryResult )
    {
        return new ContentsResultMapper( findQueryResult.getContents(), findQueryResult.getTotalHits(), findQueryResult.getAggregations() );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
