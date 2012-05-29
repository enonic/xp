package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public final class SpringHelper
    implements ApplicationContextAware
{
    private static SpringHelper INSTANCE;

    private ApplicationContext context;

    public SpringHelper()
    {
        INSTANCE = this;
    }

    @Override
    public void setApplicationContext( final ApplicationContext context )
    {
        this.context = context;
    }

    public <T> T getBean( final Class<T> type )
    {
        return this.context.getBean( type );
    }

    public HttpServletRequest getRequest()
    {
        final ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    public static SpringHelper get()
    {
        return INSTANCE;
    }
}
