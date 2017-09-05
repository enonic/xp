package com.enonic.xp.impl.task;

import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlFormMapper;
import com.enonic.xp.xml.parser.XmlModelParser;

final class XmlTaskDescriptorParser
    extends XmlModelParser<XmlTaskDescriptorParser>
{
    private TaskDescriptor.Builder builder;

    public XmlTaskDescriptorParser builder( final TaskDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "task" );
        this.builder.description( root.getChildValue( "description" ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( mapper.buildForm( root.getChild( "config" ) ) );
    }
}
