package com.enonic.xp.content;


import java.time.Instant;
import java.util.EnumSet;
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

    public Long manualOrderValue;

    public EnumSet<ContentInheritType> inherit;

    public ContentId variantOf;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.extraDatas = source.getAllExtraData().copy();
        this.page = source.getPage() != null ? source.getPage().copy() : null;
        this.valid = source.isValid();
        this.thumbnail = source.getThumbnail();
        this.permissions = source.getPermissions();
        this.owner = source.getOwner();
        this.language = source.getLanguage();
        this.creator = source.getCreator();
        this.createdTime = source.getCreatedTime();
        this.publishInfo = source.getPublishInfo();
        this.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
        this.workflowInfo = source.getWorkflowInfo();
        this.manualOrderValue = source.getManualOrderValue();
        this.inherit = source.getInherit().isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( source.getInherit() );
        this.variantOf = source.getVariantOf();
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
            permissions( permissions ).
            owner( owner ).
            language( language ).
            creator( creator ).
            createdTime( createdTime ).
            publishInfo( publishInfo ).
            processedReferences( processedReferences.build() ).
            workflowInfo( workflowInfo ).
            manualOrderValue( manualOrderValue ).
            setInherit( inherit ).
            variantOf( variantOf ).
            build();
    }
}
