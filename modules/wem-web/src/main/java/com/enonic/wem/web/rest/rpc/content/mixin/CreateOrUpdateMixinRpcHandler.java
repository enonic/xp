package com.enonic.wem.web.rest.rpc.content.mixin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.mixin.CreateMixin;
import com.enonic.wem.api.command.content.mixin.GetMixins;
import com.enonic.wem.api.command.content.mixin.UpdateMixins;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.mixin.SetMixinEditor;
import com.enonic.wem.core.content.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.IconImageHelper;

import static com.enonic.wem.api.command.Commands.mixin;

@Component
public class CreateOrUpdateMixinRpcHandler
    extends AbstractDataRpcHandler
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private IconImageHelper iconImageHelper;

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
            icon = iconImageHelper.getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException e )
        {
            context.setResult( new JsonErrorResult( e.getError().getMessage() ) );
            return;
        }

        if ( !mixinExists( mixin.getQualifiedName() ) )
        {
            final CreateMixin createCommand = parseCreateMixinCommand( mixin, icon );
            client.execute( createCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.created() );
        }
        else
        {
            final UpdateMixins updateCommand = parseUpdateMixinsCommand( mixin, icon );
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.updated() );
        }
    }

    private CreateMixin parseCreateMixinCommand( final Mixin mixin, final Icon icon )
    {
        return mixin().create().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            moduleName( mixin.getModuleName() ).
            icon( icon );
    }

    private UpdateMixins parseUpdateMixinsCommand( final Mixin mixin, final Icon icon )
    {
        final QualifiedMixinNames qualifiedNames = QualifiedMixinNames.from( mixin.getQualifiedName() );
        return mixin().update().
            qualifiedNames( qualifiedNames ).
            editor( SetMixinEditor.newSetMixinEditor().
                displayName( mixin.getDisplayName() ).
                formItem( mixin.getFormItem() ).
                icon( icon ).
                build() );
    }

    private boolean mixinExists( final QualifiedMixinName qualifiedName )
    {
        final GetMixins getMixins = mixin().get().names( QualifiedMixinNames.from( qualifiedName ) );
        return !client.execute( getMixins ).isEmpty();
    }

    @Autowired
    public void setIconImageHelper( final IconImageHelper iconImageHelper )
    {
        this.iconImageHelper = iconImageHelper;
    }
}
