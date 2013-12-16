module app_wizard {

    export class ContentWizardPanelFactory {

        private contentId: api_content.ContentId;

        private appBarTabId: api_app.AppBarTabId;

        private contentTypeName: api_schema_content.ContentTypeName;

        private contentToEdit: api_content.Content;

        private parentContent: api_content.Content;

        private contentType: api_schema_content.ContentType;

        private site: api_content.Content;

        setContentToEdit(value: api_content.ContentId): ContentWizardPanelFactory {
            this.contentId = value;
            return this;
        }

        setContentTypeName(value: api_schema_content.ContentTypeName): ContentWizardPanelFactory {
            this.contentTypeName = value;
            return this;
        }

        setParentContent(value: api_content.Content): ContentWizardPanelFactory {
            this.parentContent = value;
            return this;
        }

        setAppBarTabId(value: api_app.AppBarTabId): ContentWizardPanelFactory {
            this.appBarTabId = value;
            return this;
        }

        createForNew(): Q.Promise<ContentWizardPanel> {

            var deferred = Q.defer<ContentWizardPanel>();


            this.loadContentType(this.contentTypeName).then((loadedContentType: api_schema_content.ContentType) => {
                this.contentType = loadedContentType;

                return this.loadParentContent().then((loadedParentContent: api_content.Content) => {
                    this.parentContent = loadedParentContent;

                    return this.loadSite(loadedParentContent.getContentId()).then((loadedSite: api_content.Content) => {
                        this.site = loadedSite;

                        deferred.resolve(this.newContentWizardPanelForNew());
                    });
                });
            })

            return deferred.promise;
        }

        createForEdit(): Q.Promise<ContentWizardPanel> {

            var deferred = Q.defer<ContentWizardPanel>();

            this.loadContentToEdit().then((loadedContentToEdit: api_content.Content) => {
                this.contentToEdit = loadedContentToEdit;

                return this.loadContentType(this.contentToEdit.getType()).then((loadedContentType: api_schema_content.ContentType) => {
                    this.contentType = loadedContentType;

                    return this.loadSite(this.contentId).then((loadedSite: api_content.Content) => {
                        this.site = loadedSite;

                        return this.loadParentContent().then((loadedParentContent: api_content.Content) => {
                            this.parentContent = loadedParentContent;

                            deferred.resolve(this.newContentWizardPanelForEdit());
                        });
                    });
                });
            });

            return deferred.promise;
        }

        private loadContentToEdit(): Q.Promise<api_content.Content> {

            var deferred = Q.defer<api_content.Content>();
            new api_content.GetContentByIdRequest(this.contentId).
                sendAndParse().done((content: api_content.Content) => {
                    deferred.resolve(content);
                });
            return deferred.promise;
        }

        private loadContentType(name:api_schema_content.ContentTypeName): Q.Promise<api_schema_content.ContentType> {

            var deferred = Q.defer<api_schema_content.ContentType>();
            new api_schema_content.GetContentTypeByNameRequest(name).
                sendAndParse().done((contentType: api_schema_content.ContentType)=> {
                    deferred.resolve(contentType);
                });
            return deferred.promise;
        }

        private loadSite(contentId:api_content.ContentId): Q.Promise<api_content.Content> {
            var deferred = Q.defer<api_content.Content>();

            new api_content_site.GetNearestSiteRequest(contentId).
                sendAndParse().done((site: api_content.Content)=> {
                    deferred.resolve(site);
                });

            return deferred.promise;
        }

        private loadParentContent(): Q.Promise<api_content.Content> {

            var deferred = Q.defer<api_content.Content>();

            if (this.parentContent != null) {
                deferred.resolve(this.parentContent);
                return deferred.promise;
            }
            if (!this.contentToEdit.hasParent()) {

                deferred.resolve(null);
                return deferred.promise;
            }

            new api_content.GetContentByPathRequest(this.contentToEdit.getPath().getParentPath()).
                sendAndParse().
                done((content: api_content.Content)=> {
                    deferred.resolve(content);
                });

            return deferred.promise;
        }

        private newContentWizardPanelForNew(): app_wizard.ContentWizardPanel {

            var newSite: boolean = false;
            if (newSite) {
                var siteWizardPanel = new app_wizard.SiteWizardPanel(this.appBarTabId, this.contentType, this.parentContent,
                    null, this.site);
                return siteWizardPanel;
            }
            else {
                var contentWizardPanel = new app_wizard.ContentWizardPanel(this.appBarTabId, this.contentType, this.parentContent,
                    null, this.site);
                return contentWizardPanel;
            }
        }

        private newContentWizardPanelForEdit(): app_wizard.ContentWizardPanel {

            if (this.contentToEdit.isSite()) {
                var siteWizardPanel = new app_wizard.SiteWizardPanel(this.appBarTabId, this.contentType, this.parentContent,
                    this.contentToEdit, this.site);
                return siteWizardPanel;
            }
            else {
                var contentWizardPanel = new app_wizard.ContentWizardPanel(this.appBarTabId, this.contentType, this.parentContent,
                    this.contentToEdit, this.site);
                return contentWizardPanel;
            }
        }
    }
}