module app.wizard {

    import ModuleKey = api.module.ModuleKey;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Content = api.content.Content;
    import ContentType = api.schema.content.ContentType;
    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import DefaultModels = app.wizard.page.DefaultModels;
    import DefaultModelsFactoryConfig = app.wizard.page.DefaultModelsFactoryConfig;
    import DefaultModelsFactory = app.wizard.page.DefaultModelsFactory;

    export class ContentWizardPanelFactory {

        private creatingForNew: boolean;

        private createSite: boolean = false;

        private contentId: ContentId;

        private appBarTabId: api.app.AppBarTabId;

        private contentTypeName: ContentTypeName;

        private contentToEdit: Content;

        private parentContent: Content;

        private contentType: ContentType;

        private siteTemplateKey: SiteTemplateKey;

        private siteTemplate: SiteTemplate;

        private siteContent: Content;

        private defaultModels: DefaultModels;

        setContentToEdit(value: ContentId): ContentWizardPanelFactory {
            this.contentId = value;
            return this;
        }

        setContentTypeName(value: ContentTypeName): ContentWizardPanelFactory {
            this.contentTypeName = value;
            return this;
        }

        setParentContent(value: Content): ContentWizardPanelFactory {
            this.parentContent = value;
            return this;
        }

        setCreateSite(value: SiteTemplateKey): ContentWizardPanelFactory {
            this.siteTemplateKey = value;
            this.createSite = true;
            return this;
        }

        setAppBarTabId(value: api.app.AppBarTabId): ContentWizardPanelFactory {
            this.appBarTabId = value;
            return this;
        }

        createForNew(): wemQ.Promise<ContentWizardPanel> {

            this.creatingForNew = true;

            return this.loadContentType(this.contentTypeName).then((loadedContentType: ContentType) => {

                this.contentType = loadedContentType;
                return this.loadParentContent();

            }).then((loadedParentContent: Content) => {

                this.parentContent = loadedParentContent;
                return this.loadSite(loadedParentContent ? loadedParentContent.getContentId() : null);

            }).then((loadedSite: Content) => {

                this.siteContent = loadedSite;
                var siteTemplateToLoad: SiteTemplateKey = this.siteTemplateKey;
                if (!siteTemplateToLoad && this.siteContent) {
                    siteTemplateToLoad = this.siteContent.getSite().getTemplateKey();
                }

                return this.loadSiteTemplate(siteTemplateToLoad);

            }).then((loadedSiteTemplate: SiteTemplate) => {

                this.siteTemplate = loadedSiteTemplate;
                return this.loadDefaultModels(this.siteTemplate, this.contentTypeName);

            }).then((defaultModels: DefaultModels) => {

                this.defaultModels = defaultModels;
                return this.newContentWizardPanelForNew();

            });
        }

        createForEdit(): wemQ.Promise<ContentWizardPanel> {

            this.creatingForNew = false;

            return this.loadContentToEdit().then((loadedContentToEdit: Content) => {

                this.contentToEdit = loadedContentToEdit;
                return this.loadContentType(this.contentToEdit.getType());

            }).then((loadedContentType: ContentType) => {

                this.contentType = loadedContentType;
                return this.loadParentContent();

            }).then((loadedParentContent: Content) => {

                this.parentContent = loadedParentContent;
                return this.loadSite(this.contentId);

            }).then((loadedSite: Content) => {

                var templateKey: SiteTemplateKey;
                if (loadedSite && loadedSite.getSite()) {
                    this.siteContent = loadedSite;
                    templateKey = this.siteContent.getSite().getTemplateKey();
                }
                return this.loadSiteTemplate(templateKey);

            }).then((loadedSiteTemplate: SiteTemplate) => {

                this.siteTemplate = loadedSiteTemplate;
                return this.loadDefaultModels(this.siteTemplate, this.contentToEdit.getType());

            }).then((defaultModels: DefaultModels) => {

                this.defaultModels = defaultModels;
                return this.newContentWizardPanelForEdit();

            });
        }

        private loadContentToEdit(): wemQ.Promise<Content> {
            return new api.content.GetContentByIdRequest(this.contentId).sendAndParse();
        }

        private loadContentType(name: ContentTypeName): wemQ.Promise<ContentType> {
            return new api.schema.content.GetContentTypeByNameRequest(name).sendAndParse();
        }

        private loadSite(contentId: ContentId): wemQ.Promise<Content> {
            return contentId ? new api.content.site.GetNearestSiteRequest(contentId).sendAndParse() : Q<Content>(null);
        }

        private loadSiteTemplate(key: SiteTemplateKey): wemQ.Promise<SiteTemplate> {
            return key ? new api.content.site.template.GetSiteTemplateRequest(key).sendAndParse() : Q<SiteTemplate>(null);
        }

        private loadDefaultModels(siteTemplate: SiteTemplate, contentType: ContentTypeName): wemQ.Promise<DefaultModels> {

            if (siteTemplate) {
                return DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                    siteTemplateKey: siteTemplate.getKey(),
                    contentType: contentType,
                    modules: siteTemplate.getModules()
                });
            }
            else {
                return Q<DefaultModels>(null);
            }
        }

        private loadParentContent(): wemQ.Promise<Content> {

            if (this.parentContent != null) {
                return Q(this.parentContent);
            }
            else if (!this.creatingForNew && !this.contentToEdit.hasParent()) {
                return  Q<Content>(null);
            }
            else if (this.creatingForNew && this.parentContent == null) {
                return  Q<Content>(null);
            }

            return new api.content.GetContentByPathRequest(this.contentToEdit.getPath().getParentPath()).sendAndParse();
        }

        private newContentWizardPanelForNew(): wemQ.Promise<app.wizard.ContentWizardPanel> {

            var deferred = wemQ.defer<app.wizard.ContentWizardPanel>();

            var wizardParams = new app.wizard.ContentWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setContentType(this.contentType).
                setParentContent(this.parentContent).
                setSite(this.siteContent).
                setDefaultModels(this.defaultModels);

            if (this.createSite) {
                if (this.siteTemplate) {
                    wizardParams.setCreateSite(this.siteTemplate);
                } else {
                    wizardParams.setCreateSiteWithoutTemplate();
                }
            }
            else if (this.siteTemplate) {
                wizardParams.setSiteTemplate(this.siteTemplate);
            }

            new app.wizard.ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                deferred.resolve(wizard);
            });

            return deferred.promise;
        }

        private newContentWizardPanelForEdit(): wemQ.Promise<app.wizard.ContentWizardPanel> {

            var deferred = wemQ.defer<ContentWizardPanel>();

            var wizardParams = new ContentWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setContentType(this.contentType).
                setParentContent(this.parentContent).
                setPersistedContent(this.contentToEdit).
                setSite(this.siteContent).
                setSiteTemplate(this.siteTemplate).
                setDefaultModels(this.defaultModels);

            new ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                deferred.resolve(wizard);
            });

            return deferred.promise;
        }
    }
}