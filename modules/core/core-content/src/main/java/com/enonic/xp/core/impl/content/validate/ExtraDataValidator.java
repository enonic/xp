package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.form.Form;
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
                LOG.warn( "X-Data not found: '" + name );
                continue;
            }

            final Form xDataForm = xData.getForm();
            if ( extraData.getData().getRoot().getPropertySize() > 0 )
            {
                OccurrenceValidator.validate( xDataForm, extraData.getData().getRoot(), validationErrorsBuilder );
            }
        }
    }

}
