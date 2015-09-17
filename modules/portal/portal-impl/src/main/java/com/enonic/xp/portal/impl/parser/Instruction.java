package com.enonic.xp.portal.impl.parser;

public final class Instruction
    extends HtmlBlock
{
    private final String instruction;

    public Instruction( final String instruction )
    {
        this.instruction = instruction;
    }

    @Override
    public String getHtml()
    {
        return "<!--#" + instruction + "-->";
    }

    public String getValue()
    {
        return instruction;
    }

    @Override
    public String toString()
    {
        return getHtml();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Instruction that = (Instruction) o;
        return instruction.equals( that.instruction );
    }

    @Override
    public int hashCode()
    {
        return instruction.hashCode();
    }
}
