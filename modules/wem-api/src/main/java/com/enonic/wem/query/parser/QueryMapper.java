package com.enonic.wem.query.parser;

import java.util.List;

import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Unary;

import com.enonic.wem.query.ast.CompareExpr;
import com.enonic.wem.query.ast.ConstraintExpr;
import com.enonic.wem.query.ast.DynamicConstraintExpr;
import com.enonic.wem.query.ast.DynamicOrderExpr;
import com.enonic.wem.query.ast.FieldExpr;
import com.enonic.wem.query.ast.FieldOrderExpr;
import com.enonic.wem.query.ast.FunctionExpr;
import com.enonic.wem.query.ast.LogicalExpr;
import com.enonic.wem.query.ast.NotExpr;
import com.enonic.wem.query.ast.OrderExpr;
import com.enonic.wem.query.ast.QueryExpr;
import com.enonic.wem.query.ast.ValueExpr;

final class QueryMapper
{
    public static Map<String, ValueExpr> stringValueExpr()
    {
        return new Map<String, ValueExpr>()
        {
            @Override
            public ValueExpr map( final String value )
            {
                return ValueExpr.string( value );
            }
        };
    }

    public static Map<String, ValueExpr> numberValueExpr()
    {
        return new Map<String, ValueExpr>()
        {
            @Override
            public ValueExpr map( final String value )
            {
                final Double number = Double.parseDouble( value );
                return ValueExpr.number( number );
            }
        };
    }

    public static Map2<ConstraintExpr, List<OrderExpr>, QueryExpr> queryExpr()
    {
        return new Map2<ConstraintExpr, List<OrderExpr>, QueryExpr>()
        {
            @Override
            public QueryExpr map( final ConstraintExpr constraint, final List<OrderExpr> orderList )
            {
                return new QueryExpr( constraint, orderList );
            }
        };
    }

    public static Map<String, FieldExpr> fieldExpr()
    {
        return new Map<String, FieldExpr>()
        {
            @Override
            public FieldExpr map( final String value )
            {
                return new FieldExpr( value );
            }
        };
    }

    public static Map<String, Tokens.Fragment> fragment( final String tag )
    {
        return new Map<String, Tokens.Fragment>()
        {
            public Tokens.Fragment map( final String from )
            {
                return Tokens.fragment( from, tag );
            }
        };
    }

    public static Unary<ConstraintExpr> notExpr()
    {
        return new Unary<ConstraintExpr>()
        {
            @Override
            public ConstraintExpr map( final ConstraintExpr expr )
            {
                return new NotExpr( expr );
            }
        };
    }

    public static Binary<ConstraintExpr> andExpr()
    {
        return new Binary<ConstraintExpr>()
        {
            public ConstraintExpr map( final ConstraintExpr left, final ConstraintExpr right )
            {
                return LogicalExpr.and( left, right );
            }
        };
    }

    public static Binary<ConstraintExpr> orExpr()
    {
        return new Binary<ConstraintExpr>()
        {
            public ConstraintExpr map( final ConstraintExpr left, final ConstraintExpr right )
            {
                return LogicalExpr.or( left, right );
            }
        };
    }

    public static Map3<FieldExpr, CompareExpr.Operator, ValueExpr, CompareExpr> compareValueExpr()
    {
        return new Map3<FieldExpr, CompareExpr.Operator, ValueExpr, CompareExpr>()
        {
            @Override
            public CompareExpr map( final FieldExpr field, final CompareExpr.Operator operator, final ValueExpr value )
            {
                return CompareExpr.create( field, operator, value );
            }
        };
    }

    public static Map3<FieldExpr, CompareExpr.Operator, List<ValueExpr>, CompareExpr> compareValuesExpr()
    {
        return new Map3<FieldExpr, CompareExpr.Operator, List<ValueExpr>, CompareExpr>()
        {
            @Override
            public CompareExpr map( final FieldExpr field, final CompareExpr.Operator operator, final List<ValueExpr> value )
            {
                return CompareExpr.create( field, operator, value );
            }
        };
    }

    public static Map2<String, List<ValueExpr>, FunctionExpr> functionExpr()
    {
        return new Map2<String, List<ValueExpr>, FunctionExpr>()
        {
            @Override
            public FunctionExpr map( final String name, final List<ValueExpr> args )
            {
                return new FunctionExpr( name, args );
            }
        };
    }

    public static Map<FunctionExpr, DynamicConstraintExpr> dynamicConstraintExpr()
    {
        return new Map<FunctionExpr, DynamicConstraintExpr>()
        {
            @Override
            public DynamicConstraintExpr map( final FunctionExpr function )
            {
                return new DynamicConstraintExpr( function );
            }
        };
    }

    public static Map2<FieldExpr, OrderExpr.Direction, FieldOrderExpr> fieldOrderExpr()
    {
        return new Map2<FieldExpr, OrderExpr.Direction, FieldOrderExpr>()
        {
            @Override
            public FieldOrderExpr map( final FieldExpr field, final OrderExpr.Direction direction )
            {
                return new FieldOrderExpr( field, direction );
            }
        };
    }

    public static Map2<FunctionExpr, OrderExpr.Direction, DynamicOrderExpr> dynamicOrderExpr()
    {
        return new Map2<FunctionExpr, OrderExpr.Direction, DynamicOrderExpr>()
        {
            @Override
            public DynamicOrderExpr map( final FunctionExpr function, final OrderExpr.Direction direction )
            {
                return new DynamicOrderExpr( function, direction );
            }
        };
    }

    public static Map<FunctionExpr, ValueExpr> executeValueFunction()
    {
        return new Map<FunctionExpr, ValueExpr>()
        {
            @Override
            public ValueExpr map( final FunctionExpr function )
            {
                return StaticFunctions.execute( function );
            }
        };
    }
}
