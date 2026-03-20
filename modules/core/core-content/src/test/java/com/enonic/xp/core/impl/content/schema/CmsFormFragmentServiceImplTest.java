package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.schema.formfragment.FormFragmentName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CmsFormFragmentServiceImplTest
    extends ApplicationTestSupport
{
    private CmsFormFragmentServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp2", "/apps/myapp2" );
        this.service = new CmsFormFragmentServiceImpl( this.resourceService );
    }

    @Test
    void inlineFormItemsThrowsOnCycleStartingFromInline1()
    {
        final Form form = Form.create()
            .addFormItem( FormFragment.create()
                .formFragment( FormFragmentName.from( "myapp2:inline1" ) )
                .build() )
            .build();

        assertThrows( IllegalArgumentException.class, () -> service.inlineFormItems( form ) );
    }

    @Test
    void inlineFormItemsThrowsOnCycleStartingFromInline2()
    {
        final Form form = Form.create()
            .addFormItem( FormFragment.create()
                .formFragment( FormFragmentName.from( "myapp2:inline2" ) )
                .build() )
            .build();

        assertThrows( IllegalArgumentException.class, () -> service.inlineFormItems( form ) );
    }

    @Test
    void inlineFormItems()
    {
        final Form form = Form.create().addFormItem( FormFragment.create().formFragment( FormFragmentName.from( "myapp2:inline3" ) ).build() ).build();

        final Form inlinedForm = service.inlineFormItems( form );

        assertNotNull( inlinedForm.getInput( "text3" ) );
        assertNotNull( inlinedForm.getInput( "text4" ) );
    }
}
