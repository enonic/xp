package com.enonic.wem.api.content;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

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
    private final String displayName;

    private final QualifiedContentTypeName type;

    private final ContentPath path;

    private final ContentData data;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey owner;

    private final UserKey modifier;

    private Content( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.path = builder.path;
        this.data = builder.data;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.owner = builder.owner;
        this.modifier = builder.modifier;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public QualifiedContentTypeName getType()
    {
        return type;
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

    public String getDisplayName()
    {
        return displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UserKey getOwner()
    {
        return owner;
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

    public static Builder newContent()
    {
        return new Builder();
    }

    public static Builder newContent( final Content content )
    {
        return new Builder( content );
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

        public Builder()
        {
        }

        public Builder( final Content content )
        {
            path = content.path; // TODO make ContentPath immutable, or make copy
            type = content.type;
            data = content.data; // TODO make ContentData immutable, or make copy
            displayName = content.displayName;
            owner = content.owner;
            createdTime = content.createdTime;
            modifiedTime = content.modifiedTime;
            modifier = content.modifier;
        }

        public Builder path( ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder name( String name )
        {
            if ( this.path == null )
            {
                path = new ContentPath();
            }
            this.path = this.path.withName( name );
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
            if ( type == null )
            {
                type = QualifiedContentTypeName.unstructured();
            }
            return new Content( this );
        }
    }
}
