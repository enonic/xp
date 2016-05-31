package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class InboundDependenciesFunctionArguments
    extends AbstractFunctionArguments
{
    private final String fieldName;

    private final String contentId;

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
        return "inboundDependenciesFunctionArguments";
    }

    private InboundDependenciesFunctionArguments( final String fieldName, final String contentId )
    {
        this.fieldName = fieldName;
        this.contentId = contentId;
    }

    public static InboundDependenciesFunctionArguments create( final List<ValueExpr> arguments )
    {

        final String fieldName = arguments.get( 0 ).getValue().asString();
        final String contentId = arguments.get( 1 ).getValue().asString();

        return new InboundDependenciesFunctionArguments( fieldName, contentId );
    }

    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( fieldName, IndexValueType.STRING );
    }

    public String getContentId()
    {
        return contentId;
    }
}
