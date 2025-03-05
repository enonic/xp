package com.enonic.xp.impl.shared;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.map.IMap;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HazelcastSharedMapTest
{
    @Mock
    IMap<Object, Object> map;

    @Test
    void get()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.get( "key" );
        verify( map ).get( "key" );
    }

    @Test
    void delete()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.delete( "key" );
        verify( map ).delete( "key" );
    }

    @Test
    void set()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.set( "key", "value" );
        verify( map ).set( "key", "value", -1, TimeUnit.SECONDS );
    }

    @Test
    void setNull()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.set( "key", null );
        verify( map ).delete( "key" );
    }

    @Test
    void setWithTtl()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.set( "key", "value", 11 );
        verify( map ).set( "key", "value", 11, TimeUnit.SECONDS );
    }

    @Test
    void modify()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.modify( "key", v -> "value" );
        final InOrder inOrder = inOrder( map );
        inOrder.verify( map ).lock( "key" );
        inOrder.verify( map ).get( "key" );
        inOrder.verify( map ).set( "key", "value", -1, TimeUnit.SECONDS );
        inOrder.verify( map ).unlock( "key" );
    }

    @Test
    void modify_delete()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.modify( "key", v -> null );
        map.delete( "key" );
    }

    @Test
    void modifyWithTtl()
    {
        final HazelcastSharedMap<Object, Object> sharedMap = new HazelcastSharedMap<>( map );
        sharedMap.modify( "key", v -> "value", 11 );

        verify( map ).set( "key", "value", 11, TimeUnit.SECONDS );
    }
}
