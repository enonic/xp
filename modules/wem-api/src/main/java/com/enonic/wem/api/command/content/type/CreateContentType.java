package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.Form;
import com.enonic.wem.api.module.ModuleName;

public final class CreateContentType
    extends Command<QualifiedContentTypeName>
{
    private String name;

    private String displayName;

    private QualifiedContentTypeName superType;

    private boolean isAbstract;

    private boolean isFinal;

    private ModuleName moduleName;

    private Form form;

    private Icon icon;

    public CreateContentType contentType( final ContentType contentType )
    {
        this.name = contentType.getName();
        this.displayName = contentType.getDisplayName();
        this.superType = contentType.getSuperType();
        this.isAbstract = contentType.isAbstract();
        this.isFinal = contentType.isFinal();
        this.moduleName = contentType.getModuleName();
        this.form = contentType.form();
        this.icon = contentType.getIcon();
        return this;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public QualifiedContentTypeName getSuperType()
    {
        return superType;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public Form getForm()
    {
        return form;
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

        if ( !( o instanceof CreateContentType ) )
        {
            return false;
        }

        final CreateContentType that = (CreateContentType) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.superType, that.superType ) && Objects.equal( this.isAbstract, that.isAbstract ) &&
            Objects.equal( this.isFinal, that.isFinal ) && Objects.equal( this.moduleName, that.moduleName ) &&
            Objects.equal( this.form, that.form ) && Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, superType, isAbstract, isFinal, moduleName, form, icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
        Preconditions.checkNotNull( this.moduleName, "moduleName cannot be null" );
    }
}
