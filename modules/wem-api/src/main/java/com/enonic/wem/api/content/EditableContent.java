package com.enonic.wem.api.content;


import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.security.acl.AccessControlList;

public class EditableContent
{
    private final Content source;

    public String displayName;

    public Form form;

    public PropertyTree data;

    public Metadatas metadata;

    public Page page;

    public boolean draft;

    public Thumbnail thumbnail;

    public boolean inheritPermissions;

    public AccessControlList permissions;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.form = source.getForm();
        this.data = source.getData().copy();
        this.metadata = source.getAllMetadata();
        this.page = page != null ? page.copy() : null;
        this.draft = source.isDraft();
        this.thumbnail = source.getThumbnail();
        this.inheritPermissions = source.inheritsPermissions();
        this.permissions = source.getPermissions();
    }

    public Content build()
    {
        final Content.Builder builder = Content.newContent( this.source );
        builder.displayName( displayName );
        builder.form( form );
        builder.data( data );
        builder.metadata( metadata );
        builder.page( page );
        builder.draft( draft );
        builder.thumbnail( thumbnail );
        builder.inheritPermissions( inheritPermissions );
        builder.permissions( permissions );
        return builder.build();
    }
}
