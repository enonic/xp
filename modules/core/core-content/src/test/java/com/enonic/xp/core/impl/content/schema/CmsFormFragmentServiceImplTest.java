package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CmsFormFragmentServiceImplTest
    extends ApplicationTestSupport
{
    protected CmsFormFragmentServiceImpl service;

    @Override
    protected void initialize()
    {
        this.service = new CmsFormFragmentServiceImpl( this.resourceService );
    }

    @Test
    void testEmpty()
    {
        final FormFragmentDescriptor fragmentDescriptor = service.getByName( FormFragmentName.from( "other:mytype" ) );
        assertNull( fragmentDescriptor );
    }

    @Test
    void testApplications()
    {
        initializeApps();

        final FormFragmentDescriptor fragmentDescriptor = service.getByName( FormFragmentName.from( "myapp2:fragment1" ) );
        assertNotNull( fragmentDescriptor );
    }

    @Test
    void testInlineFormItems_input()
    {
        initializeApps();

        final Form form = Form.create()
            .addFormItem( Input.create().name( "my_input" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( FormFragment.create().formFragment( "myapp2:fragment2" ).build() )
            .build();

        final Form transformedForm = service.inlineFormItems( form );

        final Input mixedInInput = transformedForm.getInput( "input1" );
        assertNotNull( mixedInInput );
        assertEquals( "input1", mixedInInput.getPath().toString() );
        assertEquals( InputTypeName.TEXT_LINE, mixedInInput.getInputType() );
        assertEquals( "myHelpText", mixedInInput.getHelpText() );
    }

    @Test
    void testInlineFormItems_formItemSet()
    {
        initializeApps();

        final Form form = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( FormFragment.create().formFragment( "myapp2:address" ).build() )
            .build();

        final Form transformedForm = service.inlineFormItems( form );

        assertEquals( "address.label", transformedForm.getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", transformedForm.getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", transformedForm.getInput( "address.country" ).getPath().toString() );
    }

    @Test
    void testInlineFormItems_two_formItemSets_with_changed_names()
    {
        initializeApps();

        final Form form = Form.create()
            .addFormItem(
                FormItemSet.create().name( "home" ).addFormItem( FormFragment.create().formFragment( "myapp2:address" ).build() ).build() )
            .addFormItem( FormItemSet.create()
                              .name( "cottage" )
                              .addFormItem( FormFragment.create().formFragment( "myapp2:address" ).build() )
                              .build() )
            .build();

        final Form transformedForm = service.inlineFormItems( form );

        assertNotNull( transformedForm.getFormItemSet( "home" ) );
        assertNotNull( transformedForm.getFormItemSet( "cottage" ) );
        assertNotNull( transformedForm.getFormItemSet( "home.address" ) );
        assertNotNull( transformedForm.getFormItemSet( "cottage.address" ) );
        assertEquals( "home.address.street", transformedForm.getInput( "home.address.street" ).getPath().toString() );
        assertEquals( "home.address.postalNo", transformedForm.getInput( "home.address.postalNo" ).getPath().toString() );
        assertEquals( "home.address.country", transformedForm.getInput( "home.address.country" ).getPath().toString() );
        assertEquals( InputTypeName.TEXT_LINE, transformedForm.getInput( "home.address.street" ).getInputType() );
        assertEquals( "cottage.address.street", transformedForm.getInput( "cottage.address.street" ).getPath().toString() );
        assertEquals( InputTypeName.TEXT_LINE, transformedForm.getInput( "cottage.address.street" ).getInputType() );
    }

    @Test
    void testInlineFormItems_layout()
    {
        initializeApps();

        final Form form = Form.create().addFormItem( FormFragment.create().formFragment( "myapp2:address2" ).build() ).build();

        final Form transformedForm = service.inlineFormItems( form );

        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.myFieldInLayout", transformedForm.getInput( "address.myFieldInLayout" ).getPath().toString() );
    }

    @Test
    void testFormFragmentsWithCycles()
    {
        initializeApps();

        final Form form = Form.create()
            .addFormItem( Input.create().name( "my_input" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( FormFragment.create().formFragment( "myapp2:inline1" ).build() )
            .build();

        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> service.inlineFormItems( form ) );
        assertEquals( "Cycle detected in form fragment [myapp2:inline1]. It contains a form fragment that references itself.",
                      exception.getMessage() );
    }

    @Test
    void testInlineFormItems_formOptionSet()
    {
        initializeApps();

        final FormOptionSet optionSet = FormOptionSet.create()
            .name( "myOptionSet" )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption1" )
                                     .label( "option label1" )
                                     .addFormItem( FormFragment.create().formFragment( "myapp2:address" ).build() )
                                     .build() )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption2" )
                                     .label( "option label2" )
                                     .defaultOption( true )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine2" )
                                                       .label( "My text line 2" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .build();

        final Form form = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( optionSet )
            .build();

        final Form transformedForm = service.inlineFormItems( form );

        assertNotNull( transformedForm.getInput( "title" ) );
        assertNotNull( transformedForm.getOptionSet( "myOptionSet" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.label" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.street" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.postalNo" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.country" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption2.myTextLine2" ) );
    }

    private void initializeApps()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }
}
