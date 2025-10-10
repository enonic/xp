package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDefaultValuesProcessor;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;

@org.osgi.service.component.annotations.Component
public final class PageDefaultValuesProcessorImpl
    implements PageDefaultValuesProcessor
{
    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    @Activate
    public PageDefaultValuesProcessorImpl( @Reference final PageDescriptorService pageDescriptorService,
                                           @Reference final PartDescriptorService partDescriptorService,
                                           @Reference final LayoutDescriptorService layoutDescriptorService,
                                           @Reference final FormDefaultValuesProcessor formDefaultValuesProcessor )
    {
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
    }

    public void applyDefaultValues( final Page newPage )
    {
        if ( newPage == null )
        {
            return;
        }

        if ( newPage.getDescriptor() != null )
        {
            applyPageDefaultValues( newPage );
        }

        if ( newPage.hasRegions() )
        {
            applyRegionsDefaultValues( newPage, null );
        }
    }

    private void applyRegionsDefaultValues( final Page editedPage, final Page sourcePage )
    {
        for ( Region region : editedPage.getRegions() )
        {
            final Region sourceRegion = sourcePage != null ? sourcePage.getRegion( region.getName() ) : null;
            applyRegionDefaultValues( region, sourceRegion );
        }
    }

    private void applyRegionDefaultValues( final Region region, final Region sourceRegion )
    {
        for ( Component cmp : region.getComponents() )
        {
            if ( !( cmp.getType() instanceof PartComponentType || cmp.getType() instanceof LayoutComponentType ) )
            {
                continue; // skip if not Part or Layout
            }
            final DescriptorBasedComponent layoutOrPart = (DescriptorBasedComponent) cmp;
            final PropertyTree cmpData = layoutOrPart.getConfig();

            final Component sourceCmp = sourceRegion != null
                ? sourceRegion.getComponents().stream().filter( c -> c.equals( cmp ) ).findFirst().orElse( null )
                : null;

            if ( sourceCmp == null && cmpData.getRoot().getPropertySize() == 0  )
            {
                applyComponentDefaultValues( layoutOrPart );
            }

            // layout regions
            if ( cmp.getType() instanceof LayoutComponentType )
            {
                final LayoutComponent layout = (LayoutComponent) cmp;
                final LayoutComponent sourceLayout = (LayoutComponent) sourceCmp;
                for ( Region layoutRegion : layout.getRegions() )
                {
                    final Region sourceLayoutRegion = sourceLayout != null ? sourceLayout.getRegion( region.getName() ) : null;
                    applyRegionDefaultValues( layoutRegion, sourceLayoutRegion );
                }
            }
        }
    }

    private void applyComponentDefaultValues( final DescriptorBasedComponent cmp )
    {
        if ( cmp.getDescriptor() == null )
        {
            return;
        }
        final Form cmpForm;
        if ( cmp instanceof PartComponent )
        {
            final PartDescriptor partDescriptor = partDescriptorService.getByKey( cmp.getDescriptor() );
            cmpForm = partDescriptor.getConfig();
        }
        else
        {
            final LayoutDescriptor layoutDescriptor = layoutDescriptorService.getByKey( cmp.getDescriptor() );
            cmpForm = layoutDescriptor.getConfig();
        }

        if ( cmpForm != null )
        {
            final PropertyTree cmpData = cmp.getConfig();
            formDefaultValuesProcessor.setDefaultValues( cmpForm, cmpData );
        }
    }

    private void applyPageDefaultValues( final Page editedPage )
    {
        final PropertyTree pageData = editedPage.getConfig();

        if ( editedPage.getDescriptor() == null )
        {
            return;
        }

        final PageDescriptor pageForm = pageDescriptorService.getByKey( editedPage.getDescriptor() );

        if ( pageForm == null || pageForm.getConfig() == null )
        {
            return;
        }

        formDefaultValuesProcessor.setDefaultValues( pageForm.getConfig(), pageData );
    }


}
