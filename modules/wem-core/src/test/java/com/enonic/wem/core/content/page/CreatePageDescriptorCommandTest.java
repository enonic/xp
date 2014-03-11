package com.enonic.wem.core.content.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.CreatePageDescriptorParams;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.CreateModuleResourceParams;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.form.Input.newInput;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;

public class CreatePageDescriptorCommandTest
{
    private ModuleService moduleService;

    @Before
    public void setUp()
        throws Exception
    {
        moduleService = Mockito.mock( ModuleService.class );
    }

    @Test
    public void testCreatePageDescriptor()
        throws Exception
    {
        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();
        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final ComponentDescriptorName descriptorName = new ComponentDescriptorName( "landing-page" );
        final PageDescriptorKey key = PageDescriptorKey.from( module, descriptorName );
        final CreatePageDescriptorParams params = new CreatePageDescriptorParams().
            key( key ).
            name( descriptorName ).
            regions( newRegionDescriptors().build() ).
            displayName( "Landing page" ).
            config( pageForm );

        final Resource res = Resource.newResource().build();
        Mockito.when( this.moduleService.createResource( isA( CreateModuleResourceParams.class ) ) ).thenReturn( res );

        final PageDescriptor result = new CreatePageDescriptorCommand().params( params ).moduleService( this.moduleService ).execute();

        assertEquals( key, result.getKey() );
    }
}
