package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

@Component
public final class CmsConfigsValidator
    implements ContentValidator
{
    private final CmsService cmsService;

    @Activate
    public CmsConfigsValidator( @Reference final CmsService cmsService )
    {
        this.cmsService = cmsService;
    }

    @Override
    public boolean supports( final ContentTypeName contentTypeName )
    {
        return contentTypeName.isSite();
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        final SiteConfigs siteConfigs = SiteConfigsDataSerializer.fromData( params.getData().getRoot() );

        for ( final SiteConfig siteConfig : siteConfigs )
        {
            final ApplicationKey applicationKey = siteConfig.getApplicationKey();

            final CmsDescriptor cmsDescriptor = cmsService.getDescriptor( applicationKey );

            if ( cmsDescriptor != null )
            {
                OccurrenceValidator.validate( cmsDescriptor.getForm(), siteConfig.getConfig().getRoot(),
                                              ( errorCode, propertyPath, i18nPrefix ) -> ValidationError.siteConfigError( errorCode,
                                                                                                                          propertyPath,
                                                                                                                          applicationKey )
                                                  .i18n( i18nPrefix + ".siteConfig" )
                                                  .args( applicationKey ), validationErrorsBuilder );
            }
        }
    }
}
