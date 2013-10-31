/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query.parser;

import java.util.List;

import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Unary;

import com.enonic.wem.query.Constraint;
import com.enonic.wem.query.Expression;
import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.expr.ArrayExpr;
import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.Fulltext;
import com.enonic.wem.query.expr.FunctionExpr;
import com.enonic.wem.query.expr.GeoDistanceOrderFieldExpr;
import com.enonic.wem.query.expr.LogicalExpr;
import com.enonic.wem.query.expr.NotExpr;
import com.enonic.wem.query.expr.OrderBy;
import com.enonic.wem.query.expr.Query;
import com.enonic.wem.query.expr.ValueExpr;
import com.enonic.wem.query.expr.RelationExists;
import com.enonic.wem.query.function.Date;
import com.enonic.wem.query.function.GeoLocation;

final class QueryMapper
{
    public static Map<String, ValueExpr> stringToNumberExpr()
    {
        return new Map<String, ValueExpr>()
        {
            public ValueExpr map( final String from )
            {
                return ValueExpr.number( from.trim() );
            }
        };
    }

    public static Map<String, ValueExpr> stringToStringExpr()
    {
        return new Map<String, ValueExpr>()
        {
            public ValueExpr map( final String from )
            {
                return ValueExpr.string( from.trim() );
            }
        };
    }

    public static Map<String, FieldExpr> stringToFieldExpr()
    {
        return new Map<String, FieldExpr>()
        {
            public FieldExpr map( final String from )
            {
                return new FieldExpr( from.trim() );
            }
        };
    }

    public static Map<String, Tokens.Fragment> stringToFragment( final String tag )
    {
        return new Map<String, Tokens.Fragment>()
        {
            public Tokens.Fragment map( final String from )
            {
                return Tokens.fragment( from, tag );
            }
        };
    }

    public static Map3<Expression, Integer, Expression, CompareExpr> compareExprMapper()
    {
        return new Map3<Expression, Integer, Expression, CompareExpr>()
        {
            public CompareExpr map( final Expression a, final Integer b, final Expression c )
            {
                return new CompareExpr( b, a, c );
            }
        };
    }

    public static Map<List<ValueExpr>, ArrayExpr> valuesToArrayExpr( final String between )
    {
        return new Map<List<ValueExpr>, ArrayExpr>()
        {
            public ArrayExpr map( final List<ValueExpr> from )
            {
                return new ArrayExpr( from.toArray( new ValueExpr[from.size()] ), between );
            }
        };
    }

    public static Map2<String, ArrayExpr, FunctionExpr> functionExprMapper()
    {
        return new Map2<String, ArrayExpr, FunctionExpr>()
        {
            public FunctionExpr map( final String a, final ArrayExpr b )
            {
                return new FunctionExpr( a.trim(), b );
            }
        };
    }

    public static Map<ValueExpr, ValueExpr> prefixSuffixMapper( final String prefix, final String suffix )
    {
        return new Map<ValueExpr, ValueExpr>()
        {
            public ValueExpr map( final ValueExpr from )
            {
                String str = (String) from.getValue();

                if ( prefix != null )
                {
                    str = prefix + str;
                }

                if ( suffix != null )
                {
                    str = str + suffix;
                }

                return ValueExpr.string( str );
            }
        };
    }

    public static Binary<Expression> logicalExprMapper( final LogicalExpr.Operator op )
    {
        return new Binary<Expression>()
        {
            public Expression map( final Expression left, final Expression right )
            {
                return new LogicalExpr( (Constraint) left, op, (Constraint) right );
            }
        };
    }

    public static Unary<Expression> notExprMapper()
    {
        return new Unary<Expression>()
        {
            public Expression map( final Expression from )
            {
                return new NotExpr( (Constraint) from );
            }
        };
    }

    public static Map<List<OrderSpec>, OrderBy> orderByExprMapper()
    {
        return new Map<List<OrderSpec>, OrderBy>()
        {
            public OrderBy map( final List<OrderSpec> from )
            {
                return new OrderBy( from.toArray( new OrderSpec[from.size()] ) );
            }
        };
    }

    public static Map2<FieldExpr, OrderSpec.Direction, OrderSpec> orderFieldExprMapper()
    {
        return new Map2<FieldExpr, OrderSpec.Direction, OrderSpec>()
        {
            public OrderSpec map( final FieldExpr field, final OrderSpec.Direction direction )
            {
                return field.toOrder( direction );
            }
        };
    }

    public static Map2<Expression, OrderBy, Query> queryExprMapper()
    {
        return new Map2<Expression, OrderBy, Query>()
        {
            public Query map( final Expression a, final OrderBy b )
            {
                return new Query( (Constraint) a, b );
            }
        };
    }

    public static Map3<FieldExpr, ValueExpr, ValueExpr, FieldExpr> geoDistanceOrderParamsMapper()
    {
        return new Map3<FieldExpr, ValueExpr, ValueExpr, FieldExpr>()
        {
            @Override
            public FieldExpr map( final FieldExpr fieldExpr, final ValueExpr location, final ValueExpr unit )
            {
                return new GeoDistanceOrderFieldExpr( fieldExpr, location, unit );
            }
        };
    }

    public static Map2<FieldExpr, CompareExpr, Expression> relationExistsParams()
    {
        return new Map2<FieldExpr, CompareExpr, Expression>()
        {
            public Expression map( final FieldExpr a, final CompareExpr b )
            {
                return new RelationExists( a, b );
            }
        };
    }

    public static Map<Expression, CompareExpr> relationExistsMapper()
    {
        return new Map<Expression, CompareExpr>()
        {
            public CompareExpr map( Expression expr )
            {
                return (RelationExists)expr;
            }
        };
    }

    public static Map<ValueExpr, CompareExpr> fulltextMapper()
    {
        return new Map<ValueExpr, CompareExpr>()
        {
            public CompareExpr map( ValueExpr valueExpr )
            {
                return new Fulltext(valueExpr);
            }
        };
    }

    public static Map<ValueExpr, ValueExpr> geoLocationMapper()
    {
        return new Map<ValueExpr, ValueExpr>()
        {
            public ValueExpr map( ValueExpr from )
            {
                return new GeoLocation(from);
            }
        };
    }

    public static Map<ValueExpr, ValueExpr> dateMapper()
    {
        return new Map<ValueExpr, ValueExpr>()
        {
            public ValueExpr map( ValueExpr from )
            {
                return new Date(from);
            }
        };
    }

}
