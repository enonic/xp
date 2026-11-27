package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;

@Component
public final class PageValidator
    implements ContentValidator
{
    private final PageDescriptorService pageDescriptorService;

    private final ContentService contentService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    @Activate
    public PageValidator( @Reference final PageDescriptorService pageDescriptorService, @Reference final ContentService contentService,
                          @Reference final PartDescriptorService partDescriptorService,
                          @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
        this.contentService = contentService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Override
    public boolean supports( final ContentTypeName contentTypeName )
    {
        return true;
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        final Page page = params.getPage();

        if ( page == null )
        {
            return;
        }

        PageDescriptor pageDescriptor = null;

        if ( page.hasDescriptor() )
        {
            pageDescriptor = pageDescriptorService.getByKey( page.getDescriptor() );
        }
        else if ( page.hasTemplate() )
        {
            final PageTemplate pageTemplate = (PageTemplate) contentService.getById( page.getTemplate().getContentId() );
            if ( pageTemplate != null && pageTemplate.getPage() != null && pageTemplate.getPage().hasDescriptor() )
            {
                pageDescriptor = pageDescriptorService.getByKey( pageTemplate.getPage().getDescriptor() );
            }
        }

        if ( pageDescriptor != null && page.getConfig() != null )
        {
            final ApplicationKey applicationKey = pageDescriptor.getApplicationKey();
            final OccurrenceValidator.ValidationErrorFactory errorFactory =
                ( errorCode, propertyPath, i18nPrefix ) -> ValidationError.componentConfigError( errorCode, propertyPath, applicationKey,
                                                                                                 ComponentPath.from( "/" ) )
                    .i18n( i18nPrefix + ".component" )
                    .args( "/" );

            validateConfig( pageDescriptor.getConfig(), page.getConfig(), errorFactory, validationErrorsBuilder );
        }

        if ( page.hasRegions() )
        {
            validateRegions( page.getRegions(), validationErrorsBuilder );
        }

        if ( page.isFragment() )
        {
            validateComponent( page.getFragment(), validationErrorsBuilder );
        }
    }

    private void validateRegions( final Regions regions, final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( Region region : regions )
        {
            for ( com.enonic.xp.region.Component component : region.getComponents() )
            {
                validateComponent( component, validationErrorsBuilder );
            }
        }
    }

    private void validateComponent( final com.enonic.xp.region.Component component,
                                    final ValidationErrors.Builder validationErrorsBuilder )
    {
        switch ( component )
        {
            case PartComponent part -> validatePartComponent( part, validationErrorsBuilder );
            case LayoutComponent layout -> validateLayoutComponent( layout, validationErrorsBuilder );
            default ->
            {
            }
        }
    }

    private void validatePartComponent( final PartComponent component, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( !component.hasDescriptor() )
        {
            return;
        }

        final PartDescriptor partDescriptor = partDescriptorService.getByKey( component.getDescriptor() );

        if ( partDescriptor != null )
        {
            final OccurrenceValidator.ValidationErrorFactory errorFactory =
                ( errorCode, propertyPath, i18nPrefix ) -> ValidationError.componentConfigError( errorCode, propertyPath,
                                                                                                 partDescriptor.getApplicationKey(),
                                                                                                 component.getPath() )
                    .i18n( i18nPrefix + ".component" )
                    .args( component.getPath().toString() );

            validateConfig( partDescriptor.getConfig(), component.getConfig(), errorFactory, validationErrorsBuilder );
        }
    }

    private void validateLayoutComponent( final LayoutComponent component, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( !component.hasDescriptor() )
        {
            return;
        }

        final LayoutDescriptor layoutDescriptor = layoutDescriptorService.getByKey( component.getDescriptor() );

        if ( layoutDescriptor != null )
        {
            final OccurrenceValidator.ValidationErrorFactory errorFactory =
                ( errorCode, propertyPath, i18nPrefix ) -> ValidationError.componentConfigError( errorCode, propertyPath,
                                                                                                 layoutDescriptor.getApplicationKey(),
                                                                                                 component.getPath() )
                    .i18n( i18nPrefix + ".component" )
                    .args( component.getPath().toString() );

            validateConfig( layoutDescriptor.getConfig(), component.getConfig(), errorFactory, validationErrorsBuilder );
        }

        if ( component.hasRegions() )
        {
            validateRegions( component.getRegions(), validationErrorsBuilder );
        }
    }

    private static void validateConfig( final Form form, final PropertyTree config,
                                        final OccurrenceValidator.ValidationErrorFactory errorFactory,
                                        final ValidationErrors.Builder validationErrorsBuilder )
    {
        OccurrenceValidator.validate( form, config.getRoot(), errorFactory, validationErrorsBuilder );
        InputValueValidator.validate( form, config, errorFactory, validationErrorsBuilder );
    }
}
