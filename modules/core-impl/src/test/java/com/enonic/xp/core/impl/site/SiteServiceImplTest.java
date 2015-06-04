package com.enonic.xp.core.impl.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.Form;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.Assert.*;

public class SiteServiceImplTest
{
    private static final String MODULE_NAME = "mymodule";

    private static final String UNKNOWN_MODULE_NAME = "unknownmodule";


    private ModuleKey moduleKey;

    private Form form;

    private MixinNames metaSteps;

    private SiteServiceImpl siteServiceImpl;

    @Before
    public void before()
    {
        moduleKey = ModuleKey.from( MODULE_NAME );

        //Creates SiteDescriptor sub objects
        form = Form.newForm().
            build();
        metaSteps = MixinNames.empty();

        //Creates a mocked Module
        Module module = Mockito.mock( Module.class );
        Mockito.when( module.getConfig() ).thenReturn( form );
        Mockito.when( module.getMetaSteps() ).thenReturn( metaSteps );

        //Creates a mocked ModuleService
        ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( moduleKey ) ).thenReturn( module );

        //Creates the service to test
        siteServiceImpl = new SiteServiceImpl();
        siteServiceImpl.setModuleService( moduleService );
    }

    @Test
    public void get_descriptor()
    {
        final SiteDescriptor siteDescriptor = siteServiceImpl.getDescriptor( moduleKey );
        assertEquals( form, siteDescriptor.getForm() );
        assertEquals( metaSteps, siteDescriptor.getMetaSteps() );
    }

    @Test
    public void get_descriptor_for_unknown_module()
    {
        final SiteDescriptor siteDescriptor = siteServiceImpl.getDescriptor( ModuleKey.from( UNKNOWN_MODULE_NAME ) );
        assertEquals( null, siteDescriptor );
    }
}
