package com.enonic.xp.content;


import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.EditablePage;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class EditableContent
{
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public ExtraDatas extraDatas;

    public EditablePage page;

    public PrincipalKey owner;

    public Locale language;

    public ContentPublishInfo publishInfo;

    public WorkflowInfo workflowInfo;

    public ContentId variantOf;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.extraDatas = source.getAllExtraData().copy();
        this.page = source.getPage() != null ? new EditablePage( source.getPage() ) : null;
        this.owner = source.getOwner();
        this.language = source.getLanguage();
        this.publishInfo = source.getPublishInfo();
        this.workflowInfo = source.getWorkflowInfo();
        this.variantOf = source.getVariantOf();
    }

    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName )
            .data( data )
            .extraDatas( extraDatas ).page( page != null ? page.build() : null )
            .owner( owner )
            .language( language )
            .publishInfo( buildPublishInfo() )
            .workflowInfo( workflowInfo )
            .variantOf( variantOf )
            .build();
    }

    private ContentPublishInfo buildPublishInfo()
    {
        final ContentPublishInfo sourcePublishInfo = source.getPublishInfo();
        if ( sourcePublishInfo == null )
        {
            return publishInfo != null ? ContentPublishInfo.create().from( publishInfo.getFrom() ).to( publishInfo.getTo() ).build() : null;
        }
        else
        {
            return publishInfo != null ? ContentPublishInfo.create().first( sourcePublishInfo.getFirst() )
                .from( publishInfo.getFrom() )
                .to( publishInfo.getTo() )
                .build() : sourcePublishInfo;
        }
    }
}
