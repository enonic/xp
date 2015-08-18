package com.enonic.xp.lib.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.BuildPropertyTreeParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

public abstract class BaseContextHandler
    implements ScriptBean
{
    protected ContentService contentService;

    private String branch;

    private MixinService mixinService;

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

    protected <T> T checkRequired( final String paramName, final T value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( String.format( "Parameter [%s] is required", paramName ) );
        }
        return value;
    }

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    protected PropertyTree translateToPropertyTree( final JsonNode json, final ContentTypeName contentTypeName )
    {
        return contentService.buildPropertyTree( BuildPropertyTreeParams.create().
            jsonTree( json ).
            contentTypeName( contentTypeName ).
            build() );
    }

    protected PropertyTree translateToPropertyTree( final JsonNode json, final MixinName mixinName )
    {
        return mixinService.buildPropertyTree( BuildPropertyTreeParams.create().
            jsonTree( json ).
            mixinName( mixinName ).
            build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
        this.mixinService = context.getService( MixinService.class ).get();
    }
}
