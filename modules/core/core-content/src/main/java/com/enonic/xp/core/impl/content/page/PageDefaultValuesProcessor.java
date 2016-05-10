package com.enonic.xp.core.impl.content.page;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.page.Page;
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

final class PageDefaultValuesProcessor
{
    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    PageDefaultValuesProcessor( final PageDescriptorService pageDescriptorService, final PartDescriptorService partDescriptorService,
                                final LayoutDescriptorService layoutDescriptorService,
                                final FormDefaultValuesProcessor formDefaultValuesProcessor )
    {
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
    }

    void applyDefaultValues( final Page editedPage, final Page sourcePage )
    {
        if ( editedPage.getController() != null && !Objects.equals( editedPage.getController(), sourcePage.getController() ) )
        {
            applyPageDefaultValues( editedPage );
        }

        if ( editedPage.hasRegions() )
        {
            applyRegionsDefaultValues( editedPage, sourcePage );
        }
    }

    void applyDefaultValues( final Page newPage )
    {
        if ( newPage.getController() != null )
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

            if ( cmpData.getRoot().getPropertySize() > 0 )
            {
                continue; // skip if component data already modified
            }

            final Component sourceCmp = sourceRegion != null ? sourceRegion.getComponent( region.getIndex( cmp ) ) : null;
            if ( sourceCmp == null || !sourceCmp.getName().equals( cmp.getName() ) || !sourceCmp.getType().equals( cmp.getType() ) )
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
        final PropertyTree cmpData = cmp.getConfig();
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
            formDefaultValuesProcessor.setDefaultValues( cmpForm, cmpData );
        }
    }

    private void applyPageDefaultValues( final Page editedPage )
    {
        final PropertyTree pageData = editedPage.getConfig();
        if ( editedPage.getController() == null )
        {
            return;
        }
        final PageDescriptor pageForm = pageDescriptorService.getByKey( editedPage.getController() );
        if ( pageForm == null || pageForm.getConfig() == null )
        {
            return;
        }
        formDefaultValuesProcessor.setDefaultValues( pageForm.getConfig(), pageData );
    }


}
