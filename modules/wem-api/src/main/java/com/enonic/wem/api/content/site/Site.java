package com.enonic.wem.api.content.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.wem.api.support.Changes;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Site
{
    private final SiteTemplateKey template;

    private final ModuleConfigs moduleConfigs;

    private Site( final BaseBuilder builder )
    {
        this.template = builder.templateName;
        this.moduleConfigs = ModuleConfigs.from( builder.moduleConfigs );
    }

    public SiteTemplateKey getTemplate()
    {
        return template;
    }

    public ModuleConfigs getModuleConfigs()
    {
        return moduleConfigs;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Site that = (Site) o;

        return Objects.equals( this.template, that.template ) && Objects.equals( this.moduleConfigs, that.moduleConfigs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( template, moduleConfigs );
    }

    public static Builder newSite()
    {
        return new Builder();
    }

    public static Builder newSite( final Site source )
    {
        return new Builder( source );
    }

    public static EditBuilder editSite( final Site toBeEdited )
    {
        return new EditBuilder( toBeEdited );
    }

    public static class BaseBuilder
    {
        SiteTemplateKey templateName;

        List<ModuleConfig> moduleConfigs = new ArrayList<>();

        BaseBuilder( final Site source )
        {
            this.templateName = source.getTemplate();
            this.moduleConfigs = source.getModuleConfigs().getList();
        }

        BaseBuilder()
        {

        }
    }

    public static class EditBuilder
        extends BaseBuilder
    {
        private final Site original;

        private final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Site original )
        {
            super( original );
            this.original = original;
        }

        public EditBuilder template( SiteTemplateKey value )
        {
            changes.recordChange( newPossibleChange( "template" ).from( this.original.getTemplate() ).to( value ).build() );
            this.templateName = value;
            return this;
        }

        public EditBuilder moduleConfigs( ModuleConfigs configs )
        {
            changes.recordChange(
                newPossibleChange( "moduleConfigs" ).from( original.getModuleConfigs().getList() ).to( configs.getList() ).build() );
            moduleConfigs = configs.getList();
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }


        public Site build()
        {
            return new Site( this );
        }

    }

    public static class Builder
        extends BaseBuilder
    {
        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        public Builder template( SiteTemplateKey value )
        {
            this.templateName = value;
            return this;
        }

        public Builder addModuleConfig( ModuleConfig value )
        {
            moduleConfigs.add( value );
            return this;
        }

        public Builder moduleConfigs( ModuleConfigs configs )
        {
            if ( configs != null )
            {
                moduleConfigs = configs.getList();
            }
            return this;
        }

        public Site build()
        {
            return new Site( this );
        }

    }
}
