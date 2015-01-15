package com.enonic.wem.api.content;


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

    public boolean draft;

    public Thumbnail thumbnail;

    public boolean inheritPermissions;

    public AccessControlList permissions;

    public PrincipalKey owner;

    public Locale language;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.metadata = source.getAllMetadata().copy();
        this.page = source.hasPage() ? source.getPage().copy() : null;
        this.draft = source.isDraft();
        this.thumbnail = source.getThumbnail();
        this.inheritPermissions = source.inheritsPermissions();
        this.permissions = source.getPermissions();
        this.owner = source.getOwner();
        this.language = source.getLanguage();
    }

    public Content build()
    {
        final Content.Builder builder = Content.newContent( this.source );
        builder.displayName( displayName );
        builder.data( data );
        builder.metadata( metadata );
        builder.page( page );
        builder.draft( draft );
        builder.thumbnail( thumbnail );
        builder.inheritPermissions( inheritPermissions );
        builder.permissions( permissions );
        builder.owner( owner );
        builder.language( language );
        return builder.build();
    }
}
