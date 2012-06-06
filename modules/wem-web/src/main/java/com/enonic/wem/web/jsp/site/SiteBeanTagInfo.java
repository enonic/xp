package com.enonic.wem.web.jsp.site;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public final class SiteBeanTagInfo
    extends TagExtraInfo
{
    @Override
    public VariableInfo[] getVariableInfo( final TagData data )
    {
        final String varName = data.getAttributeString( "var" );
        final VariableInfo helperInfo = new VariableInfo( varName, SiteBean.class.getName(), true, VariableInfo.NESTED );
        return new VariableInfo[]{helperInfo};
    }
}
