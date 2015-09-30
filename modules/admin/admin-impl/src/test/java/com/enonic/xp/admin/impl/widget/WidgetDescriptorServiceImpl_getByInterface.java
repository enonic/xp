package com.enonic.xp.admin.impl.widget;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.admin.widget.WidgetDescriptors;

public class WidgetDescriptorServiceImpl_getByInterface
    extends AbstractWidgetDescriptorServiceTest
{
    @Test
    public void getByInterface()
        throws Exception
    {
        final Applications applications = createApplications( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-widget-descr", "barmodule:barmodule-widget-descr" );

        mockResources( applications.getApplication( ApplicationKey.from( "foomodule" ) ), "ui/widgets/", "*", false,
                       "/ui/widgets/foomodule-widget-descr" );
        mockResources( applications.getApplication( ApplicationKey.from( "barmodule" ) ), "ui/widgets/", "*", false,
                       "/ui/widgets/barmodule-widget-descr" );

        WidgetDescriptors result = this.service.getByInterface( "com.enonic.xp.my-interface" );
        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );

        result = this.service.getByInterface( "com.enonic.xp.unknown-interface" );
        Assert.assertNotNull( result );
        Assert.assertEquals( 0, result.getSize() );
    }
}
