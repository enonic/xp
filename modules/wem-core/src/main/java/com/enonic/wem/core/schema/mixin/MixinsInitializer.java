package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.support.BaseInitializer;


public class MixinsInitializer
    extends BaseInitializer
{
    private final MixinJsonSerializer serializer = new MixinJsonSerializer();

    private static final String[] DEMO_MIXINS = {"demo-mixin-address.json", "demo-mixin-norwegian-counties.json"};

    private MixinService mixinService;

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
                icon( loadSchemaIcon( mixin.getName().toString() ) ).
                build();

            storeMixin( mixin );
        }
    }

    private void storeMixin( final Mixin mixin )
    {
        final GetMixinParams getMixin = new GetMixinParams( mixin.getName() ).notFoundAsNull();

        final Mixin existingMixin = mixinService.getByName( getMixin );
        if ( existingMixin == null )
        {
            final CreateMixinParams createMixin = new CreateMixinParams().
                name( mixin.getName() ).
                displayName( mixin.getDisplayName() ).
                formItems( mixin.getFormItems() ).
                schemaIcon( mixin.getIcon() );
            mixinService.create( createMixin );
        }
        else
        {
            final UpdateMixinParams updateMixin = new UpdateMixinParams().
                name( mixin.getName() ).
                editor( SetMixinEditor.newSetMixinEditor().
                    displayName( mixin.getDisplayName() ).
                    formItems( mixin.getFormItems() ).
                    icon( mixin.getIcon() ).
                    build() );
            mixinService.update( updateMixin );
        }
    }

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
