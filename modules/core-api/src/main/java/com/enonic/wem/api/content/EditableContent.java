package com.enonic.wem.api.content;


import java.time.Instant;
import java.util.Locale;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.thumb.Thumbnail;

public class EditableContent
{
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public Metadatas metadata;

    public Page page;

    public boolean validated;

    public Thumbnail thumbnail;

    public boolean inheritPermissions;

    public AccessControlList permissions;

    public PrincipalKey owner;

    public Locale language;

    public PrincipalKey creator;

    public Instant createdTime;

    public PrincipalKey modifier;

    public Instant modifiedTime;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.metadata = source.getAllMetadata().copy();
        this.page = source.hasPage() ? source.getPage().copy() : null;
        this.validated = source.isValid();
        this.thumbnail = source.getThumbnail();
        this.inheritPermissions = source.inheritsPermissions();
        this.permissions = source.getPermissions();
        this.owner = source.getOwner();
        this.language = source.getLanguage();
        this.creator = source.getCreator();
        this.createdTime = source.getCreatedTime();
    }

    public Content build()
    {
        return Content.newContent( this.source ).
            displayName( displayName ).
            data( data ).
            metadata( metadata ).
            page( page ).
            valid( validated ).
            thumbnail( thumbnail ).
            inheritPermissions( inheritPermissions ).
            permissions( permissions ).
            owner( owner ).
            language( language ).
            creator( creator ).
            createdTime( createdTime ).
            build();
    }
}
