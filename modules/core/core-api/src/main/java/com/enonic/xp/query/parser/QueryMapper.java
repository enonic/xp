package com.enonic.xp.query.parser;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.jparsec.Tokens;
import org.jparsec.functors.Map3;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.NotExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

final class QueryMapper
{
    public static Function<String, ValueExpr> stringValueExpr()
    {
        return ValueExpr::string;
    }

    public static Function<String, ValueExpr> numberValueExpr()
    {
        return value -> {
            final Double number = Double.parseDouble( value );
            return ValueExpr.number( number );
        };
    }

    public static BiFunction<ConstraintExpr, List<OrderExpr>, QueryExpr> queryExpr()
    {
        return QueryExpr::new;
    }

    public static Function<String, FieldExpr> fieldExpr()
    {
        return FieldExpr::from;
    }

    public static Function<String, Tokens.Fragment> fragment( final String tag )
    {
        return from -> Tokens.fragment( from, tag );
    }

    public static UnaryOperator<ConstraintExpr> notExpr()
    {
        return NotExpr::new;
    }

    public static BinaryOperator<ConstraintExpr> andExpr()
    {
        return LogicalExpr::and;
    }

    public static BinaryOperator<ConstraintExpr> orExpr()
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

    public static BiFunction<String, List<ValueExpr>, FunctionExpr> functionExpr()
    {
        return FunctionExpr::new;
    }

    public static Function<FunctionExpr, DynamicConstraintExpr> dynamicConstraintExpr()
    {
        return DynamicConstraintExpr::new;
    }

    public static BiFunction<FieldExpr, OrderExpr.Direction, FieldOrderExpr> fieldOrderExpr()
    {
        return FieldOrderExpr::new;
    }

    public static BiFunction<FunctionExpr, OrderExpr.Direction, DynamicOrderExpr> dynamicOrderExpr()
    {
        return DynamicOrderExpr::new;
    }

    public static Function<FunctionExpr, ValueExpr> executeValueFunction()
    {
        return StaticFunctions::execute;
    }

    public static UnaryOperator<Object> skip()
    {
        return v -> v;
    }
}
