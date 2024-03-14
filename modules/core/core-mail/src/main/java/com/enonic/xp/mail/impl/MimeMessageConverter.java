package com.enonic.xp.mail.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.SendMailParams;
import com.enonic.xp.util.MediaTypes;

import static com.google.common.base.Strings.nullToEmpty;

class MimeMessageConverter
{
    private static final String DEFAULT_FROM_PATTERN = "<>";

    private final String defaultFromEmail;

    private final Session session;

    MimeMessageConverter( String defaultFromEmail, Session session )
    {
        this.defaultFromEmail = defaultFromEmail;
        this.session = session;
    }

    MimeMessage convert( SendMailParams params )
        throws Exception
    {
        MimeMessage message = new MimeMessage( session );

        message.setSubject( params.getSubject() );

        message.addFrom( toAddresses( resolveFrom( params.getFrom() ) ) );
        message.addRecipients( Message.RecipientType.TO, toAddresses( params.getTo() ) );
        message.addRecipients( Message.RecipientType.CC, toAddresses( params.getCc() ) );
        message.addRecipients( Message.RecipientType.BCC, toAddresses( params.getBcc() ) );
        message.setReplyTo( toAddresses( params.getReplyTo() ) );

        if ( params.getHeaders() != null )
        {
            for ( Map.Entry<String, String> header : params.getHeaders().entrySet() )
            {
                message.addHeader( header.getKey(), header.getValue() );
            }
        }

        final List<Attachment> attachmentList = resolveAttachments( params.getAttachments() );
        if ( attachmentList.isEmpty() )
        {
            message.setText( nullToEmpty( params.getBody() ), "UTF-8" );
            if ( params.getContentType() != null )
            {
                message.addHeader( "Content-Type", params.getContentType() );
            }
        }
        else
        {
            final Multipart multipart = new MimeMultipart();

            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText( nullToEmpty( params.getBody() ), "UTF-8" );
            if ( params.getContentType() != null )
            {
                textPart.addHeader( "Content-Type", params.getContentType() );
            }
            multipart.addBodyPart( textPart );

            for ( Attachment attachment : attachmentList )
            {
                final MimeBodyPart messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteSourceDataSource( attachment.data, attachment.name, attachment.mimeType );
                messageBodyPart.setDataHandler( new DataHandler( source ) );
                messageBodyPart.setFileName( attachment.name );
                for ( String headerName : attachment.headers.keySet() )
                {
                    messageBodyPart.addHeader( headerName, attachment.headers.get( headerName ) );
                }
                multipart.addBodyPart( messageBodyPart );
            }
            message.setContent( multipart );
        }

        return message;
    }

    private List<String> resolveFrom( final List<String> from )
    {
        final List<String> result = from.stream().filter( string -> !nullToEmpty( string ).isBlank() ).map( sender -> {
            if ( sender.contains( DEFAULT_FROM_PATTERN ) )
            {
                if ( nullToEmpty( defaultFromEmail ).isBlank() )
                {
                    throw new IllegalArgumentException(
                        String.format( "To use \"%s\" the \"defaultFromEmail\" configuration must be set in \"com.enonic.xp.mail.cfg\"",
                                       DEFAULT_FROM_PATTERN ) );
                }
                return sender.equals( DEFAULT_FROM_PATTERN )
                    ? defaultFromEmail
                    : sender.replace( DEFAULT_FROM_PATTERN, String.format( "<%s>", defaultFromEmail ) );
            }
            else
            {
                return sender;
            }
        } ).collect( Collectors.toList() );

        if ( result.isEmpty() )
        {
            if ( !nullToEmpty( defaultFromEmail ).isBlank() )
            {
                result.add( defaultFromEmail );
            }
            else
            {
                throw new IllegalArgumentException( "Parameter 'from' is required" );
            }
        }

        return result;
    }

    private InternetAddress[] toAddresses( final List<String> addressList )
    {
        return addressList.stream()
            .filter( string -> !nullToEmpty( string ).isBlank() )
            .map( this::toAddress )
            .toArray( InternetAddress[]::new );
    }

    private InternetAddress toAddress( final String address )
        throws MailException
    {
        try
        {
            return new InternetAddress( address );
        }
        catch ( AddressException e )
        {
            throw new MailException( e.getMessage(), e );
        }
    }

    private List<Attachment> resolveAttachments( final List<Map<String, Object>> attachments )
    {
        if ( attachments == null )
        {
            return Collections.emptyList();
        }
        final List<Attachment> result = new ArrayList<>();
        for ( Map<String, Object> attachmentObject : attachments )
        {
            final String name = getValue( attachmentObject, "fileName", String.class );
            final ByteSource data = getValue( attachmentObject, "data", ByteSource.class );
            String mimeType = getValue( attachmentObject, "mimeType", String.class );
            final Map headers = getValue( attachmentObject, "headers", Map.class );
            if ( name != null && data != null )
            {
                mimeType = mimeType == null ? getMimeType( name ) : mimeType;
                result.add( new Attachment( name, data, mimeType, headers ) );
            }
        }
        return result;
    }

    private <T> T getValue( final Map<String, Object> object, final String key, final Class<T> type )
    {
        final Object value = object.get( key );
        if ( type.isInstance( value ) )
        {
            //noinspection unchecked
            return (T) value;
        }
        return null;
    }

    private String getMimeType( final String fileName )
    {
        if ( fileName == null )
        {
            return MediaType.OCTET_STREAM.toString();
        }

        final MediaType type = MediaTypes.instance().fromFile( fileName );
        return type.toString();
    }

    private static class Attachment
    {
        public final String name;

        public final ByteSource data;

        public final String mimeType;

        public final Map<String, String> headers;

        Attachment( final String name, final ByteSource data, final String mimeType, final Map<String, String> headers )
        {
            this.name = name;
            this.data = data;
            this.mimeType = mimeType;
            this.headers = headers == null ? Collections.emptyMap() : headers;
        }
    }

    private static class ByteSourceDataSource
        implements DataSource
    {
        private final ByteSource source;

        private final String name;

        private final String mimeType;

        ByteSourceDataSource( final ByteSource source, final String name, final String mimeType )
        {
            this.source = source;
            this.name = name;
            this.mimeType = mimeType;
        }

        @Override
        public InputStream getInputStream()
            throws IOException
        {
            return this.source.openStream();
        }

        @Override
        public OutputStream getOutputStream()
            throws IOException
        {
            throw new UnsupportedOperationException( "Not implemented" );
        }

        @Override
        public String getContentType()
        {
            return this.mimeType;
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }
}
