module app.wizard {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import FormContext = api.form.FormContext;
    import ContentFormContext = api.content.form.ContentFormContext;
    import ContentFormContextBuilder = api.content.form.ContentFormContextBuilder;
    import Content = api.content.Content;
    import ContentBuilder = api.content.ContentBuilder;
    import ThumbnailBuilder = api.content.ThumbnailBuilder;
    import ContentId = api.content.ContentId;
    import ContentName = api.content.ContentName;
    import CreateContentRequest = api.content.CreateContentRequest;
    import UpdateContentRequest = api.content.UpdateContentRequest;
    import UpdateAttachments = api.content.UpdateAttachments;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
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
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;
    import UploadFinishedEvent = api.app.wizard.UploadFinishedEvent;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import DefaultModels = app.wizard.page.DefaultModels;
    import DefaultModelsFactoryConfig = app.wizard.page.DefaultModelsFactoryConfig;
    import DefaultModelsFactory = app.wizard.page.DefaultModelsFactory;
    import SiteTemplateChangedEvent = app.wizard.site.SiteTemplateChangedEvent;

    export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

        private parentContent: Content;

        private siteContent: Content;

        private contentType: ContentType;

        private formIcon: FormIcon;

        private contentWizardHeader: WizardHeaderWithDisplayNameAndName;

        private siteTemplateWizardStepForm: site.SiteTemplateWizardStepForm;

        private contentWizardStepForm: ContentWizardStepForm;

        private iconUploadItem: api.ui.uploader.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        private persistAsDraft: boolean;

        private createSite: boolean;

        private siteTemplate: SiteTemplate;

        private formContext: ContentFormContext;

        private previewAction: api.ui.Action;

        private publishAction: api.ui.Action;

        private contextWindowToggler: app.wizard.page.contextwindow.ContextWindowToggler;

        private cycleViewModeButton: api.ui.button.CycleButton;

        private contentWizardActions: app.wizard.action.ContentWizardActions;

        private isSiteTemplateFormValid: boolean;

        private isContentFormValid: boolean;

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            this.constructing = true;
            this.isSiteTemplateFormValid = false;
            this.isContentFormValid = false;

            this.persistAsDraft = true;
            this.parentContent = params.parentContent;
            this.siteContent = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();
            var iconUrl = ContentIconUrlResolver.default();
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: UploadFinishedEvent) => {

                this.iconUploadItem = event.getUploadItem();
                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.iconUploadItem.getBlobKey() + '?mimeType=' +
                                                         event.getUploadItem().getMimeType()));
            });

            this.contentWizardActions = new app.wizard.action.ContentWizardActions(this);
            this.previewAction = this.contentWizardActions.getPreviewAction();
            this.publishAction = this.contentWizardActions.getPublishAction();

            var mainToolbar = new ContentWizardToolbar({
                saveAction: this.contentWizardActions.getSaveAction(),
                duplicateAction: this.contentWizardActions.getDuplicateAction(),
                deleteAction: this.contentWizardActions.getDeleteAction(),
                closeAction: this.contentWizardActions.getCloseAction(),
                publishAction: this.contentWizardActions.getPublishAction(),
                previewAction: this.contentWizardActions.getPreviewAction(),
                showLiveEditAction: this.contentWizardActions.getShowLiveEditAction(),
                showFormAction: this.contentWizardActions.getShowFormAction(),
                showSplitEditAction: this.contentWizardActions.getShowSplitEditAction()
            });

            this.contextWindowToggler = mainToolbar.getContextWindowToggler();
            this.cycleViewModeButton = mainToolbar.getCycleViewModeButton();
            this.showLiveEditAction = this.contentWizardActions.getShowLiveEditAction();
            this.showSplitEditAction = this.contentWizardActions.getShowSplitEditAction();
            this.showLiveEditAction.setEnabled(false);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().toString() + "/");
            } else {
                this.contentWizardHeader.setPath("/");
            }

            this.createSite = params.createSite;
            this.siteTemplate = params.siteTemplate;
            if (this.createSite || (params.persistedContent && params.persistedContent.isSite())) {
                this.formIcon.addClass("site");
                this.siteTemplateWizardStepForm = new app.wizard.site.SiteTemplateWizardStepForm(this.siteTemplate);
                this.siteTemplateWizardStepForm.onSiteTemplateChanged((event: SiteTemplateChangedEvent) => this.handleSiteTemplateChanged(event));
                this.siteTemplateWizardStepForm.onValidityChanged(
                    (event: WizardStepValidityChangedEvent) =>
                        this.isSiteTemplateFormValid = event.isValid()
                );

            } else {
                this.siteTemplateWizardStepForm = null;
                this.isSiteTemplateFormValid = true;
            }
            this.contentWizardStepForm = new ContentWizardStepForm();
            this.contentWizardStepForm.onValidityChanged(
                (event: WizardStepValidityChangedEvent) =>
                    this.isContentFormValid = event.isValid()
            );

            if ((this.siteContent || this.createSite) && (params.defaultModels && params.defaultModels.hasPageTemplate())) {

                this.liveFormPanel = new page.LiveFormPanel(<page.LiveFormPanelConfig> {
                    contentWizardPanel: this,
                    siteTemplate: this.siteTemplate,
                    contentType: this.contentType.getContentTypeName(),
                    defaultModels: params.defaultModels
                });

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
                actions: this.contentWizardActions,
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
                    //Set split panel default
                    this.contentWizardActions.getShowSplitEditAction().execute();
                });

                ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                    if (this.isVisible()) {
                        this.updateStickyToolbar();
                        if (item.isInRangeOrSmaller(ResponsiveRanges._720_960)) {
                            this.cycleViewModeButton.disableAction(this.contentWizardActions.getShowSplitEditAction());
                            if (this.isSplitView()) {
                                this.cycleViewModeButton.setCurrentAction(this.contentWizardActions.getShowFormAction());
                            }
                        } else if (item.isInRangeOrBigger(ResponsiveRanges._960_1200)) {
                            this.cycleViewModeButton.enableAction(this.contentWizardActions.getShowSplitEditAction());
                        }
                    }
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
            } else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    this.contentWizardHeader.giveFocus();
                }
            }

            this.startRememberFocus();
        }

        private createSteps(): WizardStep[] {

            var steps: WizardStep[] = [];

            steps.push(new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));

            if (this.siteTemplateWizardStepForm != null) {
                steps.push(new WizardStep("Site Template", this.siteTemplateWizardStepForm));
            }

            steps.push(new WizardStep("Meta", new BaseContentWizardStepForm()));
            steps.push(new WizardStep("Security", new BaseContentWizardStepForm()));
            steps.push(new WizardStep("Summary", new BaseContentWizardStepForm()));

            return steps;
        }


        preRenderNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            // Ensure a nameless and empty content is persisted before rendering new
            this.saveChanges().
                then(() => {
                    deferred.resolve(null);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        postRenderNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                true);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedContent: Content): wemQ.Promise<void> {

            this.formIcon.setSrc(new ContentIconUrlResolver().setContent(persistedContent).setCrop(false).resolve());

            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();
                if (viewedContent.equals(persistedContent)) {

                    if (this.liveFormPanel) {
                        this.liveFormPanel.loadPage();
                    }
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

        private doLayoutPersistedItem(content: Content): wemQ.Promise<void> {

            var contentData = content.getContentData();

            this.showLiveEditAction.setVisible(false);
            this.showLiveEditAction.setEnabled(false);
            this.previewAction.setVisible(false);
            this.contextWindowToggler.setVisible(false);
            this.cycleViewModeButton.setVisible(false);

            new IsRenderableRequest(content.getContentId()).sendAndParse().
                then((renderable: boolean): void => {
                    this.showLiveEditAction.setVisible(renderable);
                    this.showLiveEditAction.setEnabled(renderable);
                    this.showSplitEditAction.setEnabled(renderable);
                    this.previewAction.setVisible(renderable);
                    this.contextWindowToggler.setVisible(renderable);
                    this.cycleViewModeButton.setVisible(renderable);

                    if (this.getEl().getWidth() > ResponsiveRanges._720_960.getMaximumRange() && renderable) {
                        this.cycleViewModeButton.setCurrentAction(this.contentWizardActions.getShowSplitEditAction());
                    }

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            return new api.content.attachment.GetAttachmentsRequest(content.getContentId()).
                sendAndParse().
                then((attachmentsArray: api.content.attachment.Attachment[]) => {

                    var attachments = new api.content.attachment.AttachmentsBuilder().
                        addAll(attachmentsArray).
                        build();

                    var formContextBuilder = new ContentFormContextBuilder().
                        setParentContent(this.parentContent).
                        setPersistedContent(content).
                        setAttachments(attachments);
                    formContextBuilder.setShowEmptyFormItemSetOccurrences(this.isItemPersisted());
                    this.formContext = formContextBuilder.build();

                    this.contentWizardStepForm.layout(this.formContext, contentData, content.getForm());

                    // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                    if (this.siteTemplateWizardStepForm) {
                        this.siteTemplateWizardStepForm.setFormContext(this.formContext);
                    }
                    return this.doLayoutSite(content, this.formContext);

                }).then(() => {

                    if (this.liveFormPanel) {
                        return this.doLayoutPage(content);
                    }
                });
        }

        postRenderExisting(existing: Content): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.contentWizardHeader.initNames(existing.getDisplayName(), existing.getName().toString(),
                false);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());

            deferred.resolve(null);
            return deferred.promise;
        }

        private doLayoutSite(content: Content, formContext: ContentFormContext): wemQ.Promise<void> {

            if (this.siteTemplateWizardStepForm != null && content.getSite()) {
                return this.siteTemplateWizardStepForm.layout(formContext, content.getSite());
            }
            else {
                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private doLayoutPage(content: Content): wemQ.Promise<void> {

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

        persistNewItem(): wemQ.Promise<Content> {

            return new PersistNewContentRoutine(this).
                setCreateContentRequestProducer(this.produceCreateContentRequest).
                setCreateSiteRequestProducer(this.produceCreateSiteRequest).
                execute();
        }

        postPersistNewItem(persistedContent: Content): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

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
            if (!this.createSite || !this.siteTemplate) {
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

        updatePersistedItem(): wemQ.Promise<Content> {

            var persistedContent = this.getPersistedItem();
            var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();
            var isNewSite = !persistedContent.isSite() && viewedContent.isSite();

            var updatePersistedContentRoutine = new UpdatePersistedContentRoutine(this, persistedContent, viewedContent).
                setUpdateContentRequestProducer(this.produceUpdateContentRequest);
            if (isNewSite) {
                updatePersistedContentRoutine.setCreateSiteRequestProducer(this.produceCreateSiteRequest);
            }
            return updatePersistedContentRoutine.
                execute().
                then((content: Content) => {

                    if (this.isRenderingNew()) {

                        new api.content.ContentCreatedEvent(content, this).fire();
                        api.notify.showFeedback('Content was created!');
                    } else {

                        new api.content.ContentUpdatedEvent(content, this).fire();
                        api.notify.showFeedback('Content was updated!');
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

            if (this.contentWizardStepForm) {
                var updateAttachments = UpdateAttachments.create(persistedContent.getContentId(),
                    this.contentWizardStepForm.getFormView().getAttachments());
                updateContentRequest.setUpdateAttachments(updateAttachments);
            }

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
            if (this.contentWizardStepForm) {
                viewedContentBuilder.setData(this.contentWizardStepForm.getContentData());
                viewedContentBuilder.setForm(this.contentWizardStepForm.getForm());
            }
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

            if (!this.siteTemplateWizardStepForm) {
                return null;
            }
            var siteTemplateKey = this.siteTemplateWizardStepForm.getTemplateKey();
            if (!siteTemplateKey) {
                return null;
            }
            var viewedSiteBuilder = new SiteBuilder();
            viewedSiteBuilder.setTemplateKey(siteTemplateKey);
            viewedSiteBuilder.setModuleConfigs(this.siteTemplateWizardStepForm.getModuleConfigs());
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
            this.getSplitPanel().removeClass("toggle-form toggle-split prerendered");
            ResponsiveManager.fireResizeEvent();
        }

        showSplitEdit() {
            this.getSplitPanel().addClass("toggle-split");
            this.getSplitPanel().removeClass("toggle-live toggle-form prerendered");
            ResponsiveManager.fireResizeEvent();
        }

        showForm() {
            this.getSplitPanel().addClass("toggle-form");
            this.getSplitPanel().removeClass("toggle-live toggle-split prerendered");
            ResponsiveManager.fireResizeEvent();
        }

        private isSplitView(): boolean {
            return this.getSplitPanel().hasClass("toggle-split");
        }

        private handleSiteTemplateChanged(siteTemplateChanged: SiteTemplateChangedEvent) {
            var siteTemplate = siteTemplateChanged.getSiteTemplate();

            var contentTypeName = this.contentType.getContentTypeName();
            this.loadDefaultModels(siteTemplate, contentTypeName).then((defaultModels) => {

                this.liveFormPanel = new page.LiveFormPanel(<page.LiveFormPanelConfig> {
                    contentWizardPanel: this,
                    siteTemplate: siteTemplate,
                    contentType: contentTypeName,
                    defaultModels: defaultModels
                });
                this.liveFormPanel.hide();
                super.setLivePanel(this.liveFormPanel);

                this.siteTemplate = siteTemplate;
                this.constructing = true;
                return this.saveChanges().then((content) => {
                    this.constructing = false;
                    if (this.liveFormPanel) {
                        this.liveFormPanel.show();
                    }
                });

            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
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

        public contentCanBePublished(): boolean {
            return this.isContentFormValid && this.isSiteTemplateFormValid;
        }

        getContextWindowToggler(): app.wizard.page.contextwindow.ContextWindowToggler {
            return this.contextWindowToggler;
        }
    }

}