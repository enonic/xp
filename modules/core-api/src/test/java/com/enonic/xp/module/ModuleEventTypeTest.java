package com.enonic.xp.module;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleEvent;

import static org.junit.Assert.*;

public class ModuleEventTypeTest
{
    @Test
    public void fromBundleEvent()
    {
        final BundleEvent bundleEvent = Mockito.mock( BundleEvent.class );
        Mockito.when( bundleEvent.getType() ).thenReturn( BundleEvent.INSTALLED );

        assertEquals( ModuleEventType.fromBundleEvent( bundleEvent ), ModuleEventType.INSTALLED );
    }
}
