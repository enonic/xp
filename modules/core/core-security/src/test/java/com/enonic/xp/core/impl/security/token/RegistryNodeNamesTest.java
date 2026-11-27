package com.enonic.xp.core.impl.security.token;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeName;
import com.enonic.xp.security.token.ScopeNames;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryNodeNamesTest
{
    @Test
    void an_id_is_named_by_its_base32_form()
    {
        assertThat( RegistryNodeNames.of( "content:read" ) ).isEqualTo( "mnxw45dfnz2du4tfmfsa" );
        assertThat( RegistryNodeNames.of( "content/read" ) ).isEqualTo( "mnxw45dfnz2c64tfmfsa" );
        assertThat( RegistryNodeNames.of( "Read" ) ).isEqualTo( "kjswcza" );
        assertThat( RegistryNodeNames.of( "read" ) ).isEqualTo( "ojswcza" );
        assertThat( RegistryNodeNames.of( "https://api.example.com/calendar.read" ) )
            .isEqualTo( "nb2hi4dthixs6ylqnexgk6dbnvygyzjomnxw2l3dmfwgk3temfzc44tfmfsa" );
        assertThat( RegistryNodeNames.of( "client-é" ) ).as( "every byte of a character beyond ASCII" ).isEqualTo( "mnwgszlooqw4hki" );
        assertThat( RegistryNodeNames.of( "client-ǩ" ) ).isEqualTo( "mnwgszlooqw4pki" );
    }

    @Test
    void every_scope_id_gets_a_valid_node_name_of_its_own()
    {
        final Set<String> names = new HashSet<>();
        for ( char c = 0x21; c <= 0x7E; c++ )
        {
            final String id = "x" + c + "y";
            if ( !ScopeNames.isScope( id ) )
            {
                continue;
            }
            final String name = RegistryNodeNames.of( id );
            assertThat( NodeName.from( name ).toString() ).isEqualTo( name );
            assertThat( names.add( name.toLowerCase( java.util.Locale.ROOT ) ) ).as( "%s clashes, paths compare without case", id ).isTrue();
        }
        assertThat( names ).hasSize( 94 - 2 );
    }
}
