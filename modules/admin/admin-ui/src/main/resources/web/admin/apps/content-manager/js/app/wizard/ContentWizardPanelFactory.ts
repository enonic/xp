module app.wizard {

    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Content = api.content.Content;
    import ContentSummary = api.content.ContentSummary;
    import Site = api.content.site.Site;
    import ContentType = api.schema.content.ContentType;
    import DefaultModels = app.wizard.page.DefaultModels;
    import DefaultModelsFactoryConfig = app.wizard.page.DefaultModelsFactoryConfig;
    import DefaultModelsFactory = app.wizard.page.DefaultModelsFactory;

    export class ContentWizardPanelFactory {

        private creatingForNew: boolean;

        private createSite: boolean = false;

        private contentId: ContentId;

        private appBarTabId: api.app.bar.AppBarTabId;

        private contentTypeName: ContentTypeName;

        private contentToEdit: Content;

        private parentContent: Content;

        private contentType: ContentType;

        private siteContent: Site;

        private defaultModels: DefaultModels;

        setContentIdToEdit(value: ContentId): ContentWizardPanelFactory {
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

        setCreateSite(value: boolean): ContentWizardPanelFactory {
            this.createSite = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): ContentWizardPanelFactory {
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

            }).then((loadedSite: Site) => {

                this.siteContent = loadedSite;
                return this.loadDefaultModels(this.siteContent, this.contentTypeName);

            }).then((defaultModels: DefaultModels) => {

                this.defaultModels = defaultModels;
                return this.newContentWizardPanelForNew();

            });
        }

        createForEdit(content: ContentSummary): wemQ.Promise<ContentWizardPanel> {

            this.creatingForNew = false;

            return this.getContentToEdit(content).then((contentType: ContentType) => {

                this.contentType = contentType;
                return this.loadParentContent();

            }).then((loadedParentContent: Content) => {

                this.parentContent = loadedParentContent;
                return this.loadSite(this.contentId);

            }).then((loadedSite: Site) => {

                if (!!loadedSite) {
                    this.siteContent = loadedSite;
                }
                return this.loadDefaultModels(this.siteContent, this.contentToEdit.getType());

            }).then((defaultModels: DefaultModels) => {

                this.defaultModels = defaultModels;
                return this.newContentWizardPanelForEdit();

            });
        }

        private loadContentToEdit(): wemQ.Promise<Content> {
            return new api.content.GetContentByIdRequest(this.contentId).sendAndParse();
        }

        private getContentToEdit(content: ContentSummary): wemQ.Promise<ContentType> {
            if (api.ObjectHelper.iFrameSafeInstanceOf(content, Content)) {
                this.contentToEdit = <Content>content;
                return this.loadContentType(this.contentToEdit.getType());
            }
            else {
                return this.loadContentToEdit().then((loadedContentToEdit: Content) => {
                    this.contentToEdit = loadedContentToEdit;
                    return this.loadContentType(this.contentToEdit.getType());

                });
            }
        }

        private loadContentType(name: ContentTypeName): wemQ.Promise<ContentType> {
            var deferred = wemQ.defer<ContentType>();
            new api.schema.content.GetContentTypeByNameRequest(name).sendAndParse().
                then((contentType) => {
                    deferred.resolve(contentType);
                }).catch((reason) => {
                    deferred.reject(new api.Exception("Content cannot be opened. Required content type '" + name.toString() +
                                                      "' not found.",
                        api.ExceptionType.WARNING));
                }).done();
            return deferred.promise;
        }

        private loadSite(contentId: ContentId): wemQ.Promise<Site> {
            return contentId ? new api.content.GetNearestSiteRequest(contentId).sendAndParse() : wemQ<Site>(null);
        }

        private loadDefaultModels(site: Site, contentType: ContentTypeName): wemQ.Promise<DefaultModels> {

            if (site) {
                return DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                    siteId: site.getContentId(),
                    contentType: contentType,
                    applications: site.getApplicationKeys()
                });
            }
            else if (contentType.isSite()) {
                return wemQ<DefaultModels>(new DefaultModels(null, null));
            }
            else {
                return wemQ<DefaultModels>(null);
            }
        }

        private loadParentContent(): wemQ.Promise<Content> {

            if (this.parentContent != null) {
                return wemQ(this.parentContent);
            }
            else if (!this.creatingForNew && !this.contentToEdit.hasParent()) {
                return wemQ<Content>(null);
            }
            else if (this.creatingForNew && this.parentContent == null) {
                return wemQ<Content>(null);
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

            wizardParams.setCreateSite(this.createSite);

            new app.wizard.ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                    deferred.resolve(wizard);
                },
                (reason) => {
                    deferred.reject(reason)
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
                setDefaultModels(this.defaultModels);

            new ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                    deferred.resolve(wizard);
                },
                (reason) => {
                    deferred.reject(reason)
                });

            return deferred.promise;
        }
    }
}