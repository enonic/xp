module app.wizard {

    import PropertyTree = api.data.PropertyTree;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import ContentFormContext = api.content.form.ContentFormContext;
    import Content = api.content.Content;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import CompareStatus = api.content.CompareStatus;
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
    import ThumbnailUploader = api.content.ThumbnailUploader;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;

    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import Mixin = api.schema.mixin.Mixin;
    import MixinName = api.schema.mixin.MixinName;
    import MixinNames = api.schema.mixin.MixinNames;
    import GetMixinByQualifiedNameRequest = api.schema.mixin.GetMixinByQualifiedNameRequest;

    export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

        private parentContent: Content;

        private defaultModels: page.DefaultModels;

        private site: Site;

        private liveEditModel: LiveEditModel;

        private siteModel: SiteModel;

        private contentType: ContentType;

        private thumbnailUploader: ThumbnailUploader;

        private contentWizardHeader: WizardHeaderWithDisplayNameAndName;

        private contentWizardStep: WizardStep;

        private contentWizardStepForm: ContentWizardStepForm;

        private settingsWizardStepForm: SettingsWizardStepForm;

        private settingsWizardStep: WizardStep;

        private securityWizardStepForm: SecurityWizardStepForm;

        private metadataStepFormByName: {[name: string]: ContentWizardStepForm;};

        // TODO: CMS-4677 private iconUploadItem: api.ui.uploader.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        private requireValid: boolean;

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

            this.requireValid = false;
            this.contentNamedListeners = [];
            this.parentContent = params.parentContent;
            this.defaultModels = params.defaultModels;
            this.site = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();

            this.thumbnailUploader = new ThumbnailUploader({
                name: 'thumbnail-uploader',
                disabled: params.contentType.isImage(),
                deferred: true
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
                this.thumbnailUploader.addClass("site");
            }

            this.contentWizardStepForm = new ContentWizardStepForm();

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
                formIcon: this.thumbnailUploader,
                mainToolbar: mainToolbar,
                header: this.contentWizardHeader,
                actions: this.wizardActions,
                livePanel: this.liveFormPanel,
                split: !!this.liveFormPanel
            }, () => {

                this.onValidityChanged((event: api.ValidityChangedEvent) => {
                    this.isContentFormValid = this.isValid();
                    this.thumbnailUploader.toggleClass("invalid", !this.isValid());
                });

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

        getContentType(): ContentType {
            return this.contentType;
        }

        giveInitialFocus() {

            console.log("ContentWizardPanel.giveInitialFocus");
            if (this.contentType.hasContentDisplayNameScript()) {
                if (!this.contentWizardStepForm.giveFocus()) {
                    console.log("ContentWizardPanel.giveInitialFocus() WARNING: Failed to give focus to contentWizardStepForm");
                    this.contentWizardHeader.giveFocus();
                }
            } else {
                this.contentWizardHeader.giveFocus();
            }

            this.startRememberFocus();
        }

        private createSteps(): wemQ.Promise<Mixin[]> {

            var moduleKeys = this.site ? this.site.getModuleKeys() : [];
            var modulePromises = moduleKeys.map((key: ModuleKey) => new api.module.GetModuleRequest(key).sendAndParse());
            return wemQ.all(modulePromises).
                then((modules: Module[]) => {
                    var metadataMixinPromises: wemQ.Promise<Mixin>[] = [];

                    metadataMixinPromises = metadataMixinPromises.concat(
                        this.contentType.getMetadata().map((name: MixinName) => {
                            return new GetMixinByQualifiedNameRequest(name).sendAndParse();
                        }));

                    modules.forEach((mdl: Module) => {
                        metadataMixinPromises = metadataMixinPromises.concat(
                            mdl.getMetaSteps().map((name: MixinName) => {
                                return new GetMixinByQualifiedNameRequest(name).sendAndParse();
                            })
                        );
                    });

                    return wemQ.all(metadataMixinPromises);
                }).then((mixins: Mixin[]) => {
                    var steps: WizardStep[] = [];

                    this.contentWizardStep = new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm)
                    steps.push(this.contentWizardStep);

                    mixins.forEach((mixin: Mixin, index: number) => {
                        if (!this.metadataStepFormByName[mixin.getMixinName().toString()]) {
                            var stepForm = new ContentWizardStepForm();
                            this.metadataStepFormByName[mixin.getMixinName().toString()] = stepForm;
                            steps.splice(index + 1, 0, new WizardStep(mixin.getDisplayName(), stepForm));
                        }
                    });
                    this.settingsWizardStep = new WizardStep("Settings", this.settingsWizardStepForm);
                    steps.push(this.settingsWizardStep);
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
            this.thumbnailUploader.
                setValue(new ContentIconUrlResolver().setContent(persistedContent).resolve()).
                setEnabled(!persistedContent.isImage()).
                setParams({
                    id: persistedContent.getContentId().toString()
                });

            this.thumbnailUploader.toggleClass("invalid", !persistedContent.isValid());

            this.notifyValidityChanged(persistedContent.isValid());

            api.content.ContentSummaryAndCompareStatusFetcher.fetch(persistedContent.getContentId()).
                then((contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus) => {
                    var ignore = contentSummaryAndCompareStatus.getCompareStatus() !== CompareStatus.NEW;
                    this.contentWizardHeader.disableNameGeneration(ignore);
                    if (!ignore) {
                        var publishHandler = (event: api.content.ContentPublishedEvent) => {
                            if (this.getPersistedItem() && event.getContentId() &&
                                (this.getPersistedItem().getId() === event.getContentId().toString())) {

                                this.contentWizardHeader.disableNameGeneration(true);
                                api.content.ContentPublishedEvent.un(publishHandler);
                            }
                        };
                        api.content.ContentPublishedEvent.on(publishHandler);
                        this.onClosed(() => {
                            api.content.ContentPublishedEvent.un(publishHandler);
                        });
                    }
                }).done();

            var viewedContent;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                viewedContent = this.assembleViewedContent(persistedContent.newBuilder()).build();
                if (viewedContent.equals(persistedContent)) {

                    if (this.liveFormPanel) {
                        this.liveFormPanel.loadPage();
                    }
                } else {
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
                            var site = content.isSite() ? <Site>content : this.site;
                            this.siteModel = new SiteModel(site);
                            return this.initLiveEditModel(content, this.siteModel, formContext).then(() => {
                                this.liveFormPanel.setModel(this.liveEditModel);
                                this.liveFormPanel.loadPage();
                                return wemQ(null);
                            });
                        }
                        else {
                            this.liveFormPanel.loadPage();
                        }
                    }
                    if (!this.siteModel && content.isSite()) {
                        this.siteModel = new SiteModel(<Site>content);
                    }
                    if (this.siteModel) {
                        this.initSiteModelListeners();
                    }
                    return wemQ(null);
                });
            });
        }

        private initSiteModelListeners() {
            this.siteModel.onModuleAdded((event: api.content.site.ModuleAddedEvent) => {
                this.addMetadataStepForms(event.getModuleKey());
            });

            this.siteModel.onModuleRemoved((event: api.content.site.ModuleRemovedEvent) => {
                this.removeMetadataStepForms();
            });
        }

        private removeMetadataStepForms() {
            var moduleKeys = this.siteModel.getModuleKeys();
            var modulePromises = moduleKeys.map((key: ModuleKey) => new api.module.GetModuleRequest(key).sendAndParse());

            return wemQ.all(modulePromises).
                then((modules: Module[]) => {
                    var metadataMixinPromises: wemQ.Promise<Mixin>[] = [];

                    modules.forEach((mdl: Module) => {
                        metadataMixinPromises = metadataMixinPromises.concat(
                            mdl.getMetaSteps().map((name: MixinName) => {
                                return new GetMixinByQualifiedNameRequest(name).sendAndParse();
                            })
                        );
                    });

                    return wemQ.all(metadataMixinPromises);
                }).then((mixins: Mixin[]) => {
                    var activeMixinsNames = api.schema.mixin.MixinNames.create().fromMixins(mixins).build();

                    var panelNamesToRemoveBuilder = MixinNames.create();

                    for (var key in this.metadataStepFormByName) {// check all old mixin panels
                        var mixinName = new MixinName(key);
                        if (!activeMixinsNames.contains(mixinName)) {
                            panelNamesToRemoveBuilder.addMixinName(mixinName);
                        }
                    }
                    var panelNamesToRemove = panelNamesToRemoveBuilder.build();
                    panelNamesToRemove.forEach((panelName: MixinName) => {
                        this.removeStepWithForm(this.metadataStepFormByName[panelName.toString()]);
                        delete this.metadataStepFormByName[panelName.toString()];
                    });

                    return mixins;
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private addMetadataStepForms(moduleKey: ModuleKey) {
            new api.module.GetModuleRequest(moduleKey).sendAndParse().
                then((currentModule: Module) => {

                    var mixinNames = currentModule.getMetaSteps();

                    //remove already existing metadata
                    var mixinNamesToAdd = mixinNames.filter((mixinName: MixinName) => {
                        return !this.metadataStepFormByName[mixinName.toString()];
                    });

                    var getMixinPromises: wemQ.Promise<Mixin>[] = mixinNamesToAdd.map((name: MixinName) => {
                        return new GetMixinByQualifiedNameRequest(name).sendAndParse();
                    });
                    return wemQ.all(getMixinPromises);
                }).then((mixins: Mixin[]) => {
                    mixins.forEach((mixin: Mixin) => {
                        if (!this.metadataStepFormByName[mixin.getMixinName().toString()]) {

                            var stepForm = new ContentWizardStepForm();
                            this.metadataStepFormByName[mixin.getMixinName().toString()] = stepForm;

                            var wizardStep = new WizardStep(mixin.getDisplayName(), stepForm);
                            this.insertStepBefore(wizardStep, this.settingsWizardStep);

                            var metadata = new Metadata(mixin.getMixinName(), new PropertyTree(api.Client.get().getPropertyIdProvider()));

                            stepForm.layout(this.createFormContext(this.getPersistedItem()), metadata.getData(), mixin.toForm());
                        }
                    });

                    return mixins;
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private initLiveEditModel(content: Content, siteModel: SiteModel, formContext: ContentFormContext): wemQ.Promise<void> {
            this.initSiteModelListeners();
            this.liveEditModel = LiveEditModel.create().
                setParentContent(this.parentContent).
                setContent(content).
                setContentFormContext(formContext).
                setSiteModel(siteModel).build();
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
            } else {
                var formContext = this.createFormContext(null);
                var form = this.contentType.getForm();
                var data = new PropertyTree();
                var formView = new FormView(formContext, form, data.getRoot());
                formView.layout().then(() => {

                    deferred.resolve(new CreateContentRequest().
                        setRequireValid(this.requireValid).
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
                setRequireValid(this.requireValid).
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

        setRequireValid(requireValid: boolean) {
            this.requireValid = requireValid;
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
            var contentFormHasValidUserInput = this.contentWizardStepForm.getFormView().hasValidUserInput();

            var allMetadataFormsValid = true;
            var allMetadataFormsHasValidUserInput = true;
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    var form = this.metadataStepFormByName[key];
                    if (!form.isValid()) {
                        form.displayValidationErrors(true);
                        allMetadataFormsValid = false;
                    }
                    var formHasValidUserInput = form.getFormView().hasValidUserInput();
                    if (!formHasValidUserInput) {
                        allMetadataFormsHasValidUserInput = false;
                    }
                }
            }
            return this.isContentFormValid && allMetadataFormsValid && contentFormHasValidUserInput && allMetadataFormsHasValidUserInput;
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

        unContentNamed(listener: (event: api.content.ContentNamedEvent)=>void) {
            this.contentNamedListeners = this.contentNamedListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
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