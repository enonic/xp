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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
    void testEmptyExtraDataWithRequiredField()
    {
        // Create XData with a required field
        final XDataName xDataName = XDataName.from( "com-enonic-app-features:all-except-folders" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create()
                              .name( "requiredField" )
                              .label( "Required Field" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .required( true )
                              .build() )
            .build();

        final XData xData = XData.create()
            .name( xDataName )
            .form( xDataForm )
            .build();

        when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        // Create empty ExtraData (PropertyTree with no properties)
        final ExtraData emptyExtraData = new ExtraData( xDataName, new PropertyTree() );

        // Create validation params
        final ContentType contentType = ContentType.create()
            .name( "myapplication:my_type" )
            .superType( ContentTypeName.structured() )
            .build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( contentType )
            .data( new PropertyTree() )
            .extraDatas( ExtraDatas.create().add( emptyExtraData ).build() )
            .build();

        // Validate
        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );
        final ValidationErrors validationErrors = validationErrorsBuilder.build();

        // Should have validation errors because required field is missing
        assertTrue( validationErrors.hasErrors(), "Empty XData with required field should produce validation errors" );
    }

    @Test
    void testEmptyExtraDataWithNoRequiredFields()
    {
        // Create XData with no required fields
        final XDataName xDataName = XDataName.from( "com-enonic-app-features:optional-data" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create()
                              .name( "optionalField" )
                              .label( "Optional Field" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .required( false )
                              .build() )
            .build();

        final XData xData = XData.create()
            .name( xDataName )
            .form( xDataForm )
            .build();

        when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        // Create empty ExtraData (PropertyTree with no properties)
        final ExtraData emptyExtraData = new ExtraData( xDataName, new PropertyTree() );

        // Create validation params
        final ContentType contentType = ContentType.create()
            .name( "myapplication:my_type" )
            .superType( ContentTypeName.structured() )
            .build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( contentType )
            .data( new PropertyTree() )
            .extraDatas( ExtraDatas.create().add( emptyExtraData ).build() )
            .build();

        // Validate
        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );
        final ValidationErrors validationErrors = validationErrorsBuilder.build();

        // Should NOT have validation errors because all fields are optional
        assertFalse( validationErrors.hasErrors(), "Empty XData with no required fields should not produce validation errors" );
    }

    @Test
    void testExtraDataWithRequiredFieldProvided()
    {
        // Create XData with a required field
        final XDataName xDataName = XDataName.from( "com-enonic-app-features:all-except-folders" );
        final Form xDataForm = Form.create()
            .addFormItem( Input.create()
                              .name( "requiredField" )
                              .label( "Required Field" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .required( true )
                              .build() )
            .build();

        final XData xData = XData.create()
            .name( xDataName )
            .form( xDataForm )
            .build();

        when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        // Create ExtraData with the required field populated
        final PropertyTree data = new PropertyTree();
        data.setString( "requiredField", "value" );
        final ExtraData extraData = new ExtraData( xDataName, data );

        // Create validation params
        final ContentType contentType = ContentType.create()
            .name( "myapplication:my_type" )
            .superType( ContentTypeName.structured() )
            .build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( contentType )
            .data( new PropertyTree() )
            .extraDatas( ExtraDatas.create().add( extraData ).build() )
            .build();

        // Validate
        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );
        final ValidationErrors validationErrors = validationErrorsBuilder.build();

        // Should NOT have validation errors because required field is provided
        assertFalse( validationErrors.hasErrors(), "XData with required field provided should not produce validation errors" );
    }
}
