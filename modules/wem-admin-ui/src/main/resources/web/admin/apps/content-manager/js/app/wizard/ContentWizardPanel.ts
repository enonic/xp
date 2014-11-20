module app.wizard {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import ContentFormContext = api.content.form.ContentFormContext;
    import ContentFormContextBuilder = api.content.form.ContentFormContextBuilder;
    import Content = api.content.Content;
    import ContentBuilder = api.content.ContentBuilder;
    import ThumbnailBuilder = api.content.ThumbnailBuilder;
    import ContentName = api.content.ContentName;
    import CreateContentRequest = api.content.CreateContentRequest;
    import UpdateContentRequest = api.content.UpdateContentRequest;
    import UpdateAttachments = api.content.UpdateAttachments;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Metadata = api.content.Metadata;
    import Page = api.content.page.Page;
    import Site = api.content.site.Site;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import ContentType = api.schema.content.ContentType;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import IsRenderableRequest = api.content.page.IsRenderableRequest;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;
    import UploadFinishedEvent = api.app.wizard.UploadFinishedEvent;

    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import MetadataSchema = api.schema.metadata.MetadataSchema;
    import MetadataSchemaName = api.schema.metadata.MetadataSchemaName;
    import GetMetadataSchemaRequest = api.schema.metadata.GetMetadataSchemaRequest;

    export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

        private parentContent: Content;

        private defaultModels: page.DefaultModels;

        private site: Site;

        private liveEditModel: LiveEditModel;

        private siteModel: SiteModel;

        private contentType: ContentType;

        private formIcon: FormIcon;

        private contentWizardHeader: WizardHeaderWithDisplayNameAndName;

        private contentWizardStepForm: ContentWizardStepForm;

        private securityWizardStepForm: SecurityWizardStepForm;

        private metadataStepFormByName: {[name: string]: ContentWizardStepForm;};

        private iconUploadItem: api.ui.uploader.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        private persistAsDraft: boolean;

        private createSite: boolean;

        private formContext: ContentFormContext;

        private previewAction: api.ui.Action;

        private publishAction: api.ui.Action;

        private contextWindowToggler: app.wizard.page.contextwindow.ContextWindowToggler;

        private cycleViewModeButton: api.ui.button.CycleButton;

        private wizardActions: app.wizard.action.ContentWizardActions;

        private isContentFormValid: boolean;

        private contentNamedListeners: {(event: api.content.ContentNamedEvent):void}[];

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            this.constructing = true;
            this.isContentFormValid = false;

            this.persistAsDraft = true;
            this.contentNamedListeners = [];
            this.parentContent = params.parentContent;
            this.defaultModels = params.defaultModels;
            this.site = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();
            var iconUrl = ContentIconUrlResolver.default();
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon",
                api.util.UriHelper.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: UploadFinishedEvent) => {

                this.iconUploadItem = event.getUploadItem();
                this.formIcon.setSrc(api.util.UriHelper.getRestUri('blob/' + this.iconUploadItem.getBlobKey() + '?mimeType=' +
                                                                   event.getUploadItem().getMimeType()));
            });

            this.wizardActions = new app.wizard.action.ContentWizardActions(this);
            this.previewAction = this.wizardActions.getPreviewAction();
            this.publishAction = this.wizardActions.getPublishAction();

            var mainToolbar = new ContentWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                duplicateAction: this.wizardActions.getDuplicateAction(),
                deleteAction: this.wizardActions.getDeleteAction(),
                closeAction: this.wizardActions.getCloseAction(),
                publishAction: this.wizardActions.getPublishAction(),
                previewAction: this.wizardActions.getPreviewAction(),
                showLiveEditAction: this.wizardActions.getShowLiveEditAction(),
                showFormAction: this.wizardActions.getShowFormAction(),
                showSplitEditAction: this.wizardActions.getShowSplitEditAction()
            });

            this.contextWindowToggler = mainToolbar.getContextWindowToggler();
            this.cycleViewModeButton = mainToolbar.getCycleViewModeButton();
            this.showLiveEditAction = this.wizardActions.getShowLiveEditAction();
            this.showSplitEditAction = this.wizardActions.getShowSplitEditAction();
            this.showLiveEditAction.setEnabled(false);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().prettifyUnnamedPathElements().toString() + "/");
            } else {
                this.contentWizardHeader.setPath("/");
            }

            this.createSite = params.createSite;
            if (this.createSite || (params.persistedContent && params.persistedContent.isSite())) {
                this.formIcon.addClass("site");
            }

            this.contentWizardStepForm = new ContentWizardStepForm();
            this.contentWizardStepForm.onValidityChanged((event: WizardStepValidityChangedEvent) =>
                    this.isContentFormValid = event.isValid()
            );
            this.metadataStepFormByName = {};

            this.securityWizardStepForm = new SecurityWizardStepForm();

            var isSiteOrWithinSite = this.site || this.createSite;
            var hasPageTemplate = this.defaultModels && this.defaultModels.hasPageTemplate();
            var hasSiteAndPageTemplate = isSiteOrWithinSite && hasPageTemplate;
            var isPageTemplate = this.contentType.getContentTypeName().isPageTemplate();
            if (hasSiteAndPageTemplate || isPageTemplate) {

                this.liveFormPanel = new page.LiveFormPanel(<page.LiveFormPanelConfig> {
                    contentWizardPanel: this,
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
                actions: this.wizardActions,
                livePanel: this.liveFormPanel,
                split: true
            }, () => {

                this.addClass("content-wizard-panel");
                if (this.getSplitPanel()) {
                    this.getSplitPanel().addClass("prerendered");
                }

                var responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                    if (this.isVisible()) {
                        this.updateStickyToolbar();
                        if (item.isInRangeOrSmaller(ResponsiveRanges._720_960)) {
                            this.cycleViewModeButton.disableAction(this.wizardActions.getShowSplitEditAction());
                            if (this.isSplitView()) {
                                this.cycleViewModeButton.setCurrentAction(this.wizardActions.getShowFormAction());
                            }
                        } else if (item.isInRangeOrBigger(ResponsiveRanges._960_1200)) {
                            this.cycleViewModeButton.enableAction(this.wizardActions.getShowSplitEditAction());
                        }
                    }
                });

                this.onRemoved((event) => {
                    ResponsiveManager.unAvailableSizeChanged(this);
                });

                this.onShown((event: api.dom.ElementShownEvent) => {
                    if (this.getPersistedItem()) {
                        app.Router.setHash("edit/" + this.getPersistedItem().getId());
                    } else {
                        app.Router.setHash("new/" + this.contentType.getName());
                    }
                    //Set split panel default
                    this.wizardActions.getShowSplitEditAction().execute();
                    responsiveItem.update();
                });

                this.constructing = false;

                callback(this);
            });
        }

        giveInitialFocus() {
            var newWithoutDisplayCameScript = this.isLayingOutNew() && !this.contentType.hasContentDisplayNameScript();
            var displayNameEmpty = api.util.StringHelper.isEmpty(this.getPersistedItem().getDisplayName());
            var editWithEmptyDisplayName = !this.isLayingOutNew() && displayNameEmpty && !this.contentType.hasContentDisplayNameScript();

            if (newWithoutDisplayCameScript || editWithEmptyDisplayName) {
                this.contentWizardHeader.giveFocus();
            } else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    this.contentWizardHeader.giveFocus();
                }
            }

            this.startRememberFocus();
        }

        private createSteps(): wemQ.Promise<MetadataSchema[]> {

            var moduleKeys = this.site ? this.site.getModuleKeys() : [];
            var modulePromises = moduleKeys.map((key: ModuleKey) => new api.module.GetModuleRequest(key).sendAndParse());
            return wemQ.all(modulePromises).
                then((modules: Module[]) => {
                    var schemaNames: string[] = [];
                    modules.forEach((mdl: Module) => {
                        var moduleSchemaNames = mdl.getMetadataSchemaDependencies().map((name: MetadataSchemaName) => name.toString()),
                            uniqNames = moduleSchemaNames.filter((name: string) => schemaNames.indexOf(name) < 0);
                        Array.prototype.push.apply(schemaNames, uniqNames);
                    });

                    var metadataSchemaPromises = schemaNames.map((name: string) => new GetMetadataSchemaRequest(new MetadataSchemaName(name)).sendAndParse());
                    return wemQ.all(metadataSchemaPromises);
                }).then((schemas: MetadataSchema[]) => {
                    var steps: WizardStep[] = [];

                    steps.push(new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));
                    schemas.forEach((schema: MetadataSchema, index: number) => {
                        if (!this.metadataStepFormByName[schema.getMetadataSchemaName().toString()]) {
                            var stepForm = new ContentWizardStepForm();
                            this.metadataStepFormByName[schema.getMetadataSchemaName().toString()] = stepForm;
                            steps.splice(index + 1, 0, new WizardStep(schema.getDisplayName(), stepForm));
                        }
                    });
                    steps.push(new WizardStep("Security", this.securityWizardStepForm));

                    this.setSteps(steps);

                    return schemas;
                });
        }


        preLayoutNew(): wemQ.Promise<void> {
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

        postLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                true);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedContent: Content): wemQ.Promise<void> {

            this.formIcon.setSrc(new ContentIconUrlResolver().setContent(persistedContent).setCrop(false).resolve());

            var viewedContent;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                viewedContent = this.assembleViewedContent(persistedContent.newBuilder()).build();
                if (viewedContent.equals(persistedContent)) {

                    if (this.liveFormPanel) {
                        this.liveFormPanel.loadPage();
                    }
                }
                else {

                    console.warn("Received Content from server differs from what's viewed:");
                    console.warn(" viewedContent: ", viewedContent);
                    console.warn(" persistedContent: ", persistedContent);

                    ConfirmationDialog.get().
                        setQuestion("Received Content from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => this.doLayoutPersistedItem(persistedContent.clone())).
                        setNoCallback(() => {/* Do nothing... */
                        }).
                        show();
                }

                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.doLayoutPersistedItem(persistedContent.clone());
            }
        }

        private doLayoutPersistedItem(content: Content): wemQ.Promise<void> {

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
                        this.cycleViewModeButton.setCurrentAction(this.wizardActions.getShowSplitEditAction());
                    }

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            var parallelPromises: wemQ.Promise<any>[] = [
                new api.content.attachment.GetAttachmentsRequest(content.getContentId()).sendAndParse(),
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>((attachmentsArray: api.content.attachment.Attachment[], schemas: MetadataSchema[]) => {

                var attachments = new api.content.attachment.AttachmentsBuilder().
                    addAll(attachmentsArray).
                    build();

                var formContextBuilder = new ContentFormContextBuilder().
                    setSite(this.site).
                    setParentContent(this.parentContent).
                    setPersistedContent(content).
                    setAttachments(attachments);
                formContextBuilder.setShowEmptyFormItemSetOccurrences(this.isItemPersisted());
                this.formContext = formContextBuilder.build();

                var contentData = content.getContentData();
                contentData.onPropertyChanged((event: api.data.PropertyChangedEvent) => {
                    if (content.isSite()) {

                        if (event.getPath().toString().indexOf(".modules") == 0) {

                            // Update SiteModel
                            if (this.liveFormPanel) {
                                var site = <Site>content;
                                var viewedSiteBuilder = site.newBuilder();
                                this.assembleViewedContent(viewedSiteBuilder);
                                var viewedSite = viewedSiteBuilder.build();
                                this.siteModel.setModules(viewedSite.getModuleConfigs());
                            }
                        }
                    }
                });
                this.contentWizardStepForm.layout(this.formContext, contentData, this.contentType.getForm());
                this.securityWizardStepForm.layout(content);

                schemas.forEach((schema: MetadataSchema, index: number) => {
                    var metadata = content.getMetadata(schema.getMetadataSchemaName());
                    if (!metadata) {
                        metadata = new Metadata(schema.getMetadataSchemaName(), new RootDataSet());
                        content.getAllMetadata().push(metadata);
                    }
                    this.metadataStepFormByName[schema.getMetadataSchemaName().toString()].layout(this.formContext, metadata.getData(),
                        schema.getForm());
                });

                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                if (this.liveFormPanel) {

                    if (!this.liveEditModel) {
                        this.initLiveEditModel(content).then(() => {
                            this.liveFormPanel.setModel(this.liveEditModel);
                            this.liveFormPanel.loadPage();
                            return wemQ(null);
                        });
                    }
                    else {
                        this.liveFormPanel.loadPage();
                        return wemQ(null);
                    }
                }
            });
        }

        private initLiveEditModel(content: Content): wemQ.Promise<void> {
            if (this.createSite) {
                this.siteModel = new SiteModel(<Site>content);
            }
            else {
                this.siteModel = new SiteModel(this.site);
            }
            this.liveEditModel = new LiveEditModel(this.siteModel);
            return this.liveEditModel.init(content, this.defaultModels.getPageTemplate());
        }

        postLayoutPersisted(existing: Content): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.contentWizardHeader.initNames(existing.getDisplayName(), existing.getName().toString(),
                false);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<Content> {

            return new PersistNewContentRoutine(this).setCreateContentRequestProducer(this.produceCreateContentRequest).execute().then((content: Content) => {
                api.notify.showFeedback('Content was created!');
                return content;
            });
        }

        postPersistNewItem(persistedContent: Content): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            if (persistedContent.isSite()) {
                this.site = <Site>persistedContent;
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
                setContentData(contentData).
                setMetadata([]);

            return createRequest;
        }

        updatePersistedItem(): wemQ.Promise<Content> {

            var persistedContent = this.getPersistedItem();
            var viewedContent = this.assembleViewedContent(persistedContent.newBuilder()).build();

            var updatePersistedContentRoutine = new UpdatePersistedContentRoutine(this, persistedContent, viewedContent).
                setUpdateContentRequestProducer(this.produceUpdateContentRequest);

            return updatePersistedContentRoutine.
                execute().
                then((content: Content) => {

                    if (persistedContent.getName().isUnnamed() && !content.getName().isUnnamed()) {
                        this.notifyContentNamed(content);
                    }
                    api.notify.showFeedback('Content was updated!');

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
                setContentData(viewedContent.getContentData()).
                setMetadata(viewedContent.getAllMetadata());

            if (this.contentWizardStepForm) {
                var updateAttachments = UpdateAttachments.create(persistedContent.getContentId(),
                    this.contentWizardStepForm.getFormView().getAttachments());
                updateContentRequest.setUpdateAttachments(updateAttachments);
            }

            if (this.securityWizardStepForm) {
                // TODO: get values
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
                viewedContentBuilder.setData(<api.content.ContentData>this.contentWizardStepForm.getRootDataSet());
            }
            if (this.securityWizardStepForm) {
                // TODO: set data
            }

            var metadata: Metadata[] = [];
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    metadata.push(new Metadata(new MetadataSchemaName(key), this.metadataStepFormByName[key].getRootDataSet()));
                }
            }

            viewedContentBuilder.setMetadata(metadata);

            viewedContentBuilder.setPage(this.assembleViewedPage());
            return viewedContentBuilder;
        }

        private assembleViewedPage(): Page {

            if (!this.liveFormPanel) {
                return null;
            }

            return this.liveFormPanel.getPage();
        }

        private resolveContentNameForUpdateReuest(): ContentName {
            if (api.util.StringHelper.isEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
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
            if (this.getSplitPanel()) {
                this.getSplitPanel().addClass("toggle-split");
                this.getSplitPanel().removeClass("toggle-live toggle-form prerendered");
            }
            ResponsiveManager.fireResizeEvent();
        }

        showForm() {
            this.getSplitPanel().addClass("toggle-form");
            this.getSplitPanel().removeClass("toggle-live toggle-split prerendered");
            ResponsiveManager.fireResizeEvent();
        }

        private isSplitView(): boolean {
            return this.getSplitPanel() && this.getSplitPanel().hasClass("toggle-split");
        }

        public checkContentCanBePublished(): boolean {
            if (!this.isContentFormValid) {
                this.contentWizardStepForm.displayValidationErrors(true);
            }

            var allMetadataFormsValid = true;
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    var form = this.metadataStepFormByName[key];
                    if (!form.isValid()) {
                        form.displayValidationErrors(true);
                        allMetadataFormsValid = false;
                    }
                }
            }
            return this.isContentFormValid && allMetadataFormsValid;
        }

        getContextWindowToggler(): app.wizard.page.contextwindow.ContextWindowToggler {
            return this.contextWindowToggler;
        }

        getCloseAction(): api.ui.Action {
            return this.wizardActions.getCloseAction();
        }

        onContentNamed(listener: (event: api.content.ContentNamedEvent)=>void) {
            this.contentNamedListeners.push(listener);
        }

        private notifyContentNamed(content: api.content.Content) {
            this.contentNamedListeners.forEach((listener: (event: api.content.ContentNamedEvent)=>void)=> {
                listener.call(this, new api.content.ContentNamedEvent(this, content));
            });
        }
    }

}