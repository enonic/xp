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
    import ContentUnnamed = api.content.ContentUnnamed;
    import CreateContentRequest = api.content.CreateContentRequest;
    import UpdateContentRequest = api.content.UpdateContentRequest;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import ExtraData = api.content.ExtraData;
    import Page = api.content.page.Page;
    import Site = api.content.site.Site;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import ContentType = api.schema.content.ContentType;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import AccessControlList = api.security.acl.AccessControlList;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import IsRenderableRequest = api.content.page.IsRenderableRequest;
    import GetNearestSiteRequest = api.content.GetNearestSiteRequest;
    import GetPageDescriptorsByApplicationsRequest = api.content.page.GetPageDescriptorsByApplicationsRequest;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import FormIcon = api.app.wizard.FormIcon;
    import ThumbnailUploader = api.content.ThumbnailUploader;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import TogglerButton = api.ui.button.TogglerButton;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;

    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import Mixin = api.schema.mixin.Mixin;
    import MixinName = api.schema.mixin.MixinName;
    import MixinNames = api.schema.mixin.MixinNames;
    import GetMixinByQualifiedNameRequest = api.schema.mixin.GetMixinByQualifiedNameRequest;

    import DialogButton = api.ui.dialog.DialogButton;

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

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private liveFormPanel: page.LiveFormPanel;

        private showLiveEditAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        private requireValid: boolean;

        private createSite: boolean;

        private previewAction: api.ui.Action;

        private publishAction: api.ui.Action;

        private contextWindowToggler: TogglerButton;

        private componentsViewToggler: TogglerButton;

        private cycleViewModeButton: api.ui.button.CycleButton;

        private wizardActions: app.wizard.action.ContentWizardActions;

        private isContentFormValid: boolean;

        private contentNamedListeners: {(event: api.content.ContentNamedEvent):void}[];

        private isSecurityWizardStepFormAllowed: boolean;

        private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

        private publishButtonForMobile: api.ui.dialog.DialogButton;

        private inMobileViewMode: boolean;

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(params: ContentWizardPanelParams, onSuccess: (wizard: ContentWizardPanel) => void, onError?: (reason: any) => void) {

            this.constructing = true;
            this.isContentFormValid = false;
            this.isSecurityWizardStepFormAllowed = false;

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
                deleteAction: this.wizardActions.getDeleteAction(),
                duplicateAction: this.wizardActions.getDuplicateAction(),
                previewAction: this.wizardActions.getPreviewAction(),
                publishAction: this.wizardActions.getPublishAction(),
                showLiveEditAction: this.wizardActions.getShowLiveEditAction(),
                showFormAction: this.wizardActions.getShowFormAction(),
                showSplitEditAction: this.wizardActions.getShowSplitEditAction()
            });

            this.contextWindowToggler = mainToolbar.getContextWindowToggler();
            this.componentsViewToggler = mainToolbar.getComponentsViewToggler();
            this.cycleViewModeButton = mainToolbar.getCycleViewModeButton();
            this.contentWizardToolbarPublishControls = mainToolbar.getContentWizardToolbarPublishControls();
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
            var isPageTemplate = this.contentType.getContentTypeName().isPageTemplate();
            if (isSiteOrWithinSite || isPageTemplate) {

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
                    this.contentWizardToolbarPublishControls.setContentCanBePublished(this.checkContentCanBePublished(false));
                });

                this.addClass("content-wizard-panel");
                if (this.getSplitPanel()) {
                    this.getSplitPanel().addClass("prerendered");
                }

                this.inMobileViewMode = false;
                var responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                    if (this.isVisible()) {
                        this.updateStickyToolbar();
                        if (item.isInRangeOrSmaller(ResponsiveRanges._720_960)) {
                            this.inMobileViewMode = true;
                            if (this.isSplitView()) {
                                if (this.isMinimized()) {
                                    this.toggleMinimize();
                                }
                                this.cycleViewModeButton.executePrevAction();
                            }
                        } else {
                            if (this.inMobileViewMode && this.isLiveView()) {
                                this.inMobileViewMode = false;
                                this.showSplitEdit();
                            }

                            this.inMobileViewMode = false;
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

                    this.wizardActions.getShowSplitEditAction().onExecuted(() => {
                        if (!this.inMobileViewMode) {
                            if (this.contentNotRenderable()) {
                                this.closeLiveEdit();
                                this.contextWindowToggler.setEnabled(false);
                            } else {
                                this.cycleViewModeButton.selectActiveAction(this.showLiveEditAction);
                            }
                        }
                    });

                    this.wizardActions.getShowSplitEditAction().execute();
                    responsiveItem.update();
                });

                if (this.thumbnailUploader) {
                    this.thumbnailUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                        var newPersistedContent: Content = event.getUploadItem().getModel();
                        this.setPersistedItem(newPersistedContent);
                        this.updateMetadataAndMetadataForms(newPersistedContent);
                        this.updateThumbnailWithContent(newPersistedContent);
                        var contentToDisplay = (newPersistedContent.getDisplayName() && newPersistedContent.getDisplayName().length > 0) ?
                                               '\"' + newPersistedContent.getDisplayName() + '\"' : "Content";
                        api.notify.showFeedback(contentToDisplay + ' saved');
                    });
                }

                this.constructing = false;

                onSuccess(this);
            }, onError);

            this.initPublishButtonForMobile();
            this.handleSiteConfigApply();

            api.app.wizard.MaskContentWizardPanelEvent.on(event => {
                if (this.getPersistedItem().getContentId().equals(event.getContentId())) {
                    this.actions.suspendActions(event.isMask());
                }
            });
        }

        private handleSiteConfigApply() {
            var siteConfigApplyHandler = (event: ContentRequiresSaveEvent) => {
                if (this.getPersistedItem().getId() == event.getContent().getId()) {
                    this.saveChanges()
                }
            };

            ContentRequiresSaveEvent.on(siteConfigApplyHandler);
            this.onClosed(() => {
                ContentRequiresSaveEvent.un(siteConfigApplyHandler);
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

            var applicationKeys = this.site ? this.site.getApplicationKeys() : [];
            var applicationPromises = applicationKeys.map((key: ApplicationKey) => this.fetchApplication(key));

            return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
                this.checkSecurityWizardStepFormAllowed(loginResult);
                this.enablePublishIfAllowed(loginResult);
                return wemQ.all(applicationPromises);
            }).then((applications: Application[]) => {
                for (var i = 0; i < applications.length; i++) {
                    var app = applications[i];
                    if (!app.isStarted()) {
                        var deferred = wemQ.defer<Mixin[]>();
                        deferred.reject(new api.Exception("Content cannot be opened. Required application '" + app.getDisplayName() +
                                                          "' is not started.",
                            api.ExceptionType.WARNING));
                        return deferred.promise;
                    }
                }

                var metadataMixinPromises: wemQ.Promise<Mixin>[] = [];
                metadataMixinPromises = metadataMixinPromises.concat(
                    this.contentType.getMetadata().map((name: MixinName) => {
                        return this.fetchMixin(name);
                    }));

                applications.forEach((app: Application) => {
                    metadataMixinPromises = metadataMixinPromises.concat(
                        app.getMetaSteps().map((name: MixinName) => {
                            return this.fetchMixin(name);
                        })
                    );
                });

                return wemQ.all(metadataMixinPromises);
            }).then((mixins: Mixin[]) => {
                var steps: WizardStep[] = [];

                this.contentWizardStep = new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm);
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

                if (this.isSecurityWizardStepFormAllowed) {
                    steps.push(new WizardStep("Security", this.securityWizardStepForm));
                }


                this.setSteps(steps);

                return mixins;
            });
        }

        private fetchMixin(name: MixinName): wemQ.Promise<Mixin> {
            var deferred = wemQ.defer<Mixin>();
            new GetMixinByQualifiedNameRequest(name).sendAndParse().
                then((mixin) => {
                    deferred.resolve(mixin);
                }).catch((reason) => {
                    deferred.reject(new api.Exception("Content cannot be opened. Required mixin '" + name.toString() + "' not found.",
                        api.ExceptionType.WARNING));
                }).done();
            return deferred.promise;
        }

        private fetchApplication(key: ApplicationKey): wemQ.Promise<Application> {
            var deferred = wemQ.defer<Application>();
            new api.application.GetApplicationRequest(key).sendAndParse().
                then((mod) => {
                    deferred.resolve(mod);
                }).catch((reason) => {
                    deferred.reject(new api.Exception("Content cannot be opened. Required application '" + key.toString() + "' not found.",
                        api.ExceptionType.WARNING));
                }).done();
            return deferred.promise;
        }

        saveChanges(): wemQ.Promise<Content> {
            if (this.liveFormPanel) {
                this.liveFormPanel.skipNextReloadConfirmation(true);
            }
            this.setRequireValid(false);
            return super.saveChanges();
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
            this.updateThumbnailWithContent(persistedContent);
            this.notifyValidityChanged(persistedContent.isValid());

            api.content.ContentSummaryAndCompareStatusFetcher.fetchByContent(persistedContent).
                then((contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus) => {
                    var ignore = contentSummaryAndCompareStatus.getCompareStatus() !== CompareStatus.NEW;
                    this.contentWizardHeader.disableNameGeneration(ignore);

                    var publishHandler = (event: api.content.ContentPublishedEvent) => {
                        if (this.getPersistedItem() && event.getContentId() &&
                            (this.getPersistedItem().getId() === event.getContentId().toString())) {

                            this.contentWizardToolbarPublishControls.setCompareStatus(CompareStatus.EQUAL);

                            if (!ignore) {
                                this.contentWizardHeader.disableNameGeneration(true);
                                api.content.ContentPublishedEvent.un(publishHandler);
                            }
                        }
                    };
                    api.content.ContentPublishedEvent.on(publishHandler);

                    this.onClosed(() => {
                        api.content.ContentPublishedEvent.un(publishHandler);
                    });
                    this.contentWizardToolbarPublishControls.setCompareStatus(contentSummaryAndCompareStatus.getCompareStatus());
                    this.managePublishButtonStateForMobile(contentSummaryAndCompareStatus.getCompareStatus());
                }).done();

            var viewedContent;
            var deferred = wemQ.defer<void>();
            if (!this.constructing) {

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
                    if (!api.ObjectHelper.arrayEquals(viewedContent.getAllExtraData(), persistedContent.getAllExtraData())) {
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

                    if (persistedContent.getType().isDescendantOfMedia()) {
                        this.updateMetadataAndMetadataForms(persistedContent.clone());
                    } else {
                        ConfirmationDialog.get().
                            setQuestion("Received Content from server differs from what you have. Would you like to load changes from server?").
                            setYesCallback(() => this.doLayoutPersistedItem(persistedContent.clone())).
                            setNoCallback(() => {/* Do nothing... */
                            }).
                            show();
                    }
                }

                deferred.resolve(null);
            } else {
                this.doLayoutPersistedItem(persistedContent.clone())
                    .then(()=> {
                        deferred.resolve(null);
                    }).catch((reason: any) => {
                        deferred.reject(reason);
                    }).done();
            }
            this.contentWizardHeader.setSimplifiedNameGeneration(persistedContent.getType().isDescendantOfMedia());
            this.contentWizardToolbarPublishControls.enableActionsForExisting(persistedContent);
            return deferred.promise;
        }

        private updateThumbnailWithContent(content: Content) {
            this.thumbnailUploader.
                setValue(new ContentIconUrlResolver().setContent(content).resolve()).
                setEnabled(!content.isImage()).
                setParams({
                    id: content.getContentId().toString()
                });

            this.thumbnailUploader.toggleClass("invalid", !content.isValid());
        }

        private doLayoutPersistedItem(content: Content): wemQ.Promise<void> {
            this.toggleClass("rendered", false);

            this.showLiveEditAction.setEnabled(false);
            this.previewAction.setVisible(false);
            this.previewAction.setEnabled(false);

            new GetNearestSiteRequest(content.getContentId()).sendAndParse().
                then((parentSite: Site) => {

                    if (parentSite || content.isSite()) {
                        this.setupWizardLiveEdit(true);
                    }
                    else {
                        this.setupWizardLiveEdit(false);
                    }

                }).catch((reason: any) => {
                    this.setupWizardLiveEdit(false);
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            var parallelPromises: wemQ.Promise<any>[] = [this.createSteps()];

            return wemQ.all(parallelPromises).
                spread<void>((schemas: Mixin[]) => {

                var formContext = this.createFormContext(content);

                var contentData = content.getContentData();

                contentData.onChanged(() => {
                    if (this.isContentFormValid && this.contentWizardToolbarPublishControls.isOnline()) {
                        this.contentWizardToolbarPublishControls.setCompareStatus(CompareStatus.NEWER);
                    }
                });

                var formViewLayoutPromises: wemQ.Promise<void>[] = [];
                formViewLayoutPromises.push(this.contentWizardStepForm.layout(formContext, contentData, this.contentType.getForm()));
                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());
                this.settingsWizardStepForm.layout(content);
                this.settingsWizardStepForm.setModel(new ContentSettingsModel(content));

                if (this.isSecurityWizardStepFormAllowed) {
                    this.securityWizardStepForm.layout(content);
                }


                schemas.forEach((schema: Mixin, index: number) => {
                    var extraData = content.getExtraData(schema.getMixinName());
                    if (!extraData) {
                        extraData = new ExtraData(schema.getMixinName(), new PropertyTree());
                        content.getAllExtraData().push(extraData);
                    }
                    var metadataFormView = this.metadataStepFormByName[schema.getMixinName().toString()];
                    var metadataForm = new api.form.FormBuilder().addFormItems(schema.getFormItems()).build();

                    formViewLayoutPromises.push(metadataFormView.layout(formContext, extraData.getData(), metadataForm));
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
                                this.updatePreviewActionVisibility();
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

        private setupWizardLiveEdit(renderable: boolean) {
            this.toggleClass("rendered", renderable);

            this.showLiveEditAction.setEnabled(renderable);
            this.showSplitEditAction.setEnabled(renderable);
            this.previewAction.setVisible(renderable);

            if (this.getEl().getWidth() > ResponsiveRanges._720_960.getMaximumRange() && renderable) {
                this.wizardActions.getShowSplitEditAction().execute();
            }
        }

        private initSiteModelListeners() {
            this.siteModel.onApplicationAdded((event: api.content.site.ApplicationAddedEvent) => {
                this.addMetadataStepForms(event.getApplicationKey());
            });

            this.siteModel.onApplicationRemoved((event: api.content.site.ApplicationRemovedEvent) => {
                this.removeMetadataStepForms();
            });
        }

        private removeMetadataStepForms() {
            var applicationKeys = this.siteModel.getApplicationKeys();
            var applicationPromises = applicationKeys.map((key: ApplicationKey) => new api.application.GetApplicationRequest(key).sendAndParse());

            return wemQ.all(applicationPromises).
                then((applications: Application[]) => {
                    var metadataMixinPromises: wemQ.Promise<Mixin>[] = [];

                    applications.forEach((app: Application) => {
                        metadataMixinPromises = metadataMixinPromises.concat(
                            app.getMetaSteps().map((name: MixinName) => {
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

        private addMetadataStepForms(applicationKey: ApplicationKey) {
            new api.application.GetApplicationRequest(applicationKey).sendAndParse().
                then((currentApplication: Application) => {

                    var mixinNames = currentApplication.getMetaSteps();

                    //remove already existing extraData
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

                            var extraData = new ExtraData(mixin.getMixinName(), new PropertyTree());

                            stepForm.layout(this.createFormContext(this.getPersistedItem()), extraData.getData(), mixin.toForm());
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
                api.notify.showFeedback('Content created');
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
                deferred.resolve(new CreateContentRequest().
                    setRequireValid(this.requireValid).
                    setName(api.content.ContentUnnamed.newUnnamed()).
                    setParent(parentPath).
                    setContentType(this.contentType.getContentTypeName()).
                    setDisplayName(this.contentWizardHeader.getDisplayName()).
                    setData(new PropertyTree()).
                    setExtraData([]));
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
                    var contentToDisplay = (content.getDisplayName() && content.getDisplayName().length > 0) ?
                                           '\"' + content.getDisplayName() + '\"' : "Content";
                    api.notify.showFeedback(contentToDisplay + ' saved');
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
                setExtraData(viewedContent.getAllExtraData()).
                setOwner(viewedContent.getOwner()).
                setLanguage(viewedContent.getLanguage());

            return updateContentRequest;
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {

                var viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();
                return !viewedContent.equals(persistedContent, true);
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

            var extraData: ExtraData[] = [];
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    extraData.push(new ExtraData(new MixinName(key), this.metadataStepFormByName[key].getData()));
                }
            }

            viewedContentBuilder.setExtraData(extraData);

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
            if (api.util.StringHelper.isEmpty(this.contentWizardHeader.getName())) {
                if (this.getPersistedItem().getName().isUnnamed()) {
                    return this.getPersistedItem().getName();
                } else {
                    return ContentUnnamed.newUnnamed();
                }
            }
            return ContentName.fromString(this.contentWizardHeader.getName());
        }

        setRequireValid(requireValid: boolean) {
            this.requireValid = requireValid;
        }

        showLiveEdit() {
            if (!this.inMobileViewMode) {
                this.showSplitEdit();
                return;
            }

            this.getSplitPanel().addClass("toggle-live");
            this.getSplitPanel().removeClass("toggle-form toggle-split prerendered");
            this.getMainToolbar().toggleClass("live", true);
            this.toggleClass("form", false);
            this.openLiveEdit();
            ResponsiveManager.fireResizeEvent();
        }

        showSplitEdit() {
            this.getSplitPanel().addClass("toggle-split");
            this.getSplitPanel().removeClass("toggle-live toggle-form prerendered");
            this.getMainToolbar().toggleClass("live", true);
            this.toggleClass("form", false);
            this.openLiveEdit();
            ResponsiveManager.fireResizeEvent();
        }

        showForm() {
            this.getSplitPanel().addClass("toggle-form");
            this.getSplitPanel().removeClass("toggle-live toggle-split prerendered");
            this.getMainToolbar().toggleClass("live", false);
            this.toggleClass("form", true);
            this.closeLiveEdit();
            ResponsiveManager.fireResizeEvent();
        }

        private isSplitView(): boolean {
            return this.getSplitPanel() && this.getSplitPanel().hasClass("toggle-split");
        }

        private isLiveView(): boolean {
            return this.getSplitPanel() && this.getSplitPanel().hasClass("toggle-live");
        }

        public checkContentCanBePublished(displayValidationErrors: boolean): boolean {
            if (!this.isContentFormValid) {
                this.contentWizardStepForm.displayValidationErrors(displayValidationErrors);
            }
            var contentFormHasValidUserInput = this.contentWizardStepForm.getFormView().hasValidUserInput();

            var allMetadataFormsValid = true;
            var allMetadataFormsHasValidUserInput = true;
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    var form = this.metadataStepFormByName[key];
                    if (!form.isValid()) {
                        form.displayValidationErrors(displayValidationErrors);
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

        getLiveFormPanel(): page.LiveFormPanel {
            return this.liveFormPanel;
        }

        getContextWindowToggler(): TogglerButton {
            return this.contextWindowToggler;
        }

        getComponentsViewToggler(): TogglerButton {
            return this.componentsViewToggler;
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
                setContentTypeName(this.contentType ? this.contentType.getContentTypeName() : undefined).
                setShowEmptyFormItemSetOccurrences(this.isItemPersisted()).
                build();
            return formContext;
        }

        private checkSecurityWizardStepFormAllowed(loginResult: api.security.auth.LoginResult) {

            var entries = this.getPersistedItem().getPermissions().getEntries();
            var accessEntriesWithEditPermissions: AccessControlEntry[] = entries.filter((item: AccessControlEntry) => {
                return item.isAllowed(api.security.acl.Permission.WRITE_PERMISSIONS);
            });

            loginResult.getPrincipals().some((principalKey: api.security.PrincipalKey) => {
                if (api.security.RoleKeys.ADMIN.equals(principalKey) ||
                    this.isPrincipalPresent(principalKey, accessEntriesWithEditPermissions)) {
                    this.isSecurityWizardStepFormAllowed = true;
                    return true;
                }
            });

        }

        private isPrincipalPresent(principalKey: api.security.PrincipalKey,
                                   accessEntriesToCheck: AccessControlEntry[]): boolean {
            var result = false;
            accessEntriesToCheck.some((entry: AccessControlEntry) => {
                if (entry.getPrincipalKey().equals(principalKey)) {
                    result = true;
                    return true;
                }
            });

            return result;
        }

        /**
         * Enables publish button if selected item has access entry with publish permission
         * for at least one of user's principals or if user contains Admin principal.
         * @param loginResult - user's authorisation state
         */
        private enablePublishIfAllowed(loginResult: api.security.auth.LoginResult) {
            var entries = this.getPersistedItem().getPermissions().getEntries();
            var accessEntriesWithPublishPermissions: AccessControlEntry[] = entries.filter((item: AccessControlEntry) => {
                return item.isAllowed(api.security.acl.Permission.PUBLISH);
            });

            loginResult.getPrincipals().some((principalKey: api.security.PrincipalKey) => {
                if (api.security.RoleKeys.ADMIN.equals(principalKey) ||
                    this.isPrincipalPresent(principalKey, accessEntriesWithPublishPermissions)) {
                    this.publishAction.setEnabled(true);
                    return true;
                }
            });
        }

        /**
         * Synchronizes wizard's extraData step forms with passed content - erases steps forms (meta)data and populates it with content's (meta)data.
         * @param content
         */
        private updateMetadataAndMetadataForms(content: Content) {
            var formContext = this.createFormContext(content);
            for (var key in this.metadataStepFormByName) {
                if (this.metadataStepFormByName.hasOwnProperty(key)) {
                    this.metadataStepFormByName[key].removeChildren();
                    var mixinName = new MixinName(key);
                    var extraData = content.getExtraData(mixinName);
                    if (!extraData) { // ensure ExtraData object corresponds to each step form
                        extraData = new ExtraData(mixinName, new PropertyTree());
                        content.getAllExtraData().push(extraData);
                    }
                    this.metadataStepFormByName[key].layout(formContext, extraData.getData(), this.metadataStepFormByName[key].getForm());
                }
            }
        }

        private initPublishButtonForMobile() {

            var action: api.ui.Action = new api.ui.Action("Publish", "enter");
            action.setIconClass("publish-action");
            action.onExecuted(() => {
                this.publishAction.execute();
            });

            this.publishButtonForMobile = new DialogButton(action);
            this.publishButtonForMobile.addClass("mobile-edit-publish-button");

            this.subscribePublishButtonForMobileToPublishEvents();

            var publishButtonContainer: api.ui.panel.Panel = this.getSplitPanel();
            if (!publishButtonContainer) {
                publishButtonContainer = this.getWizardStepsPanel();
            }

            publishButtonContainer.appendChild(this.publishButtonForMobile);
        }

        private managePublishButtonStateForMobile(compareStatus: CompareStatus) {
            var canBeShown = compareStatus !== CompareStatus.EQUAL;
            this.publishButtonForMobile.toggleClass("visible", canBeShown);
            this.publishButtonForMobile.setLabel("Publish " + api.content.CompareStatusFormatter.formatStatus(compareStatus) + " item");
        }

        private subscribePublishButtonForMobileToPublishEvents() {

            var publishHandler = (event: api.content.ContentsPublishedEvent) => {
                if (this.getPersistedItem() && event.getContentIds()) {
                    var isPublished = (event.getContentIds().some((obj: api.content.ContentId) => {
                        return obj.toString() == this.getPersistedItem().getId();
                    }));
                    if (isPublished) {
                        this.managePublishButtonStateForMobile(CompareStatus.EQUAL);
                    }
                }
            };

            var publishHandlerOfServerEvent = (event: api.content.ContentPublishedEvent) => {
                if (this.getPersistedItem() && event.getContentId() &&
                    (this.getPersistedItem().getId() === event.getContentId().toString())) {
                    this.managePublishButtonStateForMobile(CompareStatus.EQUAL);
                }
            };

            api.content.ContentsPublishedEvent.on(publishHandler);
            api.content.ContentPublishedEvent.on(publishHandlerOfServerEvent);

            this.onClosed(() => {
                api.content.ContentPublishedEvent.un(publishHandlerOfServerEvent);
                api.content.ContentsPublishedEvent.un(publishHandler);
            });
        }

        private openLiveEdit() {
            this.getSplitPanel().showSecondPanel();
            this.liveFormPanel.clearPageViewSelectionAndOpenInspectPage();
            this.showMinimizeEditButton();
        }

        private closeLiveEdit() {
            this.getSplitPanel().hideSecondPanel();
            this.hideMinimizeEditButton();

            if (this.isMinimized()) {
                this.toggleMinimize();
            }
        }

        private contentNotRenderable(): boolean {
            return this.liveEditModel.getPageModel().getMode() == api.content.page.PageMode.NO_CONTROLLER;
        }

        private updatePreviewActionVisibility() {
            this.previewAction.setEnabled(!this.contentNotRenderable());

            this.liveEditModel.getPageModel().onPageModeChanged(()=> {
                this.previewAction.setEnabled(!this.contentNotRenderable());
            });
        }
    }

}