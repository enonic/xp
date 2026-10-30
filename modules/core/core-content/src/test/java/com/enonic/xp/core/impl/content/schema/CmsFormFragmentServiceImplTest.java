package com.enonic.xp.core.impl.content.schema;

import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.schema.formfragment.FormFragmentName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // two fragments, each valid, that both contribute an item of the same name: the expanded form is invalid, and that is reported
    @Test
    void inlineFormItemsReportsInvalidExpandedForm()
    {
        final Form form = Form.create()
            .addFormItem( FormFragment.create().formFragment( FormFragmentName.from( "myapp2:address" ) ).build() )
            .addFormItem( FormFragment.create().formFragment( FormFragmentName.from( "myapp2:address2" ) ).build() )
            .build();

        final IllegalArgumentException e = assertThrows( IllegalArgumentException.class, () -> service.inlineFormItems( form ) );
        assertTrue( e.getMessage().contains( "Multiple entries with same key: address=address" ), e.getMessage() );
    }

    @Test
    void invalidInlineFormItems()
    {
        final Form form = Form.create()
            .addFormItem( FormFragment.create().formFragment( FormFragmentName.from( "myapp2:invalid-fragment" ) ).build() )
            .build();

        final UncheckedIOException ex = assertThrows( UncheckedIOException.class, () -> service.inlineFormItems( form ) );
        assertTrue( ex.getMessage().contains( "Form fragment name must not contain ':'" ) );
    }
}
