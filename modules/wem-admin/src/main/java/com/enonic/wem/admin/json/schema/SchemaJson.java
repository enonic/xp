package com.enonic.wem.admin.json.schema;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.Schema;

public class SchemaJson
    implements ItemJson
{

    private String key;

    private String name;

    private String module;

    private String qualifiedName;

    private String displayName;

    private String type;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private String iconUrl;

    private boolean hasChildren;

    public SchemaJson( Schema schema )
    {
        this.key = schema.getSchemaKey().toString();
        this.name = schema.getName();
        this.qualifiedName = schema.getQualifiedName().toString();
        this.displayName = schema.getDisplayName();
        this.type = schema.getClass().getSimpleName();
        this.createdTime = schema.getCreatedTime();
        this.modifiedTime = schema.getModifiedTime();
        this.iconUrl = SchemaImageUriResolver.resolve( schema.getSchemaKey() );
        this.hasChildren = schema.hasChildren();
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule( final String module )
    {
        this.module = module;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public void setQualifiedName( final String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public String getType()
    {
        return type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public String getCreatedTime()
    {
        return DateTimeFormatter.format( createdTime );
    }

    public void setCreatedTime( final DateTime createdTime )
    {
        this.createdTime = createdTime;
    }

    public String getModifiedTime()
    {
        return DateTimeFormatter.format( modifiedTime );
    }

    public void setModifiedTime( final DateTime modifiedTime )
    {
        this.modifiedTime = modifiedTime;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl( final String iconUrl )
    {
        this.iconUrl = iconUrl;
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }

    public void setHasChildren( final boolean hasChildren )
    {
        this.hasChildren = hasChildren;
    }

    @Override
    public boolean getEditable()
    {
        return true;
    }

    @Override
    public boolean getDeletable()
    {
        return true;
    }
}
