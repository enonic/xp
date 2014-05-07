package com.enonic.wem.core.index.query

import com.enonic.wem.api.query.expr.*
import com.enonic.wem.core.index.query.builder.SortBuilderFactory
import com.google.common.collect.Sets
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.GeoDistanceSortBuilder
import spock.lang.Specification

class SortBuilderFactoryTest
        extends Specification
{

    def "create field sort"()
    {
        given:
        SortBuilderFactory factory = new SortBuilderFactory()
        Set<OrderExpr> orderExprs = Sets.newHashSet()
        orderExprs.add( new FieldOrderExpr( new FieldExpr( "myField" ), OrderExpr.Direction.ASC ) )

        when:
        def sortBuilders = factory.create( orderExprs )

        then:
        sortBuilders.size() == 1
        sortBuilders.iterator().next() instanceof FieldSortBuilder
    }

    def "create multiple field sort"()
    {
        given:
        SortBuilderFactory factory = new SortBuilderFactory();
        Set<OrderExpr> orderExprs = Sets.newHashSet();
        orderExprs.add( new FieldOrderExpr( new FieldExpr( "myField" ), OrderExpr.Direction.ASC ) )
        orderExprs.add( new FieldOrderExpr( new FieldExpr( "mySecondField" ), OrderExpr.Direction.DESC ) )

        when:
        def sortBuilders = factory.create( orderExprs )

        then:
        sortBuilders.size() == 2
        sortBuilders.iterator().next() instanceof FieldSortBuilder
        sortBuilders.iterator().next() instanceof FieldSortBuilder
    }

    def "create geo distance"()
    {
        given:
        SortBuilderFactory factory = new SortBuilderFactory()
        Set<OrderExpr> orderExpressions = Sets.newHashSet()

        orderExpressions.add(
                new DynamicOrderExpr( new FunctionExpr( "geoDistance", [ValueExpr.string( "myField" ), ValueExpr.geoPoint( "-50,40" )] ),
                                      OrderExpr.Direction.ASC ) )

        when:
        def sortBuilders = factory.create( orderExpressions )

        then:
        sortBuilders.size() == 1
        sortBuilders.iterator().next() instanceof GeoDistanceSortBuilder
    }

}
