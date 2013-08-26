package com.enonic.wem.admin.rpc.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.serializer.ParsingException;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.schema.mixin.editor.SetMixinEditor.newSetMixinEditor;


public class CreateOrUpdateMixinRpcHandler
    extends AbstractDataRpcHandler
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private UploadService uploadService;

    public CreateOrUpdateMixinRpcHandler()
    {
        super( "mixin_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String mixinJson = context.param( "mixin" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        final Mixin mixin;
        try
        {
            mixin = mixinXmlSerializer.toMixin( mixinJson );
        }
        catch ( ParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid Mixin format" ) );
            return;
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException e )
        {
            context.setResult( new JsonErrorResult( e.getError().getMessage() ) );
            return;
        }

        if ( !mixinExists( mixin.getQualifiedName() ) )
        {
            createMixin( context, mixin, icon );
        }
        else
        {
            updateMixin( context, mixin, icon );
        }
    }

    private void updateMixin( final JsonRpcContext context, final Mixin mixin, final Icon icon )
    {
        final SetMixinEditor editor = newSetMixinEditor().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            icon( icon ).
            build();
        final UpdateMixin updateCommand = mixin().update().
            qualifiedName( mixin.getQualifiedName() ).
            editor( editor );
        try
        {
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.updated() );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private void createMixin( final JsonRpcContext context, final Mixin mixin, final Icon icon )
    {
        final CreateMixin createCommand = mixin().create().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            moduleName( mixin.getModuleName() ).
            icon( icon );
        try
        {
            client.execute( createCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.created() );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private boolean mixinExists( final QualifiedMixinName qualifiedName )
    {
        final GetMixins getMixins = mixin().get().names( QualifiedMixinNames.from( qualifiedName ) );
        return !client.execute( getMixins ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
