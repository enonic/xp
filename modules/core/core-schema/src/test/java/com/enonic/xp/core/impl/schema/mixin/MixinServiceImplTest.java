package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractSchemaTest;
import com.enonic.xp.core.impl.schema.content.ContentTypeServiceImpl;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class MixinServiceImplTest
    extends AbstractSchemaTest
{
    protected MixinServiceImpl service;

    protected ContentTypeServiceImpl contentTypeService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new MixinServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        this.contentTypeService = new ContentTypeServiceImpl();
        this.contentTypeService.setResourceService( this.resourceService );
        this.contentTypeService.setApplicationService( this.applicationService );
        this.contentTypeService.setMixinService( this.service );
    }

    @Test
    public void testEmpty()
    {
        final Mixins types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 3, types1.getSize() );

        final Mixins types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final Mixin mixin = service.getByName( MixinName.from( "other:mytype" ) );
        assertEquals( null, mixin );
    }

    @Test
    public void testSystemMixins()
    {
        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( mixins );
        assertEquals( 2, mixins.getSize() );

        Mixin mixin = service.getByName( MediaInfo.GPS_INFO_METADATA_NAME );
        assertNotNull( mixin );

        mixin = service.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME );
        assertNotNull( mixin );

        mixin = service.getByName( MediaInfo.CAMERA_INFO_METADATA_NAME );
        assertNotNull( mixin );
    }

    @Test
    public void testGetByContentType()
    {
        initializeApps();

        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapp2:mixin1" ).
            metadata( MixinNames.from( "myapp2:mixin1", "myapp2:mixin3" ) ).
            build();

        final Mixins mixins = service.getByContentType( contentType );
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );
    }

    @Test
    public void testGetByNames()
    {
        Mixins mixins = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME ) );
        assertEquals( 1, mixins.getSize() );

        mixins = service.getByNames( MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME ) );
        assertEquals( 2, mixins.getSize() );

        mixins = service.getByNames(
            MixinNames.from( MediaInfo.GPS_INFO_METADATA_NAME, MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME ) );
        assertEquals( 3, mixins.getSize() );
    }

    @Test
    public void testApplications()
    {
        initializeApps();

        final Mixins types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 11, types1.getSize() );

        final Mixins types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 2, types2.getSize() );

        final Mixins types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 6, types3.getSize() );

        final Mixin mixin = service.getByName( MixinName.from( "myapp2:mixin1" ) );
        assertNotNull( mixin );
    }

    @Test
    public void testInlineFormItems_input()
    {
        initializeApps();

        final Form form = Form.create().
            addFormItem( Input.create().name( "my_input" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.create().mixin( "myapp2:mixin2" ).build() ).
            build();

        final Form transformedForm = service.inlineFormItems( form );

        final Input mixedInInput = transformedForm.getInput( "input1" );
        assertNotNull( mixedInInput );
        assertEquals( "input1", mixedInInput.getPath().toString() );
        assertEquals( InputTypeName.TEXT_LINE, mixedInInput.getInputType() );
        assertEquals( "myHelpText", mixedInInput.getHelpText() );
    }

    @Test
    public void testInlineFormItems_formItemSet()
    {
        initializeApps();

        final Form form = Form.create().
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.create().mixin( "myapp2:address" ).build() ).
            build();

        final Form transformedForm = service.inlineFormItems( form );

        assertEquals( "address.label", transformedForm.getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", transformedForm.getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", transformedForm.getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void testInlineFormItems_two_formItemSets_with_changed_names()
    {
        initializeApps();

        final Form form = Form.create().
            addFormItem( FormItemSet.create().
                name( "home" ).
                addFormItem( InlineMixin.create().mixin( "myapp2:address" ).build() ).
                build() ).
            addFormItem( FormItemSet.create().
                name( "cottage" ).
                addFormItem( InlineMixin.create().mixin( "myapp2:address" ).build() ).
                build() ).
            build();

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
    public void testInlineFormItems_layout()
    {
        initializeApps();

        final Form form = Form.create().
            addFormItem( InlineMixin.create().mixin( "myapp2:address2" ).build() ).
            build();

        final Form transformedForm = service.inlineFormItems( form );

        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.myFieldInLayout", transformedForm.getInput( "address.myFieldInLayout" ).getPath().toString() );
    }

    @Test
    public void testInlineMixinsWithCycles()
    {
        initializeApps();

        final Form form = Form.create().
            addFormItem( Input.create().name( "my_input" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.create().mixin( "myapp2:inline1" ).build() ).
            build();

        try
        {
            service.inlineFormItems( form );
            fail( "Expected exception due to cycle in mixins" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "Cycle detected in mixin [myapp2:inline1]. It contains an inline mixin that references itself.", e.getMessage() );
        }
    }

    @Test
    public void testInlineFormItems_formOptionSet()
    {
        initializeApps();

        final FormOptionSet optionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).
                addFormItem( InlineMixin.create().mixin( "myapp2:address" ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).
                defaultOption( true ).addFormItem(
                Input.create().name( "myTextLine2" ).label( "My text line 2" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        final Form form = Form.create().
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( optionSet ).
            build();

        final Form transformedForm = service.inlineFormItems( form );

        assertNotNull( transformedForm.getInput( "title" ) );
        assertNotNull( transformedForm.getOptionSet( "myOptionSet" ) );
        assertNotNull( transformedForm.getOptionSetOption( "myOptionSet.myOptionSetOption1" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.label" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.street" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.postalNo" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption1.address.country" ) );
        assertNotNull( transformedForm.getOptionSetOption( "myOptionSet.myOptionSetOption2" ) );
        assertNotNull( transformedForm.getInput( "myOptionSet.myOptionSetOption2.myTextLine2" ) );
    }
}
