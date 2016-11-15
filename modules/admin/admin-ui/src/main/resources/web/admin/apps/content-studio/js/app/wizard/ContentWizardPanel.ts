import "../../api.ts";
import {DefaultModels} from "./page/DefaultModels";
import {ContentWizardStepForm} from "./ContentWizardStepForm";
import {SettingsWizardStepForm} from "./SettingsWizardStepForm";
import {SecurityWizardStepForm} from "./SecurityWizardStepForm";
import {DisplayNameScriptExecutor} from "./DisplayNameScriptExecutor";
import {LiveFormPanel, LiveFormPanelConfig} from "./page/LiveFormPanel";
import {ContentWizardToolbarPublishControls} from "./ContentWizardToolbarPublishControls";
import {ContentWizardActions} from "./action/ContentWizardActions";
import {ContentWizardPanelParams} from "./ContentWizardPanelParams";
import {ContentWizardToolbar} from "./ContentWizardToolbar";
import {ContentPermissionsAppliedEvent} from "./ContentPermissionsAppliedEvent";
import {Router} from "../Router";
import {PersistNewContentRoutine} from "./PersistNewContentRoutine";
import {UpdatePersistedContentRoutine} from "./UpdatePersistedContentRoutine";
import {ContentWizardDataLoader} from "./ContentWizardDataLoader";
import {ThumbnailUploaderEl} from "./ThumbnailUploaderEl";

import PropertyTree = api.data.PropertyTree;
import FormView = api.form.FormView;
import FormContextBuilder = api.form.FormContextBuilder;
import ContentFormContext = api.content.form.ContentFormContext;
import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentPath = api.content.ContentPath;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import ContentBuilder = api.content.ContentBuilder;
import Thumbnail = api.thumb.Thumbnail;
import ContentName = api.content.ContentName;
import ContentUnnamed = api.content.ContentUnnamed;
import CreateContentRequest = api.content.resource.CreateContentRequest;
import UpdateContentRequest = api.content.resource.UpdateContentRequest;
import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
import ExtraData = api.content.ExtraData;
import Page = api.content.page.Page;
import Site = api.content.site.Site;
import SiteModel = api.content.site.SiteModel;
import LiveEditModel = api.liveedit.LiveEditModel;
import ContentType = api.schema.content.ContentType;
import ContentTypeName = api.schema.content.ContentTypeName;
import PageTemplate = api.content.page.PageTemplate;
import PageDescriptor = api.content.page.PageDescriptor;
import AccessControlList = api.security.acl.AccessControlList;
import AccessControlEntry = api.security.acl.AccessControlEntry;
import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
import IsRenderableRequest = api.content.page.IsRenderableRequest;
import GetNearestSiteRequest = api.content.resource.GetNearestSiteRequest;
import GetPageDescriptorsByApplicationsRequest = api.content.page.GetPageDescriptorsByApplicationsRequest;

import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import FormIcon = api.app.wizard.FormIcon;
import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
import TogglerButton = api.ui.button.TogglerButton;
import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
import WizardStep = api.app.wizard.WizardStep;
import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;
import ContentRequiresSaveEvent = api.content.event.ContentRequiresSaveEvent;
import ImageErrorEvent = api.content.image.ImageErrorEvent;

import Application = api.application.Application;
import ApplicationKey = api.application.ApplicationKey;
import ApplicationEvent = api.application.ApplicationEvent;
import ApplicationEventType = api.application.ApplicationEventType;
import Mixin = api.schema.mixin.Mixin;
import MixinName = api.schema.mixin.MixinName;
import MixinNames = api.schema.mixin.MixinNames;
import GetMixinByQualifiedNameRequest = api.schema.mixin.GetMixinByQualifiedNameRequest;

import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import ContentNamedEvent = api.content.event.ContentNamedEvent;
import ActiveContentVersionSetEvent = api.content.event.ActiveContentVersionSetEvent;
import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;

import DialogButton = api.ui.dialog.DialogButton;

import Toolbar = api.ui.toolbar.Toolbar;
import CycleButton = api.ui.button.CycleButton;

import Permission = api.security.acl.Permission;

export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

    private contentParams: ContentWizardPanelParams;

    private parentContent: Content;

    private defaultModels: DefaultModels;

    private site: Site;

    private contentType: ContentType;

    private siteModel: SiteModel;

    private liveEditModel: LiveEditModel;

    private contentWizardStep: WizardStep;

    private contentWizardStepForm: ContentWizardStepForm;

    private settingsWizardStepForm: SettingsWizardStepForm;

    private settingsWizardStep: WizardStep;

    private securityWizardStepForm: SecurityWizardStepForm;

    private metadataStepFormByName: {[name: string]: ContentWizardStepForm;};

    private displayNameScriptExecutor: DisplayNameScriptExecutor;

    private requireValid: boolean;

    private wizardActions: ContentWizardActions;

    private isContentFormValid: boolean;

    private contentNamedListeners: {(event: ContentNamedEvent): void}[];

    private isSecurityWizardStepFormAllowed: boolean;

    private inMobileViewMode: boolean;

    private skipValidation: boolean;

    private currentContentCompareStatus: CompareStatus;

    private persistedContentCompareStatus: CompareStatus;

    private dataChangedListener: () => void;

    private applicationAddedListener: (event: api.content.site.ApplicationAddedEvent) => void;

    private applicationRemovedListener: (event: api.content.site.ApplicationRemovedEvent) => void;

    private applicationUnavailableListener: (event: ApplicationEvent) => void;

    private static EDITOR_DISABLED_TYPES = [ContentTypeName.FOLDER, ContentTypeName.TEMPLATE_FOLDER, ContentTypeName.SHORTCUT,
        ContentTypeName.UNSTRUCTURED];

    private contentUpdateDisabled: boolean;

    public static debug: boolean = false;

    constructor(params: ContentWizardPanelParams) {

        this.contentParams = params;

        this.isContentFormValid = false;
        this.isSecurityWizardStepFormAllowed = false;

        this.requireValid = false;
        this.skipValidation = false;
        this.contentNamedListeners = [];
        this.contentUpdateDisabled = false;

        this.displayNameScriptExecutor = new DisplayNameScriptExecutor();

        this.initWizardActions();

        this.metadataStepFormByName = {};

        super({
            tabId: params.tabId,
            persistedItem: null,
            actions: this.wizardActions
        });

        this.initListeners();
        this.listenToContentEvents();
        this.handleSiteConfigApply();
        this.handleBrokenImageInTheWizard();
    }

    private initWizardActions() {
        this.wizardActions = new ContentWizardActions(this);
        this.wizardActions.getShowLiveEditAction().setEnabled(false);
        this.wizardActions.getSaveAction().onExecuted(() => {
            this.contentWizardStepForm.validate();
            this.displayValidationErrors();
        });

        this.wizardActions.getShowSplitEditAction().onExecuted(() => {
            if (!this.inMobileViewMode) {
                this.getCycleViewModeButton()
                    .selectActiveAction(this.wizardActions.getShowLiveEditAction());
            }
        });
    }

    private initListeners() {

        let shownAndLoadedHandler = () => {
            if (this.getPersistedItem()) {
                Router.setHash("edit/" + this.getPersistedItem().getId());
            } else {
                Router.setHash("new/" + this.contentType.getName());
            }
        };

        this.onShown(() => {
            if (this.isDataLoaded()) {
                shownAndLoadedHandler();
            } else {
                this.onDataLoaded(shownAndLoadedHandler);
            }
        });

        this.dataChangedListener = () => {
            setTimeout(
                this.updatePublishStatusOnDataChange.bind(this), 100);
        };

        this.applicationAddedListener = (event: api.content.site.ApplicationAddedEvent) => {
            this.addMetadataStepForms(event.getApplicationKey());
        };

        this.applicationRemovedListener = (event: api.content.site.ApplicationRemovedEvent) => {
            this.removeMetadataStepForms();
        };

        this.applicationUnavailableListener = (event: ApplicationEvent) => {
            var isAppFromSiteModelUnavailable: boolean = this.siteModel.getApplicationKeys().some((applicationKey: ApplicationKey) => {
                return event.getApplicationKey().equals(applicationKey);
            });

            if (isAppFromSiteModelUnavailable) {
                let message = "Required application " + event.getApplicationKey().toString() + " not available.";

                if (this.isVisible()) {
                    api.notify.showWarning(message);
                }
                else {
                    let shownHandler = () => {
                        new api.application.GetApplicationRequest(event.getApplicationKey()).sendAndParse()
                            .then(
                                (application: Application) => {
                                    if (application.getState() == "stopped") {
                                        api.notify.showWarning(message);
                                    }
                                })
                            .catch((reason: any) => { //app was uninstalled
                                api.notify.showWarning(message);
                            });

                        this.unShown(shownHandler);
                    };

                    this.onShown(shownHandler);

                }

            }

        };

        api.app.wizard.MaskContentWizardPanelEvent.on(event => {
            if (this.getPersistedItem().getContentId().equals(event.getContentId())) {
                this.params.actions.suspendActions(event.isMask());
            }
        });

        ContentPermissionsAppliedEvent.on((event) => this.contentPermissionsUpdated(event.getContent()));
    }

    protected doLoadData(): Q.Promise<api.content.Content> {
        if (ContentWizardPanel.debug) {
            console.debug("ContentWizardPanel.doLoadData at " + new Date().toISOString());
        }
        return new ContentWizardDataLoader().loadData(this.contentParams)
            .then((loader) => {
                if (ContentWizardPanel.debug) {
                    console.debug("ContentWizardPanel.doLoadData: loaded data at " + new Date().toISOString(), loader);
                }
                if (loader.content) {
                    // in case of new content will be created in super.loadData()
                    this.formState.setIsNew(false);
                    this.setPersistedItem(loader.content);
                }
                this.defaultModels = loader.defaultModels;
                this.site = loader.siteContent;
                this.contentType = loader.contentType;
                this.parentContent = loader.parentContent;
                this.persistedContentCompareStatus = this.currentContentCompareStatus = loader.compareStatus;

            }).then(() => super.doLoadData());
    }


    protected createFormIcon(): ThumbnailUploaderEl {
        var thumbnailUploader = new ThumbnailUploaderEl({
            name: 'thumbnail-uploader',
            deferred: true
        });

        if (this.contentParams.createSite || this.getPersistedItem().isSite()) {
            thumbnailUploader.addClass("site");
        }

        return thumbnailUploader;
    }

    public getFormIcon(): ThumbnailUploaderEl {
        return <ThumbnailUploaderEl> super.getFormIcon();
    }

    protected createMainToolbar(): Toolbar {
        return new ContentWizardToolbar({
            saveAction: this.wizardActions.getSaveAction(),
            deleteAction: this.wizardActions.getDeleteAction(),
            duplicateAction: this.wizardActions.getDuplicateAction(),
            previewAction: this.wizardActions.getPreviewAction(),
            publishAction: this.wizardActions.getPublishAction(),
            publishTreeAction: this.wizardActions.getPublishTreeAction(),
            unpublishAction: this.wizardActions.getUnpublishAction(),
            showLiveEditAction: this.wizardActions.getShowLiveEditAction(),
            showFormAction: this.wizardActions.getShowFormAction(),
            showSplitEditAction: this.wizardActions.getShowSplitEditAction(),
            publishMobileAction: this.wizardActions.getPublishMobileAction()
        });
    }

    public getMainToolbar(): ContentWizardToolbar {
        return <ContentWizardToolbar> super.getMainToolbar();
    }

    protected createWizardHeader(): api.app.wizard.WizardHeader {
        var header = new WizardHeaderWithDisplayNameAndNameBuilder()
            .setDisplayNameGenerator(this.displayNameScriptExecutor)
            .build();

        if (this.parentContent) {
            header.setPath(this.parentContent.getPath().prettifyUnnamedPathElements().toString() + "/");
        } else {
            header.setPath("/");
        }

        var existing = this.getPersistedItem();
        if (!!existing) {
            header.initNames(existing.getDisplayName(), existing.getName().toString(), false);
        }

        header.onPropertyChanged(this.dataChangedListener);

        return header;
    }

    public getWizardHeader(): WizardHeaderWithDisplayNameAndName {
        return <WizardHeaderWithDisplayNameAndName> super.getWizardHeader();
    }

    protected createLivePanel(): api.ui.panel.Panel {
        var liveFormPanel;
        var isSiteOrWithinSite = !!this.site || this.contentParams.createSite;
        var isPageTemplate = this.contentType.isPageTemplate();
        var isShortcut = this.contentType.isShortcut();

        if ((isSiteOrWithinSite || isPageTemplate) && !isShortcut) {

            liveFormPanel = new LiveFormPanel(<LiveFormPanelConfig> {
                contentWizardPanel: this,
                contentType: this.contentType.getContentTypeName(),
                defaultModels: this.defaultModels
            });
        }
        return liveFormPanel;
    }

    public getLivePanel(): LiveFormPanel {
        return <LiveFormPanel> super.getLivePanel();
    }

    doRenderOnDataLoaded(rendered): Q.Promise<boolean> {

        return super.doRenderOnDataLoaded(rendered, true).then((rendered) => {
            if (ContentWizardPanel.debug) {
                console.debug("ContentWizardPanel.doRenderOnDataLoaded at " + new Date().toISOString());
            }

            this.appendChild(this.getContentWizardToolbarPublishControls().getPublishButtonForMobile());

            if (this.contentType.hasContentDisplayNameScript()) {
                this.displayNameScriptExecutor.setScript(this.contentType.getContentDisplayNameScript());
            }

            this.addClass("content-wizard-panel");

            this.inMobileViewMode = false;

            var responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, this.availableSizeChangedHandler.bind(this));

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onShown(() => {
                this.updateButtonsState();
            });

            this.onValidityChanged((event: api.ValidityChangedEvent) => {
                let isThisValid = this.isValid(); // event.isValid() = false will prevent the call to this.isValid()
                this.isContentFormValid = isThisValid;
                var thumbnailUploader = this.getFormIcon();
                thumbnailUploader.toggleClass("invalid", isThisValid);
                this.getContentWizardToolbarPublishControls().setContentCanBePublished(this.checkContentCanBePublished());
                if (!this.formState.isNew()) {
                    this.displayValidationErrors();
                }
            });

            var thumbnailUploader = this.getFormIcon();
            if (thumbnailUploader) {
                thumbnailUploader.setEnabled(!this.contentType.isImage());
                thumbnailUploader.onFileUploaded(this.onFileUploaded.bind(this));
            }

            return rendered;
        });
    }

    private availableSizeChangedHandler(item: ResponsiveItem) {
        if (this.isVisible()) {
            this.updateStickyToolbar();
            if (item.isInRangeOrSmaller(ResponsiveRanges._720_960)) {
                this.inMobileViewMode = true;
                if (this.isSplitView()) {
                    if (this.isMinimized()) {
                        this.toggleMinimize();
                    }
                    this.showForm();
                    this.getCycleViewModeButton().selectActiveAction(this.wizardActions.getShowFormAction());
                }
            } else {
                if (this.inMobileViewMode && this.isLiveView()) {
                    this.inMobileViewMode = false;
                    this.showSplitEdit();
                }

                this.inMobileViewMode = false;
            }
        }
    }

    private onFileUploaded(event: api.ui.uploader.FileUploadedEvent<api.content.Content>) {
        var newPersistedContent: Content = event.getUploadItem().getModel();
        this.setPersistedItem(newPersistedContent);
        this.updateMetadataAndMetadataStepForms(newPersistedContent);
        this.updateThumbnailWithContent(newPersistedContent);
        var contentToDisplay = (newPersistedContent.getDisplayName() && newPersistedContent.getDisplayName().length > 0) ?
                               '\"' + newPersistedContent.getDisplayName() + '\"' : "Content";
        api.notify.showFeedback(contentToDisplay + ' saved');
    }

    private handleSiteConfigApply() {
        var siteConfigApplyHandler = (event: ContentRequiresSaveEvent) => {
            if (this.isCurrentContentId(event.getContentId())) {
                this.saveChanges();
            }
        };

        ContentRequiresSaveEvent.on(siteConfigApplyHandler);
        this.onClosed(() => {
            ContentRequiresSaveEvent.un(siteConfigApplyHandler);
        });
    }

    private handleBrokenImageInTheWizard() {
        var brokenImageHandler = (event: ImageErrorEvent) => {
            if (this.isCurrentContentId(event.getContentId())) {
                this.wizardActions.setDeleteOnlyMode(this.getPersistedItem());
            }
        };

        ImageErrorEvent.on(brokenImageHandler);
        this.onClosed(() => {
            ImageErrorEvent.un(brokenImageHandler);
        });
    }

    getContentType(): ContentType {
        return this.contentType;
    }

    giveInitialFocus() {

        if (this.contentType.hasContentDisplayNameScript()) {
            if (!this.contentWizardStepForm.giveFocus()) {
                this.getWizardHeader().giveFocus();
            }
        } else {
            this.getWizardHeader().giveFocus();
        }

        this.startRememberFocus();
    }

    private createSteps(): wemQ.Promise<Mixin[]> {

        this.contentWizardStepForm = new ContentWizardStepForm();
        this.settingsWizardStepForm = new SettingsWizardStepForm();
        this.securityWizardStepForm = new SecurityWizardStepForm();

        var applicationKeys = this.site ? this.site.getApplicationKeys() : [];
        var applicationPromises = applicationKeys.map((key: ApplicationKey) => this.fetchApplication(key));

        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            this.checkSecurityWizardStepFormAllowed(loginResult);
            return wemQ.all(applicationPromises);
        }).then((applications: Application[]) => {
            for (var i = 0; i < applications.length; i++) {
                var app = applications[i];
                if (!app.isStarted()) {
                    var deferred = wemQ.defer<Mixin[]>();
                    deferred.reject(new api.Exception("Application '" + app.getDisplayName() +
                                                      "' required by the site is not available. " +
                                                      "Make sure all applications specified in the site configuration are installed and started.",
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


    close(checkCanClose: boolean = false) {
        var liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {
            liveFormPanel.skipNextReloadConfirmation(true);
        }
        super.close(checkCanClose);
    }

    private fetchMixin(name: MixinName): wemQ.Promise<Mixin> {
        var deferred = wemQ.defer<Mixin>();
        new GetMixinByQualifiedNameRequest(name).sendAndParse().then((mixin) => {
            deferred.resolve(mixin);
        }).catch((reason) => {
            deferred.reject(new api.Exception("Content cannot be opened. Required mixin '" + name.toString() + "' not found.",
                api.ExceptionType.WARNING));
        }).done();
        return deferred.promise;
    }

    private fetchApplication(key: ApplicationKey): wemQ.Promise<Application> {
        var deferred = wemQ.defer<Application>();
        new api.application.GetApplicationRequest(key).sendAndParse().then((mod) => {
            deferred.resolve(mod);
        }).catch((reason) => {
            deferred.reject(new api.Exception("Content cannot be opened. Required application '" + key.toString() + "' not found.",
                api.ExceptionType.WARNING));
        }).done();
        return deferred.promise;
    }

    saveChanges(): wemQ.Promise<Content> {
        var liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {
            liveFormPanel.skipNextReloadConfirmation(true);
        }
        this.setRequireValid(false);
        this.contentUpdateDisabled = true;
        return super.saveChanges().then((content: Content) => {
            if (liveFormPanel) {
                this.liveEditModel.setContent(content);
                liveFormPanel.loadPage(false);
            }

            if (content.getType().isImage()) {
                this.updateWizard(content);
            }

            return content;
        }).finally(() => {
            this.contentUpdateDisabled = false;
        });
    }

    private isCurrentContentId(id: api.content.ContentId): boolean {
        return this.getPersistedItem() && id && this.getPersistedItem().getContentId().equals(id);
    }

    private persistedItemPathIsDescendantOrEqual(path: ContentPath): boolean {
        return this.getPersistedItem().getPath().isDescendantOf(path) || this.getPersistedItem().getPath().equals(path);
    }

    private updateWizard(content: Content, unchangedOnly: boolean = true) {

        this.updateWizardHeader(content);
        this.updateWizardStepForms(content, unchangedOnly);
        this.updateMetadataAndMetadataStepForms(content.clone(), unchangedOnly);
        this.resetLastFocusedElement();
    }

    private resetWizard() {

        this.getWizardHeader().resetBaseValues();

        this.contentWizardStepForm.reset();
        this.settingsWizardStepForm.reset();

        for (var key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                let form = this.metadataStepFormByName[key];
                form.reset();
            }
        }

    }

    private listenToContentEvents() {

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        var deleteHandler = (event: api.content.event.ContentDeletedEvent) => {
            if (this.getPersistedItem()) {
                event.getDeletedItems().filter((deletedItem) => {
                    return !!deletedItem;
                }).some((deletedItem) => {
                    if (this.getPersistedItem().getPath().equals(deletedItem.getContentPath())) {
                        if (deletedItem.isPending()) {
                            let publishControls = this.getContentWizardToolbarPublishControls();
                            publishControls.setContentCanBePublished(true, false);
                            publishControls.setCompareStatus(CompareStatus.PENDING_DELETE);
                            this.persistedContentCompareStatus = this.currentContentCompareStatus = CompareStatus.PENDING_DELETE;
                        } else {
                            this.close();
                        }

                        return true;
                    }
                });
            }
        };

        var publishOrUnpublishHandler = (contents: ContentSummaryAndCompareStatus[]) => {
            contents.forEach(content => {
                if (this.isCurrentContentId(content.getContentId())) {

                    this.persistedContentCompareStatus = this.currentContentCompareStatus = content.getCompareStatus();
                    this.getContentWizardToolbarPublishControls().setCompareStatus(this.currentContentCompareStatus);

                    this.getWizardHeader().disableNameGeneration(this.currentContentCompareStatus === CompareStatus.EQUAL);
                }
            });
        };

        var updateHandler = (contentId: ContentId, compareStatus?: CompareStatus) => {

            if (this.isCurrentContentId(contentId)) {
                if (compareStatus != undefined) {
                    this.persistedContentCompareStatus = this.currentContentCompareStatus = compareStatus;
                    this.getContentWizardToolbarPublishControls().setCompareStatus(compareStatus);
                }
                new GetContentByIdRequest(this.getPersistedItem().getContentId()).sendAndParse().done((content: Content) => {
                    let isAlreadyUpdated = content.equals(this.getPersistedItem());

                    if (!isAlreadyUpdated) {
                        this.setPersistedItem(content);
                        this.updateWizard(content, true);

                        if (this.isEditorEnabled()) {
                            // also update live form panel for renderable content without asking
                            this.updateLiveForm();
                        }
                        if (!this.isDisplayNameUpdated()) {
                            this.getWizardHeader().resetBaseValues();
                        }
                        this.wizardActions.setDeleteOnlyMode(this.getPersistedItem(), false);
                    } else {
                        this.resetWizard();
                    }
                });
            } else if (this.doHtmlAreasContainId(contentId.toString())) {   // Check if there are html areas in form that contain event.getContentId() that was updated
                new GetContentByIdRequest(this.getPersistedItem().getContentId()).sendAndParse().done((content: Content) => {
                    this.updateWizard(content, true);
                    if (this.isEditorEnabled()) {
                        let liveFormPanel = this.getLivePanel();
                        liveFormPanel.skipNextReloadConfirmation(true);
                        liveFormPanel.loadPage(false);
                    }
                });
            }

        };

        var sortedHandler = (data: ContentSummaryAndCompareStatus[]) => {
            var indexOfCurrentContent;
            var wasSorted = data.some((sorted: ContentSummaryAndCompareStatus, index: number) => {
                indexOfCurrentContent = index;
                return this.isCurrentContentId(sorted.getContentId());
            });
            if (wasSorted) {
                this.getContentWizardToolbarPublishControls().setCompareStatus(data[indexOfCurrentContent].getCompareStatus());
            }
        };

        var movedHandler = (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) => {
            var wasMoved = oldPaths.some((oldPath: ContentPath) => {
                return this.persistedItemPathIsDescendantOrEqual(oldPath);
            });

            if (wasMoved) {
                updateHandler(this.getPersistedItem().getContentId(), data[0].getCompareStatus());
            }
        };

        var contentUpdatedHandler = (data: ContentSummaryAndCompareStatus[]) => {
            if (!this.contentUpdateDisabled) {
                data.forEach((updated: ContentSummaryAndCompareStatus) => {
                    updateHandler(updated.getContentId(), updated.getCompareStatus());
                });
            }
        };

        ContentDeletedEvent.on(deleteHandler);
        ContentServerEventsHandler.getInstance().onContentMoved(movedHandler);
        ContentServerEventsHandler.getInstance().onContentSorted(sortedHandler);
        ContentServerEventsHandler.getInstance().onContentUpdated(contentUpdatedHandler);

        serverEvents.onContentPublished(publishOrUnpublishHandler);
        serverEvents.onContentUnpublished(publishOrUnpublishHandler);

        this.onClosed(() => {
            ContentDeletedEvent.un(deleteHandler);
            ContentServerEventsHandler.getInstance().unContentMoved(movedHandler);
            ContentServerEventsHandler.getInstance().unContentSorted(sortedHandler);
            ContentServerEventsHandler.getInstance().unContentUpdated(contentUpdatedHandler);

            serverEvents.unContentPublished(publishOrUnpublishHandler);
            serverEvents.unContentUnpublished(publishOrUnpublishHandler);
        });
    }

    private updateLiveForm() {
        var content = this.getPersistedItem(),
            formContext = this.createFormContext(content);

        if (!!this.siteModel) {
            this.unbindSiteModelListeners();
        }

        var liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {

            var site = content.isSite() ? <Site>content : this.site;
            this.siteModel = new SiteModel(site);
            return this.initLiveEditModel(content, this.siteModel, formContext).then(() => {
                liveFormPanel.setModel(this.liveEditModel);
                liveFormPanel.skipNextReloadConfirmation(true);
                liveFormPanel.loadPage(false);

                this.updateButtonsState();
                if (this.liveEditModel.getPageModel()) {
                    this.liveEditModel.getPageModel().onPageModeChanged(this.updateButtonsState.bind(this));
                }
                return wemQ(null);
            });

        }
        if (!this.siteModel && content.isSite()) {
            this.siteModel = new SiteModel(<Site>content);
        }
        if (this.siteModel) {
            this.initSiteModelListeners();
        }
    }

    private doHtmlAreasContainId(id: string): boolean {
        var areas = this.getHtmlAreasInForm(this.getContentType().getForm()),
            data: api.data.PropertyTree = this.getPersistedItem().getContentData();

        return areas.some((area) => {
            var property = data.getProperty(area);
            if (property && property.hasNonNullValue() && property.getType().equals(api.data.ValueTypes.STRING)) {
                return property.getString().indexOf(id) >= 0
            }
        });
    }

    private getHtmlAreasInForm(formItemContainer: api.form.FormItemContainer): string[] {
        var result: string[] = [];

        formItemContainer.getFormItems().forEach((item) => {
            if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormItemSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FieldSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSetOption)) {
                result = result.concat(this.getHtmlAreasInForm(<any>item));
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.Input)) {
                var input = <api.form.Input> item;
                if (input.getInputType().getName() === "HtmlArea") {
                    result.push(input.getPath().toString());
                }
            }
        });

        return result;
    }

    doLayout(persistedContent: Content): wemQ.Promise<void> {

        return super.doLayout(persistedContent).then(() => {

            if (ContentWizardPanel.debug) {
                console.debug("ContentWizardPanel.doLayout at " + new Date().toISOString(), persistedContent);
            }

            this.updateThumbnailWithContent(persistedContent);

            var publishControls = this.getContentWizardToolbarPublishControls();
            let wizardHeader = this.getWizardHeader();

            api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByContent(persistedContent).then((summaryAndStatus) => {
                this.persistedContentCompareStatus = this.currentContentCompareStatus = summaryAndStatus.getCompareStatus();

                wizardHeader.disableNameGeneration(this.currentContentCompareStatus !== CompareStatus.NEW);

                publishControls.setCompareStatus(this.currentContentCompareStatus);
                publishControls.setLeafContent(!this.getPersistedItem().hasChildren());
            });

            wizardHeader.setSimplifiedNameGeneration(persistedContent.getType().isDescendantOfMedia());
            publishControls.enableActionsForExisting(persistedContent);

            if (this.isRendered()) {

                var viewedContent = this.assembleViewedContent(persistedContent.newBuilder()).build();
                if (viewedContent.equals(persistedContent) || this.skipValidation) {

                    // force update wizard with server bounced values to erase incorrect ones
                    this.updateWizard(persistedContent, false);

                    var liveFormPanel = this.getLivePanel();
                    if (liveFormPanel) {
                        liveFormPanel.loadPage();
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
                        this.updateMetadataAndMetadataStepForms(persistedContent);
                    } else {
                        ConfirmationDialog.get().setQuestion(
                            "Received Content from server differs from what you have. Would you like to load changes from server?").setYesCallback(
                            () => this.doLayoutPersistedItem(persistedContent.clone())).setNoCallback(() => {/* Do nothing... */
                        }).show();
                    }
                }

            } else {

                return this.doLayoutPersistedItem(persistedContent.clone());
            }

        });

    }

    saveChangesWithoutValidation(): wemQ.Promise<Content> {
        this.skipValidation = true;

        let result = this.saveChanges();
        result.then(() => this.skipValidation = false);

        return result;
    }

    private updateThumbnailWithContent(content: Content) {
        var thumbnailUploader = this.getFormIcon();

        thumbnailUploader
            .setParams({
                id: content.getContentId().toString()
            })
            .setEnabled(!content.isImage())
            .setValue(new api.content.util.ContentIconUrlResolver().setContent(content).resolve());

        thumbnailUploader.toggleClass("invalid", !content.isValid());
    }

    private initLiveEditor(formContext: ContentFormContext, content: Content): wemQ.Promise<void> {
        if (ContentWizardPanel.debug) {
            console.debug("ContentWizardPanel.initLiveEditor at " + new Date().toISOString());
        }
        var deferred = wemQ.defer<void>();

        this.wizardActions.getShowLiveEditAction().setEnabled(false);
        this.wizardActions.getPreviewAction().setVisible(false);
        this.wizardActions.getPreviewAction().setEnabled(false);

        var liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {

            if (!this.liveEditModel) {
                var site = content.isSite() ? <Site>content : this.site;
                this.siteModel = new SiteModel(site);
                this.initLiveEditModel(content, this.siteModel, formContext).then(() => {
                    liveFormPanel.setModel(this.liveEditModel);
                    liveFormPanel.loadPage();
                    this.setupWizardLiveEdit();

                    this.updateButtonsState();
                    if (this.liveEditModel.getPageModel()) {
                        this.liveEditModel.getPageModel().onPageModeChanged(this.updateButtonsState.bind(this));
                    }

                    deferred.resolve(null);
                });
            }
            else {
                liveFormPanel.loadPage();
                deferred.resolve(null);
            }
        } else {
            deferred.resolve(null);
        }
        return deferred.promise;
    }

    // Remember that content has been cloned here and it is not the persistedItem any more
    private doLayoutPersistedItem(content: Content): wemQ.Promise<void> {
        if (ContentWizardPanel.debug) {
            console.debug("ContentWizardPanel.doLayoutPersistedItem at " + new Date().toISOString());
        }

        this.toggleClass("rendered", false);

        var formContext = this.createFormContext(content);

        return this.initLiveEditor(formContext, content).then(() => {
            return this.createSteps().then((schemas: Mixin[]) => {

                var contentData = content.getContentData();

                contentData.onChanged(this.dataChangedListener);

                var formViewLayoutPromises: wemQ.Promise<void>[] = [];
                formViewLayoutPromises.push(this.contentWizardStepForm.layout(formContext, contentData, this.contentType.getForm()));
                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());
                this.settingsWizardStepForm.layout(content);
                this.settingsWizardStepForm.getModel().onPropertyChanged(this.dataChangedListener);

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

                    var data = extraData.getData();
                    data.onChanged(this.dataChangedListener);

                    formViewLayoutPromises.push(metadataFormView.layout(formContext, data, metadataForm));
                });

                return wemQ.all(formViewLayoutPromises).spread<void>(() => {

                    this.contentWizardStepForm.getFormView().addClass("panel-may-display-validation-errors");
                    if (this.formState.isNew()) {
                        this.contentWizardStepForm.getFormView().highlightInputsOnValidityChange(true);
                    } else {
                        this.displayValidationErrors();
                    }

                    this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());

                    if (!this.siteModel && content.isSite()) {
                        this.siteModel = new SiteModel(<Site>content);
                    }
                    if (this.siteModel) {
                        this.initSiteModelListeners();
                    }
                    return wemQ(null);
                });
            });
        });
    }

    private setupWizardLiveEdit() {

        let editorEnabled = this.isEditorEnabled();
        let shouldOpenEditor = this.shouldOpenEditorByDefault();

        this.toggleClass("rendered", editorEnabled);

        this.wizardActions.getShowLiveEditAction().setEnabled(editorEnabled);
        this.wizardActions.getShowSplitEditAction().setEnabled(editorEnabled);
        this.wizardActions.getPreviewAction().setVisible(editorEnabled);

        this.getCycleViewModeButton().setVisible(editorEnabled);

        if (this.getEl().getWidth() > ResponsiveRanges._720_960.getMaximumRange() && (editorEnabled && shouldOpenEditor)) {
            this.wizardActions.getShowSplitEditAction().execute();
        } else if (!!this.getSplitPanel()) {

            this.wizardActions.getShowFormAction().execute();
        }
    }

    private initSiteModelListeners() {
        this.siteModel.onApplicationAdded(this.applicationAddedListener);
        this.siteModel.onApplicationRemoved(this.applicationRemovedListener);
        this.siteModel.onApplicationUnavailable(this.applicationUnavailableListener);
    }

    private unbindSiteModelListeners() {
        this.siteModel.unApplicationAdded(this.applicationAddedListener);
        this.siteModel.unApplicationRemoved(this.applicationRemovedListener);
        this.siteModel.unApplicationUnavailable(this.applicationUnavailableListener);
    }

    private removeMetadataStepForms() {
        var applicationKeys = this.siteModel.getApplicationKeys();
        var applicationPromises = applicationKeys.map(
            (key: ApplicationKey) => new api.application.GetApplicationRequest(key).sendAndParse());

        return wemQ.all(applicationPromises).then((applications: Application[]) => {
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
        new api.application.GetApplicationRequest(applicationKey).sendAndParse().then((currentApplication: Application) => {

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
        this.liveEditModel =
            LiveEditModel.create().setParentContent(this.parentContent).setContent(content).setContentFormContext(formContext).setSiteModel(
                siteModel).build();
        return this.liveEditModel.init(this.defaultModels.getPageTemplate(), this.defaultModels.getPageDescriptor());
    }

    persistNewItem(): wemQ.Promise<Content> {
        return new PersistNewContentRoutine(this).setCreateContentRequestProducer(this.produceCreateContentRequest).execute().then(
            (content: Content) => {
                api.notify.showFeedback('Content created');
                return content;
            });
    }

    postPersistNewItem(persistedContent: Content): wemQ.Promise<Content> {

        if (persistedContent.isSite()) {
            this.site = <Site>persistedContent;
        }

        return wemQ(persistedContent);
    }

    private produceCreateContentRequest(): wemQ.Promise<CreateContentRequest> {
        var deferred = wemQ.defer<CreateContentRequest>();

        var parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

        if (this.contentType.getContentTypeName().isMedia()) {
            deferred.resolve(null);
        } else {
            deferred.resolve(
                new CreateContentRequest()
                    .setRequireValid(this.requireValid)
                    .setName(api.content.ContentUnnamed.newUnnamed())
                    .setParent(parentPath)
                    .setContentType(this.contentType.getContentTypeName())
                    .setDisplayName("")     // new content is created on wizard open so display name is always empty
                    .setData(new PropertyTree()).setExtraData([]));
        }

        return deferred.promise;
    }

    private getOptionSetsInForm(formItemContainer: api.form.FormItemContainer): api.form.FormOptionSet[] {
        var result: api.form.FormOptionSet[] = [];

        formItemContainer.getFormItems().forEach((item) => {
            if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormItemSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FieldSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSetOption)) {
                result = result.concat(this.getOptionSetsInForm(<any>item));
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSet)) {
                var optionSet = <api.form.FormOptionSet> item;
                result.push(optionSet);
            }
        });

        return result;
    }

    updatePersistedItem(): wemQ.Promise<Content> {
        var persistedContent = this.getPersistedItem();

        var viewedContent = this.assembleViewedContent(persistedContent.newBuilder(), true).build();

        var updatePersistedContentRoutine = new UpdatePersistedContentRoutine(this, persistedContent,
            viewedContent).setUpdateContentRequestProducer(
            this.produceUpdateContentRequest);

        return updatePersistedContentRoutine.execute().then((content: Content) => {

            if (persistedContent.getName().isUnnamed() && !content.getName().isUnnamed()) {
                this.notifyContentNamed(content);
            }
            var contentToDisplay = (content.getDisplayName() && content.getDisplayName().length > 0) ?
                                   '\"' + content.getDisplayName() + '\"' : "Content";
            api.notify.showFeedback(contentToDisplay + ' saved');
            this.getWizardHeader().resetBaseValues();

            return content;
        });
    }

    private produceUpdateContentRequest(persistedContent: Content, viewedContent: Content): UpdateContentRequest {
        var persistedContent = this.getPersistedItem();

        var updateContentRequest = new UpdateContentRequest(persistedContent.getId()).setRequireValid(this.requireValid).setContentName(
            viewedContent.getName()).setDisplayName(viewedContent.getDisplayName()).setData(viewedContent.getContentData()).setExtraData(
            viewedContent.getAllExtraData()).setOwner(viewedContent.getOwner()).setLanguage(viewedContent.getLanguage());

        return updateContentRequest;
    }

    private isDisplayNameUpdated(): boolean {
        return this.getPersistedItem().getDisplayName() !== this.getWizardHeader().getDisplayName();
    }

    hasUnsavedChanges(): boolean {
        if (!this.isRendered()) {
            return false;
        }
        var persistedContent: Content = this.getPersistedItem();
        if (persistedContent == undefined) {
            return true;
        } else {

            let viewedContent = this.assembleViewedContent(new ContentBuilder(persistedContent)).build();

            // ignore empty values for auto-created content that hasn't been updated yet because it doesn't have data at all
            let ignoreEmptyValues = !persistedContent.getModifiedTime() || !persistedContent.getCreatedTime() ||
                                    persistedContent.getCreatedTime().getTime() == persistedContent.getModifiedTime().getTime();

            return !viewedContent.equals(persistedContent, ignoreEmptyValues);
        }
    }

    private enableDisplayNameScriptExecution(formView: FormView) {

        if (this.displayNameScriptExecutor.hasScript()) {

            formView.onKeyUp((event: KeyboardEvent) => {
                if (this.displayNameScriptExecutor.hasScript()) {
                    this.getWizardHeader().setDisplayName(this.displayNameScriptExecutor.execute());
                }
            });
        }
    }

    private assembleViewedContent(viewedContentBuilder: ContentBuilder, cleanFormRedundantData: boolean = false): ContentBuilder {

        viewedContentBuilder.setName(this.resolveContentNameForUpdateRequest());
        viewedContentBuilder.setDisplayName(this.getWizardHeader().getDisplayName());
        if (this.contentWizardStepForm) {
            if (!cleanFormRedundantData) {
                viewedContentBuilder.setData(this.contentWizardStepForm.getData());
            } else {
                var data: api.data.PropertyTree = new api.data.PropertyTree(this.contentWizardStepForm.getData().getRoot()); // copy
                viewedContentBuilder.setData(this.cleanFormRedundantData(data));
            }
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

    private cleanFormRedundantData(data: api.data.PropertyTree): api.data.PropertyTree {
        var optionSets = this.getOptionSetsInForm(this.getContentType().getForm());

        optionSets.forEach((optionSet) => {
            var property = data.getProperty(optionSet.getPath().toString());
            if (!!property) {
                var optionSetProperty = property.getPropertySet();
                var selectionArray = optionSetProperty.getPropertyArray("_selected");
                if (!selectionArray) {
                    return;
                }
                optionSet.getOptions().forEach((option: api.form.FormOptionSetOption) => {
                    var isSelected = false;
                    selectionArray.forEach((selectedOptionName: api.data.Property) => {
                        if (selectedOptionName.getString() == option.getName()) {
                            isSelected = true;
                        }
                    })
                    if (!isSelected) {
                        optionSetProperty.removeProperty(option.getName(), 0);
                    }
                })
            }
        });

        return data;
    }

    private assembleViewedPage(): Page {
        var liveFormPanel = this.getLivePanel();
        return liveFormPanel ? liveFormPanel.getPage() : null;
    }

    private resolveContentNameForUpdateRequest(): ContentName {
        if (api.util.StringHelper.isEmpty(this.getWizardHeader().getName())) {
            if (this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            } else {
                return ContentUnnamed.newUnnamed();
            }
        }
        return ContentName.fromString(this.getWizardHeader().getName());
    }

    setRequireValid(requireValid: boolean) {
        this.requireValid = requireValid;
    }

    showLiveEdit() {
        if (!this.inMobileViewMode) {
            this.showSplitEdit();
            return;
        }

        this.getSplitPanel().addClass("toggle-live").removeClass("toggle-form toggle-split");
        this.getMainToolbar().toggleClass("live", true);
        this.toggleClass("form", false);

        this.openLiveEdit();
    }

    showSplitEdit() {
        this.getSplitPanel().addClass("toggle-split").removeClass("toggle-live toggle-form");
        this.getMainToolbar().toggleClass("live", true);
        this.toggleClass("form", false);

        this.openLiveEdit();
    }

    showForm() {
        this.getSplitPanel().addClass("toggle-form").removeClass("toggle-live toggle-split");
        this.getMainToolbar().toggleClass("live", false);
        this.toggleClass("form", true);

        this.closeLiveEdit();
    }

    private isSplitView(): boolean {
        return this.getSplitPanel() && this.getSplitPanel().hasClass("toggle-split");
    }

    private isLiveView(): boolean {
        return this.getSplitPanel() && this.getSplitPanel().hasClass("toggle-live");
    }

    private displayValidationErrors() {
        if (!this.isContentFormValid) {
            this.contentWizardStepForm.displayValidationErrors(true);
        }

        for (var key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                var form = this.metadataStepFormByName[key];
                if (!form.isValid()) {
                    form.displayValidationErrors(true);
                }
            }
        }
    }

    public checkContentCanBePublished(): boolean {
        if (this.getContentWizardToolbarPublishControls().isPendingDelete()) {
            // allow deleting published content without validity check
            return true;
        }

        var allMetadataFormsValid = true,
            allMetadataFormsHaveValidUserInput = true;
        for (var key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                var form = this.metadataStepFormByName[key];
                if (!form.isValid()) {
                    allMetadataFormsValid = false;
                }
                var formHasValidUserInput = form.getFormView().hasValidUserInput();
                if (!formHasValidUserInput) {
                    allMetadataFormsHaveValidUserInput = false;
                }
            }
        }
        return this.isContentFormValid && allMetadataFormsValid && allMetadataFormsHaveValidUserInput;
    }

    getContextWindowToggler(): TogglerButton {
        return this.getMainToolbar().getContextWindowToggler();
    }

    getComponentsViewToggler(): TogglerButton {
        return this.getMainToolbar().getComponentsViewToggler();
    }

    getContentWizardToolbarPublishControls(): ContentWizardToolbarPublishControls {
        return this.getMainToolbar().getContentWizardToolbarPublishControls();
    }

    getCycleViewModeButton(): CycleButton {
        return this.getMainToolbar().getCycleViewModeButton();
    }

    getCloseAction(): api.ui.Action {
        return this.wizardActions.getCloseAction();
    }

    onContentNamed(listener: (event: ContentNamedEvent)=>void) {
        this.contentNamedListeners.push(listener);
    }

    unContentNamed(listener: (event: ContentNamedEvent)=>void) {
        this.contentNamedListeners = this.contentNamedListeners.filter((curr) => {
            return curr != listener;
        });
        return this;
    }

    getContentCompareStatus(): CompareStatus {
        return this.currentContentCompareStatus;
    }

    private notifyContentNamed(content: api.content.Content) {
        this.contentNamedListeners.forEach((listener: (event: ContentNamedEvent)=>void)=> {
            listener.call(this, new ContentNamedEvent(this, content));
        });
    }

    private contentPermissionsUpdated(content: Content) {
        var persistedContent: Content = this.getPersistedItem();

        if (persistedContent && (content.getId() === persistedContent.getId())) {
            var updatedContent: Content = persistedContent.newBuilder().setInheritPermissionsEnabled(
                content.isInheritPermissionsEnabled()).setPermissions(content.getPermissions().clone()).build();
            this.setPersistedItem(updatedContent);
        }
    }

    private createFormContext(content: Content): ContentFormContext {
        var formContext: ContentFormContext = <ContentFormContext>ContentFormContext.create().setSite(this.site).setParentContent(
            this.parentContent).setPersistedContent(content).setContentTypeName(
            this.contentType ? this.contentType.getContentTypeName() : undefined).setFormState(
            this.formState).setShowEmptyFormItemSetOccurrences(this.isItemPersisted()).build();
        return formContext;
    }

    private checkSecurityWizardStepFormAllowed(loginResult: api.security.auth.LoginResult) {

        if (this.getPersistedItem().isAnyPrincipalAllowed(loginResult.getPrincipals(), Permission.WRITE_PERMISSIONS)) {
            this.isSecurityWizardStepFormAllowed = true;
        }
    }

    private isPrincipalPresent(principalKey: api.security.PrincipalKey,
                               accessEntriesToCheck: AccessControlEntry[]): boolean {

        return accessEntriesToCheck.some((entry: AccessControlEntry) => {
            if (entry.getPrincipalKey().equals(principalKey)) {
                return true;
            }
        });
    }

    /**
     * Synchronizes wizard's extraData step forms with passed content - erases steps forms (meta)data and populates it with content's (meta)data.
     * @param content
     */
    private updateMetadataAndMetadataStepForms(content: Content, unchangedOnly: boolean = true) {
        var contentCopy = content.clone();

        for (var key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {

                var mixinName = new MixinName(key);
                var extraData = contentCopy.getExtraData(mixinName);
                if (!extraData) { // ensure ExtraData object corresponds to each step form
                    extraData = new ExtraData(mixinName, new PropertyTree());
                    contentCopy.getAllExtraData().push(extraData);
                }

                let form = this.metadataStepFormByName[key];
                form.getData().unChanged(this.dataChangedListener);

                let data = extraData.getData();
                data.onChanged(this.dataChangedListener);

                form.update(data, unchangedOnly);
            }
        }
    }

    private updateWizardStepForms(content: Content, unchangedOnly: boolean = true) {

        this.contentWizardStepForm.getData().unChanged(this.dataChangedListener);

        // remember to copy data to have persistedItem pristine
        var contentCopy = content.clone();
        contentCopy.getContentData().onChanged(this.dataChangedListener);

        this.contentWizardStepForm.update(contentCopy.getContentData(), unchangedOnly).then(() => {
            setTimeout(this.contentWizardStepForm.validate.bind(this.contentWizardStepForm), 100);
        });


        if (contentCopy.isSite()) {
            this.siteModel.update(<Site>contentCopy);
        }

        this.settingsWizardStepForm.update(contentCopy, unchangedOnly);

        if (this.isSecurityWizardStepFormAllowed) {
            this.securityWizardStepForm.update(contentCopy, unchangedOnly);
        }
    }

    private updateWizardHeader(content: Content) {

        this.updateThumbnailWithContent(content);

        this.getWizardHeader().initNames(content.getDisplayName(), content.getName().toString(), true, false);

        // case when content was moved
        this.getWizardHeader()
            .setPath(content.getPath().getParentPath().isRoot() ? "/" : content.getPath().getParentPath().toString() + "/");
    }

    private openLiveEdit() {
        var livePanel = this.getLivePanel();

        this.getSplitPanel().showSecondPanel();
        livePanel.clearPageViewSelectionAndOpenInspectPage();
        this.showMinimizeEditButton();
    }

    private closeLiveEdit() {
        this.getSplitPanel().hideSecondPanel();
        this.hideMinimizeEditButton();

        if (this.liveMask && this.liveMask.isVisible()) {
            this.liveMask.hide();
        }

        if (this.isMinimized()) {
            this.toggleMinimize();
        }
    }

    private isContentRenderable(): boolean {
        return !!this.liveEditModel && this.liveEditModel.isPageRenderable();
    }

    private shouldOpenEditorByDefault(): boolean {
        let isTemplate = this.contentType.getContentTypeName().isPageTemplate();
        let isSite = this.contentType.getContentTypeName().isSite();

        return this.isContentRenderable() || isSite || isTemplate;
    }

    private isEditorEnabled(): boolean {

        return !!this.site || ( this.shouldOpenEditorByDefault() && !api.ObjectHelper.contains(ContentWizardPanel.EDITOR_DISABLED_TYPES,
                this.contentType.getContentTypeName()));
    }

    private updateButtonsState() {
        let isRenderable = this.isContentRenderable();

        this.wizardActions.getPreviewAction().setEnabled(isRenderable);
        this.getContextWindowToggler().setEnabled(isRenderable);
        this.getComponentsViewToggler().setEnabled(isRenderable);

        this.getComponentsViewToggler().setVisible(isRenderable);
        this.getContextWindowToggler().setVisible(isRenderable);
    }

    private updatePublishStatusOnDataChange() {
        var publishControls = this.getContentWizardToolbarPublishControls();

        if (this.isContentFormValid) {
            if (!this.hasUnsavedChanges()) {
                // WARN: intended to restore status to persisted value if data is changed to original values,
                // but if invoked after save this will revert status to persisted one as well 
                this.currentContentCompareStatus = this.persistedContentCompareStatus;

            } else if (publishControls.isOnline()) {
                this.currentContentCompareStatus = CompareStatus.NEWER;
            }
            publishControls.setCompareStatus(this.currentContentCompareStatus);
        }
    }

    getLiveMask(): api.ui.mask.LoadMask {
        return this.liveMask;
    }

}
