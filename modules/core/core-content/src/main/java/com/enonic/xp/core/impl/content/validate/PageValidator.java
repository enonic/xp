package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateService;
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
public class PageValidator
    implements ContentValidator
{
    private final PageDescriptorService pageDescriptorService;

    private final PageTemplateService pageTemplateService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    @Activate
    public PageValidator( @Reference final PageDescriptorService pageDescriptorService,
                          @Reference final PageTemplateService pageTemplateService,
                          @Reference final PartDescriptorService partDescriptorService,
                          @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
        this.pageTemplateService = pageTemplateService;
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

        // Get the page descriptor to validate against
        PageDescriptor pageDescriptor = null;

        if ( page.hasDescriptor() )
        {
            // Direct page descriptor
            pageDescriptor = pageDescriptorService.getByKey( page.getDescriptor() );
        }
        else if ( page.hasTemplate() )
        {
            // Page template - get descriptor from the template
            final PageTemplate pageTemplate = pageTemplateService.getByKey( page.getTemplate() );
            if ( pageTemplate != null && pageTemplate.getPage() != null && pageTemplate.getPage().hasDescriptor() )
            {
                pageDescriptor = pageDescriptorService.getByKey( pageTemplate.getPage().getDescriptor() );
            }
        }

        // Validate page config against descriptor
        if ( pageDescriptor != null )
        {
            OccurrenceValidator.validate( pageDescriptor.getConfig(), page.getConfig().getRoot(), validationErrorsBuilder );

            try
            {
                InputValidator.create()
                    .form( pageDescriptor.getConfig() )
                    .inputTypeResolver( InputTypes.BUILTIN )
                    .build()
                    .validate( page.getConfig() );
            }
            catch ( final InputTypeValidationException e )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.pageConfigInvalid" ),
                                               e.getPropertyPath() )
                        .i18n( "cms.validation.componentConfigPropertyInvalid" )
                        .args( pageDescriptor.getKey(), "/", e.getPropertyPath() )
                                                 .build() );
            }
        }

        // Validate component configs in regions
        if ( page.hasRegions() )
        {
            validateRegions( page.getRegions(), validationErrorsBuilder );
        }
    }

    private void validateRegions( final Regions regions, final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( Region region : regions )
        {
            for ( com.enonic.xp.region.Component component : region.getComponents() )
            {
                if ( component instanceof PartComponent )
                {
                    validatePartComponent( (PartComponent) component, validationErrorsBuilder );
                }
                else if ( component instanceof LayoutComponent )
                {
                    validateLayoutComponent( (LayoutComponent) component, validationErrorsBuilder );
                }
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
            OccurrenceValidator.validate( partDescriptor.getConfig(), component.getConfig().getRoot(), validationErrorsBuilder );

            try
            {
                InputValidator.create()
                    .form( partDescriptor.getConfig() )
                    .inputTypeResolver( InputTypes.BUILTIN )
                    .build()
                    .validate( component.getConfig() );
            }
            catch ( final InputTypeValidationException e )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.partConfigInvalid" ),
                                               e.getPropertyPath() )
                        .i18n( "cms.validation.componentConfigPropertyInvalid" )
                        .args( component.getDescriptor(), component.getPath(), e.getPropertyPath() )
                                                 .build() );
            }
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
            OccurrenceValidator.validate( layoutDescriptor.getConfig(), component.getConfig().getRoot(), validationErrorsBuilder );

            try
            {
                InputValidator.create()
                    .form( layoutDescriptor.getConfig() )
                    .inputTypeResolver( InputTypes.BUILTIN )
                    .build()
                    .validate( component.getConfig() );
            }
            catch ( final InputTypeValidationException e )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.layoutConfigInvalid" ),
                                               e.getPropertyPath() )
                        .i18n( "cms.validation.componentConfigPropertyInvalid" )
                        .args( component.getDescriptor(), component.getPath(), e.getPropertyPath() )
                                                 .build() );
            }
        }

        // Recursively validate nested regions in layout
        if ( component.hasRegions() )
        {
            validateRegions( component.getRegions(), validationErrorsBuilder );
        }
    }
}
