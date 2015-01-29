package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.MetadataJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.PropertyTreeJson;
import com.enonic.wem.api.security.PrincipalKey;

public final class UpdateContentJson
{
    final ContentName contentName;

    final UpdateContentParams updateContentParams;

    final RenameContentParams renameContentParams;

    @JsonCreator
    UpdateContentJson( @JsonProperty("contentId") final String contentId, @JsonProperty("contentName") final String contentName,
                       @JsonProperty("data") final List<PropertyArrayJson> propertyArrayJsonList,
                       @JsonProperty("meta") final List<MetadataJson> metadataJsonList,
                       @JsonProperty("displayName") final String displayName, @JsonProperty("draft") final String draft,
                       @JsonProperty("owner") final String owner, @JsonProperty("language") final String language )
    {
        this.contentName = ContentName.from( contentName );

        final PropertyTree contentData = PropertyTreeJson.fromJson( propertyArrayJsonList );
        final Metadatas metadatas = parseMetadata( metadataJsonList );

        this.updateContentParams = new UpdateContentParams().
            requireValid( !Boolean.valueOf( draft ) ).
            contentId( ContentId.from( contentId ) ).
            modifier( PrincipalKey.ofAnonymous() ).
            editor( edit -> {
                edit.data = contentData;
                edit.metadata = metadatas;
                edit.displayName = displayName;
                edit.owner = StringUtils.isNotEmpty( owner ) ? PrincipalKey.from( owner ) : null;
                edit.language = StringUtils.isNotEmpty( language ) ? Locale.forLanguageTag( language ) : null;
            } );

        this.renameContentParams = new RenameContentParams().
            contentId( ContentId.from( contentId ) ).
            newName( this.contentName );
    }

    @JsonIgnore
    public UpdateContentParams getUpdateContentParams()
    {
        return updateContentParams;
    }

    @JsonIgnore
    public RenameContentParams getRenameContentParams()
    {
        return renameContentParams;
    }

    @JsonIgnore
    public ContentName getContentName()
    {
        return contentName;
    }

    private Metadatas parseMetadata( final List<MetadataJson> metadataJsonList )
    {
        final List<Metadata> metadataList = new ArrayList<>();
        for ( MetadataJson metadataJson : metadataJsonList )
        {
            metadataList.add( metadataJson.getMetadata() );
        }
        return Metadatas.from( metadataList );
    }
}
