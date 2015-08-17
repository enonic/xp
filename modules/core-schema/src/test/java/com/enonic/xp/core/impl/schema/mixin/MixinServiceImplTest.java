package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class MixinServiceImplTest
    extends AbstractBundleTest
{

    private Bundle myBundle;

    private ApplicationKey myApplicationKey;

    private Mixin mixin1;

    private Application myApplication;

    private ApplicationService applicationService;

    private MixinServiceImpl service;

    @Before
    @Override
    public void setup()
        throws Exception
    {
        super.setup();

        //Mocks an application
        startBundles( newBundle( "application2" ) );
        myBundle = findBundle( "application2" );
        myApplicationKey = ApplicationKey.from( myBundle );
        this.mixin1 = createMixin( "application2:mixin1" );
        myApplication = Mockito.mock( Application.class );
        Mockito.when( myApplication.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myApplication.getBundle() ).thenReturn( myBundle );
        Mockito.when( myApplication.isStarted() ).thenReturn( true );

        //Mocks the application service
        applicationService = Mockito.mock( ApplicationService.class );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        service = new MixinServiceImpl();
        service.setApplicationService( applicationService );

        //Starts the service
        service.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByApplication( myApplicationKey );
        assertNotNull( mixins );
        assertEquals( 0, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertEquals( null, mixin );
    }

    @Test
    public void test_get_by_local_name()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Mixin mixin = service.getByLocalName( "mixin1" );
        assertNotNull( mixin );
        assertEquals( mixin.getName(), this.mixin1.getName() );
    }

    @Test
    public void test_get_by_content_type()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "application2:mixin1" ).
            metadata( MixinNames.from( "application2:mixin1", "application2:mixin3" ) ).
            build();

        Mixins mixins = service.getByContentType( contentType );
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );

    }

    @Test
    public void test_add_removal_application()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 7, mixins.getSize() );

        mixins = service.getByApplication( myApplicationKey );
        assertNotNull( mixins );
        assertEquals( 4, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertNotNull( mixin );

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( null );
        service.bundleChanged( new BundleEvent( BundleEvent.STOPPED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_application()
    {

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

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
    public void inlineFormItems_input()
    {
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Form form = Form.create().
            addFormItem( Input.create().name( "my_input" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.create().mixin( "application2:mixin2" ).build() ).
            build();

        // exercise
        Form transformedForm = service.inlineFormItems( form );

        // verify:
        final Input mixedInInput = transformedForm.getInput( "input1" );
        assertNotNull( mixedInInput );
        assertEquals( "input1", mixedInInput.getPath().toString() );
        assertEquals( InputTypeName.TEXT_LINE, mixedInInput.getInputType() );
        assertEquals( "myHelpText", mixedInInput.getHelpText() );
    }

    @Test
    public void inlineFormItems_formItemSet()
    {
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Form form = Form.create().
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.create().mixin( "application2:address" ).build() ).
            build();

        // exercise
        Form transformedForm = service.inlineFormItems( form );

        // verify:
        assertEquals( "address.label", transformedForm.getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", transformedForm.getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", transformedForm.getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void inlineFormItems_two_formItemSets_with_changed_names()
    {
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Form form = Form.create().
            addFormItem( FormItemSet.create().
                name( "home" ).
                addFormItem( InlineMixin.create().mixin( "application2:address" ).build() ).
                build() ).
            addFormItem( FormItemSet.create().
                name( "cottage" ).
                addFormItem( InlineMixin.create().mixin( "application2:address" ).build() ).
                build() ).
            build();

        // exercise
        Form transformedForm = service.inlineFormItems( form );

        // verify
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
    public void inlineFormItems_layout()
    {
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Form form = Form.create().
            addFormItem( InlineMixin.create().mixin( "application2:address2" ).build() ).
            build();

        // exercise
        Form transformedForm = service.inlineFormItems( form );

        // verify:
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.myFieldInLayout", transformedForm.getInput( "address.myFieldInLayout" ).getPath().toString() );
    }


    @Test
    public void test_stop()
    {
        service.stop();
    }

    private Mixin createMixin( final String name )
    {
        return Mixin.create().name( name ).build();
    }

}
