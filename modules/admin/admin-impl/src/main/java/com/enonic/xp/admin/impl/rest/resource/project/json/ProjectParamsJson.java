package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

public final class ProjectParamsJson
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final CreateAttachment icon;

    @JsonCreator
    ProjectParamsJson( @JsonProperty("name") final String name, @JsonProperty("displayName") final String displayName,
                       @JsonProperty("description") final String description, @JsonProperty("icon") final MultipartForm icon )
    {
        this.name = ProjectName.from( name );
        this.displayName = displayName;
        this.description = description;

        if ( icon != null )
        {
            final MultipartItem mediaFile = icon.get( "file" );

            this.icon = CreateAttachment.create().
                name( ProjectConstants.PROJECT_ICON_PROPERTY ).
                mimeType( mediaFile.toString() ).
                byteSource( mediaFile.getBytes() ).
                build();
        }
        else
        {
            this.icon = null;
        }
    }

    public CreateProjectParams getCreateParams()
    {
        return CreateProjectParams.create().
            name( name ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            build();
    }

    public ModifyProjectParams getModifyParams()
    {
        return ModifyProjectParams.create().
            name( name ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            build();
    }
}
