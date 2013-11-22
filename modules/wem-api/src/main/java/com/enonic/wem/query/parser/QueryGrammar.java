package com.enonic.wem.query.parser;

import java.util.List;

import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Unary;
import org.codehaus.jparsec.misc.Mapper;
import org.codehaus.jparsec.pattern.Patterns;

import com.enonic.wem.query.ast.CompareExpr;
import com.enonic.wem.query.ast.ConstraintExpr;
import com.enonic.wem.query.ast.DynamicConstraintExpr;
import com.enonic.wem.query.ast.DynamicOrderExpr;
import com.enonic.wem.query.ast.FieldExpr;
import com.enonic.wem.query.ast.FieldOrderExpr;
import com.enonic.wem.query.ast.FunctionExpr;
import com.enonic.wem.query.ast.OrderExpr;
import com.enonic.wem.query.ast.QueryExpr;
import com.enonic.wem.query.ast.ValueExpr;

final class QueryGrammar
{
    private final static String[] OPERATORS = {"=", "!=", ">", ">=", "<", "<=", ",", "(", ")"};

    private final static String[] KEYWORDS = {"AND", "OR", "NOT", "LIKE", "IN", "ASC", "DESC", "ORDER", "BY", "GEOPOINT", "DATETIME"};

    private final Terminals terminals;

    private final Parser<Tokens.Fragment> identifierToken;

    public QueryGrammar()
    {
        this.identifierToken = identifierToken();
        this.terminals = Terminals.caseInsensitive( this.identifierToken.source(), OPERATORS, KEYWORDS );
    }

    private Parser<Tokens.Fragment> identifierToken()
    {
        return fragmentToken( "[a-zA-Z\\*@]+[a-zA-Z0-9\\-_/\\.\\*@]*", Tokens.Tag.IDENTIFIER.name() );
    }

    private Parser<Tokens.Fragment> fragmentToken( final String pattern, final String tag )
    {
        return Scanners.pattern( Patterns.regex( pattern ), tag ).source().map( QueryMapper.fragment( tag ) );
    }

    private Parser<?> tokenizer()
    {
        return Parsers.or( this.terminals.tokenizer(), this.identifierToken, Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER,
                           Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER, decimalToken() );
    }

    private Parser<String> identifier()
    {
        return Terminals.fragment( Tokens.Tag.IDENTIFIER );
    }

    private Parser<Tokens.Fragment> decimalToken()
    {
        return fragmentToken( "([-+])?[0-9]+(\\.[0-9]+)?", Tokens.Tag.DECIMAL.name() );
    }

    private Parser<ValueExpr> stringLiteral()
    {
        return Terminals.StringLiteral.PARSER.map( QueryMapper.stringValueExpr() );
    }

    private Parser<ValueExpr> numberLiteral()
    {
        return Terminals.fragment( Tokens.Tag.DECIMAL.name() ).map( QueryMapper.numberValueExpr() );
    }

    private Parser<ValueExpr> parseValue( final boolean allowValueFunctions )
    {
        final Parser<ValueExpr> simple = Parsers.or( stringLiteral(), numberLiteral() );

        if ( !allowValueFunctions )
        {
            return simple;
        }

        return Parsers.or( simple, parseValueFunction() );
    }

    private Parser<ValueExpr> parseValueFunction()
    {
        final Parser<FunctionExpr> function = parseFunction( false );
        return function.map( QueryMapper.executeValueFunction() );
    }

    private Parser<List<ValueExpr>> parseValues( final boolean allowValueFunctions )
    {
        return parseValue( allowValueFunctions ).sepBy( term( "," ) ).between( term( "(" ), term( ")" ) );
    }

    private Parser<String> parseName()
    {
        return identifier().or( this.terminals.token( KEYWORDS ).source() );
    }

    private Parser<?> term( final String term )
    {
        return Mapper._( this.terminals.token( term ) );
    }

    private Parser<FieldExpr> parseField()
    {
        return parseName().map( QueryMapper.fieldExpr() );
    }

    private Parser<CompareExpr> parseCompare()
    {
        final Parser<CompareExpr> eq = parseCompare( "=", CompareExpr.Operator.EQ );
        final Parser<CompareExpr> neq = parseCompare( "!=", CompareExpr.Operator.NEQ );
        final Parser<CompareExpr> lt = parseCompare( "<", CompareExpr.Operator.LT );
        final Parser<CompareExpr> lte = parseCompare( "<=", CompareExpr.Operator.LTE );
        final Parser<CompareExpr> gt = parseCompare( ">", CompareExpr.Operator.GT );
        final Parser<CompareExpr> gte = parseCompare( ">=", CompareExpr.Operator.GTE );
        final Parser<CompareExpr> like = parseCompareWithNot( "LIKE", CompareExpr.Operator.LIKE );
        final Parser<CompareExpr> in = parseCompareWithNot( "IN", CompareExpr.Operator.IN );

        return Parsers.or( eq, neq, lt, lte, gt, gte, like, in );
    }

    private Parser<CompareExpr> parseCompare( final String op, final CompareExpr.Operator opCode )
    {
        return Parsers.sequence( parseField(), term( op ).retn( opCode ), parseValue( true ), QueryMapper.compareValueExpr() );
    }

    private Parser<CompareExpr> parseCompareWithNot( final String op, final CompareExpr.Operator opCode )
    {
        final Parser<CompareExpr.Operator> opParser = term( op ).retn( opCode );
        final Parser<CompareExpr.Operator> negOpParser = term( "NOT" ).followedBy( term( op ) ).retn( opCode.negate() );
        final Parser<CompareExpr.Operator> combined = Parsers.or( opParser, negOpParser );

        if ( opCode.allowMultipleValues() )
        {
            return Parsers.sequence( parseField(), combined, parseValues( true ), QueryMapper.compareValuesExpr() );
        }

        return Parsers.sequence( parseField(), combined, parseValue( true ), QueryMapper.compareValueExpr() );
    }

    private Parser<Unary<ConstraintExpr>> parseNot()
    {
        return term( "NOT" ).next( Parsers.constant( QueryMapper.notExpr() ) );
    }

    private Parser<Binary<ConstraintExpr>> parseAnd()
    {
        return term( "AND" ).next( Parsers.constant( QueryMapper.andExpr() ) );
    }

    private Parser<Binary<ConstraintExpr>> parseOr()
    {
        return term( "OR" ).next( Parsers.constant( QueryMapper.orExpr() ) );
    }

    private Parser<ConstraintExpr> parseConstraint()
    {
        final Parser.Reference<ConstraintExpr> ref = Parser.newReference();

        final OperatorTable<ConstraintExpr> table = new OperatorTable<>();
        table.prefix( parseNot(), 30 );
        table.infixl( parseAnd(), 20 );
        table.infixl( parseOr(), 10 );

        final Parser<ConstraintExpr> inner = Parsers.or( parseCompare(), parseDynamicConstraint() );
        final Parser<ConstraintExpr> unit = ref.lazy().between( term( "(" ), term( ")" ) ).or( inner );
        final Parser<ConstraintExpr> parser = table.build( unit );

        ref.set( parser );
        return parser;
    }

    private Parser<FunctionExpr> parseFunction( final boolean allowValueFunctions )
    {
        return Parsers.sequence( parseName(), parseValues( allowValueFunctions ), QueryMapper.functionExpr() );
    }

    private Parser<DynamicConstraintExpr> parseDynamicConstraint()
    {
        return parseFunction( true ).map( QueryMapper.dynamicConstraintExpr() );
    }

    private Parser<FieldOrderExpr> parseFieldOrder()
    {
        return Parsers.sequence( parseField(), parseOrderDirection(), QueryMapper.fieldOrderExpr() );
    }

    private Parser<DynamicOrderExpr> parseDynamicOrder()
    {
        return Parsers.sequence( parseFunction( true ), parseOrderDirection(), QueryMapper.dynamicOrderExpr() );
    }

    private Parser<OrderExpr> parseOrderElement()
    {
        return Parsers.or( parseDynamicOrder(), parseFieldOrder() );
    }

    private Parser<List<OrderExpr>> parseOrderList()
    {
        return parseOrderElement().sepBy( term( "," ) );
    }

    private Parser<List<OrderExpr>> parseOrderBy()
    {
        return Parsers.sequence( term( "ORDER" ), term( "BY" ), parseOrderList() );
    }

    private Parser<OrderExpr.Direction> parseOrderDirection()
    {
        final Parser<OrderExpr.Direction> asc = term( "ASC" ).retn( OrderExpr.Direction.ASC );
        final Parser<OrderExpr.Direction> desc = term( "DESC" ).retn( OrderExpr.Direction.DESC );
        return Parsers.or( asc, desc ).optional( OrderExpr.Direction.ASC );
    }

    private Parser<QueryExpr> parseQuery()
    {
        final Parser<ConstraintExpr> constraint = parseConstraint().optional();
        final Parser<List<OrderExpr>> orderList = parseOrderBy().optional();
        return Parsers.sequence( constraint, orderList, QueryMapper.queryExpr() );
    }

    public Parser<QueryExpr> grammar()
    {
        return parseQuery().from( tokenizer(), Scanners.SQL_DELIMITER );
    }
}
