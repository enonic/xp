package com.enonic.xp.core.impl.content.validate;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.MixinConfigValidationError;
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
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

import static org.assertj.core.api.Assertions.assertThat;

class ExtraDataValidatorTest
{
    private static final String MIN_OCCURRENCES_I18N = "system.cms.validation.minOccurrencesInvalid.mixin";

    private XDataService xDataService;

    private ExtraDataValidator validator;

    @BeforeEach
    void setUp()
    {
        xDataService = Mockito.mock( XDataService.class );
        validator = new ExtraDataValidator( xDataService );
    }

    @Test
    void extra_data_without_form_data_is_skipped()
    {
        final XDataName mixinName = XDataName.from( ApplicationKey.from( "app1" ), "myMixin" );
        final XData xData = Mockito.mock( XData.class );
        Mockito.when( xData.getForm() ).thenReturn( Form.create().build() );
        Mockito.when( xDataService.getByName( mixinName ) ).thenReturn( xData );

        final ExtraData extraData = new ExtraData( mixinName, new PropertyTree() );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( ExtraDatas.from( List.of( extraData ) ) )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();

        validator.validate( params, builder );

        assertThat( builder.build().hasErrors() ).isFalse();
    }

    @Test
    void missing_required_field_in_mixin_produces_error()
    {
        final XDataName mixinName = XDataName.from( ApplicationKey.from( "app1" ), "myMixin" );
        final Form form = Form.create()
            .addFormItem(
                Input.create().name( "headline" ).label( "Headline" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final XData xData = Mockito.mock( XData.class );
        Mockito.when( xData.getForm() ).thenReturn( form );
        Mockito.when( xDataService.getByName( mixinName ) ).thenReturn( xData );

        final PropertyTree data = new PropertyTree();
        final ExtraData extraData = new ExtraData( mixinName, data );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( ExtraDatas.from( List.of( extraData ) ) )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();

        validator.validate( params, builder );

        final ValidationErrors errors = builder.build();
        assertThat( errors.hasErrors() ).isTrue();
        assertThat( errors.stream() ).hasSize( 1 );

        final ValidationError error = errors.stream().findFirst().orElseThrow();
        assertThat( error ).isInstanceOf( MixinConfigValidationError.class );
        assertThat( error.getI18n() ).isEqualTo( MIN_OCCURRENCES_I18N );
        assertThat( error.getArgs() ).containsExactly( mixinName.toString(), "headline", 1, 0 );

        final MixinConfigValidationError mixinError = (MixinConfigValidationError) error;
        assertThat( mixinError.getMixinName() ).isEqualTo( mixinName );
        assertThat( mixinError.getPropertyPath().toString() ).isEqualTo( "headline" );
    }

    @Test
    void exceeding_maximum_occurrence_in_mixin_produces_error()
    {
        final XDataName mixinName = XDataName.from( ApplicationKey.from( "app1" ), "myMixin" );
        final Form form = Form.create()
            .addFormItem(
                Input.create().name( "summary" ).label( "Summary" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() )
            .build();
        final XData xData = Mockito.mock( XData.class );
        Mockito.when( xData.getForm() ).thenReturn( form );
        Mockito.when( xDataService.getByName( mixinName ) ).thenReturn( xData );

        final PropertyTree data = new PropertyTree();
        data.setString( "summary[0]", "first" );
        data.setString( "summary[1]", "second" );
        final ExtraData extraData = new ExtraData( mixinName, data );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( ExtraDatas.from( List.of( extraData ) ) )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        final ValidationErrors errors = builder.build();
        assertThat( errors.stream() ).hasSize( 1 );
        final ValidationError error = errors.stream().findFirst().orElseThrow();
        assertThat( error.getI18n() ).isEqualTo( "system.cms.validation.maxOccurrencesInvalid.mixin" );
        assertThat( error.getArgs() ).containsExactly( mixinName.toString(), "summary", 1, 2 );
    }

    @Test
    void option_set_selection_violation_in_mixin_produces_error()
    {
        final XDataName mixinName = XDataName.from( ApplicationKey.from( "app1" ), "myMixin" );
        final Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "colors" )
                              .multiselection( Occurrences.create( 1, 1 ) )
                              .addOptionSetOption( FormOptionSetOption.create().name( "red" ).build() )
                              .addOptionSetOption( FormOptionSetOption.create().name( "blue" ).build() )
                              .build() )
            .build();
        final XData xData = Mockito.mock( XData.class );
        Mockito.when( xData.getForm() ).thenReturn( form );
        Mockito.when( xDataService.getByName( mixinName ) ).thenReturn( xData );

        final PropertyTree data = new PropertyTree();
        data.getRoot().addSet( "colors" ).addString( "_selected", "red" );
        data.getRoot().getSet( "colors" ).addString( "_selected", "blue" );
        final ExtraData extraData = new ExtraData( mixinName, data );

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( ExtraDatas.from( List.of( extraData ) ) )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        final ValidationErrors errors = builder.build();
        assertThat( errors.stream() ).hasSize( 1 );
        final ValidationError error = errors.stream().findFirst().orElseThrow();
        assertThat( error.getI18n() ).isEqualTo( "system.cms.validation.optionsetOccurrencesInvalid.mixin" );
        assertThat( error.getArgs() ).containsExactly( mixinName.toString(), "colors", 1, 1, 2 );
    }
}
