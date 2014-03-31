module app.wizard {

    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentName = api.content.ContentName;
    import Page = api.content.page.Page;
    import ContentType = api.schema.content.ContentType;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplate = api.content.page.PageTemplate;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import IsRenderableRequest = api.content.page.IsRenderableRequest;
    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepForm = api.app.wizard.WizardStepForm;

    export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

        private parentContent: Content;

        private siteContent: Content;

        private contentType: ContentType;

        private formIcon: api.app.wizard.FormIcon;

        private contentWizardHeader: api.app.wizard.WizardHeaderWithDisplayNameAndName;

        private siteWizardStepForm: app.wizard.site.SiteWizardStepForm;

        private contentWizardStepForm: ContentWizardStepForm;

        private iconUploadItem: api.ui.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private persistAsDraft: boolean;

        private createSite: boolean;

        private siteTemplate: SiteTemplate;

        private previewAction: api.ui.Action;

        private publishAction: api.ui.Action;

        private contextWindowToggler: ContextWindowToggler;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            console.log("ContentWizardPanel.constructor started");

            this.persistAsDraft = true;
            this.parentContent = params.parentContent;
            this.siteContent = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();
            var iconUrl = api.content.ContentIconUrlResolver.default();
            this.formIcon = new api.app.wizard.FormIcon(iconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {

                this.iconUploadItem = event.getUploadItem();
                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.iconUploadItem.getBlobKey()));
            });

            var actions = new app.wizard.action.ContentWizardActions(this);
            this.previewAction = actions.getPreviewAction();
            this.publishAction = actions.getPublishAction();

            var mainToolbar = new ContentWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction(),
                publishAction: actions.getPublishAction(),
                previewAction: actions.getPreviewAction(),
                showLiveEditAction: actions.getShowLiveEditAction(),
                showFormAction: actions.getShowFormAction()
            });

            this.contextWindowToggler = mainToolbar.getContextWindowToggler();
            this.showLiveEditAction = actions.getShowLiveEditAction();
            this.showLiveEditAction.setEnabled(false);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().toString() + "/");
            } else {
                this.contentWizardHeader.setPath("/");
            }

            this.createSite = params.createSite;
            this.siteTemplate = params.siteTemplate;
            if (this.createSite || params.persistedContent != null && params.persistedContent.isSite()) {
                this.siteWizardStepForm = new app.wizard.site.SiteWizardStepForm();
            }
            else {
                this.siteWizardStepForm = null;
            }
            this.contentWizardStepForm = new ContentWizardStepForm(this.publishAction);

            if (this.siteContent || this.createSite) {

                this.liveFormPanel = new page.LiveFormPanel(<page.LiveFormPanelConfig> {
                    contentWizardPanel: this, siteTemplate: this.siteTemplate});
            }

            if (this.contentType.hasContentDisplayNameScript()) {

                this.displayNameScriptExecutor.setScript(this.contentType.getContentDisplayNameScript());
            }


            super({
                tabId: params.tabId,
                persistedItem: params.persistedContent,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: this.liveFormPanel,
                steps: this.createSteps(params.persistedContent)
            }, () => {
                console.log("ContentWizardPanel.constructor finished");
                callback(this);
            });

            this.onShown((event) => {
                if (this.getPersistedItem()) {
                    app.Router.setHash("edit/" + this.getPersistedItem().getId());
                } else {
                    app.Router.setHash("new/" + this.contentType.getName());
                }
            });
        }

        giveInitialFocus() {
            //console.log("ContentWizardPanel.giveInitialFocus");

            var newWithoutDisplayCameScript = this.isRenderingNew() && !this.contentType.hasContentDisplayNameScript();
            var displayNameEmpty = api.util.isStringEmpty(this.getPersistedItem().getDisplayName());
            var editWithEmptyDisplayName = !this.isRenderingNew() && displayNameEmpty && !this.contentType.hasContentDisplayNameScript();

            if (newWithoutDisplayCameScript || editWithEmptyDisplayName) {
                this.contentWizardHeader.giveFocus();
            }
            else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    this.contentWizardHeader.giveFocus();
                }
            }

            this.startRememberFocus();
        }

        private createSteps(content: Content): WizardStep[] {

            var steps: WizardStep[] = [];

            steps.push(new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));

            if (this.siteWizardStepForm != null) {
                steps.push(new WizardStep("Site", this.siteWizardStepForm));
            }

            steps.push(new WizardStep("Meta", new WizardStepForm()));
            steps.push(new WizardStep("Security", new WizardStepForm()));
            steps.push(new WizardStep("Summary", new WizardStepForm()));

            return steps;
        }


        preRenderNew(): Q.Promise<void> {
            //console.log("ContentWizardPanel.preRenderNew");
            var deferred = Q.defer<void>();

            // Ensure a nameless and empty content is persisted before rendering new
            this.saveChanges().
                done(() => {
                    deferred.resolve(null);
                });

            return deferred.promise;
        }

        postRenderNew(): Q.Promise<void> {
            //console.log("ContentWizardPanel.postRenderNew");
            var deferred = Q.defer<void>();

            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                true);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedContent: Content): Q.Promise<void> {
            console.log("ContentWizardPanel.layoutPersistedItem");

            var deferred = Q.defer<void>();

            this.previewAction.setEnabled(persistedContent.isPage());

            this.formIcon.setSrc(persistedContent.getIconUrl());
            var contentData: api.content.ContentData = persistedContent.getContentData();

            new IsRenderableRequest(persistedContent.getContentId()).sendAndParse().
                done((renderable: boolean) => {
                    this.showLiveEditAction.setVisible(renderable);
                    this.showLiveEditAction.setEnabled(renderable);
                    this.previewAction.setVisible(renderable);
                    this.contextWindowToggler.setVisible(renderable);
                });

            new api.content.attachment.GetAttachmentsRequest(persistedContent.getContentId()).
                sendAndParse().
                done((attachmentsArray: api.content.attachment.Attachment[]) => {

                    var attachments = new api.content.attachment.AttachmentsBuilder().
                        addAll(attachmentsArray).
                        build();

                    var formContext = new api.form.FormContextBuilder().
                        setParentContent(this.parentContent).
                        setPersistedContent(persistedContent).
                        setAttachments(attachments).
                        setShowEmptyFormItemSetOccurrences(this.isNew()).
                        build();

                    this.contentWizardStepForm.renderExisting(formContext, contentData, persistedContent.getForm());
                    // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                    this.doRenderExistingSite(persistedContent, formContext)
                        .then(() => {

                            if (this.liveFormPanel) {
                                this.doRenderExistingPage(persistedContent, this.siteContent, formContext).
                                    then(() => {

                                        deferred.resolve(null);
                                    });
                            }
                            else {
                                deferred.resolve(null);
                            }
                        });
                });

            return deferred.promise;
        }

        postRenderExisting(existing: Content): Q.Promise<void> {
            console.log("ContentWizardPanel.postRenderExisting");
            var deferred = Q.defer<void>();

            this.contentWizardHeader.initNames(existing.getDisplayName(), existing.getName().toString(),
                false);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());

            deferred.resolve(null);
            return deferred.promise;
        }

        private doRenderExistingSite(content: Content, formContext: api.form.FormContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            if (this.siteWizardStepForm != null && content.getSite()) {
                this.siteWizardStepForm.renderExisting(formContext, content.getSite(), this.contentType).
                    done(() => {
                        deferred.resolve(null);
                    });
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doRenderExistingPage(content: Content, siteContent: Content, formContext: api.form.FormContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.layout(content, siteContent).
                done(() => {
                    deferred.resolve(null);
                });


            return deferred.promise;
        }

        private layout(content: Content, siteContent: Content): Q.Promise<void> {

            var deferred = Q.defer<void>();

            var page: Page = content.getPage();

            if (page != null && page.getTemplate() != null) {

                new GetPageTemplateByKeyRequest(page.getTemplate()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().done((pageTemplate: PageTemplate) => {

                        this.liveFormPanel.setPage(content, pageTemplate).done(() => {
                            deferred.resolve(null);
                        });
                    });
            }
            else {
                this.liveFormPanel.setPage(content, null).done( () => {
                    deferred.resolve(null);
                });
            }

            return deferred.promise;
        }

        persistNewItem(): Q.Promise<Content> {
            console.log("ContentWizardPanel.persistNewItem");

            var deferred = Q.defer<Content>();

            new PersistNewContentRoutine(this).
                setCreateContentRequestProducer(this.produceCreateContentRequest).
                setCreateSiteRequestProducer(this.produceCreateSiteRequest).
                execute().
                done((content: Content) => {

                    deferred.resolve(content);

                });

            return deferred.promise;
        }

        postPersistNewItem(persistedContent: Content): Q.Promise<void> {
            var deferred = Q.defer<void>();

            if (persistedContent.isSite()) {
                this.siteContent = persistedContent;
            }

            deferred.resolve(null);
            return deferred.promise;
        }

        private produceCreateContentRequest(): api.content.CreateContentRequest {

            var contentData = new api.content.ContentData();

            var parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

            var createRequest = new api.content.CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(api.content.ContentUnnamed.newUnnamed()).
                setParent(parentPath).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentType.getForm()).
                setContentData(contentData);

            return createRequest;
        }

        private produceCreateSiteRequest(content: Content): api.content.site.CreateSiteRequest {

            if (!this.createSite) {
                return null;
            }

            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            this.siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                var moduleConfig = new api.content.site.ModuleConfigBuilder().
                    setModuleKey(moduleKey).
                    setConfig(new api.data.RootDataSet()).
                    build();
                moduleConfigs.push(moduleConfig);
            });

            return new api.content.site.CreateSiteRequest(content.getId())
                .setSiteTemplateKey(this.siteTemplate.getKey())
                .setModuleConfigs(moduleConfigs);

        }

        private producePageCUDRequest(content: Content): api.content.page.PageCUDRequest {

            if (!this.siteTemplate) {
                return null;
            }

            var pageTemplateKey = this.liveFormPanel.getPageTemplate();

            if (content.isPage() && !pageTemplateKey) {
                console.log("*** producePageCUDRequest: delete");
                return new api.content.page.DeletePageRequest(content.getContentId());
            }
            else if (!content.isPage() && pageTemplateKey) {

                console.log("*** producePageCUDRequest: create");
                var createRequest = new api.content.page.CreatePageRequest(content.getContentId()).
                    setPageTemplateKey(pageTemplateKey).
                    setConfig(this.liveFormPanel.getConfig()).
                    setRegions(this.liveFormPanel.getRegions());
                return createRequest;
            }
            else if (content.isPage() && pageTemplateKey) {

                console.log("*** producePageCUDRequest: update");
                var updatePageRequest = new api.content.page.UpdatePageRequest(content.getContentId()).
                    setPageTemplateKey(pageTemplateKey).
                    setConfig(this.liveFormPanel.getConfig()).
                    setRegions(this.liveFormPanel.getRegions());

                return updatePageRequest;
            }
            console.log("*** producePageCUDRequest: none");
        }

        updatePersistedItem(): Q.Promise<Content> {
            console.log("ContentWizardPanel.updatePersistedItem");

            var deferred = Q.defer<Content>();


            new UpdatePersistedContentRoutine(this).
                setUpdateContentRequestProducer(this.produceUpdateContentRequest).
                setUpdateSiteRequestProducer(this.produceUpdateSiteRequest).
                setPageCUDRequestProducer(this.producePageCUDRequest).
                execute().
                done((content: Content) => {

                    new api.content.ContentUpdatedEvent(content).fire();
                    api.notify.showFeedback('Content was updated!');

                    deferred.resolve(content);
                });

            return deferred.promise;
        }

        private produceUpdateContentRequest(content: Content): api.content.UpdateContentRequest {

            var updateContentRequest: api.content.UpdateContentRequest = new api.content.UpdateContentRequest(this.getPersistedItem().getId()).
                setDraft(this.persistAsDraft).
                setContentName(this.resolveContentNameForUpdateReuest()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(this.contentWizardStepForm.getContentData());

            var contentId: ContentId = new ContentId(this.getPersistedItem().getId());
            var updateAttachments: api.content.UpdateAttachments =
                api.content.UpdateAttachments.create(contentId, this.contentWizardStepForm.getFormView().getAttachments());
            updateContentRequest.setUpdateAttachments(updateAttachments);

            if (this.iconUploadItem) {
                var attachment = new api.content.attachment.AttachmentBuilder().
                    setBlobKey(this.iconUploadItem.getBlobKey()).
                    setAttachmentName(new api.content.attachment.AttachmentName('_thumb.png')).
                    setMimeType(this.iconUploadItem.getMimeType()).
                    setSize(this.iconUploadItem.getSize()).
                    build();

                var updateAttachments = api.content.UpdateAttachments.create(contentId, [attachment]);
                updateContentRequest.setUpdateAttachments(updateAttachments);
            }

            return updateContentRequest;
        }

        private produceUpdateSiteRequest(content: Content): api.content.site.UpdateSiteRequest {

            if (this.siteWizardStepForm == null) {
                return null;
            }

            return new api.content.site.UpdateSiteRequest(content.getId()).
                setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey()).
                setModuleConfigs(this.siteWizardStepForm.getModuleConfigs());
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                           || !api.util.isStringsEqual(persistedContent.getName().toString(), this.contentWizardHeader.getName().toString())
                    || !persistedContent.getContentData().equals(this.contentWizardStepForm.getContentData());
            }
        }

        private enableDisplayNameScriptExecution(formView: api.form.FormView) {

            if (this.displayNameScriptExecutor.hasScript()) {

                formView.onKeyUp((event: KeyboardEvent) => {

                    if (this.displayNameScriptExecutor.hasScript()) {
                        this.contentWizardHeader.setDisplayName(this.displayNameScriptExecutor.execute());
                    }
                });
            }
        }

        private resolveContentNameForUpdateReuest(): ContentName {
            if (api.util.isStringEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            }
            else {
                return ContentName.fromString(this.contentWizardHeader.getName());
            }
        }

        getParentContent(): Content {
            return this.parentContent;
        }

        getContentType(): ContentType {
            return this.contentType;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        setPersistAsDraft(draft: boolean) {
            this.persistAsDraft = draft;
        }

        showLiveEdit() {

            super.showPanel(this.liveFormPanel);
            this.liveFormPanel.loadPageIfNotLoaded().
                done(() => {

                });
        }

        showWizard() {
            super.showMainPanel();
        }
    }

}