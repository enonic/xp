package com.enonic.xp.mail.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.io.ByteSource;

import com.enonic.xp.mail.MailAttachment;
import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailHeader;
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

        for ( MailHeader header : params.getHeaders() )
        {
            message.addHeader( header.getKey(), header.getValue() );
        }

        final List<MailAttachment> attachmentList = params.getAttachments();
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
            message.setContent( createMultiPart( params ) );
        }

        return message;
    }

    private Multipart createMultiPart( SendMailParams params )
        throws MessagingException
    {
        final Multipart result = new MimeMultipart();

        result.addBodyPart( createTextPart( params.getBody(), params.getContentType() ) );
        for ( MailAttachment attachment : params.getAttachments() )
        {
            result.addBodyPart( createMimeBodyPart( attachment ) );
        }

        return result;
    }

    private MimeBodyPart createTextPart( String body, String contentType )
        throws MessagingException
    {
        final MimeBodyPart result = new MimeBodyPart();
        result.setText( nullToEmpty( body ), "UTF-8" );
        if ( contentType != null )
        {
            result.addHeader( "Content-Type", contentType );
        }
        return result;
    }

    private MimeBodyPart createMimeBodyPart( MailAttachment attachment )
        throws MessagingException
    {
        final MimeBodyPart result = new MimeBodyPart();

        String mimeType = Objects.requireNonNullElseGet( attachment.getMimeType(),
                                                         () -> MediaTypes.instance().fromFile( attachment.getFileName() ).toString() );

        DataSource source = new ByteSourceDataSource( attachment.getData(), attachment.getFileName(), mimeType );
        result.setDataHandler( new DataHandler( source ) );
        result.setFileName( attachment.getFileName() );

        for ( Map.Entry<String, String> header : attachment.getHeaders().entrySet() )
        {
            result.addHeader( header.getKey(), header.getValue() );
        }

        return result;
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
