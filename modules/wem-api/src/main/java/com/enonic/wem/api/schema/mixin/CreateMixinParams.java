package com.enonic.wem.api.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItems;

public final class CreateMixinParams
{
    private MixinName name;

    private FormItems formItems = new FormItems( null );

    private String displayName;

    private String description;

    private Icon schemaIcon;


    public CreateMixinParams name( final MixinName name )
    {
        this.name = name;
        return this;
    }

    public CreateMixinParams name( final String name )
    {
        this.name = MixinName.from( name );
        return this;
    }

    public CreateMixinParams formItems( final FormItems formItems )
    {
        this.formItems = formItems;
        return this;
    }

    public CreateMixinParams addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
        return this;
    }

    public CreateMixinParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateMixinParams description( final String description )
    {
        this.description = description;
        return this;
    }

    public CreateMixinParams schemaIcon( final Icon schemaIcon )
    {
        this.schemaIcon = schemaIcon;
        return this;
    }

    public MixinName getName()
    {
        return name;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Icon getSchemaIcon()
    {
        return schemaIcon;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateMixinParams ) )
        {
            return false;
        }

        final CreateMixinParams that = (CreateMixinParams) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.formItems, that.formItems ) &&
            Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.description, that.description ) &&
            Objects.equal( this.schemaIcon, that.schemaIcon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name, this.formItems, this.displayName, this.description, this.schemaIcon );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.formItems, "formItems cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
