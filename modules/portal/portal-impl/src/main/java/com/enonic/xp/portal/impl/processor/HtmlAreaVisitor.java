package com.enonic.xp.portal.impl.processor;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputTypeName;

public class HtmlAreaVisitor
    extends InputVisitor
{

    private Set<FormItemPath> paths = Sets.newHashSet();

    @Override
    public void visit( final Input input )
    {
        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            paths.add( input.getPath() );
        }

    }

    public Set<FormItemPath> getPaths()
    {
        return paths;
    }
}
