package com.enonic.xp.lib.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class BaseContextHandler
    implements ScriptBean
{
    protected NodeService nodeService;

    private String branch;

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public final Object execute()
    {
        if ( Strings.isNullOrEmpty( this.branch ) )
        {
            return doExecute();
        }

        final Context context = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( this.branch ).
            build();

        return context.callWith( this::doExecute );
    }

    protected abstract Object doExecute();

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    protected PropertyTree translateToPropertyTree( final JsonNode json )
    {
        return new JsonToPropertyTreeTranslator().translate( json );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
    }
}
