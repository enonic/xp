package com.enonic.wem.taglib.site;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.collect.Lists;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;

public final class SiteBeanTag
    extends TagSupport
    implements IterationTag
{
    private String var;

    private Iterator<SiteBeanImpl> current;

    public void setVar( final String var )
    {
        this.var = var;
    }

    @Override
    public int doStartTag()
        throws JspException
    {
        this.current = getAllSites().iterator();

        if ( this.current.hasNext() )
        {
            assignElement();
            return Tag.EVAL_BODY_INCLUDE;
        }

        return Tag.SKIP_BODY;
    }

    @Override
    public int doAfterBody()
        throws JspException
    {
        if ( this.current.hasNext() )
        {
            assignElement();
            return IterationTag.EVAL_BODY_AGAIN;
        }

        return Tag.SKIP_BODY;
    }

    private void assignElement()
    {
        this.pageContext.setAttribute( this.var, this.current.next() );
    }

    public List<SiteBeanImpl> getAllSites()
    {
        final List<SiteBeanImpl> list = Lists.newArrayList();

        final SiteDao siteDao = getSpringContext().getBean( SiteDao.class );
        for ( final SiteEntity entity : siteDao.findAll() )
        {
            final SiteBeanImpl bean = new SiteBeanImpl();
            bean.setKey( entity.getKey().toInt() );
            bean.setName( entity.getName() );
            list.add( bean );
        }

        Collections.sort( list );
        return list;
    }

    private WebApplicationContext getSpringContext()
    {
        return WebApplicationContextUtils.getRequiredWebApplicationContext( this.pageContext.getServletContext() );
    }
}
