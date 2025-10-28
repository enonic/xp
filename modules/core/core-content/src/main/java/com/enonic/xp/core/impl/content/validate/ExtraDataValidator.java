package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.schema.xdata.MixinService;

@Component
public class ExtraDataValidator
    implements ContentValidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ExtraDataValidator.class );

    private final MixinService xDataService;

    @Activate
    public ExtraDataValidator( @Reference final MixinService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( final Mixin extraData : params.getExtraDatas() )
        {
            final MixinName name = extraData.getName();

            final MixinDescriptor xData = this.xDataService.getByName( name );
            if ( xData == null )
            {
                LOG.warn( "XData not found: '{}'", name );
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
