package com.enonic.xp.app;


import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationRelativeResolverTest
{
    @Test
    void toContentTypeName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        ContentTypeName contentTypeName = resolver.toContentTypeName( "bbb" );
        assertEquals( contentTypeName.getLocalName(), "bbb" );

        contentTypeName = resolver.toContentTypeName( "ccc:ddd" );
        assertEquals( contentTypeName.getLocalName(), "ddd" );
    }

    @Test
    void toContentTypeNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        assertThrows(IllegalArgumentException.class, () -> resolver.toContentTypeName( "aaa" ) );
    }

    @Test
    void toMixinName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        MixinName mixinName = resolver.toMixinName( "bbb" );
        assertEquals( mixinName.getLocalName(), "bbb" );

        mixinName = resolver.toMixinName( "ccc:ddd" );
        assertEquals( mixinName.getLocalName(), "ddd" );
    }

    @Test
    void toMixinNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        assertThrows(IllegalArgumentException.class, () -> resolver.toMixinName( "aaa" ) );
    }
}
