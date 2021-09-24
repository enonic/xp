package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component
public class SiteConfigsValidator
    implements ContentValidator
{
    private final SiteService siteService;

    @Activate
    public SiteConfigsValidator( @Reference final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Override
    public boolean supports( final ContentTypeName contentTypeName )
    {
        return contentTypeName.isSite();
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        final SiteConfigs siteConfigs = new SiteConfigsDataSerializer().fromProperties( params.getData().getRoot() ).build();

        for ( final SiteConfig siteConfig : siteConfigs )
        {
            final ApplicationKey applicationKey = siteConfig.getApplicationKey();

            final SiteDescriptor siteDescriptor = siteService.getDescriptor( applicationKey );

            if ( siteDescriptor != null )
            {
                OccurrenceValidator.validate( siteDescriptor.getForm(), siteConfig.getConfig().getRoot(), validationErrorsBuilder );

                try
                {
                    InputValidator.create()
                        .form( siteDescriptor.getForm() )
                        .inputTypeResolver( InputTypes.BUILTIN )
                        .build()
                        .validate( siteConfig.getConfig() );
                }
                catch ( final Exception e )
                {
                    validationErrorsBuilder.add( ValidationError.generalError( "com.enonic.cms.site_config_invalid" )
                                                     .i18n( "system.cms.validation.site" )
                                                     .args( siteConfig.getApplicationKey().getName() )
                                                     .build() );
                }

            }
        }
    }
}
