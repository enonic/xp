package com.enonic.xp.portal.jslib.impl.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.FindContentByQueryParams;
import com.enonic.wem.api.content.FindContentByQueryResult;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.expr.ConstraintExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.parser.QueryParser;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.xp.portal.jslib.impl.mapper.ContentsResultMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class QueryContentHandler
    implements CommandHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.query";
    }

    @Override
    public Object execute( final CommandRequest req )
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
