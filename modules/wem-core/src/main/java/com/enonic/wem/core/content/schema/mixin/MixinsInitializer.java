package com.enonic.wem.core.content.schema.mixin;

import javax.inject.Inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.content.schema.mixin.GetMixins;
import com.enonic.wem.api.command.content.schema.mixin.UpdateMixin;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.initializer.InitializerTask;
import com.enonic.wem.core.support.BaseInitializer;

@Component
@Order(10)
public class MixinsInitializer
    extends BaseInitializer
    implements InitializerTask
{
    private final MixinJsonSerializer serializer = new MixinJsonSerializer();

    private static final String[] DEMO_MIXINS = {"demo-mixin-address.json"};


    protected MixinsInitializer()
    {
        super( "mixins" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        for ( String demoMixinFileName : DEMO_MIXINS )
        {
            final String mixinJson = loadFileAsString( demoMixinFileName );
            Mixin mixin = serializer.toMixin( mixinJson );
            mixin = Mixin.newMixin( mixin ).
                build();

            storeMixin( mixin );
        }
    }

    private void storeMixin( final Mixin mixin )
    {
        final GetMixins getMixins = Commands.mixin().get().names( QualifiedMixinNames.from( mixin.getQualifiedName() ) );
        final Mixins existingMixins = client.execute( getMixins );
        if ( existingMixins.isEmpty() )
        {
            final CreateMixin createMixin = Commands.mixin().create();
            createMixin.displayName( mixin.getDisplayName() );
            createMixin.moduleName( mixin.getModuleName() );
            createMixin.formItem( mixin.getFormItem() );
            createMixin.icon( mixin.getIcon() );
            client.execute( createMixin );
        }
        else
        {
            final UpdateMixin updateMixin = Commands.mixin().update();
            updateMixin.qualifiedName( mixin.getQualifiedName() );
            updateMixin.editor( SetMixinEditor.newSetMixinEditor().
                displayName( mixin.getDisplayName() ).
                formItem( mixin.getFormItem() ).
                icon( mixin.getIcon() ).
                build() );
            client.execute( updateMixin );
        }
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
