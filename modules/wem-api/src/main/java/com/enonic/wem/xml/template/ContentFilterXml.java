package com.enonic.wem.xml.template;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.xml.XmlObject;

public final class ContentFilterXml
    implements XmlObject<ContentTypeFilter, ContentTypeFilter.Builder>
{
    @XmlElement(name = "deny")
    private List<String> deny = Lists.newArrayList();

    @XmlElement(name = "allow")
    private List<String> allow = Lists.newArrayList();

    @Override
    public void from( final ContentTypeFilter filter )
    {
        if ( filter.getDefaultAccess() == ContentTypeFilter.AccessType.ALLOW )
        {
            this.allow.add( "*" );
        }
        else
        {
            this.deny.add( "*" );
        }
        for ( ContentTypeName contentType : filter )
        {
            if ( filter.isContentTypeAllowed( contentType ) )
            {
                this.allow.add( contentType.toString() );
            }
            else
            {
                this.deny.add( contentType.toString() );
            }
        }
    }

    @Override
    public void to( final ContentTypeFilter.Builder filterBuilder )
    {
        for ( String denyItem : this.deny )
        {
            if ( "*".equals( denyItem ) )
            {
                filterBuilder.defaultDeny();
            }
            else
            {
                filterBuilder.denyContentType( denyItem );
            }
        }
        for ( String allowItem : this.allow )
        {
            if ( "*".equals( allowItem ) )
            {
                filterBuilder.defaultAllow();
            }
            else
            {
                filterBuilder.allowContentType( allowItem );
            }
        }
    }
}
