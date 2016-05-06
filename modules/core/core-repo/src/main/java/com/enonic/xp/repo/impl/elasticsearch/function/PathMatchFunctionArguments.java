package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class PathMatchFunctionArguments
    extends AbstractFunctionArguments
{
    private final String fieldName;

    private final String path;

    @Override
    protected int getMinArguments()
    {
        return 2;
    }

    @Override
    protected int getMaxArguments()
    {
        return 2;
    }

    @Override
    protected String getFunctionName()
    {
        return "pathMatch";
    }

    private PathMatchFunctionArguments( final String fieldName, final String path )
    {
        this.fieldName = fieldName;
        this.path = path;
    }

    public static PathMatchFunctionArguments create( final List<ValueExpr> arguments )
    {

        final String fieldName = arguments.get( 0 ).getValue().asString();
        final String path = arguments.get( 1 ).getValue().asString();

        return new PathMatchFunctionArguments( fieldName, path );
    }

    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( fieldName, IndexValueType.PATH );
    }

    public String getPath()
    {
        return path;
    }
}
