package com.enonic.wem.api.command.content.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.module.ModuleName;

public final class CreateMixin
    extends Command<QualifiedMixinName>
{
    private FormItem formItem;

    private ModuleName moduleName;

    private String displayName;


    public CreateMixin mixin( final Mixin mixin )
    {
        this.formItem = mixin.getFormItem();
        this.moduleName = mixin.getModuleName();
        this.displayName = mixin.getDisplayName();
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
            Objects.equal( this.displayName, that.displayName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.formItem, this.moduleName, this.displayName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.formItem, "formItem cannot be null" );
        Preconditions.checkNotNull( this.moduleName, "moduleName cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
