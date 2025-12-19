package com.enonic.xp.core.impl.content.serializer;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.ComponentConfigValidationError;
import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.MixinConfigValidationError;
import com.enonic.xp.content.SiteConfigValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorsSerializerTest
{
    private static final ValidationErrorCode ERROR_CODE = ValidationErrorCode.from( ApplicationKey.SYSTEM, "test" );

    private final ValidationErrorsSerializer serializer = new ValidationErrorsSerializer();

    @Test
    void toDataRemovesPropertyWhenNoErrors()
    {
        final PropertyTree tree = new PropertyTree();
        tree.addSet( "validationErrors" );

        serializer.toData( ValidationErrors.create().build(), tree.getRoot() );

        assertThat( tree.getRoot().hasProperty( "validationErrors" ) ).isFalse();
    }

    @Test
    void roundTripDataValidationError()
    {
        final ValidationError error = ValidationError.dataError( ERROR_CODE, PropertyPath.from( "field" ) )
            .message( "message", true )
            .i18n( "i18n.key" )
            .args( "arg", 42 )
            .build();

        final ValidationError result = roundTrip( error );

        assertThat( result ).isInstanceOf( DataValidationError.class );
        assertThat( ( (DataValidationError) result ).getPropertyPath().toString() ).isEqualTo( "field" );
        assertThat( result.getArgs() ).containsExactly( "arg", 42 );
    }

    @Test
    void roundTripSiteConfigValidationError()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        final ValidationError error = ValidationError.siteConfigError( ERROR_CODE, PropertyPath.from( "config" ), applicationKey )
            .message( "message", true )
            .i18n( "i18n.site" )
            .args( 1 )
            .build();

        final ValidationError result = roundTrip( error );

        assertThat( result ).isInstanceOf( SiteConfigValidationError.class );
        final SiteConfigValidationError siteError = (SiteConfigValidationError) result;
        assertThat( siteError.getPropertyPath().toString() ).isEqualTo( "config" );
        assertThat( siteError.getApplicationKey() ).isEqualTo( applicationKey );
        assertThat( siteError.getArgs() ).containsExactly( 1 );
    }

    @Test
    void roundTripComponentConfigValidationError()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app2" );
        final ComponentPath componentPath = ComponentPath.from( "/region/0" );
        final ValidationError error =
            ValidationError.componentConfigError( ERROR_CODE, PropertyPath.from( "component" ), applicationKey, componentPath )
                .message( "message", true )
                .i18n( "i18n.component" )
                .args( "component" )
                .build();

        final ValidationError result = roundTrip( error );

        assertThat( result ).isInstanceOf( ComponentConfigValidationError.class );
        final ComponentConfigValidationError componentError = (ComponentConfigValidationError) result;
        assertThat( componentError.getApplicationKey() ).isEqualTo( applicationKey );
        assertThat( componentError.getComponentPath().toString() ).isEqualTo( componentPath.toString() );
        assertThat( componentError.getPropertyPath().toString() ).isEqualTo( "component" );
        assertThat( componentError.getArgs() ).containsExactly( "component" );
    }

    @Test
    void roundTripMixinConfigValidationError()
    {
        final XDataName mixinName = XDataName.from( ApplicationKey.from( "app3" ), "mixin" );
        final ValidationError error = ValidationError.mixinConfigError( ERROR_CODE, PropertyPath.from( "mixinPath" ), mixinName )
            .message( "message", true )
            .i18n( "i18n.mixin" )
            .args( "value" )
            .build();

        final ValidationError result = roundTrip( error );

        assertThat( result ).isInstanceOf( MixinConfigValidationError.class );
        final MixinConfigValidationError mixinError = (MixinConfigValidationError) result;
        assertThat( mixinError.getMixinName() ).isEqualTo( mixinName );
        assertThat( mixinError.getPropertyPath().toString() ).isEqualTo( "mixinPath" );
        assertThat( mixinError.getArgs() ).containsExactly( "value" );
    }

    @Test
    void roundTripAttachmentValidationError()
    {
        final BinaryReference attachment = BinaryReference.from( "binary" );
        final ValidationError error = ValidationError.attachmentError( ERROR_CODE, attachment )
            .message( "message", true )
            .i18n( "i18n.attachment" )
            .args( "att" )
            .build();

        final ValidationError result = roundTrip( error );

        assertThat( result ).isInstanceOf( AttachmentValidationError.class );
        assertThat( ( (AttachmentValidationError) result ).getAttachment() ).isEqualTo( attachment );
        assertThat( result.getArgs() ).containsExactly( "att" );
    }

    @Test
    void roundTripGeneralValidationError()
    {
        final ValidationError error =
            ValidationError.generalError( ERROR_CODE ).message( "message", true ).i18n( "i18n.general" ).args( "value" ).build();

        final ValidationError result = roundTrip( error );

        assertThat( result.getClass() ).isEqualTo( ValidationError.class );
        assertThat( result.getArgs() ).containsExactly( "value" );
    }

    private ValidationError roundTrip( final ValidationError error )
    {
        final ValidationErrors errors = ValidationErrors.create().add( error ).build();
        final PropertyTree tree = new PropertyTree();
        serializer.toData( errors, tree.getRoot() );
        return serializer.fromData( tree.getRoot() ).stream().findFirst().orElseThrow();
    }
}
