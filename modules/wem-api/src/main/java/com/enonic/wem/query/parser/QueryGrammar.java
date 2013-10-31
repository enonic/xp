package com.enonic.wem.query.parser;

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

import com.enonic.wem.query.Expression;
import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.expr.ArrayExpr;
import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.FunctionExpr;
import com.enonic.wem.query.expr.LogicalExpr;
import com.enonic.wem.query.expr.OrderBy;
import com.enonic.wem.query.expr.Query;
import com.enonic.wem.query.expr.ScoreFieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public class QueryGrammar
{
    private final static String[] OPERATORS = {"=", "!=", ">", ">=", "<", "<=", "(", ")", ",", "[", "]"};

    private final static String[] KEYWORDS =
        {"LIKE", "NOT", "IN", "CONTAINS", "STARTS", "ENDS", "WITH", "OR", "AND", "ORDER", "BY", "ASC", "DESC", "FT", "SCORE",
            "GEODISTANCEORDER", "RELATIONEXISTS", "FULLTEXT", "DATE", "GEOLOCATION"};

    public static final ScoreFieldExpr SCORE_FIELD_EXPR = new ScoreFieldExpr();

    private final Terminals terms;

    private final Parser<Tokens.Fragment> identifierToken;

    public QueryGrammar()
    {
        this.identifierToken = identifierToken();
        this.terms = Terminals.caseInsensitive( this.identifierToken.source(), OPERATORS, KEYWORDS );
    }

    public Parser<Query> definition()
    {
        return queryExpr().from( tokenizer(), ignored() );
    }

    private Parser<Tokens.Fragment> fragmentToken( final String pattern, final String tag )
    {
        return Scanners.pattern( Patterns.regex( pattern ), tag ).source().map( QueryMapper.stringToFragment( tag ) );
    }

    private Parser<Tokens.Fragment> identifierToken()
    {
        return fragmentToken( "[a-zA-Z\\*@]+[a-zA-Z0-9\\-_/\\.\\*@]*", Tokens.Tag.IDENTIFIER.name() );
    }

    private Parser<Tokens.Fragment> decimalToken()
    {
        return fragmentToken( "([-+])?[0-9]+(\\.[0-9]+)?", Tokens.Tag.DECIMAL.name() );
    }

    private Parser<?> tokenizer()
    {
        return Parsers.or( this.terms.tokenizer(), this.identifierToken, decimalToken(), Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER,
                           Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER );
    }

    private Parser<Void> ignored()
    {
        return Scanners.SQL_DELIMITER;
    }

    private Parser<String> identifier()
    {
        return Terminals.fragment( Tokens.Tag.IDENTIFIER );
    }

    private Parser<FieldExpr> fieldExpr()
    {
        return identifier().map( QueryMapper.stringToFieldExpr() );
    }

    private Parser<ValueExpr> numberExpr()
    {
        return Terminals.fragment( Tokens.Tag.DECIMAL.name() ).map( QueryMapper.stringToNumberExpr() );
    }

    private Parser<ValueExpr> stringExpr()
    {
        return Terminals.StringLiteral.PARSER.map( QueryMapper.stringToStringExpr() );
    }

    private Parser<ValueExpr> advancedValueExpr()
    {
        return Parsers.or( numberExpr(), stringExpr(), arrayExpr(), fieldExpr(), staticFunction() );
    }

    private Parser<ValueExpr> staticFunction()
    {
        return Parsers.or( geoLocationFunction(), dateFunction() );
    }

    private Parser<ValueExpr> geoLocationFunction()
    {
        return Parsers.sequence( term( "GEOLOCATION" ).followedBy( term( "(" ) ), stringExpr().followedBy( term( ")" ) ) ).
            map( QueryMapper.geoLocationMapper() );    }

    private Parser<ValueExpr> dateFunction()
    {
        return Parsers.sequence( term( "DATE" ).followedBy( term( "(" ) ), stringExpr().followedBy( term( ")" ) ) ).
            map( QueryMapper.dateMapper() );
    }

    private Parser<ValueExpr> valueExpr()
    {
        return Parsers.or( numberExpr(), stringExpr() );
    }

    private Parser<ArrayExpr> arrayExpr()
    {
        return valueExpr().sepBy( term( "," ) ).map( QueryMapper.valuesToArrayExpr( "[]" ) ).between( term( "[" ), term( "]" ) );
    }

    private Parser<ArrayExpr> paramExpr()
    {
        return advancedValueExpr().sepBy( term( "," ) ).map( QueryMapper.valuesToArrayExpr( "()" ) ).between( term( "(" ), term( ")" ) );
    }

    private Parser<FunctionExpr> functionExpr()
    {
        return Parsers.sequence( identifier(), paramExpr(), QueryMapper.functionExprMapper() );
    }

    private Parser<Expression> computedExpr( final boolean skip )
    {
        if ( skip )
        {
            return Parsers.or( valueExpr(), functionExpr() );
        }
        else
        {
            return Parsers.or( dynamicConstraint(), valueExpr(), functionExpr() );
        }
    }

    private Parser<Expression> dynamicConstraint()
    {
        return Parsers.or( relationExists(), fulltext(), dateFunction(), geoLocationFunction() );
    }

    private Parser<CompareExpr> relationExists()
    {
        return Parsers.sequence( term( "RELATIONEXISTS" ).followedBy( term( "(" ) ), relationExistsParams().followedBy( term( ")" ) ) ).
            map( QueryMapper.relationExistsMapper() );
    }

    private Parser<Expression> relationExistsParams()
    {
        return Parsers.sequence( fieldExpr().followedBy( term( "," ) ), relationalExpr( true ), QueryMapper.relationExistsParams() );
    }

    private Parser<CompareExpr> fulltext()
    {
        return Parsers.sequence( term( "FULLTEXT" ).followedBy( term( "(" ) ), stringExpr().followedBy( term( ")" ) ) ).
            map( QueryMapper.fulltextMapper() );
    }

    private Parser<CompareExpr> compareExpr( final String opStr, final Integer opNum, final Parser<? extends Expression> right,
                                             final boolean skip )
    {
        return Parsers.sequence( left( skip ), term( opStr ).retn( opNum ), right, QueryMapper.compareExprMapper() );
    }

    private Parser<Expression> left( final boolean skip )
    {
        if ( skip )
        {
            return Parsers.or( functionExpr(), fieldExpr() );
        }
        else
        {
            return Parsers.or( dynamicConstraint(), functionExpr(), fieldExpr() );
        }
    }

    private Parser<CompareExpr> relationalEqExpr( final boolean skip )
    {
        return compareExpr( "=", CompareExpr.EQ, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> relationalNeqExpr( final boolean skip )
    {
        return compareExpr( "!=", CompareExpr.NEQ, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> relationalGtExpr( final boolean skip )
    {
        return compareExpr( ">", CompareExpr.GT, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> relationalGteExpr( final boolean skip )
    {
        return compareExpr( ">=", CompareExpr.GTE, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> relationalLtExpr( final boolean skip )
    {
        return compareExpr( "<", CompareExpr.LT, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> relationalLteExpr( final boolean skip )
    {
        return compareExpr( "<=", CompareExpr.LTE, computedExpr( skip ), skip );
    }

    private Parser<CompareExpr> compareWithNotExpr( final Parser<?> opStr, final Integer opNum, final Integer notOpNum,
                                                    final Parser<? extends Expression> right )
    {
        final Parser<Integer> op = Parsers.or( phrase( "NOT" ).followedBy( opStr ).retn( notOpNum ), opStr.retn( opNum ) );
        return Parsers.sequence( fieldExpr(), op, right, QueryMapper.compareExprMapper() );
    }

    private Parser<CompareExpr> compareLikeExpr( final Parser<?> op, final Parser<ValueExpr> right )
    {
        return compareWithNotExpr( op, CompareExpr.LIKE, CompareExpr.NOT_LIKE, right );
    }

    private Parser<CompareExpr> compareLikeExpr()
    {
        return compareLikeExpr( term( "LIKE" ), stringExpr() );
    }

    private Parser<CompareExpr> compareLikePrefixSuffixExpr( final Parser<?> op, final String prefix, final String suffix )
    {
        return compareLikeExpr( op, stringExpr().map( QueryMapper.prefixSuffixMapper( prefix, suffix ) ) );
    }

    private Parser<CompareExpr> compareInExpr()
    {
        return compareWithNotExpr( term( "IN" ), CompareExpr.IN, CompareExpr.NOT_IN, paramExpr() );
    }

    private Parser<CompareExpr> compareContainsExpr()
    {
        return compareLikePrefixSuffixExpr( term( "CONTAINS" ), "", "" );
    }

    private Parser<CompareExpr> compareStartsWithExpr()
    {
        return compareLikePrefixSuffixExpr( term( "STARTS" ).followedBy( term( "WITH" ).optional() ), null, "%" );
    }

    private Parser<CompareExpr> compareEndsWithExpr()
    {
        return compareLikePrefixSuffixExpr( term( "ENDS" ).followedBy( term( "WITH" ).optional() ), "%", null );
    }

    private Parser<CompareExpr> compareFulltextExpr()
    {
        final Parser<Integer> op = term( "FT" ).retn( CompareExpr.FT );
        return Parsers.sequence( fieldExpr(), op, stringExpr(), QueryMapper.compareExprMapper() );
    }

    private Parser<CompareExpr> relationalExpr( final boolean skip )
    {
        if ( skip )
        {
            return Parsers.or( relationalEqExpr( skip ), relationalNeqExpr( skip ), relationalLtExpr( skip ), relationalLteExpr( skip ),
                               relationalGtExpr( skip ), relationalGteExpr( skip ) );
        }
        else
        {
            return Parsers.or( relationalEqExpr( skip ), relationalNeqExpr( skip ), relationalLtExpr( skip ), relationalLteExpr( skip ),
                               relationalGtExpr( skip ), relationalGteExpr( skip ), relationExists(), fulltext() );
        }
    }

    private Parser<CompareExpr> matchExpr()
    {
        return Parsers.or( compareLikeExpr(), compareFulltextExpr(), compareInExpr(), compareContainsExpr(), compareStartsWithExpr(),
                           compareEndsWithExpr(), functionExpr() );
    }

    private Parser<CompareExpr> compareExpr()
    {
        return Parsers.or( relationalExpr( false ), matchExpr() );
    }

    private Parser<Expression> logicalExpr()
    {
        final Parser.Reference<Expression> ref = Parser.newReference();
        final Parser<Expression> parser =
            new OperatorTable<Expression>().prefix( notExpr(), 30 ).infixl( logicalExpr( "AND", LogicalExpr.Operator.AND ), 20 ).infixl(
                logicalExpr( "OR", LogicalExpr.Operator.OR ), 10 ).build( paren( ref.lazy() ).or( compareExpr() ) ).label( "logicalExpr" );
        ref.set( parser );
        return parser;
    }

    private <T> Parser<T> paren( final Parser<T> parser )
    {
        return parser.between( term( "(" ), term( ")" ) );
    }

    private Parser<Unary<Expression>> notExpr()
    {
        return term( "NOT" ).next( Parsers.constant( QueryMapper.notExprMapper() ) );
    }

    private Parser<Binary<Expression>> logicalExpr( final String opStr, final LogicalExpr.Operator opNum )
    {
        return term( opStr ).next( Parsers.constant( QueryMapper.logicalExprMapper( opNum ) ) );
    }

    private Parser<OrderBy> orderByExpr()
    {
        return Parsers.sequence( term( "ORDER" ), term( "BY" ).optional(), orderFieldExpr().sepBy( term( "," ) ) ).map(
            QueryMapper.orderByExprMapper() );
    }

    private Parser<OrderSpec> orderFieldExpr()
    {
        final Parser<OrderSpec.Direction> optional =
            Parsers.or( term( "ASC" ).retn( OrderSpec.Direction.ASC ), term( "DESC" ).retn( OrderSpec.Direction.DESC ) ).optional(
                OrderSpec.Direction.ASC );

        return Parsers.sequence( orderSpec(), optional, QueryMapper.orderFieldExprMapper() );
    }

    private Parser<FieldExpr> orderSpec()
    {
        return Parsers.or( fieldExpr(), dynamicOrder() );
    }

    private Parser<FieldExpr> dynamicOrder()
    {
        return Parsers.or( score(), geoDistanceOrder() );
    }

    private Parser<FieldExpr> geoDistanceOrder()
    {
        return Parsers.sequence( term( "GEODISTANCEORDER" ), geoDistanceOrderParams().between( term( "(" ), term( ")" ) ) );
    }

    private Parser<FieldExpr> geoDistanceOrderParams()
    {
        return Parsers.sequence( fieldExpr().followedBy( term( "," ) ), stringExpr().followedBy( term( "," ) ), stringExpr(),
                                 QueryMapper.geoDistanceOrderParamsMapper() );
    }

    private Parser<ScoreFieldExpr> score()
    {
        return Parsers.sequence( term( "score" ).followedBy( term( "(" ) ).followedBy( term( ")" ) ) ).retn( SCORE_FIELD_EXPR );
    }

    private Parser<Query> queryExpr()
    {
        return Parsers.sequence( logicalExpr().optional(), orderByExpr().optional(), QueryMapper.queryExprMapper() );
    }

    private Parser<?> term( final String term )
    {
        return Mapper._( this.terms.token( term ) );
    }

    private Parser<?> phrase( final String phrase )
    {
        return Mapper._( this.terms.phrase( phrase.split( "\\s" ) ) );
    }

}
