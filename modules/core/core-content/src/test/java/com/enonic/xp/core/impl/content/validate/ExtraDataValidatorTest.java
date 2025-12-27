package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtraDataValidatorTest
{
    private XDataService xDataService;

    private ExtraDataValidator validator;

    @BeforeEach
    void setUp()
    {
        this.xDataService = Mockito.mock( XDataService.class );
        this.validator = new ExtraDataValidator( xDataService );
    }

    @Test
    void extradata_with_valid_config_passes_validation()
    {
        final XDataName xDataName = XDataName.from( "myapp:metadata" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        final XData xData = XData.create().name( xDataName ).form( xDataForm ).build();

        Mockito.when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        final PropertyTree extraDataConfig = new PropertyTree();
        extraDataConfig.addString( "title", "My Title" );

        final ExtraData extraData = new ExtraData( xDataName, extraDataConfig );
        final ExtraDatas extraDatas = ExtraDatas.create().add( extraData ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( extraDatas )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void extradata_with_missing_required_config_fails_validation()
    {
        final XDataName xDataName = XDataName.from( "myapp:metadata" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .addFormItem( Input.create().name( "description" ).label( "Description" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        final XData xData = XData.create().name( xDataName ).form( xDataForm ).build();

        Mockito.when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        final PropertyTree extraDataConfig = new PropertyTree();
        // Add optional field but missing required 'title' field
        extraDataConfig.addString( "description", "Some description" );

        final ExtraData extraData = new ExtraData( xDataName, extraDataConfig );
        final ExtraDatas extraDatas = ExtraDatas.create().add( extraData ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( extraDatas )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
    }

    @Test
    void extradata_with_invalid_type_triggers_input_type_validation_exception()
    {
        final XDataName xDataName = XDataName.from( "myapp:metadata" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create().name( "number" ).label( "Number" ).inputType( InputTypeName.LONG ).build() )
            .build();
        final XData xData = XData.create().name( xDataName ).form( xDataForm ).build();

        Mockito.when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        final PropertyTree extraDataConfig = new PropertyTree();
        // Add invalid value that will cause InputTypeValidationException
        extraDataConfig.addString( "number", "not-a-number" );

        final ExtraData extraData = new ExtraData( xDataName, extraDataConfig );
        final ExtraDatas extraDatas = ExtraDatas.create().add( extraData ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( extraDatas )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSizeGreaterThanOrEqualTo( 1 );
    }

    @Test
    void extradata_not_found_skips_validation()
    {
        final XDataName xDataName = XDataName.from( "myapp:metadata" );

        Mockito.when( xDataService.getByName( xDataName ) ).thenReturn( null );

        final PropertyTree extraDataConfig = new PropertyTree();
        extraDataConfig.addString( "title", "My Title" );

        final ExtraData extraData = new ExtraData( xDataName, extraDataConfig );
        final ExtraDatas extraDatas = ExtraDatas.create().add( extraData ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .extraDatas( extraDatas )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }
}
