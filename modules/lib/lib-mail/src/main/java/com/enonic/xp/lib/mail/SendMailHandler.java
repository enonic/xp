package com.enonic.xp.lib.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.MediaTypes;

import static com.google.common.base.Strings.nullToEmpty;

public final class SendMailHandler
    implements MailMessage, ScriptBean
{
    private final static Logger LOG = LoggerFactory.getLogger( SendMailHandler.class );

    private String[] to;

    private String[] from;

    private String[] cc;

    private String[] bcc;

    private String[] replyTo;

    private String subject;

    private String contentType;

    private String body;

    private Supplier<MailService> mailService;

    private Map<String, String> headers;

    private List<Map<String, Object>> attachments;

    public void setTo( final String[] to )
    {
        this.to = to;
    }

    public void setFrom( final String[] from )
    {
        this.from = from;
    }

    public void setCc( final String[] cc )
    {
        this.cc = cc;
    }

    public void setBcc( final String[] bcc )
    {
        this.bcc = bcc;
    }

    public void setReplyTo( final String[] replyTo )
    {
        this.replyTo = replyTo;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public void setBody( final String body )
    {
        this.body = body;
    }

    public void setSubject( final String subject )
    {
        this.subject = subject;
    }

    public void setHeaders( final Map<String, String> headers )
    {
        this.headers = headers;
    }

    public void setAttachments( final List<Map<String, Object>> attachments )
    {
        this.attachments = attachments;
    }

    public boolean send()
    {
        try
        {
            this.mailService.get().send( this );
            return true;
        }
        catch ( final Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
            return false;
        }
    }

    @Override
    public void compose( final MimeMessage message )
        throws Exception
    {
        message.setSubject( this.subject );

        message.addFrom( toAddresses( this.from ) );
        message.addRecipients( Message.RecipientType.TO, toAddresses( this.to ) );
        message.addRecipients( Message.RecipientType.CC, toAddresses( this.cc ) );
        message.addRecipients( Message.RecipientType.BCC, toAddresses( this.bcc ) );
        message.setReplyTo( toAddresses( this.replyTo ) );

        if ( this.headers != null )
        {
            for ( Map.Entry<String, String> header : this.headers.entrySet() )
            {
                message.addHeader( header.getKey(), header.getValue() );
            }
        }

        final List<Attachment> attachmentList = getAttachments();
        if ( attachmentList.isEmpty() )
        {
            message.setText( nullToEmpty( this.body ), "UTF-8" );
            if ( this.contentType != null )
            {
                message.addHeader( "Content-Type", this.contentType );
            }
        }
        else
        {
            final Multipart multipart = new MimeMultipart();

            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText( nullToEmpty( this.body ), "UTF-8" );
            if ( this.contentType != null )
            {
                textPart.addHeader( "Content-Type", this.contentType );
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

    private InternetAddress[] toAddresses( final String[] addressList )
        throws Exception
    {
        return Stream.of( addressList ).filter( StringUtils::isNotBlank ).map( ( this::toAddress ) ).toArray( InternetAddress[]::new );
    }

    private List<Attachment> getAttachments()
    {
        if ( this.attachments == null )
        {
            return Collections.emptyList();
        }
        final List<Attachment> attachments = new ArrayList<>();
        for ( Map<String, Object> attachmentObject : this.attachments )
        {
            final String name = getValue( attachmentObject, "fileName", String.class );
            final ByteSource data = getValue( attachmentObject, "data", ByteSource.class );
            String mimeType = getValue( attachmentObject, "mimeType", String.class );
            final Map headers = getValue( attachmentObject, "headers", Map.class );
            if ( name != null && data != null )
            {
                mimeType = mimeType == null ? getMimeType( name ) : mimeType;
                attachments.add( new Attachment( name, data, mimeType, headers ) );
            }
        }
        return attachments;
    }

    private <T> T getValue( final Map<String, Object> object, final String key, final Class<T> type )
    {
        final Object value = object.get( key );
        if ( value != null && type.isInstance( value ) )
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }

    private static class Attachment
    {
        public final String name;

        public final ByteSource data;

        public final String mimeType;

        public final Map<String, String> headers;

        public Attachment( final String name, final ByteSource data, final String mimeType, final Map<String, String> headers )
        {
            this.name = name;
            this.data = data;
            this.mimeType = mimeType;
            this.headers = headers == null ? Collections.emptyMap() : headers;
        }
    }
}
