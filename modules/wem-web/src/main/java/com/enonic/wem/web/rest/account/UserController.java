package com.enonic.wem.web.rest.account;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.framework.util.ImageHelper;

import com.enonic.cms.core.image.filter.ImageFilter;
import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

@Controller
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public final class UserController
{

    private static final Logger LOG = LoggerFactory.getLogger( UserController.class );

    private static final int DEFAULT_IMAGE_SIZE = 100;

    private static final int DEFAULT_IMAGE_BACKGROUND_COLOR = 0x00FFFFFF;

    private static final String UPLOAD_PATH = "/admin/resources/uploads/";

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @Autowired
    private AccountSearchService searchService;


    @RequestMapping(value = "/photo", method = RequestMethod.POST,
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public PhotoRestResponse uploadPhoto( @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request )
    {
        PhotoRestResponse response = new PhotoRestResponse();
        try
        {
            final File folder = new File( request.getSession().getServletContext().getRealPath( UPLOAD_PATH ) );
            if ( !folder.exists() )
            {
                folder.mkdirs();
            }

            final String filename = StringUtils.substringBeforeLast( multipartFile.getOriginalFilename(), "." );
            final String extension = "." + StringUtils.substringAfterLast( multipartFile.getOriginalFilename(), "." );
            final File uploadFile = File.createTempFile( filename, extension, folder );

            multipartFile.transferTo( uploadFile );
            BufferedImage image = ImageIO.read( uploadFile );
            scaleImage( image, uploadFile );

            response.setSuccess( true );
            response.setSrc( "resources/uploads/" + uploadFile.getName() );
            response.setPhotoRef( uploadFile.getName() );
        }
        catch ( IOException e )
        {
            LOG.error( "Could not store uploaded photo", e );
            response.setSuccess( false );
        }
        return response;
    }

    private void scaleImage( BufferedImage image, File imageFile )
        throws IOException
    {
        ImageFilter scaleFilter = new ScaleSquareFilter( DEFAULT_IMAGE_SIZE );
        image = scaleFilter.filter( image );
        String type = StringUtils.substringAfterLast( imageFile.getAbsolutePath(), "." ).toUpperCase();
        if ( !ImageHelper.supportsAlphaChannel( type ) )
        {
            image = ImageHelper.removeAlphaChannel( image, DEFAULT_IMAGE_BACKGROUND_COLOR );
        }
        ImageIO.write( image, type, imageFile );

    }

    @RequestMapping(value = "update", method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserRestResponse saveUser( @RequestBody UserModel userData, final HttpServletRequest request )
    {
        final boolean isValid = isValidUserData( userData );
        final UserRestResponse res = new UserRestResponse();
        if ( isValid )
        {
            final String photoRef = userData.getPhoto();
            if ( StringUtils.isNotEmpty( photoRef ) )
            {
                ServletContext context = request.getSession().getServletContext();
                final File photoFile = new File( context.getRealPath( UPLOAD_PATH ), photoRef );
                if ( photoFile.exists() )
                {
                    userData.setPhoto( photoFile.getAbsolutePath() );
                }
                else
                {
                    userData.setPhoto( null );
                }
            }

            if ( userData.getKey() == null )
            {
                StoreNewUserCommand command = accountModelTranslator.getUserModelTranslator().toNewUserCommand( userData );
                UserKey userKey = userStoreService.storeNewUser( command );
                res.setUserkey( userKey.toString() );
                indexUser( userKey.toString() );
            }
            else
            {
                UpdateUserCommand command = accountModelTranslator.getUserModelTranslator().toUpdateUserCommand( userData );
                userStoreService.updateUser( command );
                res.setUserkey( userData.getKey() );
                indexUser( userData.getKey() );
            }
            res.setSuccess( true );
        }
        else
        {
            res.setSuccess( false );
            res.setError( "Validation failed" );
        }
        return res;
    }

    private void indexUser( String userKey )
    {
        final UserEntity userEntity = this.userDao.findByKey( userKey );
        if ( userEntity == null )
        {
            searchService.deleteIndex( userKey );
            return;
        }

        final com.enonic.wem.core.search.account.User user = new com.enonic.wem.core.search.account.User();
        user.setKey( new AccountKey( userEntity.getKey().toString() ) );
        user.setName( userEntity.getName() );
        user.setEmail( userEntity.getEmail() );
        user.setDisplayName( userEntity.getDisplayName() );
        user.setUserStoreName( userEntity.getUserStore().getName() );
        user.setLastModified( userEntity.getTimestamp() );
        user.setUserInfo( UserInfoHelper.toUserInfo( userEntity ) );
        final AccountIndexData accountIndexData = new AccountIndexData( user );
        searchService.index( accountIndexData );
    }

    private boolean isValidUserData( UserModel userData )
    {
        boolean isValid = StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
            StringUtils.isNotBlank( userData.getEmail() );
        return isValid;
    }

}
