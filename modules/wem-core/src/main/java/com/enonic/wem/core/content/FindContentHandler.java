package com.enonic.wem.core.content;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityQuery;
import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.entity.EntityQueryResult;
import com.enonic.wem.core.index.entity.EntitySearchResultEntry;
import com.enonic.wem.core.index.entity.EntitySearchService;

public class FindContentHandler
    extends CommandHandler<FindContent>
{
    private EntitySearchService entitySearchService;

    @Override
    public void handle()
        throws Exception
    {
        final QueryExpr queryExpr = tempBuildFulltextFunction();

        final EntityQuery query = EntityQuery.newQuery().
            addFilter( Filter.newValueQueryFilter().
                fieldName( "_collection" ).
                add( new Value.String( "content" ) ).
                build() ).
            query( queryExpr ).
            build();

        final EntityQueryResult result = entitySearchService.find( query );

        ContentIndexQueryResult contentIndexQueryResult = translateToContentIndexQueryResult( result );

        command.setResult( contentIndexQueryResult );
    }

    private QueryExpr tempBuildFulltextFunction()
    {
        final List<ValueExpr> valueExprs = Lists.newArrayList();
        valueExprs.add( ValueExpr.string( "displayname" ) );
        valueExprs.add( ValueExpr.string( this.command.getContentIndexQuery().getFullTextSearchString() ) );
        valueExprs.add( ValueExpr.string( "OR" ) );

        final FunctionExpr functionExpr = new FunctionExpr( "fulltext", valueExprs );

        final DynamicConstraintExpr fulltextSearch = new DynamicConstraintExpr( functionExpr );

        return new QueryExpr( fulltextSearch, null );
    }

    private ContentIndexQueryResult translateToContentIndexQueryResult( final EntityQueryResult result )
    {
        final ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( new Long( result.getTotalHits() ).intValue() );

        final ImmutableSet<EntitySearchResultEntry> entries = result.getEntries();

        for ( final EntitySearchResultEntry entry : entries )
        {
            contentIndexQueryResult.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }
        return contentIndexQueryResult;
    }


    @Inject
    public void setEntitySearchService( final EntitySearchService entitySearchService )
    {
        this.entitySearchService = entitySearchService;
    }
}
