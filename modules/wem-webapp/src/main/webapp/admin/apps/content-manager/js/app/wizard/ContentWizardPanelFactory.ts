module app.wizard {

    import ModuleKey = api.module.ModuleKey;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Content = api.content.Content;
    import ContentType = api.schema.content.ContentType;
    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import DefaultModels = page.DefaultModels;
    import DefaultModelsFactoryConfig = page.DefaultModelsFactoryConfig;
    import DefaultModelsFactory = page.DefaultModelsFactory;

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

        createForNew(): Q.Promise<ContentWizardPanel> {

            this.creatingForNew = true;

            var deferred = Q.defer<ContentWizardPanel>();


            this.loadContentType(this.contentTypeName).then((loadedContentType: ContentType) => {
                this.contentType = loadedContentType;

                return this.loadParentContent().then((loadedParentContent: Content) => {
                    this.parentContent = loadedParentContent;

                    var parentContentId = loadedParentContent != null ? loadedParentContent.getContentId() : null;

                    return this.loadSite(parentContentId).then((loadedSite: Content) => {
                        this.siteContent = loadedSite;

                        var siteTemplateToLoad: SiteTemplateKey = this.siteTemplateKey;
                        if (siteTemplateToLoad == null && this.siteContent) {
                            siteTemplateToLoad = this.siteContent.getSite().getTemplateKey();
                        }

                        return this.loadSiteTemplate(siteTemplateToLoad).then((loadedSiteTemplate: SiteTemplate) => {
                            this.siteTemplate = loadedSiteTemplate;

                            return this.loadDefaultModels(this.siteTemplate, this.contentTypeName).
                                then((defaultModels: DefaultModels) => {

                                    this.defaultModels = defaultModels;

                                    this.newContentWizardPanelForNew().then((wizardPanel: ContentWizardPanel)=> {
                                        deferred.resolve(wizardPanel);
                                    }).catch((reason) => {
                                        deferred.reject(reason);
                                    }).done();
                                });
                        });
                    });
                });
            }).catch((reason) => {
                deferred.reject(reason);
            }).done();

            return deferred.promise;
        }

        createForEdit(): Q.Promise<ContentWizardPanel> {

            this.creatingForNew = false;

            var deferred = Q.defer<ContentWizardPanel>();

            this.loadContentToEdit().then((loadedContentToEdit: Content) => {
                this.contentToEdit = loadedContentToEdit;

                return this.loadContentType(this.contentToEdit.getType()).then((loadedContentType: ContentType) => {
                    this.contentType = loadedContentType;

                    return this.loadParentContent().then((loadedParentContent: Content) => {
                        this.parentContent = loadedParentContent;

                        return this.loadSite(this.contentId).then((loadedSite: Content) => {

                            var templateKey: SiteTemplateKey;
                            if (loadedSite && loadedSite.getSite()) {
                                this.siteContent = loadedSite;
                                templateKey = this.siteContent.getSite().getTemplateKey();
                            }

                            return this.loadSiteTemplate(templateKey).then((loadedSiteTemplate: SiteTemplate) => {
                                this.siteTemplate = loadedSiteTemplate;

                                return this.loadDefaultModels(this.siteTemplate, this.contentToEdit.getType()).
                                    then((defaultModels: DefaultModels) => {

                                        this.defaultModels = defaultModels;

                                        this.newContentWizardPanelForEdit().
                                            then((wizardPanel: ContentWizardPanel)=> {
                                                deferred.resolve(wizardPanel);

                                            }).catch((reason) => {
                                                deferred.reject(reason);
                                            }).done();
                                    });
                            });
                        });
                    });
                });
            }).catch((reason) => {
                deferred.reject(reason);
            }).done();

            return deferred.promise;
        }

        private loadContentToEdit(): Q.Promise<Content> {

            var deferred = Q.defer<Content>();
            new api.content.GetContentByIdRequest(this.contentId).
                sendAndParse().then((content: Content) => {
                    deferred.resolve(content);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();
            return deferred.promise;
        }

        private loadContentType(name: ContentTypeName): Q.Promise<ContentType> {

            var deferred = Q.defer<ContentType>();
            new api.schema.content.GetContentTypeByNameRequest(name).
                sendAndParse().then((contentType: ContentType)=> {
                    deferred.resolve(contentType);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();
            return deferred.promise;
        }

        private loadSite(contentId: ContentId): Q.Promise<Content> {
            var deferred = Q.defer<Content>();

            if (contentId == null) {
                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.site.GetNearestSiteRequest(contentId).
                sendAndParse().then((site: Content)=> {
                    deferred.resolve(site);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        private loadSiteTemplate(key: SiteTemplateKey): Q.Promise<SiteTemplate> {
            var deferred = Q.defer<SiteTemplate>();

            if (key == null) {
                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.site.template.GetSiteTemplateRequest(key).
                sendAndParse().then((siteTemplate: SiteTemplate)=> {
                    deferred.resolve(siteTemplate);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        private loadDefaultModels(siteTemplate: SiteTemplate, contentType: ContentTypeName): Q.Promise<DefaultModels> {

            var deferred = Q.defer<DefaultModels>();

            if (siteTemplate) {
                DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                    siteTemplateKey: siteTemplate.getKey(),
                    contentType: contentType,
                    modules: siteTemplate.getModules()
                }).then((defaultModels: DefaultModels) => {

                    deferred.resolve(defaultModels);

                }).catch((reason) => {
                    deferred.reject(reason)
                }).done();
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private loadParentContent(): Q.Promise<Content> {

            var deferred = Q.defer<Content>();

            if (this.parentContent != null) {
                deferred.resolve(this.parentContent);
                return deferred.promise;
            }
            else if (!this.creatingForNew && !this.contentToEdit.hasParent()) {

                deferred.resolve(null);
                return deferred.promise;
            }
            else if (this.creatingForNew && this.parentContent == null) {

                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.GetContentByPathRequest(this.contentToEdit.getPath().getParentPath()).
                sendAndParse().
                then((content: Content)=> {
                    deferred.resolve(content);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        private newContentWizardPanelForNew(): Q.Promise<app.wizard.ContentWizardPanel> {

            var deferred = Q.defer<app.wizard.ContentWizardPanel>();

            var wizardParams = new app.wizard.ContentWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setContentType(this.contentType).
                setParentContent(this.parentContent).
                setSite(this.siteContent).
                setDefaultModels(this.defaultModels);

            if (this.createSite) {
                wizardParams.setCreateSite(this.siteTemplate);
            }
            else {
                wizardParams.setSiteTemplate(this.siteTemplate);
            }


            new app.wizard.ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                deferred.resolve(wizard);
            });

            return deferred.promise;
        }

        private newContentWizardPanelForEdit(): Q.Promise<app.wizard.ContentWizardPanel> {

            var deferred = Q.defer<ContentWizardPanel>();

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