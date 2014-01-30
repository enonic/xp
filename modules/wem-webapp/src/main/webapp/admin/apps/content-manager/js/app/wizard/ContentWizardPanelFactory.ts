module app.wizard {

    export class ContentWizardPanelFactory {

        private creatingForNew: boolean;

        private contentId: api.content.ContentId;

        private appBarTabId: api.app.AppBarTabId;

        private contentTypeName: api.schema.content.ContentTypeName;

        private contentToEdit: api.content.Content;

        private parentContent: api.content.Content;

        private contentType: api.schema.content.ContentType;

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private siteTemplate: api.content.site.template.SiteTemplate;

        private site: api.content.Content;

        setContentToEdit(value: api.content.ContentId): ContentWizardPanelFactory {
            this.contentId = value;
            return this;
        }

        setContentTypeName(value: api.schema.content.ContentTypeName): ContentWizardPanelFactory {
            this.contentTypeName = value;
            return this;
        }

        setParentContent(value: api.content.Content): ContentWizardPanelFactory {
            this.parentContent = value;
            return this;
        }

        setSiteTemplate(value: api.content.site.template.SiteTemplateKey): ContentWizardPanelFactory {
            this.siteTemplateKey = value;
            return this;
        }

        setAppBarTabId(value: api.app.AppBarTabId): ContentWizardPanelFactory {
            this.appBarTabId = value;
            return this;
        }

        createForNew(): Q.Promise<ContentWizardPanel> {

            this.creatingForNew = true;

            var deferred = Q.defer<ContentWizardPanel>();


            this.loadContentType(this.contentTypeName).then((loadedContentType: api.schema.content.ContentType) => {
                this.contentType = loadedContentType;

                return this.loadParentContent().then((loadedParentContent: api.content.Content) => {
                    this.parentContent = loadedParentContent;

                    var parentContentId = loadedParentContent != null ? loadedParentContent.getContentId() : null;

                    return this.loadSite(parentContentId).then((loadedSite: api.content.Content) => {
                        this.site = loadedSite;

                        return this.loadSiteTemplate(this.siteTemplateKey).then((loadedSiteTemplate: api.content.site.template.SiteTemplate) => {
                            this.siteTemplate = loadedSiteTemplate;

                            this.newContentWizardPanelForNew().done((wizardPanel: ContentWizardPanel)=> {
                                deferred.resolve(wizardPanel);
                            });
                        });
                    });
                });
            })

            return deferred.promise;
        }

        createForEdit(): Q.Promise<ContentWizardPanel> {

            this.creatingForNew = false;

            var deferred = Q.defer<ContentWizardPanel>();

            this.loadContentToEdit().then((loadedContentToEdit: api.content.Content) => {
                this.contentToEdit = loadedContentToEdit;

                return this.loadContentType(this.contentToEdit.getType()).then((loadedContentType: api.schema.content.ContentType) => {
                    this.contentType = loadedContentType;

                    return this.loadParentContent().then((loadedParentContent: api.content.Content) => {
                        this.parentContent = loadedParentContent;

                        return this.loadSite(this.contentId).then((loadedSite: api.content.Content) => {

                            var templateKey: api.content.site.template.SiteTemplateKey;
                            if (loadedSite && loadedSite.getSite()) {
                                this.site = loadedSite;
                                templateKey = this.site.getSite().getTemplateKey();
                            }

                            return this.loadSiteTemplate(templateKey).then((loadedSiteTemplate: api.content.site.template.SiteTemplate) => {
                                this.siteTemplate = loadedSiteTemplate;

                                this.newContentWizardPanelForEdit().done((wizardPanel: ContentWizardPanel)=> {
                                    deferred.resolve(wizardPanel);
                                });
                            });
                        });
                    });
                });
            });

            return deferred.promise;
        }

        private loadContentToEdit(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();
            new api.content.GetContentByIdRequest(this.contentId).
                sendAndParse().done((content: api.content.Content) => {
                    deferred.resolve(content);
                });
            return deferred.promise;
        }

        private loadContentType(name: api.schema.content.ContentTypeName): Q.Promise<api.schema.content.ContentType> {

            var deferred = Q.defer<api.schema.content.ContentType>();
            new api.schema.content.GetContentTypeByNameRequest(name).
                sendAndParse().done((contentType: api.schema.content.ContentType)=> {
                    deferred.resolve(contentType);
                });
            return deferred.promise;
        }

        private loadSite(contentId: api.content.ContentId): Q.Promise<api.content.Content> {
            var deferred = Q.defer<api.content.Content>();

            if (contentId == null) {
                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.site.GetNearestSiteRequest(contentId).
                sendAndParse().done((site: api.content.Content)=> {
                    deferred.resolve(site);
                });

            return deferred.promise;
        }

        private loadSiteTemplate(key: api.content.site.template.SiteTemplateKey): Q.Promise<api.content.site.template.SiteTemplate> {
            var deferred = Q.defer<api.content.site.template.SiteTemplate>();

            if (key == null) {
                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.site.template.GetSiteTemplateRequest(key).
                sendAndParse().done((siteTemplate: api.content.site.template.SiteTemplate)=> {
                    deferred.resolve(siteTemplate);
                });

            return deferred.promise;
        }

        private loadParentContent(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

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
                done((content: api.content.Content)=> {
                    deferred.resolve(content);
                });

            return deferred.promise;
        }

        private newContentWizardPanelForNew(): Q.Promise<app.wizard.ContentWizardPanel> {

            var deferred = Q.defer<app.wizard.ContentWizardPanel>();

            var wizardParams = new app.wizard.ContentWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setContentType(this.contentType).
                setParentContent(this.parentContent).
                setCreateSite(this.siteTemplate).
                setSite(this.site);

            // TODO: Configure ContentWizardPanel to open up support for editing site data
            var newSite = false;
            if (newSite) {

            }

            new app.wizard.ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                deferred.resolve(wizard);
            });

            return deferred.promise;
        }

        private newContentWizardPanelForEdit(): Q.Promise<app.wizard.ContentWizardPanel> {

            var deferred = Q.defer<app.wizard.ContentWizardPanel>();

            var wizardParams = new app.wizard.ContentWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setContentType(this.contentType).
                setParentContent(this.parentContent).
                setPersistedContent(this.contentToEdit).
                setSite(this.site).
                setSiteTemplate(this.siteTemplate);

            new app.wizard.ContentWizardPanel(wizardParams, (wizard: ContentWizardPanel) => {
                deferred.resolve(wizard);
            });

            return deferred.promise;
        }
    }
}