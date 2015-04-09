package com.enonic.xp.image.scale;

public final class ScaleParams
{
    private final String name;

    private final Object[] args;

    public ScaleParams( String name, Object[] args )
    {
        this.name = name;
        this.args = args != null ? args : new Object[0];
    }

    public String getName()
    {
        return this.name;
    }

    public Object[] getArguments()
    {
        return this.args;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( this.name ).append( "(" );

        for ( int i = 0; i < this.args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            str.append( encode( this.args[i] ) );
        }

        str.append( ")" );
        return str.toString();
    }

    public int getRequiredImageSize() {
        if(args == null) {
          return -1;
        }

        if(args.length == 1)
        {
            int size = (Integer) args[0];
            return size;
        }

        if(args.length > 1)
        {
            int max =  Math.max( (Integer) args[0] , (Integer) args[1] );
            return max;
        }

        return -1;
    }

    private String encode( Object arg )
    {
        if ( arg == null )
        {
            return "";
        }

        if ( arg instanceof String )
        {
            return quote( (String) arg );
        }
        else
        {
            return arg.toString();
        }
    }

    private String quote( String arg )
    {
        if ( arg.contains( "'" ) )
        {
            return "\"" + arg + "\"";
        }
        else
        {
            return "'" + arg + "'";
        }
    }
}
