package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeManualOrderValueResolverTest
{
    @Test
    void resolve()
    {
        double previousValue = NodeManualOrderValueResolver.first() + 1;
        final NodeManualOrderValueResolver resolver = new NodeManualOrderValueResolver();
        for ( int i = 0; i < 10; i++ )
        {
            final long manualOrderValue = resolver.getAsLong();

            assertTrue( previousValue > manualOrderValue );
            previousValue = manualOrderValue;
        }
    }

    @Test
    void between() {
        assertEquals( 4611686018427387905L, NodeManualOrderValueResolver.between( 3L, Long.MAX_VALUE ));

        assertEquals( 0, NodeManualOrderValueResolver.after( NodeManualOrderValueResolver.first() ),
                      NodeManualOrderValueResolver.before( NodeManualOrderValueResolver.first() ) );
    }
}
