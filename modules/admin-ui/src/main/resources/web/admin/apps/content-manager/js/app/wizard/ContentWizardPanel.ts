module app.wizard {

    import PropertyTree = api.data.PropertyTree;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import ContentFormContext = api.content.form.ContentFormContext;
    import Content = api.content.Content;
    import ContentBuilder = api.content.ContentBuilder;
    import Attachment = api.content.attachment.Attachment;
    import Thumbnail = api.thumb.Thumbnail;
    import ContentName = api.content.ContentName;
    import CreateContentRequest = api.content.CreateContentRequest;
    import UpdateContentRequest = api.content.UpdateContentRequest;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Metadata = api.content.Metadata;
    import Page = api.content.page.Page;
    import Site = api.content.site.Site;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import ContentType = api.schema.content.ContentType;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import AccessControlList = api.security.acl.AccessControlList;
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
    import Mixin = api.schema.mixin.Mixin;
    import MixinName = api.schema.mixin.MixinName;
    import GetMixinByQualifiedNameRequest = api.schema.mixin.GetMixinByQualifiedNameRequest;

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

        private settingsWizardStepForm: SettingsWizardStepForm;

        private securityWizardStepForm: SecurityWizardStepForm;

        private metadataStepFormByName: {[name: string]: ContentWizardStepForm;};

        // TODO: CMS-4677 private iconUploadItem: api.ui.uploader.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        private persistAsDraft: boolean;

        private createSite: boolean;

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
                // TODO: CMS-4677
                //this.iconUploadItem = event.getUploadItem();
                //this.formIcon.setSrc(api.util.UriHelper.getRestUri('blob/' + this.iconUploadItem.getBlobKey() + '?mimeType=' +
                //                                                   event.getUploadItem().getMimeType()));
            });

            this.wizardActions = new app.wizard.action.ContentWizardActions(this);
            this.previewAction = this.wizardActions.getPreviewAction();
            this.publishAction = this.wizardActions.getPublishAction();

            var mainToolbar = new ContentWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                duplicateAction: this.wizardActions.getDuplicateAction(),
                deleteAction: this.wizardActions.getDeleteAction(),
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
            this.contentWizardStepForm.onValidityChanged((event: WizardStepValidityChangedEvent) => {
                this.isContentFormValid = event.isValid();
            });
            this.metadataStepFormByName = {};

            this.settingsWizardStepForm = new SettingsWizardStepForm();
            this.securityWizardStepForm = new SecurityWizardStepForm();

            ContentPermissionsAppliedEvent.on((event) => this.contentPermissionsUpdated(event.getContent()));

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
                split: !!this.liveFormPanel
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

            console.log("ContentWizardPanel.giveInitialFocus");
            var newWithoutDisplayCameScript = this.isLayingOutNew() && !this.contentType.hasContentDisplayNameScript();
            var displayNameEmpty = this.isLayingOutNew() || api.util.StringHelper.isEmpty(this.getPersistedItem().getDisplayName());
            var editWithEmptyDisplayName = !this.isLayingOutNew() && displayNameEmpty && !this.contentType.hasContentDisplayNameScript();

            if (newWithoutDisplayCameScript || editWithEmptyDisplayName) {
                this.contentWizardHeader.giveFocus();
            } else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    console.log("ContentWizardPanel.giveInitialFocus() WARNING: Failed to give focus to contentWizardStepForm");
                    this.contentWizardHeader.giveFocus();
                }
            }

            this.startRememberFocus();
        }

        private createSteps(): wemQ.Promise<Mixin[]> {

            var moduleKeys = this.site ? this.site.getModuleKeys() : [];
            var modulePromises = moduleKeys.map((key: ModuleKey) => new api.module.GetModuleRequest(key).sendAndParse());
            return wemQ.all(modulePromises).
                then((modules: Module[]) => {
                    var mixinNames: string[] = [];
                    modules.forEach((mdl: Module) => {
                        var moduleStepMixinNames = mdl.getMetaSteps().map((name: MixinName) => name.toString()),
                            uniqNames = moduleStepMixinNames.filter((name: string) => mixinNames.indexOf(name) < 0);
                        Array.prototype.push.apply(mixinNames, uniqNames);
                    });

                    var metadataMixinPromises = mixinNames.map((name: string) => new GetMixinByQualifiedNameRequest(new MixinName(name)).sendAndParse());
                    return wemQ.all(metadataMixinPromises);
                }).then((mixins: Mixin[]) => {
                    var steps: WizardStep[] = [];

                    steps.push(new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));
                    mixins.forEach((mixin: Mixin, index: number) => {
                        if (!this.metadataStepFormByName[mixin.getMixinName().toString()]) {
                            var stepForm = new ContentWizardStepForm();
                            this.metadataStepFormByName[mixin.getMixinName().toString()] = stepForm;
                            steps.splice(index + 1, 0, new WizardStep(mixin.getDisplayName(), stepForm));
                        }
                    });
                    steps.push(new WizardStep("Settings", this.settingsWizardStepForm));
                    steps.push(new WizardStep("Security", this.securityWizardStepForm));

                    this.setSteps(steps);

                    return mixins;
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
                    if (!viewedContent.getContentData().equals(persistedContent.getContentData())) {
                        console.warn(" inequality found in Content.data");
                        if (persistedContent.getContentData() && viewedContent.getContentData()) {
                            console.warn(" comparing persistedContent.data against viewedContent.data:");
                            new api.data.PropertyTreeComparator().compareTree(persistedContent.getContentData(),
                                viewedContent.getContentData());
                        }
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getPage(), persistedContent.getPage())) {
                        console.warn(" inequality found in Content.page");
                        if (persistedContent.getPage() && viewedContent.getPage()) {
                            console.warn(" comparing persistedContent.page.config against viewedContent.page.config:");
                            new api.data.PropertyTreeComparator().compareTree(persistedContent.getPage().getConfig(),
                                viewedContent.getPage().getConfig());
                        }
                    }
                    if (!api.ObjectHelper.arrayEquals(viewedContent.getAllMetadata(), persistedContent.getAllMetadata())) {
                        console.warn(" inequality found in Content.meta");
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getAttachments(), persistedContent.getAttachments())) {
                        console.warn(" inequality found in Content.attachments");
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getPermissions(), persistedContent.getPermissions())) {
                        console.warn(" inequality found in Content.permissions");
                    }
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

            var parallelPromises: wemQ.Promise<any>[] = [this.createSteps()];

            return wemQ.all(parallelPromises).
                spread<void>((schemas: Mixin[]) => {

                var formContext = this.createFormContext(content);

                var contentData = content.getContentData();
                contentData.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                    if (content.isSite()) {

                        // TODO: Move this listening into SiteModel instead
                        if (event.getProperty().getPath().toString().indexOf(".modules") == 0) {

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

                var formViewLayoutPromises: wemQ.Promise<void>[] = [];
                formViewLayoutPromises.push(this.contentWizardStepForm.layout(formContext, contentData, this.contentType.getForm()));
                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());
                this.settingsWizardStepForm.layout(content);
                this.settingsWizardStepForm.setModel(new ContentSettingsModel(content));

                this.securityWizardStepForm.layout(content);

                schemas.forEach((schema: Mixin, index: number) => {
                    var metadata = content.getMetadata(schema.getMixinName());
                    if (!metadata) {
                        metadata = new Metadata(schema.getMixinName(), new PropertyTree(api.Client.get().getPropertyIdProvider()));
                        content.getAllMetadata().push(metadata);
                    }
                    var metadataFormView = this.metadataStepFormByName[schema.getMixinName().toString()];
                    var metadataForm = new api.form.FormBuilder().addFormItems(schema.getFormItems()).build();

                    formViewLayoutPromises.push(metadataFormView.layout(formContext, metadata.getData(), metadataForm));
                });

                return wemQ.all(formViewLayoutPromises).spread<void>(() => {

                    console.log("ContentWizardPanel.doLayoutPersistedItem: all FormView-s layed out");
                    if (this.liveFormPanel) {

                        if (!this.liveEditModel) {
                            return this.initLiveEditModel(content, formContext).then(() => {
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
            });
        }

        private initLiveEditModel(content: Content, formContext: ContentFormContext): wemQ.Promise<void> {
            if (this.createSite) {
                this.siteModel = new SiteModel(<Site>content);
            }
            else {
                this.siteModel = new SiteModel(this.site);
            }
            this.liveEditModel = LiveEditModel.create().
                setParentContent(this.parentContent).
                setContent(content).
                setContentFormContext(formContext).
                setSiteModel(this.siteModel).build();
            return this.liveEditModel.init(this.defaultModels.getPageTemplate(), this.defaultModels.getPageDescriptor());
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
                new api.content.ContentCreatedEvent(content.getContentId()).fire();
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

        private produceCreateContentRequest(): wemQ.Promise<CreateContentRequest> {

            var deferred = wemQ.defer<CreateContentRequest>();

            var parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

            if (this.contentType.getContentTypeName().isMedia()) {
                deferred.resolve(null);
            }
            else {

                var formContext = this.createFormContext(null);
                var form = this.contentType.getForm();
                var data = new PropertyTree();
                var formView = new FormView(formContext, form, data.getRoot());
                formView.layout().then(() => {

                    deferred.resolve(new CreateContentRequest().
                        setDraft(this.persistAsDraft).
                        setName(api.content.ContentUnnamed.newUnnamed()).
                        setParent(parentPath).
                        setContentType(this.contentType.getContentTypeName()).
                        setDisplayName(this.contentWizardHeader.getDisplayName()).
                        setData(data).
                        setMetadata([]));

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            }

            return deferred.promise;
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
                    new api.content.ContentUpdatedEvent(content.getContentId()).fire();

                    return content;
                });
        }

        private produceUpdateContentRequest(persistedContent: Content, viewedContent: Content): UpdateContentRequest {
            var persistedContent = this.getPersistedItem();

            var updateContentRequest = new UpdateContentRequest(persistedContent.getId()).
                setDraft(this.persistAsDraft).
                setContentName(viewedContent.getName()).
                setDisplayName(viewedContent.getDisplayName()).
                setData(viewedContent.getContentData()).
                setMetadata(viewedContent.getAllMetadata()).
                setOwner(viewedContent.getOwner()).
                setLanguage(viewedContent.getLanguage());

            /* TODO: CMS-4677 if (this.iconUploadItem) {
             var thumbnail = Thumbnail.create().
             setBinaryReference(this.iconUploadItem.getBlobKey()).
             setMimeType(this.iconUploadItem.getMimeType()).
             setSize(this.iconUploadItem.getSize()).
             build();
             updateContentRequest.setThumbnail(thumbnail);
             }*/

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

            viewedContentBuilder.setName(this.resolveContentNameForUpdateRequest());
            viewedContentBuilder.setDisplayName(this.contentWizardHeader.getDisplayName());
            if (this.contentWizardStepForm) {
                viewedContentBuilder.setData(this.contentWizardStepForm.getData());
            }

            var metadata: Metadata[] = [];
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    metadata.push(new Metadata(new MixinName(key), this.metadataStepFormByName[key].getData()));
                }
            }

            viewedContentBuilder.setMetadata(metadata);

            this.settingsWizardStepForm.getModel().apply(viewedContentBuilder);

            viewedContentBuilder.setPage(this.assembleViewedPage());
            return viewedContentBuilder;
        }

        private assembleViewedPage(): Page {

            if (!this.liveFormPanel) {
                return null;
            }

            return this.liveFormPanel.getPage();
        }

        private resolveContentNameForUpdateRequest(): ContentName {
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

        private contentPermissionsUpdated(content: Content) {
            var persistedContent: Content = this.getPersistedItem();

            if (persistedContent && (content.getId() === persistedContent.getId())) {
                var updatedContent: Content = persistedContent.newBuilder().
                    setInheritPermissionsEnabled(content.isInheritPermissionsEnabled()).
                    setPermissions(content.getPermissions().clone()).
                    build();
                this.setPersistedItem(updatedContent);
            }

        }

        private createFormContext(content: Content): ContentFormContext {
            var formContext: ContentFormContext = <ContentFormContext>ContentFormContext.create().
                setSite(this.site).
                setParentContent(this.parentContent).
                setPersistedContent(content).
                setShowEmptyFormItemSetOccurrences(this.isItemPersisted()).
                build();
            return formContext;
        }

    }

}