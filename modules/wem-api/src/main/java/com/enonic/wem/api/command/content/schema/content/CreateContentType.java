package com.enonic.wem.api.command.content.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.module.ModuleName;

public final class CreateContentType
    extends Command<QualifiedContentTypeName>
{
    private String name;

    private String displayName;

    private QualifiedContentTypeName superType;

    private boolean isAbstract;

    private boolean isFinal;

    private boolean allowChildren = true;

    private ModuleName moduleName;

    private Form form;

    private Icon icon;

    private String contentDisplayNameScript;

    public String getName()
    {
        return name;
    }

    public CreateContentType name( final String name )
    {
        this.name = name;
        return this;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public CreateContentType displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public QualifiedContentTypeName getSuperType()
    {
        return superType;
    }

    public CreateContentType superType( final QualifiedContentTypeName superType )
    {
        this.superType = superType;
        return this;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public CreateContentType setAbstract( final boolean isAbstract )
    {
        this.isAbstract = isAbstract;
        return this;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public CreateContentType setFinal( final boolean isFinal )
    {
        this.isFinal = isFinal;
        return this;
    }

    public boolean getAllowChildren()
    {
        return allowChildren;
    }

    public CreateContentType allowChildren( final boolean allowChildren )
    {
        this.allowChildren = allowChildren;
        return this;
    }

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public CreateContentType moduleName( final ModuleName moduleName )
    {
        this.moduleName = moduleName;
        return this;
    }

    public Form getForm()
    {
        return form;
    }

    public CreateContentType form( final Form form )
    {
        this.form = form;
        return this;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public CreateContentType icon( final Icon icon )
    {
        this.icon = icon;
        return this;
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    public CreateContentType contentDisplayNameScript( final String contentDisplayNameScript )
    {
        this.contentDisplayNameScript = contentDisplayNameScript;
        return this;
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
            Objects.equal( this.isFinal, that.isFinal ) && Objects.equal( this.allowChildren, that.allowChildren ) &&
            Objects.equal( this.moduleName, that.moduleName ) && Objects.equal( this.form, that.form ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, superType, isAbstract, isFinal, allowChildren, moduleName, form, icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
        Preconditions.checkNotNull( this.moduleName, "moduleName cannot be null" );
    }
}
