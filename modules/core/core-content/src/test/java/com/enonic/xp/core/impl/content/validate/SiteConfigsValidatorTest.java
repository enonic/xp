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
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class SiteConfigsValidatorTest
{
    private static final String MIN_OCCURRENCES_I18N = "system.cms.validation.minOccurrencesInvalid.siteConfig";

    private CmsService cmsService;

    private CmsConfigsValidator validator;

    @BeforeEach
    void setUp()
    {
        cmsService = Mockito.mock( CmsService.class );
        validator = new CmsConfigsValidator( cmsService );
    }

    @Test
    void site_configs_with_missing_required_field_produces_error()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

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
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

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
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

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

    @Test
    void site_configs_with_nested_itemset_missing_required_field_produces_error_with_correct_path()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( com.enonic.xp.form.FormItemSet.create()
                              .name( "settings" )
                              .addFormItem( Input.create()
                                                .name( "nestedField" )
                                                .label( "Nested Field" )
                                                .inputType( InputTypeName.TEXT_LINE )
                                                .required( true )
                                                .build() )
                              .build() )
            .build();
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.addSet( "settings" );

        final SiteConfig siteConfig = SiteConfig.create().application( appKey ).config( config ).build();
        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), data.getRoot() );

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
        final SiteConfigValidationError scError = (SiteConfigValidationError) error;
        assertThat( scError.getPropertyPath().toString() ).isEqualTo( "settings.nestedField" );
        assertThat( scError.getApplicationKey() ).isEqualTo( appKey );
    }

    @Test
    void site_configs_with_array_field_exceeding_max_produces_error_with_index()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem(
                Input.create().name( "items" ).label( "Items" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 2 ).build() )
            .build();
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.setString( "items[0]", "first" );
        config.setString( "items[1]", "second" );
        config.setString( "items[2]", "third" );

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
        assertThat( error.getArgs() ).containsExactly( appKey.toString(), "items", 2, 3 );

        final SiteConfigValidationError scError = (SiteConfigValidationError) error;
        assertThat( scError.getPropertyPath().toString() ).isEqualTo( "items" );
    }

    @Test
    void site_configs_with_nested_array_in_itemset_produces_error_with_correct_path()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( com.enonic.xp.form.FormItemSet.create()
                              .name( "container" )
                              .addFormItem( Input.create()
                                                .name( "tags" )
                                                .label( "Tags" )
                                                .inputType( InputTypeName.TEXT_LINE )
                                                .maximumOccurrences( 2 )
                                                .build() )
                              .build() )
            .build();
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.setString( "container.tags[0]", "tag1" );
        config.setString( "container.tags[1]", "tag2" );
        config.setString( "container.tags[2]", "tag3" );

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
        final SiteConfigValidationError scError = (SiteConfigValidationError) error;
        assertThat( scError.getPropertyPath().toString() ).isEqualTo( "container.tags" );
        assertThat( scError.getApplicationKey() ).isEqualTo( appKey );
        assertThat( error.getArgs() ).containsExactly( appKey.toString(), "container.tags", 2, 3 );
    }

    @Test
    void site_configs_with_deeply_nested_structure_produces_error_with_correct_path()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp" );
        final Form form = Form.create()
            .addFormItem( com.enonic.xp.form.FormItemSet.create()
                              .name( "outer" )
                              .addFormItem( com.enonic.xp.form.FormItemSet.create()
                                                .name( "inner" )
                                                .addFormItem( Input.create()
                                                                  .name( "deepField" )
                                                                  .label( "Deep Field" )
                                                                  .inputType( InputTypeName.TEXT_LINE )
                                                                  .required( true )
                                                                  .build() )
                                                .build() )
                              .build() )
            .build();
        final CmsDescriptor siteDescriptor = CmsDescriptor.create().applicationKey( appKey ).form( form ).build();
        Mockito.when( cmsService.getDescriptor( appKey ) ).thenReturn( siteDescriptor );

        final PropertyTree config = new PropertyTree();
        config.addSet( "outer" ).addSet( "inner" );

        final SiteConfig siteConfig = SiteConfig.create().application( appKey ).config( config ).build();
        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( SiteConfigs.from( siteConfig ), data.getRoot() );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.site() ).build() )
            .data( data )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        final ValidationErrors errors = builder.build();
        assertThat( errors.hasErrors() ).isTrue();

        final SiteConfigValidationError scError = (SiteConfigValidationError) errors.stream().findFirst().orElseThrow();
        assertThat( scError.getPropertyPath().toString() ).isEqualTo( "outer.inner.deepField" );
        assertThat( scError.getApplicationKey() ).isEqualTo( appKey );
    }
}
