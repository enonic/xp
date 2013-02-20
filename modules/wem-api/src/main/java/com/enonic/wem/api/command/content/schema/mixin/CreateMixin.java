package com.enonic.wem.api.command.content.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.type.form.FormItem;
import com.enonic.wem.api.module.ModuleName;

public final class CreateMixin
    extends Command<QualifiedMixinName>
{
    private FormItem formItem;

    private ModuleName moduleName;

    private String displayName;

    private Icon icon;


    public CreateMixin formItem( final FormItem formItem )
    {
        this.formItem = formItem;
        return this;
    }

    public CreateMixin moduleName( final ModuleName moduleName )
    {
        this.moduleName = moduleName;
        return this;
    }

    public CreateMixin displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateMixin icon( final Icon icon )
    {
        this.icon = icon;
        return this;
    }

    public FormItem getFormItem()
    {
        return formItem;
    }

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateMixin ) )
        {
            return false;
        }

        final CreateMixin that = (CreateMixin) o;
        return Objects.equal( this.formItem, that.formItem ) && Objects.equal( this.moduleName, that.moduleName ) &&
            Objects.equal( this.displayName, that.displayName ) && Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.formItem, this.moduleName, this.displayName, this.icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.formItem, "formItem cannot be null" );
        Preconditions.checkNotNull( this.moduleName, "moduleName cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
