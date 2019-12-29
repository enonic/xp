package com.enonic.xp.content;


import java.time.Instant;
import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.page.Page;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
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

    public ContentPublishInfo publishInfo;

    public ContentIds.Builder processedReferences;

    public WorkflowInfo workflowInfo;

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
        this.publishInfo = source.getPublishInfo();
        this.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
        this.workflowInfo = source.getWorkflowInfo();
    }

    public Content build()
    {
        return Content.create( this.source ).
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
            publishInfo( publishInfo ).
            processedReferences( processedReferences.build() ).
            workflowInfo( workflowInfo ).
            build();
    }
}
