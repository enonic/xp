package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class PathMatchFunctionArguments
    extends AbstractFunctionArguments
{
    private final String fieldName;

    private final String path;

    private final int minimumMatch;

    @Override
    protected int getMinArguments()
    {
        return 2;
    }

    @Override
    protected int getMaxArguments()
    {
        return 3;
    }

    @Override
    protected String getFunctionName()
    {
        return "pathMatch";
    }

    private PathMatchFunctionArguments( final String fieldName, final String path, final int minimumMatch )
    {
        this.fieldName = fieldName;
        this.path = path;
        this.minimumMatch = minimumMatch;
    }

    public static PathMatchFunctionArguments create( final List<ValueExpr> arguments )
    {

        final String fieldName = arguments.get( 0 ).getValue().asString();
        final String path = arguments.get( 1 ).getValue().asString();

        int minimumMatch = 1;

        if ( arguments.size() > 2 )
        {
            minimumMatch = arguments.get( 2 ).getValue().asDouble().intValue();
        }

        return new PathMatchFunctionArguments( fieldName, path, minimumMatch );
    }

    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( fieldName, IndexValueType.PATH );
    }

    public String getPath()
    {
        return path;
    }

    public int getMinimumMatch()
    {
        return minimumMatch;
    }
}
