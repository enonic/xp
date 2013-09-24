package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.support.BaseInitializer;


public class MixinsInitializer
    extends BaseInitializer
{
    private final MixinJsonSerializer serializer = new MixinJsonSerializer();

    private static final String[] DEMO_MIXINS = {"demo-mixin-address.json"};


    protected MixinsInitializer()
    {
        super( 10, "mixins" );
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
            createMixin.formItems( mixin.getFormItems() );
            createMixin.icon( mixin.getIcon() );
            client.execute( createMixin );
        }
        else
        {
            final UpdateMixin updateMixin = Commands.mixin().update();
            updateMixin.qualifiedName( mixin.getQualifiedName() );
            updateMixin.editor( SetMixinEditor.newSetMixinEditor().
                displayName( mixin.getDisplayName() ).
                formItems( mixin.getFormItems() ).
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
