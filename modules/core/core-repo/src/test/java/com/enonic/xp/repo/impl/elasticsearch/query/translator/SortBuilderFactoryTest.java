package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;
import java.util.Set;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.SortQueryBuilderFactory;

public class SortBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void createFieldSort()
    {
        final Set<OrderExpr> orderExprs = Sets.newHashSet();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( new SearchQueryFieldNameResolver() ).create( orderExprs );

        Assert.assertEquals( 1, sortBuilders.size() );
        Assert.assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
    }

    @Test
    public void createMultipleFieldSort()
    {
        final Set<OrderExpr> orderExprs = Sets.newHashSet();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ) );
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "mySecondField" ), OrderExpr.Direction.DESC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( new SearchQueryFieldNameResolver() ).create( orderExprs );

        Assert.assertEquals( 2, sortBuilders.size() );
        Assert.assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
        Assert.assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
    }

    @Test
    public void createGeoDistance()
    {
        final Set<OrderExpr> orderExprs = Sets.newHashSet();
        orderExprs.add( new DynamicOrderExpr(
            new FunctionExpr( "geoDistance", Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.geoPoint( "-50,40" ) ) ),
            OrderExpr.Direction.ASC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( new SearchQueryFieldNameResolver() ).create( orderExprs );

        Assert.assertEquals( 1, sortBuilders.size() );
        Assert.assertTrue( sortBuilders.iterator().next() instanceof GeoDistanceSortBuilder );
    }
}
