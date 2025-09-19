package com.enonic.xp.content;


import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class EditableContent
{
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public ExtraDatas extraDatas;

    public Page page;

    public PrincipalKey owner;

    public Locale language;

    public ContentPublishInfo publishInfo;

    public ContentIds.Builder processedReferences;

    public WorkflowInfo workflowInfo;

    public ContentId variantOf;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.extraDatas = source.getAllExtraData().copy();
        this.page = source.getPage() != null ? source.getPage().copy() : null;
        this.owner = source.getOwner();
        this.language = source.getLanguage();
        this.publishInfo = source.getPublishInfo();
        this.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
        this.workflowInfo = source.getWorkflowInfo();
        this.variantOf = source.getVariantOf();
    }

    public Content build()
    {
        return Content.create( this.source ).
            displayName( displayName ).
            data( data ).
            extraDatas( extraDatas ).
            page( page ).
            owner( owner ).
            language( language ).
            publishInfo( publishInfo ).
            processedReferences( processedReferences.build() ).
            workflowInfo( workflowInfo ).
            variantOf( variantOf ).
            build();
    }
}
