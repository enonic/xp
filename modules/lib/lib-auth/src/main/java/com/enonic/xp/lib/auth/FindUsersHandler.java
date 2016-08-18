package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserQuery;
import com.enonic.xp.security.UserQueryResult;

public final class FindUsersHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private int start = 0;

    private int count = 10;

    private String query;

    private String sort;

    private boolean includeProfile;

    public void setStart( final Integer start )
    {
        if ( start != null )
        {
            this.start = start;
        }
    }

    public void setCount( final Integer count )
    {
        if ( count != null )
        {
            this.count = count;
        }
    }

    public void setQuery( final String query )
    {
        this.query = query;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public void setIncludeProfile( final boolean includeProfile )
    {
        this.includeProfile = includeProfile;
    }

    public PrincipalsResultMapper execute()
    {
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( this.query == null ? "" : this.query );
        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( this.sort == null ? "" : this.sort );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final UserQuery userQuery = UserQuery.create().
            from( this.start ).
            size( this.count ).
            queryExpr( queryExpr ).
            build();

        final UserQueryResult result = this.securityService.get().
            query( userQuery );

        return new PrincipalsResultMapper( result.getUsers(), result.getTotalSize(), this.includeProfile );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
