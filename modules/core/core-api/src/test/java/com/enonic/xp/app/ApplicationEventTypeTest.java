package com.enonic.xp.app;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleEvent;


public class ApplicationEventTypeTest
{
    @Test
    public void fromBundleEvent()
    {
        final BundleEvent bundleEvent = Mockito.mock( BundleEvent.class );
        Mockito.when( bundleEvent.getType() ).thenReturn( BundleEvent.INSTALLED );

        Assert.assertEquals( ApplicationEventType.fromBundleEvent( bundleEvent ), ApplicationEventType.INSTALLED );
    }
}
