package com.enonic.xp.core.impl.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.media.MediaTypeProvider;
import com.enonic.xp.util.MediaTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaTypeProviderRegisterTest
{
    private MediaTypeProviderRegister register;

    @BeforeEach
    void setup()
    {
        this.register = new MediaTypeProviderRegister();
    }

    @Test
    void testAddRemove()
    {
        final MediaTypeProvider provider = Mockito.mock( MediaTypeProvider.class );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );

        this.register.addProvider( provider );
        assertEquals( 1, Lists.newArrayList( MediaTypes.instance() ).size() );

        this.register.removeProvider( provider );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );
    }
}
