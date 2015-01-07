package com.enonic.wem.core.schema.mixin;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.mixin.Mixins;

@Component(immediate = true)
public final class BuiltinMixinProvider
    implements MixinProvider
{
    private static MixinName MENU_NAME = MixinName.from( ModuleKey.SYSTEM, "menu-item" );

    private static final String MIXINS_FOLDER = "mixins";

    private static final Mixin MENU = Mixin.newMixin().
        name( MENU_NAME ).
        displayName( "Menu" ).
        formItems( createMenuMixinForm() ).
        build();

    private static final Mixin[] MIXINS = {MENU};

    private final Mixins mixins;

    public BuiltinMixinProvider()
    {
        this.mixins = Mixins.from( generateSystemMixins() );
    }

    private static FormItems createMenuMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( Input.newInput().
            name( "menuItem" ).
            label( "Menu item" ).
            inputType( InputTypes.CHECKBOX ).
            occurrences( 0, 1 ).
            helpText( "Check this to include this Page in the menu" ).
            build() );
        formItems.add( Input.newInput().name( "menuName" ).
            inputType( InputTypes.TEXT_LINE ).
            label( "Name in menu" ).
            occurrences( 0, 1 ).
            helpText( "Name to be used in menu. Optional" ).
            build() );
        return formItems;
    }

    private List<Mixin> generateSystemMixins()
    {
        final List<Mixin> mixins = Lists.newArrayList();
        for ( Mixin mixin : MIXINS )
        {
            mixin = Mixin.newMixin( mixin ).
                icon( loadSchemaIcon( MIXINS_FOLDER, mixin.getName().getLocalName() ) ).
                build();
            mixins.add( mixin );
        }
        return mixins;
    }

    @Override
    public Mixins get()
    {
        return this.mixins;
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try (final InputStream stream = this.getClass().getResourceAsStream( filePath ))
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }
}
