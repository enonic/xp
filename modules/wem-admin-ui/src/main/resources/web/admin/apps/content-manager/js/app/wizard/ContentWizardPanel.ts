module app.wizard {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import FormContext = api.form.FormContext;
    import Content = api.content.Content;
    import ContentBuilder = api.content.ContentBuilder;
    import ThumbnailBuilder = api.content.ThumbnailBuilder;
    import ContentId = api.content.ContentId;
    import ContentName = api.content.ContentName;
    import CreateContentRequest = api.content.CreateContentRequest;
    import UpdateContentRequest = api.content.UpdateContentRequest;
    import UpdateAttachments = api.content.UpdateAttachments;
    import Page = api.content.page.Page;
    import PageBuilder = api.content.page.PageBuilder;
    import Site = api.content.site.Site;
    import SiteBuilder = api.content.site.SiteBuilder;
    import CreateSiteRequest = api.content.site.CreateSiteRequest;
    import ContentType = api.schema.content.ContentType;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplate = api.content.page.PageTemplate;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import IsRenderableRequest = api.content.page.IsRenderableRequest;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.ResponsiveManager;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepForm = api.app.wizard.WizardStepForm;
    import UploadFinishedEvent = api.app.wizard.UploadFinishedEvent;


    export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

        private parentContent: Content;

        private siteContent: Content;

        private contentType: ContentType;

        private formIcon: FormIcon;

        private contentWizardHeader: WizardHeaderWithDisplayNameAndName;

        private siteWizardStepForm: site.SiteWizardStepForm;

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

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            this.constructing = true;

            this.persistAsDraft = true;
            this.parentContent = params.parentContent;
            this.siteContent = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();
            var iconUrl = api.content.ContentIconUrlResolver.default();
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: UploadFinishedEvent) => {

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
                this.siteWizardStepForm = new app.wizard.site.SiteWizardStepForm(this.siteTemplate, this.contentType);
            }
            else {
                this.siteWizardStepForm = null;
            }
            this.contentWizardStepForm = new ContentWizardStepForm(this.publishAction);

            if (this.siteContent || this.createSite) {

                this.liveFormPanel = new page.LiveFormPanel(<page.LiveFormPanelConfig> {
                    contentWizardPanel: this,
                    siteTemplate: this.siteTemplate,
                    contentType: this.contentType.getContentTypeName(),
                    defaultModels: params.defaultModels
                });

            }

            if (this.siteWizardStepForm) {
                this.formIcon.addClass("site");
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
                steps: this.createSteps(),
                split: true
            }, () => {

                this.addClass("content-wizard-panel");
                if (this.getSplitPanel()) {
                    this.getSplitPanel().addClass("prerendered");
                }

                this.onShown((event: api.dom.ElementShownEvent) => {
                    if (this.getPersistedItem()) {
                        app.Router.setHash("edit/" + this.getPersistedItem().getId());
                    } else {
                        app.Router.setHash("new/" + this.contentType.getName());
                    }
                    if (this.liveFormPanel) {
                        this.liveFormPanel.loadPageIfNotLoaded();
                    }
                });

                ResponsiveManager.onAvailableSizeChanged(this, () => {
                    this.updateStickyToolbar()
                });

                this.onRemoved((event) => {
                    ResponsiveManager.unAvailableSizeChanged(this);
                });

                this.constructing = false;

                callback(this);
            });
        }

        giveInitialFocus() {
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

        private createSteps(): WizardStep[] {

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
            var deferred = Q.defer<void>();

            // Ensure a nameless and empty content is persisted before rendering new
            this.saveChanges().
                then(() => {
                    deferred.resolve(null);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        postRenderNew(): Q.Promise<void> {
            var deferred = Q.defer<void>();

            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                true);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedContent: Content): Q.Promise<void> {

            this.formIcon.setSrc(persistedContent.getIconUrl() + '?crop=false');

            if (!this.constructing) {

                var deferred = Q.defer<void>();

                var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();
                if (viewedContent.equals(persistedContent)) {

                    // Do nothing, since viewed data equals persisted data
                    deferred.resolve(null);
                    return deferred.promise;
                }
                else {
                    ConfirmationDialog.get().
                        setQuestion("Received Content from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => {

                            this.doLayoutPersistedItem(persistedContent.clone());
                        }).
                        setNoCallback(() => {
                            // Do nothing...
                        }).show();


                    deferred.resolve(null);
                    return deferred.promise;
                }
            }
            else {
                return this.doLayoutPersistedItem(persistedContent.clone());
            }
        }

        private doLayoutPersistedItem(content: Content): Q.Promise<void> {

            var contentData = content.getContentData();

            this.showLiveEditAction.setVisible(false);
            this.showLiveEditAction.setEnabled(false);
            this.previewAction.setVisible(false);
            this.contextWindowToggler.setVisible(false);

            new IsRenderableRequest(content.getContentId()).sendAndParse().
                then((renderable: boolean): void => {

                    this.showLiveEditAction.setVisible(renderable);
                    this.showLiveEditAction.setEnabled(renderable);
                    this.previewAction.setVisible(renderable);
                    this.contextWindowToggler.setVisible(renderable);

                }).catch((reason) => {
                    api.notify.showWarning(reason.toString());
                }).done();

            return new api.content.attachment.GetAttachmentsRequest(content.getContentId()).
                sendAndParse().
                then((attachmentsArray: api.content.attachment.Attachment[]) => {

                    var attachments = new api.content.attachment.AttachmentsBuilder().
                        addAll(attachmentsArray).
                        build();

                    var formContext = new api.form.FormContextBuilder().
                        setParentContent(this.parentContent).
                        setPersistedContent(content).
                        setAttachments(attachments).
                        setShowEmptyFormItemSetOccurrences(this.isPersisted()).
                        build();

                    this.contentWizardStepForm.renderExisting(formContext, contentData, content.getForm());
                    // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                    return this.doRenderExistingSite(content, formContext)
                        .then(() => {

                            if (this.liveFormPanel) {
                                return this.doRenderExistingPage(content);
                            }

                        });

                });
        }

        postRenderExisting(existing: Content): Q.Promise<void> {
            var deferred = Q.defer<void>();

            this.contentWizardHeader.initNames(existing.getDisplayName(), existing.getName().toString(),
                false);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());

            deferred.resolve(null);
            return deferred.promise;
        }

        private doRenderExistingSite(content: Content, formContext: FormContext): Q.Promise<void> {

            if (this.siteWizardStepForm != null && content.getSite()) {
                return this.siteWizardStepForm.renderExisting(formContext, content.getSite());
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private doRenderExistingPage(content: Content): Q.Promise<void> {

            return this.layout(content);
        }

        private layout(content: Content): Q.Promise<void> {

            var page: Page = content.getPage();

            if (page != null && page.getTemplate() != null) {

                return new GetPageTemplateByKeyRequest(page.getTemplate()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().
                    then((pageTemplate: PageTemplate) => {

                        return this.liveFormPanel.setPage(content, pageTemplate);

                    });
            }
            else {
                return this.liveFormPanel.setPage(content, null);
            }
        }

        persistNewItem(): Q.Promise<Content> {

            return new PersistNewContentRoutine(this).
                setCreateContentRequestProducer(this.produceCreateContentRequest).
                setCreateSiteRequestProducer(this.produceCreateSiteRequest).
                execute();
        }

        postPersistNewItem(persistedContent: Content): Q.Promise<void> {
            var deferred = Q.defer<void>();

            if (persistedContent.isSite()) {
                this.siteContent = persistedContent;
            }

            deferred.resolve(null);
            return deferred.promise;
        }

        private produceCreateContentRequest(): CreateContentRequest {

            var contentData = new api.content.ContentData();

            var parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

            var createRequest = new CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(api.content.ContentUnnamed.newUnnamed()).
                setParent(parentPath).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentType.getForm()).
                setContentData(contentData);

            return createRequest;
        }

        private produceCreateSiteRequest(content: Content): CreateSiteRequest {

            if (!this.createSite) {
                return null;
            }

            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            this.siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                var moduleConfig = new api.content.site.ModuleConfigBuilder().
                    setModuleKey(moduleKey).
                    setConfig(new RootDataSet()).
                    build();
                moduleConfigs.push(moduleConfig);
            });

            return new CreateSiteRequest(content.getId())
                .setSiteTemplateKey(this.siteTemplate.getKey())
                .setModuleConfigs(moduleConfigs);

        }

        updatePersistedItem(): Q.Promise<Content> {

            var persistedContent = this.getPersistedItem();
            var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();

            return new UpdatePersistedContentRoutine(this, persistedContent, viewedContent).
                setUpdateContentRequestProducer(this.produceUpdateContentRequest).
                execute().
                then((content: Content) => {

                    new api.content.ContentUpdatedEvent(content).fire();
                    api.notify.showFeedback('Content was updated!');

                    if (this.liveFormPanel) {
                        this.liveFormPanel.contentSaved();
                    }

                    return content;
                });
        }

        private produceUpdateContentRequest(persistedContent: Content, viewedContent: Content): UpdateContentRequest {

            var persistedContent = this.getPersistedItem();

            var updateContentRequest = new UpdateContentRequest(this.getPersistedItem().getId()).
                setDraft(this.persistAsDraft).
                setContentType(persistedContent.getType()).
                setForm(persistedContent.getForm()).
                setContentName(viewedContent.getName()).
                setDisplayName(viewedContent.getDisplayName()).
                setContentData(viewedContent.getContentData());

            var updateAttachments = UpdateAttachments.create(persistedContent.getContentId(),
                this.contentWizardStepForm.getFormView().getAttachments());
            updateContentRequest.setUpdateAttachments(updateAttachments);

            if (this.iconUploadItem) {
                var thumbnail = new ThumbnailBuilder().
                    setBlobKey(this.iconUploadItem.getBlobKey()).
                    setMimeType(this.iconUploadItem.getMimeType()).
                    setSize(this.iconUploadItem.getSize()).
                    build();
                updateContentRequest.setThumbnail(thumbnail);
            }

            return updateContentRequest;
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {

                var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();
                return !viewedContent.equals(persistedContent);
            }
        }

        private enableDisplayNameScriptExecution(formView: FormView) {

            if (this.displayNameScriptExecutor.hasScript()) {

                formView.onKeyUp((event: KeyboardEvent) => {

                    if (this.displayNameScriptExecutor.hasScript()) {
                        this.contentWizardHeader.setDisplayName(this.displayNameScriptExecutor.execute());
                    }
                });
            }
        }

        private assembleViewedContent(viewedContentBuilder: ContentBuilder): ContentBuilder {

            viewedContentBuilder.setName(this.resolveContentNameForUpdateReuest());
            viewedContentBuilder.setDisplayName(this.contentWizardHeader.getDisplayName());
            viewedContentBuilder.setData(this.contentWizardStepForm.getContentData());
            viewedContentBuilder.setForm(this.contentWizardStepForm.getForm());
            viewedContentBuilder.setSite(this.assembleViewedSite());
            viewedContentBuilder.setPage(this.assembleViewedPage());
            return viewedContentBuilder;
        }

        private assembleViewedPage(): Page {

            if (!this.liveFormPanel) {
                return null;
            }

            var pageTemplateKey = this.liveFormPanel.getPageTemplate();
            if (!pageTemplateKey) {
                return null;
            }

            var viewedPageBuilder = new PageBuilder();
            viewedPageBuilder.setTemplate(pageTemplateKey);
            viewedPageBuilder.setConfig(this.liveFormPanel.getConfig());
            viewedPageBuilder.setRegions(this.liveFormPanel.getRegions());
            return viewedPageBuilder.build();
        }

        private assembleViewedSite(): Site {

            if (!this.siteWizardStepForm) {
                return null;
            }
            var viewedSiteBuilder = new SiteBuilder();
            viewedSiteBuilder.setTemplateKey(this.siteWizardStepForm.getTemplateKey());
            viewedSiteBuilder.setModuleConfigs(this.siteWizardStepForm.getModuleConfigs());
            return viewedSiteBuilder.build();
        }

        private resolveContentNameForUpdateReuest(): ContentName {
            if (api.util.isStringEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            }
            else {
                return ContentName.fromString(this.contentWizardHeader.getName());
            }
        }

        setPersistAsDraft(draft: boolean) {
            this.persistAsDraft = draft;
        }

        showLiveEdit() {
            this.getSplitPanel().addClass("toggle-live");
            this.getSplitPanel().removeClass("toggle-form prerendered");
            ResponsiveManager.fireResizeEvent();
        }

        showWizard() {
            this.getSplitPanel().addClass("toggle-form");
            this.getSplitPanel().removeClass("toggle-live prerendered");
            ResponsiveManager.fireResizeEvent();
        }
    }

}