package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateContentTypeParams
{
    private ContentTypeName name;

    private String displayName;

    private String description;

    private ContentTypeName superType;

    private boolean isAbstract;

    private boolean isFinal;

    private boolean allowChildContent = true;

    private boolean isBuiltIn = false;

    private Form form;

    private SchemaIcon schemaIcon;

    private String contentDisplayNameScript;

    public ContentTypeName getName()
    {
        return name;
    }


    public CreateContentTypeParams name( final ContentTypeName name )
    {
        this.name = name;
        return this;
    }

    public CreateContentTypeParams name( final String name )
    {
        this.name = ContentTypeName.from( name );
        return this;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public CreateContentTypeParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public CreateContentTypeParams description( final String description )
    {
        this.description = description;
        return this;
    }

    public ContentTypeName getSuperType()
    {
        return superType;
    }

    public CreateContentTypeParams superType( final ContentTypeName superType )
    {
        this.superType = superType;
        return this;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public CreateContentTypeParams setAbstract( final boolean isAbstract )
    {
        this.isAbstract = isAbstract;
        return this;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public CreateContentTypeParams setFinal( final boolean isFinal )
    {
        this.isFinal = isFinal;
        return this;
    }

    public boolean getAllowChildContent()
    {
        return allowChildContent;
    }

    public CreateContentTypeParams allowChildContent( final boolean value )
    {
        this.allowChildContent = value;
        return this;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }

    public CreateContentTypeParams builtIn( final boolean builtIn )
    {
        isBuiltIn = builtIn;
        return this;
    }

    public Form getForm()
    {
        return form;
    }

    public CreateContentTypeParams form( final Form form )
    {
        this.form = form;
        return this;
    }

    public SchemaIcon getSchemaIcon()
    {
        return schemaIcon;
    }

    public CreateContentTypeParams schemaIcon( final SchemaIcon schemaIcon )
    {
        this.schemaIcon = schemaIcon;
        return this;
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    public CreateContentTypeParams contentDisplayNameScript( final String contentDisplayNameScript )
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

        if ( !( o instanceof CreateContentTypeParams ) )
        {
            return false;
        }

        final CreateContentTypeParams that = (CreateContentTypeParams) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.superType, that.superType ) && Objects.equal( this.isAbstract, that.isAbstract ) &&
            Objects.equal( this.isFinal, that.isFinal ) && Objects.equal( this.allowChildContent, that.allowChildContent ) &&
            Objects.equal( this.isBuiltIn, that.isBuiltIn ) &&
            Objects.equal( this.form, that.form ) &&
            Objects.equal( this.schemaIcon, that.schemaIcon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, superType, isAbstract, isFinal, allowChildContent, isBuiltIn, form, schemaIcon );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
