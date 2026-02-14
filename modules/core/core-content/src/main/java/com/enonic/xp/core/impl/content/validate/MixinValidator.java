package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public class MixinValidator
    implements ContentValidator
{
    private static final Logger LOG = LoggerFactory.getLogger( MixinValidator.class );

    private final MixinService mixinService;

    @Activate
    public MixinValidator( @Reference final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( final Mixin mixin : params.getMixins() )
        {
            final MixinName name = mixin.getName();

            final MixinDescriptor mixinDescriptor = this.mixinService.getByName( name );
            if ( mixinDescriptor == null )
            {
                LOG.warn( "MixinDescriptor not found: '{}'", name );
                continue;
            }

            final Form form = mixinDescriptor.getForm();
            OccurrenceValidator.validate( form, mixin.getData().getRoot(),
                                          ( errorCode, propertyPath, i18nPrefix ) -> ValidationError.mixinConfigError( errorCode,
                                                                                                                       propertyPath, name )
                                              .i18n( i18nPrefix + ".mixin" )
                                              .args( name ), validationErrorsBuilder );
        }
    }

}
