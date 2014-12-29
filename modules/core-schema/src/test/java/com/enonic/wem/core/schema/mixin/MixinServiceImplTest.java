package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class MixinServiceImplTest
{
    private MixinRegistry registry;

    private MixinServiceImpl service;

    @Before
    public void setup()
    {
        this.registry = Mockito.mock( MixinRegistry.class );

        this.service = new MixinServiceImpl();
        this.service.setRegistry( this.registry );
    }

    @Test
    public void testEmpty()
    {
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList() );

        final Mixins result = this.service.getAll();
        assertNotNull( result );
        assertEquals( 0, result.getSize() );
    }

    @Test
    public void testGetByName()
    {
        final Mixin mixin = createMixin( "mymodule:test" );
        Mockito.when( this.registry.get( mixin.getName() ) ).thenReturn( mixin );

        final Mixin result = this.service.getByName( mixin.getName() );
        assertNotNull( result );
    }

    @Test
    public void testGetAll()
    {
        final Mixin mixin = createMixin( "mymodule:test" );
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList( mixin ) );

        final Mixins result = this.service.getAll();
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( mixin, result.get( 0 ) );
    }

    @Test
    public void testGetByModule()
    {
        final Mixin mixin1 = createMixin( "mymodule:test" );
        final Mixin mixin2 = createMixin( "othermodule:test" );
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList( mixin1, mixin2 ) );

        final Mixins result = this.service.getByModule( ModuleKey.from( "mymodule" ) );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( mixin1, result.get( 0 ) );
    }

    private Mixin createMixin( final String name )
    {
        return Mixin.newMixin().name( name ).build();
    }
}
