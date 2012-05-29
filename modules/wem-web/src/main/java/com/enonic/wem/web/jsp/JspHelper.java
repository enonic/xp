package com.enonic.wem.web.jsp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.cms.core.product.ProductVersion;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;

public final class JspHelper
{
    public static String ellipsis( final String text, final int length )
    {
        if ( text.length() <= length )
        {
            return text;
        }
        else
        {
            final String outStr = Splitter.fixedLength( length ).split( text ).iterator().next();
            return outStr + "...";
        }
    }

    public static String getTitleAndVersion()
    {
        return ProductVersion.getFullTitleAndVersion();
    }

    public static List<SiteInfoBean> getSites()
    {
        final List<SiteInfoBean> list = Lists.newArrayList();

        final SiteDao siteDao = SpringHelper.get().getBean( SiteDao.class );
        for ( final SiteEntity entity : siteDao.findAll() )
        {
            final SiteInfoBean bean = new SiteInfoBean();
            bean.setKey( entity.getKey().toInt() );
            bean.setName( entity.getName() );
            bean.setUrl( createUrl( "site/" + entity.getKey().toInt() ) );
            list.add( bean );
        }

        return list;
    }

    public static String createUrl( final String path )
    {
        final HttpServletRequest req = SpringHelper.get().getRequest();
        final String url = ServletUriComponentsBuilder.fromRequest( req ).build().toString();
        final String baseUrl = url.substring( 0, url.length() - 1 );

        if ( path == null )
        {
            return baseUrl;
        }
        else
        {
            return baseUrl + "/" + path;
        }
    }
}
