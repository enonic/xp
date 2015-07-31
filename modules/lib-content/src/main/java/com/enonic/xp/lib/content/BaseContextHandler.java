package com.enonic.xp.lib.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.JsonToPropertyTreeTranslator;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

public abstract class BaseContextHandler
    implements ScriptBean
{
    protected ContentService contentService;

    private String branch;

    private ContentTypeService contentTypeService;

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
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Content type not found [" + contentTypeName + "]" );
        }

        return JsonToPropertyTreeTranslator.create().
            formItems( contentType.form().getFormItems() ).
            mode( contentType.getName().isUnstructured()
                      ? JsonToPropertyTreeTranslator.Mode.LENIENT
                      : JsonToPropertyTreeTranslator.Mode.STRICT ).
            build().
            translate( json );
    }

    protected PropertyTree translateToPropertyTree( final JsonNode json, final MixinName mixinName )
    {
        final Mixin mixin = this.mixinService.getByName( mixinName );

        if ( mixin == null )
        {
            throw new IllegalArgumentException( "Mixin  not found [" + mixinName + "]" );
        }

        return JsonToPropertyTreeTranslator.create().
            formItems( mixin.getFormItems() ).
            mode( JsonToPropertyTreeTranslator.Mode.STRICT ).
            build().
            translate( json );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
        this.contentTypeService = context.getService( ContentTypeService.class ).get();
        this.mixinService = context.getService( MixinService.class ).get();
    }
}
