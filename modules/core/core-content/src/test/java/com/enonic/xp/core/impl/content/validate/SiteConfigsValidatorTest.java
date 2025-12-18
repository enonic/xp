package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.SiteConfigValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.assertj.core.api.Assertions.assertThat;

class SiteConfigsValidatorTest
{
    private static final String MIN_OCCURRENCES_I18N = "system.cms.validation.minOccurrencesInvalid.siteConfig";

    private SiteService siteService;

    private SiteConfigsValidator validator;

    @BeforeEach
    void setUp()
    {
        siteService = Mockito.mock( SiteService.class );
        validator = new SiteConfigsValidator( siteService );
    }

    @Test
    void site_configs_with_missing_required_field_produces_error()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( siteService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree siteConfigTree = new PropertyTree();
        final SiteConfig siteConfig = SiteConfig.create().application( appKey ).config( siteConfigTree ).build();
        final SiteConfigs siteConfigs = SiteConfigs.from( siteConfig );
        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( siteConfigs, data.getRoot() );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();

        validator.validate( params, builder );

        final ValidationErrors errors = builder.build();
        assertThat( errors.hasErrors() ).isTrue();
        assertThat( errors.stream() ).hasSize( 1 );

        final ValidationError error = errors.stream().findFirst().orElseThrow();
        assertThat( error ).isInstanceOf( SiteConfigValidationError.class );
        assertThat( error.getI18n() ).isEqualTo( MIN_OCCURRENCES_I18N );
        assertThat( error.getArgs() ).containsExactly( appKey.toString(), "title", 1, 0 );

        final SiteConfigValidationError scError = (SiteConfigValidationError) error;
        assertThat( scError.getApplicationKey() ).isEqualTo( appKey );
        assertThat( scError.getPropertyPath().toString() ).isEqualTo( "title" );
    }

    @Test
    void site_configs_with_exceeding_max_occurrence_produces_error()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem(
                Input.create().name( "tagline" ).label( "Tagline" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( siteService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.setString( "tagline[0]", "first" );
        config.setString( "tagline[1]", "second" );
        final SiteConfig siteConfig = SiteConfig.create().application( appKey ).config( config ).build();
        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), data.getRoot() );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        final ValidationError error = builder.build().stream().findFirst().orElseThrow();
        assertThat( error.getI18n() ).isEqualTo( "system.cms.validation.maxOccurrencesInvalid.siteConfig" );
        assertThat( error.getArgs() ).containsExactly( appKey.toString(), "tagline", 1, 2 );
    }

    @Test
    void site_configs_option_set_selection_violation_produces_error()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "channels" )
                              .multiselection( Occurrences.create( 1, 1 ) )
                              .addOptionSetOption( FormOptionSetOption.create().name( "web" ).build() )
                              .addOptionSetOption( FormOptionSetOption.create().name( "mobile" ).build() )
                              .build() )
            .build();
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( siteService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.getRoot().addSet( "channels" ).addString( "_selected", "web" );
        config.getRoot().getSet( "channels" ).addString( "_selected", "mobile" );
        final SiteConfig siteConfig = SiteConfig.create().application( appKey ).config( config ).build();
        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), data.getRoot() );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        final ValidationError error = builder.build().stream().findFirst().orElseThrow();
        assertThat( error.getI18n() ).isEqualTo( "system.cms.validation.optionsetOccurrencesInvalid.siteConfig" );
        assertThat( error.getArgs() ).containsExactly( appKey.toString(), "channels", 1, 1, 2 );
    }
}
