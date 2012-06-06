package com.enonic.wem.web.jsp;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public final class JspHelperTagInfo
    extends TagExtraInfo
{
    @Override
    public VariableInfo[] getVariableInfo( final TagData data )
    {
        final String varName = data.getAttributeString( "var" );
        final VariableInfo helperInfo = new VariableInfo( varName, JspHelper.class.getName(), true, VariableInfo.AT_BEGIN );
        return new VariableInfo[]{helperInfo};
    }
}
