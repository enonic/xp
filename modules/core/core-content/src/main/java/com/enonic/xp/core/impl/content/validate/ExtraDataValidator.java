package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

@Component
public class ExtraDataValidator
    implements ContentValidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ExtraDataValidator.class );

    private final XDataService xDataService;

    @Activate
    public ExtraDataValidator( @Reference final XDataService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( final ExtraData extraData : params.getExtraDatas() )
        {
            final XDataName name = extraData.getName();

            final XData xData = this.xDataService.getByName( name );
            if ( xData == null )
            {
                LOG.warn( "XData not found: '{}'", name );
                continue;
            }

            final Form mixinForm = xData.getForm();
            if ( extraData.getData().getRoot().getPropertySize() > 0 )
            {
                OccurrenceValidator.validate( mixinForm, extraData.getData().getRoot(), validationErrorsBuilder );
            }

            try
            {
                InputValidator.create().form( mixinForm ).inputTypeResolver( InputTypes.BUILTIN ).build().validate( extraData.getData() );
            }
            catch ( final InputTypeValidationException e )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.xDataPropertyInvalid" ),
                                               e.getPropertyPath() )
                        .i18n( "system.cms.validation.xDataPropertyInvalid" )
                        .args( name.getLocalName(), name.getApplicationKey(), e.getPropertyPath() )
                        .build() );
            }
        }
    }

}
