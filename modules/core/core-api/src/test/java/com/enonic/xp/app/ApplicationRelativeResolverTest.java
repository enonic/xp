package com.enonic.xp.app;


import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationRelativeResolverTest
{
    @Test
    public void toContentTypeName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        ContentTypeName contentTypeName = resolver.toContentTypeName( "bbb" );
        assertEquals( contentTypeName.getLocalName(), "bbb" );

        contentTypeName = resolver.toContentTypeName( "ccc:ddd" );
        assertEquals( contentTypeName.getLocalName(), "ddd" );
    }

    @Test
    public void toContentTypeNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        assertThrows(IllegalArgumentException.class, () -> resolver.toContentTypeName( "aaa" ) );
    }

    @Test
    public void toMixinName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        MixinName mixinName = resolver.toMixinName( "bbb" );
        assertEquals( mixinName.getLocalName(), "bbb" );

        mixinName = resolver.toMixinName( "ccc:ddd" );
        assertEquals( mixinName.getLocalName(), "ddd" );
    }

    @Test
    public void toMixinNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        assertThrows(IllegalArgumentException.class, () -> resolver.toMixinName( "aaa" ) );
    }
}
