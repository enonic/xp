package com.enonic.cms.web.rest.account;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.enonic.cms.framework.util.ImageHelper;

import com.enonic.cms.core.image.filter.ImageFilter;
import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.web.rest.common.RestResponse;
import com.enonic.cms.web.rest.exception.EntityNotFoundException;
import com.enonic.cms.web.rest.exception.EntityRedirectException;

@Controller
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public final class UsersController
{

    private static final Logger LOG = LoggerFactory.getLogger( UsersController.class );

    private static final int PHOTO_CACHE_TIMEOUT = Period.minutes( 1 ).getSeconds();

    private static final int DEFAULT_IMAGE_SIZE = 100;

    private static final int DEFAULT_IMAGE_QUALITY = 100;

    private static final int DEFAULT_IMAGE_BACKGROUND_COLOR = 0x00FFFFFF;

    private static final String UPLOAD_PATH = "/admin/resources/uploads/";

    private static final String DEFAULT_IMAGE_PATH = "/admin/resources/images/icons/256x256/dummy-user.png";

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserPhotoService photoService;

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @Autowired
    private AccountSearchService searchService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public AccountsModel getAll( final UserLoadRequest req )
    {
        // TODO: Is this in use? If not, we will remove it. For now it only return dummy results.
        /*final EntityPageList<UserEntity> list =
                this.userDao.findAll( req.getStart(), req.getLimit(), req.buildHqlQuery(), req.buildHqlOrder() );
        return accountModelTranslator.toModel( list );*/
        return new AccountsModel();
    }

    @RequestMapping(value = "userinfo", method = RequestMethod.GET)
    @ResponseBody
    public AccountModel getUserInfo( @RequestParam("key") final String key )
    {
        final UserEntity entity = findEntity( key );
        return accountModelTranslator.toInfoModel( entity );
    }

    @RequestMapping(value = "photo", method = RequestMethod.GET,
                    produces = MediaType.IMAGE_PNG_VALUE)
    public HttpEntity<BufferedImage> getPhoto( @RequestParam("key") final String key, @RequestParam(value = "thumb",
                                                                                                    defaultValue = "false") final boolean thumb,
                                               @RequestParam(value = "def",
                                                             defaultValue = "") final String defaultImageUrl,
                                               WebRequest request )
            throws Exception
    {
        final String contextPath = request.getContextPath();
        final UserEntity entity = findEntity( key );
        if ( entity == null )
        {
            throw new EntityNotFoundException();
        }
        if ( entity.getPhoto() == null )
        {
            if ( defaultImageUrl == null )
            {
                throw new EntityNotFoundException();
            }
            else
            {
                String redirectUrl = defaultImageUrl.startsWith( "http://" )
                        ? defaultImageUrl
                        : String.format( "%s/%s", request.getContextPath(), defaultImageUrl );
                throw new EntityRedirectException( new URI( redirectUrl ) );
            }
        }

        final BufferedImage photo = this.photoService.renderPhoto( entity, thumb ? 40 : 100 );
        return new HttpEntity<BufferedImage>( photo );
    }

    @RequestMapping(value = "/photo", method = RequestMethod.POST,
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public PhotoRestResponse uploadPhoto( @RequestParam("file") MultipartFile multipartFile,
                                          HttpServletRequest request )
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

    @RequestMapping(value = "changepassword", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse changePassword( @RequestParam("newPassword") final String newPassword,
                                        @RequestParam("userKey") final String userKey )
    {

        RestResponse res = new RestResponse();
        if ( newPassword.length() <= 64 && newPassword.length() >= 8 )
        {
            LOG.info( "Password has been changed for user " + userKey );
            res.setSuccess( true );
        }
        else
        {
            res.setSuccess( false );
            res.setError( "Password is out of possible length" );
        }
        return res;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse deleteUser( @RequestParam("key") final List<String> key )
    {
        RestResponse res = new RestResponse();

        for ( String k : key )
        {
            LOG.info( "Delete account: " + k );
        }

        res.setSuccess( true );
        return res;
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
                StoreNewUserCommand command =
                        accountModelTranslator.getUserModelTranslator().toNewUserCommand( userData );
                UserKey userKey = userStoreService.storeNewUser( command );
                res.setUserkey( userKey.toString() );
                indexUser( userKey.toString() );
            }
            else
            {
                UpdateUserCommand command =
                        accountModelTranslator.getUserModelTranslator().toUpdateUserCommand( userData );
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
        boolean isValid =
                StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
                        StringUtils.isNotBlank( userData.getEmail() );
        return isValid;
    }

    private UserEntity findEntity( final String key )
    {
        if ( key == null )
        {
            return null;
        }

        final UserEntity entity = this.userDao.findByKey( key );
        if ( ( entity == null ) || entity.isDeleted() )
        {
            return null;
        }

        return entity;
    }

}
