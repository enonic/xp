package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class PartTemplate
    extends Template<PartTemplateName, PartTemplateKey, PartDescriptorKey>
{
    private PartTemplate( final Builder builder )
    {
        super( builder );
    }

    private PartTemplate( final PartTemplateProperties properties )
    {
        super( properties );
    }

    @Override
    protected PartTemplateKey createKey( final ModuleName moduleName, final PartTemplateName name )
    {
        return PartTemplateKey.from( moduleName, name );
    }

    public static Builder newPartTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PartTemplate, PartTemplateName, PartTemplateKey, PartDescriptorKey>
    {
        private Builder()
        {
        }

        @Override
        public Builder name( final String name )
        {
            this.name = PartTemplateName.from( name );
            return this;
        }

        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }

    public static class PartTemplateProperties
        extends TemplateProperties
    {

    }

    public static PartTemplateEditBuilder editPartTemplate( final PartTemplate toBeEdited )
    {
        return new PartTemplateEditBuilder( toBeEdited );
    }

    public static class PartTemplateEditBuilder
        extends PartTemplateProperties
        implements EditBuilder<PartTemplate>
    {
        private final PartTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private PartTemplateEditBuilder( PartTemplate original )
        {
            this.original = original;
        }

        public PartTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public PartTemplateEditBuilder descriptor( final PartDescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public PartTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
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

        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }
}
