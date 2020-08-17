package com.enonic.xp.launcher.impl.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class FrameworkLifecycleActorTest
{
    @Test
    void testReset()
    {
        final FrameworkService frameworkService = mock( FrameworkService.class );
        final FrameworkLifecycleActor actor = new FrameworkLifecycleActor( frameworkService );
        actor.accept( 1 );
        verify( frameworkService ).reset();
    }

    @Test
    void testRestart()
    {
        final FrameworkService frameworkService = mock( FrameworkService.class );
        final FrameworkLifecycleActor actor = new FrameworkLifecycleActor( frameworkService );
        actor.accept( 2 );
        verify( frameworkService ).restart();
    }

    @Test
    void testInvalid()
    {
        final FrameworkService frameworkService = mock( FrameworkService.class );
        final FrameworkLifecycleActor actor = new FrameworkLifecycleActor( frameworkService );
        assertThrows( IllegalArgumentException.class, () -> actor.accept( 0 ) );
        verifyNoInteractions( frameworkService );
    }
}