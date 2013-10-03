package com.enonic.wem.admin.json.schema;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.Schema;

public class SchemaJson
    extends ItemJson
{

    private String key;

    private String name;

    private String module;

    private String qualifyName;

    private String displayName;

    private String type;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private String iconUrl;

    public SchemaJson( Schema schema )
    {
        this.setKey( schema.getSchemaKey().toString() );
        this.setName( schema.getName() );
        this.setQualifyName( schema.getQualifiedName().toString() );
        this.setDisplayName( schema.getDisplayName() );
        this.setType( schema.getClass().getSimpleName() );
        this.setCreatedTime( schema.getCreatedTime() );
        this.setModifiedTime( schema.getModifiedTime() );
        this.setIconUrl( SchemaImageUriResolver.resolve( schema.getSchemaKey() ) );
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

    public String getQualifyName()
    {
        return qualifyName;
    }

    public void setQualifyName( final String qualifyName )
    {
        this.qualifyName = qualifyName;
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
