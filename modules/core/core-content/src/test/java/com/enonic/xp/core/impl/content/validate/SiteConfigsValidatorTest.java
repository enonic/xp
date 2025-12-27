package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteConfigsValidatorTest
{
    private SiteService siteService;

    private SiteConfigsValidator validator;

    @BeforeEach
    void setUp()
    {
        this.siteService = Mockito.mock( SiteService.class );
        this.validator = new SiteConfigsValidator( siteService );
    }

    @Test
    void site_config_with_valid_config_passes_validation()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final Form siteForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().form( siteForm ).build();

        Mockito.when( siteService.getDescriptor( applicationKey ) ).thenReturn( siteDescriptor );

        final PropertyTree data = new PropertyTree();
        data.addSet( "siteConfig" ).addString( "applicationKey", "myapp" );
        data.getSet( "siteConfig" ).addSet( "config" ).addString( "title", "My Site Title" );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void site_config_with_missing_required_config_fails_validation()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final Form siteForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().form( siteForm ).build();

        Mockito.when( siteService.getDescriptor( applicationKey ) ).thenReturn( siteDescriptor );

        final PropertyTree data = new PropertyTree();
        data.addSet( "siteConfig" ).addString( "applicationKey", "myapp" );
        data.getSet( "siteConfig" ).addSet( "config" );
        // Missing required 'title' field

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
    }

    @Test
    void site_config_with_invalid_type_triggers_input_type_validation_exception()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final Form siteForm = Form.create()
            .addFormItem( Input.create().name( "number" ).label( "Number" ).inputType( InputTypeName.LONG ).build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().form( siteForm ).build();

        Mockito.when( siteService.getDescriptor( applicationKey ) ).thenReturn( siteDescriptor );

        final PropertyTree data = new PropertyTree();
        data.addSet( "siteConfig" ).addString( "applicationKey", "myapp" );
        // Add invalid value that will cause InputTypeValidationException
        data.getSet( "siteConfig" ).addSet( "config" ).addString( "number", "not-a-number" );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSizeGreaterThanOrEqualTo( 1 );
    }

    @Test
    void site_descriptor_not_found_skips_validation()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        Mockito.when( siteService.getDescriptor( applicationKey ) ).thenReturn( null );

        final PropertyTree data = new PropertyTree();
        data.addSet( "siteConfig" ).addString( "applicationKey", "myapp" );
        data.getSet( "siteConfig" ).addSet( "config" ).addString( "title", "My Site Title" );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }
}
