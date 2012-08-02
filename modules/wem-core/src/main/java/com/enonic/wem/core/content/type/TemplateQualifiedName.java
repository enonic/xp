package com.enonic.wem.core.content.type;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class TemplateQualifiedName
{
    private String moduleName;

    private String templateName;

    public TemplateQualifiedName( final String qualifiedName )
    {
        Preconditions.checkNotNull( qualifiedName, "QualifiedName is null" );
        Preconditions.checkArgument( StringUtils.isNotBlank( qualifiedName ), "QualifiedName is blank" );

        int colonPos = qualifiedName.indexOf( ":" );
        Preconditions.checkArgument( colonPos >= 0, "QualifiedName is missing colon: " + qualifiedName );
        Preconditions.checkArgument( colonPos > 0, "QualifiedName is missing module name: " + qualifiedName );
        Preconditions.checkArgument( colonPos < qualifiedName.length() - 1, "QualifiedName is missing template name: " + qualifiedName );

        this.moduleName = qualifiedName.substring( 0, colonPos );
        this.templateName = qualifiedName.substring( colonPos + 1, qualifiedName.length() );
    }

    public TemplateQualifiedName( final String moduleName, final String templateName )
    {
        this.moduleName = moduleName;
        this.templateName = templateName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public String getTemplateName()
    {
        return templateName;
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

        final TemplateQualifiedName that = (TemplateQualifiedName) o;

        if ( !moduleName.equals( that.moduleName ) )
        {
            return false;
        }
        if ( !templateName.equals( that.templateName ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = moduleName.hashCode();
        result = 31 * result + templateName.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append( moduleName ).append( ":" ).append( templateName );
        return s.toString();
    }
}
