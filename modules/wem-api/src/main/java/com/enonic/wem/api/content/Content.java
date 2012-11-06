package com.enonic.wem.api.content;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.BlobToKeyReplacer;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.MockBlobKeyResolver;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class Content
{
    private ContentPath path = new ContentPath();

    private QualifiedContentTypeName type;

    private ContentData data = new ContentData();

    private String displayName;

    private UserKey owner;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private UserKey modifier;

    public void setPath( final ContentPath path )
    {
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public QualifiedContentTypeName getType()
    {
        return type;
    }

    public void setType( final QualifiedContentTypeName type )
    {
        this.type = type;
    }

    public String getName()
    {
        if ( path.hasName() )
        {
            return path.getName();
        }
        else
        {
            return null;
        }
    }

    public void setName( final String name )
    {
        this.path = this.path.withName( name );
    }

    public void setData( final ContentData value )
    {
        this.data = value;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( final DateTime createdTime )
    {
        this.createdTime = createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public void setModifiedTime( final DateTime modifiedTime )
    {
        this.modifiedTime = modifiedTime;
    }

    public AccountKey getModifier()
    {
        return modifier;
    }

    public void setModifier( final AccountKey modifier )
    {
        this.modifier = UserKey.from( modifier );
    }

    public AccountKey getOwner()
    {
        return owner;
    }

    public void setOwner( final AccountKey owner )
    {
        this.owner = UserKey.from( owner );
    }

    public ContentData getData()
    {
        return data;
    }

    public void setData( final String path, final String value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.TEXT );
    }

    public void setData( final String path, final DateMidnight value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.DATE );
    }

    public void setData( final String path, final Long value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.WHOLE_NUMBER );
    }

    public void setData( final String path, final Double value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.DECIMAL_NUMBER );
    }

    public void setData( final String path, final Object value, DataType dataType )
    {
        this.data.setData( new EntryPath( path ), value, dataType );
    }

    public Data getData( final String path )
    {
        return this.data.getData( new EntryPath( path ) );
    }

    public String getValueAsString( final String path )
    {
        return this.data.getValueAsString( new EntryPath( path ) );
    }

    public DataSet getDataSet( String path )
    {
        return this.data.getDataSet( new EntryPath( path ) );
    }

    public Object getIndexableValues()
    {
        // TODO
        return null;
    }

    public void replaceBlobsWithKeys( final MockBlobKeyResolver blobToKeyResolver )
    {
        new BlobToKeyReplacer( blobToKeyResolver ).replace( data );
    }


    public static Content create( final ContentPath contentPath )
    {
        Content content = new Content();
        content.setPath( contentPath );
        return content;
    }

    public static Builder newContent()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentPath path = new ContentPath();

        private QualifiedContentTypeName type;

        private ContentData data = new ContentData();

        private String displayName;

        private UserKey owner;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey modifier;

        public Builder path( ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder type( QualifiedContentTypeName type )
        {
            this.type = type;
            return this;
        }

        public Builder data( ContentData data )
        {
            this.data = data;
            return this;
        }

        public Builder displayName( String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder owner( UserKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder modifier( UserKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder createdTime( DateTime createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime( DateTime modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Content build()
        {
            Preconditions.checkNotNull( path, "path is mandatory for a content" );
            Preconditions.checkNotNull( createdTime, "createdTime is mandatory for a content" );
            Preconditions.checkNotNull( owner, "owner is mandatory for a content" );

            Content content = new Content();
            content.path = path;
            content.type = type;
            content.data = data;
            content.displayName = displayName;
            content.owner = owner;
            content.createdTime = createdTime;
            content.modifiedTime = modifiedTime;
            content.modifier = modifier;
            return content;
        }
    }
}
