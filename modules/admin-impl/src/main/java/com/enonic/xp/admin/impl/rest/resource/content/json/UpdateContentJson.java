package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.ExtraDataJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.PrincipalKey;

public final class UpdateContentJson
{
    final ContentName contentName;

    final UpdateContentParams updateContentParams;

    final RenameContentParams renameContentParams;

    @JsonCreator
    UpdateContentJson( @JsonProperty("contentId") final String contentId,
                       @JsonProperty("contentName") final String contentName,
                       @JsonProperty("data") final List<PropertyArrayJson> propertyArrayJsonList,
                       @JsonProperty("meta") final List<ExtraDataJson> extraDataJsonList,
                       @JsonProperty("displayName") final String displayName,
                       @JsonProperty("valid") final String valid,
                       @JsonProperty("requireValid") final String requireValid,
                       @JsonProperty("owner") final String owner,
                       @JsonProperty("language") final String language )
    {
        this.contentName = ContentName.from( contentName );

        final PropertyTree contentData = PropertyTreeJson.fromJson( propertyArrayJsonList );
        final ExtraDatas extraDatas = parseExtradata( extraDataJsonList );

        this.updateContentParams = new UpdateContentParams().
            requireValid( Boolean.valueOf( requireValid ) ).
            contentId( ContentId.from( contentId ) ).
            modifier( PrincipalKey.ofAnonymous() ).
            editor( edit -> {
                edit.data = contentData;
                edit.extraDatas = extraDatas;
                edit.displayName = displayName;
                edit.owner = StringUtils.isNotEmpty( owner ) ? PrincipalKey.from( owner ) : null;
                edit.language = StringUtils.isNotEmpty( language ) ? Locale.forLanguageTag( language ) : null;
            } );

        this.renameContentParams = RenameContentParams.create().
            contentId( ContentId.from( contentId ) ).
            newName( this.contentName ).
            build();
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

    private ExtraDatas parseExtradata( final List<ExtraDataJson> extraDataJsonList )
    {
        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.builder();
        for ( ExtraDataJson extraDataJson : extraDataJsonList )
        {
            extradatasBuilder.add( extraDataJson.getExtraData() );
        }
        return extradatasBuilder.build();
    }
}
