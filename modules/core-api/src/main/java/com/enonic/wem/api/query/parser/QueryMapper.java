package com.enonic.wem.api.query.parser;

import java.util.List;

import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Unary;

import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.ConstraintExpr;
import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.DynamicOrderExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.LogicalExpr;
import com.enonic.wem.api.query.expr.NotExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;

final class QueryMapper
{
    public static Map<String, ValueExpr> stringValueExpr()
    {
        return ValueExpr::string;
    }

    public static Map<String, ValueExpr> numberValueExpr()
    {
        return value -> {
            final Double number = Double.parseDouble( value );
            return ValueExpr.number( number );
        };
    }

    public static Map2<ConstraintExpr, List<OrderExpr>, QueryExpr> queryExpr()
    {
        return QueryExpr::new;
    }

    public static Map<String, FieldExpr> fieldExpr()
    {
        return FieldExpr::from;
    }

    public static Map<String, Tokens.Fragment> fragment( final String tag )
    {
        return from -> Tokens.fragment( from, tag );
    }

    public static Unary<ConstraintExpr> notExpr()
    {
        return NotExpr::new;
    }

    public static Binary<ConstraintExpr> andExpr()
    {
        return LogicalExpr::and;
    }

    public static Binary<ConstraintExpr> orExpr()
    {
        return LogicalExpr::or;
    }

    public static Map3<FieldExpr, CompareExpr.Operator, ValueExpr, CompareExpr> compareValueExpr()
    {
        return CompareExpr::create;
    }

    public static Map3<FieldExpr, CompareExpr.Operator, List<ValueExpr>, CompareExpr> compareValuesExpr()
    {
        return CompareExpr::create;
    }

    public static Map2<String, List<ValueExpr>, FunctionExpr> functionExpr()
    {
        return FunctionExpr::new;
    }

    public static Map<FunctionExpr, DynamicConstraintExpr> dynamicConstraintExpr()
    {
        return DynamicConstraintExpr::new;
    }

    public static Map2<FieldExpr, OrderExpr.Direction, FieldOrderExpr> fieldOrderExpr()
    {
        return FieldOrderExpr::new;
    }

    public static Map2<FunctionExpr, OrderExpr.Direction, DynamicOrderExpr> dynamicOrderExpr()
    {
        return DynamicOrderExpr::new;
    }

    public static Map<FunctionExpr, ValueExpr> executeValueFunction()
    {
        return StaticFunctions::execute;
    }

    public static Unary<Object> skip()
    {
        return v -> v;
    }
}
