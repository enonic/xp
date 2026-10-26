package com.enonic.xp.core.impl.content.schema;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Schemas are resolved in the flat structure {@code cms/<kind>/<name>.yaml} as well as in the legacy folder structure
 * {@code cms/<kind>/<name>/<name>.yaml}; the flat structure wins.
 */
class FlatSchemaStructureTest
    extends ApplicationTestSupport
{
    private static final ApplicationKey APP = ApplicationKey.from( "flatapp" );

    private ContentTypeLoader contentTypeLoader;

    private MixinDescriptorLoader mixinLoader;

    private CmsFormFragmentLoader formFragmentLoader;

    @Override
    protected void initialize()
    {
        addApplication( "flatapp", "/apps/flatapp" );
        addApplication( "myapp1", "/apps/myapp1" );

        this.contentTypeLoader = new ContentTypeLoader( this.resourceService );
        this.mixinLoader = new MixinDescriptorLoader( this.resourceService );
        this.formFragmentLoader = new CmsFormFragmentLoader( this.resourceService );
    }

    @Test
    void content_types_flat_and_legacy()
    {
        assertEquals( List.of( "flatapp:flattype", "flatapp:mixed" ),
                      contentTypeLoader.findNames( APP ).stream().map( ContentTypeName::toString ).sorted().toList() );

        final ContentType flat = contentTypeLoader.get( ContentTypeName.from( APP, "flattype" ) );
        assertEquals( "Flat type", flat.getTitle() );
        assertNotNull( flat.getIcon() );
        assertEquals( "<svg>flat</svg>", new String( flat.getIcon().toByteArray(), StandardCharsets.UTF_8 ) );

        // flat wins over legacy, also a flat .yml over a legacy .yaml; the icon comes from the structure of the descriptor
        final ContentType mixed = contentTypeLoader.get( ContentTypeName.from( APP, "mixed" ) );
        assertEquals( "Flat", mixed.getTitle() );
        assertNull( mixed.getIcon() );
    }

    @Test
    void content_types_legacy_only()
    {
        final ContentType legacy = contentTypeLoader.get( ContentTypeName.from( ApplicationKey.from( "myapp1" ), "tag" ) );
        assertEquals( "Tag", legacy.getTitle() );
        assertNotNull( legacy.getIcon() );
    }

    @Test
    void mixins_and_form_fragments_flat()
    {
        assertEquals( List.of( "flatapp:flatmixin" ), mixinLoader.findNames( APP ).stream().map( MixinName::toString ).toList() );
        assertEquals( "Flat mixin", mixinLoader.get( MixinName.from( APP, "flatmixin" ) ).getTitle() );

        assertEquals( List.of( "flatapp:flatfragment" ),
                      formFragmentLoader.findNames( APP ).stream().map( FormFragmentName::toString ).toList() );
        assertEquals( "Flat fragment", formFragmentLoader.get( FormFragmentName.from( APP, "flatfragment" ) ).getTitle() );
    }
}
