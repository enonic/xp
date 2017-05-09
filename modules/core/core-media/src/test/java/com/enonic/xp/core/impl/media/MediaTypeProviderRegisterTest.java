package com.enonic.xp.core.impl.media;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.media.MediaTypeProvider;
import com.enonic.xp.util.MediaTypes;

import static org.junit.Assert.*;

public class MediaTypeProviderRegisterTest
{
    private MediaTypeProviderRegister register;

    @Before
    public void setup()
    {
        this.register = new MediaTypeProviderRegister();
    }

    @Test
    public void testAddRemove()
    {
        final MediaTypeProvider provider = Mockito.mock( MediaTypeProvider.class );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );

        this.register.addProvider( provider );
        assertEquals( 1, Lists.newArrayList( MediaTypes.instance() ).size() );

        this.register.removeProvider( provider );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );
    }
}
