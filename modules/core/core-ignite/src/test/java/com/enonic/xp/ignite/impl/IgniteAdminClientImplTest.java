package com.enonic.xp.ignite.impl;

import org.apache.ignite.Ignite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class IgniteAdminClientImplTest
{
    @Mock
    private Ignite ignite;

    @Test
    void getIgnite()
    {
        final IgniteAdminClientImpl igniteAdminClient = new IgniteAdminClientImpl( ignite );
        assertSame( ignite, igniteAdminClient.getIgnite() );
    }
}
