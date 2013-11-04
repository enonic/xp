package com.enonic.wem.query.builder;

import com.enonic.wem.query.Constraint;
import com.enonic.wem.query.expr.OrderBy;
import com.enonic.wem.query.expr.Query;

public final class QueryBuilder
{
    public interface EndBuilder<T>
    {
        public T build();
    }

    public interface ConstraintBuilder
        extends EndBuilder<Constraint>
    {
    }

    public interface OrderByBuilder
        extends EndBuilder<OrderBy>
    {
        public OrderSpecBuilder ascending();

        public OrderSpecBuilder descending();
    }

    public interface OrderSpecBuilder
//        extends FieldBuilder<OrderByBuilder>, OrderFunctions
    {
    }

    public interface QueryExprBuilder
        extends EndBuilder<Query>
    {
        public QueryExprBuilder where( Constraint constraint );

        public QueryExprBuilder orderBy( OrderBy orderBy );
    }

    public static ConstraintBuilder constraint()
    {
        return null;
    }

    public static OrderByBuilder orderBy()
    {
        return null;
    }

    public static QueryExprBuilder query()
    {
        return null;
    }
}
