package com.enonic.xp.core.impl.security.token;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.security.InMemorySharedMapService;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static org.assertj.core.api.Assertions.assertThat;

class SharedMapTokenSigningKeysTest
{
    private final SharedMapService sharedMapService = new InMemorySharedMapService();

    @Test
    void generates_once_and_reuses()
    {
        final SharedMapTokenSigningKeys keys = new SharedMapTokenSigningKeys( sharedMapService );
        final SigningKey first = keys.signingKey();
        final SigningKey second = keys.signingKey();

        assertThat( second ).isEqualTo( first );
        assertThat( first.kid() ).startsWith( "g" );
        assertThat( first.secret() ).hasSize( 64 );
        assertThat( keys.verificationKey( first.kid() ) ).contains( first );
        assertThat( keys.verificationKey( "unknown" ) ).isEmpty();
        assertThat( keys.verificationKey( SharedMapTokenSigningKeys.SIGNING_ENTRY ) ).isEmpty();
    }

    @Test
    void other_node_sees_the_same_key()
    {
        final SharedMapTokenSigningKeys nodeA = new SharedMapTokenSigningKeys( sharedMapService );
        final SigningKey key = nodeA.signingKey();

        final SharedMapTokenSigningKeys nodeB = new SharedMapTokenSigningKeys( sharedMapService );
        assertThat( nodeB.signingKey() ).isEqualTo( key );
        assertThat( nodeB.verificationKey( key.kid() ) ).contains( key );
    }

    @Test
    void stale_signing_entry_is_replaced()
    {
        final SharedMap<String, String> map = sharedMapService.getSharedMap( SharedMapTokenSigningKeys.MAP_NAME );
        map.set( SharedMapTokenSigningKeys.SIGNING_ENTRY, "gone" );

        final SharedMapTokenSigningKeys keys = new SharedMapTokenSigningKeys( sharedMapService );
        final SigningKey key = keys.signingKey();
        assertThat( key.kid() ).isNotEqualTo( "gone" );
        assertThat( map.get( SharedMapTokenSigningKeys.SIGNING_ENTRY ) ).isEqualTo( key.kid() );
    }

    @Test
    void loser_of_the_race_discards_its_key()
    {
        final SharedMap<String, String> map = sharedMapService.getSharedMap( SharedMapTokenSigningKeys.MAP_NAME );
        final SharedMapTokenSigningKeys otherNode = new SharedMapTokenSigningKeys( sharedMapService );
        final SharedMapService racing = new SharedMapService()
        {
            @Override
            public <K, V> SharedMap<K, V> getSharedMap( final String name )
            {
                return new RacingSharedMap<>( sharedMapService.getSharedMap( name ), otherNode );
            }
        };

        final SharedMapTokenSigningKeys keys = new SharedMapTokenSigningKeys( racing );
        final SigningKey key = keys.signingKey();

        assertThat( key ).isEqualTo( otherNode.signingKey() );
        assertThat( map.get( SharedMapTokenSigningKeys.SIGNING_ENTRY ) ).isEqualTo( key.kid() );
        assertThat( map.get( key.kid() ) ).isNotNull();
    }

    /**
     * Delegates to the real map, but lets another node win the signing entry first.
     */
    private static final class RacingSharedMap<K, V>
        implements SharedMap<K, V>
    {
        private final SharedMap<K, V> delegate;

        private final SharedMapTokenSigningKeys otherNode;

        private RacingSharedMap( final SharedMap<K, V> delegate, final SharedMapTokenSigningKeys otherNode )
        {
            this.delegate = delegate;
            this.otherNode = otherNode;
        }

        @Override
        public V get( final K key )
        {
            return delegate.get( key );
        }

        @Override
        public void delete( final K key )
        {
            delegate.delete( key );
        }

        @Override
        public void set( final K key, final V value )
        {
            delegate.set( key, value );
        }

        @Override
        public void set( final K key, final V value, final int ttlSeconds )
        {
            delegate.set( key, value, ttlSeconds );
        }

        @Override
        public V modify( final K key, final java.util.function.Function<V, V> modifier )
        {
            if ( SharedMapTokenSigningKeys.SIGNING_ENTRY.equals( key ) )
            {
                otherNode.signingKey();
            }
            return delegate.modify( key, modifier );
        }

        @Override
        public V modify( final K key, final java.util.function.Function<V, V> modifier, final int ttlSeconds )
        {
            return delegate.modify( key, modifier, ttlSeconds );
        }
    }
}
