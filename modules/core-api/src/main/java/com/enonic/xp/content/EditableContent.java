package com.enonic.xp.content;


import java.time.Instant;
import java.util.Locale;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.page.Page;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

@Beta
public class EditableContent
{
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public ExtraDatas extraDatas;

    public Page page;

    public boolean valid;

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
        this.extraDatas = source.getAllExtraData().copy();
        this.page = source.hasPage() ? source.getPage().copy() : null;
        this.valid = source.isValid();
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
            extraDatas( extraDatas ).
            page( page ).
            valid( valid ).
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
