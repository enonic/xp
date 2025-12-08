package com.enonic.xp.repo.impl.dump.serializer.json;


import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.util.GenericValue;

public class VersionDumpEntryJson
{
    @JsonProperty("nodePath")
    private String nodePath;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("nodeBlobKey")
    private String nodeBlobKey;

    @JsonProperty("indexConfigBlobKey")
    private String indexConfigBlobKey;

    @JsonProperty("accessControlBlobKey")
    private String accessControlBlobKey;

    @JsonProperty("nodeState")
    private String nodeState;

    @JsonProperty("commitId")
    private String commitId;

    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @SuppressWarnings("unused")
    public VersionDumpEntryJson()
    {
    }

    private VersionDumpEntryJson( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeBlobKey = builder.nodeBlobKey;
        indexConfigBlobKey = builder.indexConfigBlobKey;
        accessControlBlobKey = builder.accessControlBlobKey;
        commitId = builder.commitId;
        attributes = builder.attributes;
    }

    public static VersionMeta fromJson( final VersionDumpEntryJson json )
    {
        final Attributes attributes;
        if ( json.getAttributes() != null )
        {
            final Attributes.Builder attrsBuilder = Attributes.create();
            json.getAttributes().entrySet().stream()
                .forEach( entry -> attrsBuilder.attribute( entry.getKey(), GenericValue.fromRawJava( entry.getValue() ) ) );
            attributes = attrsBuilder.build();
        }
        else
        {
            attributes = null;
        }

        return VersionMeta.create()
            .nodePath( new NodePath( json.nodePath ) )
            .timestamp( json.getTimestamp() != null ? Instant.parse( json.getTimestamp() ) : null )
            .version( json.getVersion() != null ? NodeVersionId.from( json.getVersion() ) : null )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( json.getNodeBlobKey() ) )
                                 .indexConfigBlobKey( BlobKey.from( json.getIndexConfigBlobKey() ) )
                                 .accessControlBlobKey( BlobKey.from( json.getAccessControlBlobKey() ) )
                                 .build() )
            .nodeCommitId( json.getCommitId() == null ? null : NodeCommitId.from( json.getCommitId() ) )
            .attributes( attributes )
            .build();
    }

    public static VersionDumpEntryJson from( final VersionMeta meta )
    {
        final Map<String, Object> attributesMap;
        if ( meta.attributes() != null )
        {
            attributesMap = meta.attributes().entrySet().stream()
                .collect( Collectors.toMap( Map.Entry::getKey, entry -> entry.getValue().toRawJava() ) );
        }
        else
        {
            attributesMap = null;
        }

        return VersionDumpEntryJson.create().
            nodePath( meta.nodePath().toString() ).
            timestamp( Objects.toString( meta.timestamp(), null ) ).
            version( Objects.toString( meta.version(), null ) ).
            nodeBlobKey( meta.nodeVersionKey().getNodeBlobKey().toString() ).
            indexConfigBlobKey( meta.nodeVersionKey().getIndexConfigBlobKey().toString() ).
            accessControlBlobKey( meta.nodeVersionKey().getAccessControlBlobKey().toString() ).
            commitId( Objects.toString( meta.nodeCommitId(), null ) ).
            attributes( attributesMap ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final VersionDumpEntryJson source )
    {
        return new Builder( source );
    }

    public String getNodePath()
    {
        return nodePath;
    }

    private String getTimestamp()
    {
        return timestamp;
    }

    private String getVersion()
    {
        return version;
    }

    public String getNodeBlobKey()
    {
        return nodeBlobKey;
    }

    public String getIndexConfigBlobKey()
    {
        return indexConfigBlobKey;
    }

    public String getAccessControlBlobKey()
    {
        return accessControlBlobKey;
    }

    public String getCommitId()
    {
        return commitId;
    }

    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

    public static final class Builder
    {
        private String nodePath;

        private String timestamp;

        private String version;

        private String nodeBlobKey;

        private String indexConfigBlobKey;

        private String accessControlBlobKey;

        private String commitId;

        private Map<String, Object> attributes;

        private Builder()
        {
        }

        private Builder( final VersionDumpEntryJson source )
        {
            this.nodePath = source.getNodePath();
            this.timestamp = source.getTimestamp();
            this.version = source.getVersion();
            this.nodeBlobKey = source.getNodeBlobKey();
            this.indexConfigBlobKey = source.getIndexConfigBlobKey();
            this.accessControlBlobKey = source.getAccessControlBlobKey();
            this.commitId = source.getCommitId();
            this.attributes = source.getAttributes();
        }

        public Builder nodePath( final String val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final String val )
        {
            timestamp = val;
            return this;
        }

        public Builder version( final String val )
        {
            version = val;
            return this;
        }

        public Builder nodeBlobKey( final String val )
        {
            nodeBlobKey = val;
            return this;
        }

        public Builder indexConfigBlobKey( final String val )
        {
            indexConfigBlobKey = val;
            return this;
        }

        public Builder accessControlBlobKey( final String val )
        {
            accessControlBlobKey = val;
            return this;
        }

        public Builder commitId( final String val )
        {
            commitId = val;
            return this;
        }

        public Builder attributes( final Map<String, Object> val )
        {
            attributes = val;
            return this;
        }

        public VersionDumpEntryJson build()
        {
            return new VersionDumpEntryJson( this );
        }
    }
}
