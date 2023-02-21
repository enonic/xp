package com.enonic.xp.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

public final class SerializableUtils
{
    private SerializableUtils()
    {
    }

    public static byte[] serialize( Serializable serializable )
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream( baos ))
        {
            oos.writeObject( serializable );
            return baos.toByteArray();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public static Object deserialize( byte[] bytes )
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); ObjectInputStream ois = new ObjectInputStream( bais ))
        {
            return ois.readObject();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
    }

}
