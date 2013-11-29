package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.content.serializer.ContentDataJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.FormItemsJsonSerializer;

import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENTS_ROOT_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.NON_CONTENT_NODE_PREFIX;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;
import static org.apache.commons.lang.StringUtils.substringAfter;

final class ContentJcrMapper
{
    static final String DRAFT = "draft";

    static final String TYPE = "type";

    static final String FORM = "form";

    static final String DATA = "data";

    static final String SITE = NON_CONTENT_NODE_PREFIX + "site";

    static final String SITE_MODULE_CONFIGS = NON_CONTENT_NODE_PREFIX + "moduleConfigs";

    static final String SITE_TEMPLATE = "template";

    static final String SITE_MODULE_CONFIG = NON_CONTENT_NODE_PREFIX + "moduleConfig";

    static final String SITE_MODULE = "module";

    static final String SITE_CONFIG = "config";

    static final String PAGE = NON_CONTENT_NODE_PREFIX + "page";

    static final String PAGE_TEMPLATE = "template";

    static final String PAGE_CONFIG = "config";

    static final String CREATED_TIME = "createdTime";

    static final String MODIFIED_TIME = "modifiedTime";

    static final String CREATOR = "creator";

    static final String MODIFIER = "modifier";

    static final String OWNER = "owner";

    static final String NAME = "name";

    static final String DISPLAY_NAME = "displayName";

    static final String VERSION_ID = "versionId";

    private FormItemsJsonSerializer formItemsJsonSerializer = new FormItemsJsonSerializer();

    private ContentDataJsonSerializer contentDataSerializer = new ContentDataJsonSerializer();

    private RootDataSetJsonSerializer rootDataSetJsonSerializer = new RootDataSetJsonSerializer();

    void toJcr( final Content content, final Node contentNode )
        throws RepositoryException
    {
        contentNode.setProperty( NAME, content.getName() );
        contentNode.setProperty( DRAFT, content.isDraft() );
        contentNode.setProperty( TYPE, content.getType() != null ? content.getType().toString() : null );
        contentNode.setProperty( FORM, content.getForm() != null ? formItemsJsonSerializer.toString( content.getForm() ) : null );
        if ( content.getSite() != null )
        {
            Node siteNode = contentNode.addNode( SITE );
            siteToJcr( content.getSite(), siteNode );
        }
        if ( content.getPage() != null )
        {
            Node pageNode = contentNode.addNode( PAGE );
            pageToJcr( content.getPage(), pageNode );
        }
        contentNode.setProperty( DATA, contentDataSerializer.toString( content.getContentData() ) );
        setPropertyDateTime( contentNode, CREATED_TIME, content.getCreatedTime() );
        setPropertyDateTime( contentNode, MODIFIED_TIME, content.getModifiedTime() );
        contentNode.setProperty( CREATOR, content.getModifier() == null ? null : content.getCreator().toString() );
        contentNode.setProperty( MODIFIER, content.getModifier() == null ? null : content.getModifier().toString() );
        contentNode.setProperty( OWNER, content.getOwner() == null ? null : content.getOwner().toString() );
        contentNode.setProperty( DISPLAY_NAME, content.getDisplayName() );
        contentNode.setProperty( VERSION_ID, content.getVersionId().id() );
    }

    private void siteToJcr( final Site site, final Node siteNode )
        throws RepositoryException
    {
        siteNode.setProperty( SITE_TEMPLATE, site.getTemplate().toString() );
        Node moduleConfigsNode = siteNode.addNode( SITE_MODULE_CONFIGS );
        for ( ModuleConfig moduleConfig : site.getModuleConfigs() )
        {
            moduleConfigToJcr( moduleConfig, moduleConfigsNode );
        }
    }

    private void moduleConfigToJcr( final ModuleConfig moduleConfig, final Node moduleConfigsNode )
        throws RepositoryException
    {
        Node moduleConfigNode = moduleConfigsNode.addNode( SITE_MODULE_CONFIG );
        moduleConfigNode.setProperty( SITE_MODULE, moduleConfig.getModule().toString() );
        moduleConfigNode.setProperty( SITE_CONFIG, rootDataSetJsonSerializer.toString( moduleConfig.getConfig() ) );
    }

    private void pageToJcr( final Page page, final Node pageNode )
        throws RepositoryException
    {
        pageNode.setProperty( PAGE_TEMPLATE, page.getTemplateName().toString() );
        pageNode.setProperty( PAGE_CONFIG, rootDataSetJsonSerializer.toString( page.getConfig() ) );
    }

    void toContent( final Node contentNode, final Content.Builder contentBuilder )
        throws RepositoryException
    {
        contentBuilder.draft( contentNode.getProperty( DRAFT ).getBoolean() );
        if ( contentNode.hasProperty( FORM ) )
        {
            final Form form =
                Form.newForm().addFormItems( formItemsJsonSerializer.toObject( contentNode.getProperty( FORM ).getString() ) ).build();
            contentBuilder.form( form );
        }

        if ( contentNode.hasNode( SITE ) )
        {
            final Node siteNode = contentNode.getNode( SITE );
            final Site site = jcrToSite( siteNode );
            contentBuilder.site( site );
        }
        if ( contentNode.hasNode( PAGE ) )
        {
            final Node pageNode = contentNode.getNode( PAGE );
            final Page page = jcrToPage( pageNode );
            contentBuilder.page( page );
        }

        final ContentData contentData = contentDataSerializer.toObject( contentNode.getProperty( DATA ).getString() );
        contentBuilder.contentData( contentData );

        contentBuilder.createdTime( getPropertyDateTime( contentNode, CREATED_TIME ) );
        contentBuilder.modifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        if ( contentNode.hasProperty( MODIFIER ) )
        {
            contentBuilder.modifier( AccountKey.from( getPropertyString( contentNode, MODIFIER ) ).asUser() );
        }
        if ( contentNode.hasProperty( CREATOR ) )
        {
            contentBuilder.modifier( AccountKey.from( getPropertyString( contentNode, CREATOR ) ).asUser() );
        }
        if ( contentNode.hasProperty( OWNER ) )
        {
            contentBuilder.owner( AccountKey.from( getPropertyString( contentNode, OWNER ) ).asUser() );
        }
        contentBuilder.displayName( getPropertyString( contentNode, DISPLAY_NAME ) );
        final String contentType = getPropertyString( contentNode, TYPE );
        if ( contentType != null )
        {
            contentBuilder.type( ContentTypeName.from( contentType ) );
        }
        contentBuilder.id( ContentIdFactory.from( contentNode ) );
        contentBuilder.path( getPathFromNode( contentNode ) );
        if ( contentNode.hasProperty( VERSION_ID ) )
        {
            contentBuilder.version( ContentVersionId.of( getPropertyLong( contentNode, VERSION_ID ) ) );
        }
        NodeIterator children = contentNode.getNodes();
        while ( children.hasNext() )
        {
            Node child = children.nextNode();
            if ( !ContentJcrHelper.isNonContentNode( child ) )
            {
                contentBuilder.addChildId( ContentIdFactory.from( child ) );
            }
        }
    }

    private Site jcrToSite( final Node siteNode )
        throws RepositoryException
    {
        final Site.Builder site = Site.newSite();
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( getPropertyString( siteNode, SITE_TEMPLATE ) );
        site.template( siteTemplateKey );
        if ( siteNode.hasNode( SITE_MODULE_CONFIGS ) )
        {
            NodeIterator children = siteNode.getNode( SITE_MODULE_CONFIGS ).getNodes( SITE_MODULE_CONFIG );
            while ( children.hasNext() )
            {
                Node moduleConfigNode = children.nextNode();
                final ModuleConfig moduleConfig = jcrToModuleConfig( moduleConfigNode );
                site.addModuleConfig( moduleConfig );
            }
        }
        return site.build();
    }

    private ModuleConfig jcrToModuleConfig( final Node moduleConfigNode )
        throws RepositoryException
    {
        final ModuleKey moduleKey = ModuleKey.from( getPropertyString( moduleConfigNode, SITE_MODULE ) );
        final RootDataSet config = rootDataSetJsonSerializer.toObject( moduleConfigNode.getProperty( SITE_CONFIG ).getString() );
        return newModuleConfig().
            module( moduleKey ).
            config( config ).
            build();
    }

    private Page jcrToPage( final Node pageNode )
        throws RepositoryException
    {
        final PageTemplateName pageTemplate = new PageTemplateName( getPropertyString( pageNode, PAGE_TEMPLATE ) );
        final RootDataSet config = rootDataSetJsonSerializer.toObject( pageNode.getProperty( PAGE_CONFIG ).getString() );
        return Page.newPage().
            pageTemplateName( pageTemplate ).
            config( config ).
            build();
    }

    private ContentPath getPathFromNode( final Node contentNode )
        throws RepositoryException
    {
        final String contentNodePath;
        if ( contentNode.getParent().getName().equals( CONTENT_VERSION_HISTORY_NODE ) )
        {
            contentNodePath = contentNode.getParent().getParent().getPath();
        }
        else
        {
            contentNodePath = contentNode.getPath();
        }
        final String fullPath = substringAfter( contentNodePath, CONTENTS_ROOT_PATH );
        return ContentPath.from( fullPath );
    }
}
