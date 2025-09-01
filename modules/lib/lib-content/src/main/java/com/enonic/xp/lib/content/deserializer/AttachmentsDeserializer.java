package com.enonic.xp.lib.content.deserializer;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;


public class AttachmentsDeserializer
{
    public BinaryReferences deserializeBinaryReferences( final List<String> refs )
    {
        if ( refs == null || refs.isEmpty() )
        {
            return BinaryReferences.empty();
        }

        return refs.stream().map( BinaryReference::from ).collect( BinaryReferences.collector() );
    }

    public Attachments deserializeAttachments( final List<Map<String, Object>> list, final Attachments originalAttachments )
    {
        if ( list == null || list.isEmpty() )
        {
            return Attachments.empty();
        }

        final Attachments.Builder builder = Attachments.create();

        final Set<String> visitedNames = new HashSet<>();

        if ( originalAttachments != null )
        {
            for ( Attachment originalAttachment : originalAttachments )
            {
                final Map<String, Object> attachmentMap = list.stream()
                    .filter( map -> originalAttachment.getName().equals( map.get( "name" ) ) )
                    .findFirst()
                    .orElse( null ); // not optimal, but list is expected to be small

                final Attachment attachment = attachmentMap != null
                    ? deserializeAttachment( originalAttachment.getName(), attachmentMap, originalAttachment )
                    : originalAttachment;

                builder.add( attachment );
                visitedNames.add( attachment.getName() );
            }
        }

        for ( Map<String, Object> attachmentMap : list )
        {
            final String name = (String) attachmentMap.get( "name" );

            if ( visitedNames.contains( name ) )
            {
                continue;
            }

            final Attachment attachment = deserializeAttachment( name, attachmentMap, null );
            if ( attachment != null )
            {
                builder.add( attachment );
                visitedNames.add( name );
            }
        }

        return builder.build();
    }


    public CreateAttachments deserializeCreateAttachments( final List<Map<String, Object>> list )
    {
        if ( list == null || list.isEmpty() )
        {
            return CreateAttachments.empty();
        }

        final CreateAttachments.Builder builder = CreateAttachments.create();

        for ( final Map<String, Object> createAttachment : list )
        {
            builder.add( deserializeCreateAttachment( createAttachment ) );
        }

        return builder.build();
    }

    private Attachment deserializeAttachment( final String name, final Map<String, Object> map, final Attachment originalAttachment )
    {
        if ( map == null || map.isEmpty() )
        {
            return null;
        }

        final Attachment.Builder builder = originalAttachment == null ? Attachment.create() : Attachment.create( originalAttachment );
        builder.name( name );

        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            switch ( key )
            {
                case "label":
                    builder.label( (String) value );
                    break;
                case "mimeType":
                    builder.mimeType( (String) value );
                    break;
                case "textContent":
                    builder.textContent( (String) value );
                    break;
                case "sha512":
                    builder.sha512( (String) value );
                    break;
                case "size":
                    builder.size( Long.parseLong( value.toString() ) );
                    break;
                case "name":
                    // already set
                    break;
                default:
                    throw new IllegalArgumentException( "Unknown attachment key: " + key );
            }
        }

        return builder.build();
    }

    private CreateAttachment deserializeCreateAttachment( final Map<String, Object> map )
    {
        if ( map == null || map.isEmpty() )
        {
            return null;
        }

        final CreateAttachment.Builder builder = CreateAttachment.create();

        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            switch ( key )
            {
                case "label":
                    builder.label( (String) value );
                    break;
                case "name":
                    builder.name( (String) value );
                    break;
                case "mimeType":
                    builder.mimeType( (String) value );
                    break;
                case "textContent":
                    builder.text( (String) value );
                    break;
                case "data":
                    if ( value instanceof ByteSource )
                    {
                        builder.byteSource( (ByteSource) value );
                    }
                    else if ( value instanceof String )
                    {
                        builder.byteSource( ByteSource.wrap( ( (String) value ).getBytes( StandardCharsets.UTF_8 ) ) );
                    }
                    else
                    {
                        builder.byteSource( ByteSource.wrap( value.toString().getBytes( StandardCharsets.UTF_8 ) ) );
                    }
                    break;
                default:
                    throw new IllegalArgumentException( "Unknown CreateAttachment key: " + key );
            }
        }

        return builder.build();

    }
}
