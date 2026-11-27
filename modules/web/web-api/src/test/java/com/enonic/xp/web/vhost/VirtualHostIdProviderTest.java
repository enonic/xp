package com.enonic.xp.web.vhost;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualHostIdProviderTest
{
    @Test
    void token_flows_are_served_only_when_listed_and_the_rest_unless_left_out()
    {
        final VirtualHostIdProvider unlisted = VirtualHostIdProvider.create().build();
        assertThat( unlisted.serves( IdProviderFlow.LOGIN ) ).isTrue();
        assertThat( unlisted.serves( "basic" ) ).isTrue();
        assertThat( unlisted.serves( IdProviderFlow.DEVICE ) ).isFalse();

        final VirtualHostIdProvider listed = VirtualHostIdProvider.create().flows( Set.of( IdProviderFlow.LOGIN, IdProviderFlow.DEVICE ) ).build();
        assertThat( listed.serves( IdProviderFlow.LOGIN ) ).isTrue();
        assertThat( listed.serves( IdProviderFlow.DEVICE ) ).isTrue();
        assertThat( listed.serves( IdProviderFlow.AUTOLOGIN ) ).isFalse();
        assertThat( listed.serves( "basic" ) ).isFalse();

        final VirtualHostIdProvider all = VirtualHostIdProvider.create().allFlows().build();
        assertThat( all.serves( IdProviderFlow.DEVICE ) ).isTrue();
        assertThat( all.serves( "basic" ) ).isTrue();
        assertThat( all.getFlows() ).as( "an app reads no restriction" ).isEmpty();
    }
}
