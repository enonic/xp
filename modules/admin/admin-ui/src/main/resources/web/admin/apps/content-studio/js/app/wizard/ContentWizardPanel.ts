import '../../api.ts';
import {DefaultModels} from './page/DefaultModels';
import {ContentWizardStepForm} from './ContentWizardStepForm';
import {SettingsWizardStepForm} from './SettingsWizardStepForm';
import {ScheduleWizardStepForm} from './ScheduleWizardStepForm';
import {SecurityWizardStepForm} from './SecurityWizardStepForm';
import {DisplayNameScriptExecutor} from './DisplayNameScriptExecutor';
import {LiveFormPanel, LiveFormPanelConfig} from './page/LiveFormPanel';
import {ContentWizardToolbarPublishControls} from './ContentWizardToolbarPublishControls';
import {ContentWizardActions} from './action/ContentWizardActions';
import {ContentWizardPanelParams} from './ContentWizardPanelParams';
import {ContentWizardToolbar} from './ContentWizardToolbar';
import {Router} from '../Router';
import {PersistNewContentRoutine} from './PersistNewContentRoutine';
import {UpdatePersistedContentRoutine} from './UpdatePersistedContentRoutine';
import {ContentWizardDataLoader} from './ContentWizardDataLoader';
import {ThumbnailUploaderEl} from './ThumbnailUploaderEl';

import PropertyTree = api.data.PropertyTree;
import FormView = api.form.FormView;
import FormContextBuilder = api.form.FormContextBuilder;
import ContentFormContext = api.content.form.ContentFormContext;
import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentPath = api.content.ContentPath;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
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
import BeforeContentSavedEvent = api.content.event.BeforeContentSavedEvent;

import DialogButton = api.ui.dialog.DialogButton;

import Toolbar = api.ui.toolbar.Toolbar;
import CycleButton = api.ui.button.CycleButton;

import Permission = api.security.acl.Permission;
import Region = api.content.page.region.Region;
import Component = api.content.page.region.Component;
import ImageComponent = api.content.page.region.ImageComponent;
import ImageComponentType = api.content.page.region.ImageComponentType;
import ObjectHelper = api.ObjectHelper;
import LayoutComponentType = api.content.page.region.LayoutComponentType;
import LayoutComponent = api.content.page.region.LayoutComponent;
import FragmentComponent = api.content.page.region.FragmentComponent;
import FragmentComponentType = api.content.page.region.FragmentComponentType;

export class ContentWizardPanel extends api.app.wizard.WizardPanel<Content> {

    protected wizardActions: ContentWizardActions;

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

    private scheduleWizardStepForm: ScheduleWizardStepForm;

    private scheduleWizardStep: WizardStep;

    private scheduleWizardStepIndex: number;

    private securityWizardStepForm: SecurityWizardStepForm;

    private metadataStepFormByName: {[name: string]: ContentWizardStepForm;};

    private displayNameScriptExecutor: DisplayNameScriptExecutor;

    private requireValid: boolean;

    private isContentFormValid: boolean;

    private contentNamedListeners: {(event: ContentNamedEvent): void}[];

    private isSecurityWizardStepFormAllowed: boolean;

    private inMobileViewMode: boolean;

    private skipValidation: boolean;

    private currentContent: ContentSummaryAndCompareStatus;

    private persistedContent: ContentSummaryAndCompareStatus;

    private dataChangedListener: () => void;

    private applicationAddedListener: (event: api.content.site.ApplicationAddedEvent) => void;

    private applicationRemovedListener: (event: api.content.site.ApplicationRemovedEvent) => void;

    private applicationUnavailableListener: (event: ApplicationEvent) => void;

    private applicationStartedListener: (event: ApplicationEvent) => void;

    private static EDITOR_DISABLED_TYPES: ContentTypeName[] = [
        ContentTypeName.FOLDER,
        ContentTypeName.TEMPLATE_FOLDER,
        ContentTypeName.SHORTCUT,
        ContentTypeName.UNSTRUCTURED,
    ];

    private contentUpdateDisabled: boolean;

    private missingOrStoppedAppKeys: ApplicationKey[] = [];

    private contentDeleted: boolean;

    public static debug: boolean = false;

    constructor(params: ContentWizardPanelParams) {
        super({
            tabId: params.tabId
        });

        this.contentParams = params;

        this.loadData();

        this.isContentFormValid = false;
        this.isSecurityWizardStepFormAllowed = false;

        this.requireValid = false;
        this.skipValidation = false;
        this.contentNamedListeners = [];
        this.contentUpdateDisabled = false;

        this.displayNameScriptExecutor = new DisplayNameScriptExecutor();

        this.metadataStepFormByName = {};

        this.initListeners();
        this.listenToContentEvents();
        this.handleSiteConfigApply();
        this.handleBrokenImageInTheWizard();
        this.initBindings();
    }

    private initBindings() {
        let nextActions = this.resolveActions(this);
        let currentKeyBindings = api.ui.Action.getKeyBindings(nextActions);
        api.ui.KeyBindings.get().bindKeys(currentKeyBindings);
    }

    protected createWizardActions(): ContentWizardActions {
        let wizardActions: ContentWizardActions = new ContentWizardActions(this);
        wizardActions.getShowLiveEditAction().setEnabled(false);
        wizardActions.getSaveAction().onExecuted(() => {
            this.contentWizardStepForm.validate();
            this.displayValidationErrors();
        });

        wizardActions.getShowSplitEditAction().onExecuted(() => {
            if (!this.inMobileViewMode) {
                this.getCycleViewModeButton()
                    .selectActiveAction(wizardActions.getShowLiveEditAction());
            }
        });

        let publishActionHandler = () => {
            if (this.hasUnsavedChanges()) {
                this.contentWizardStepForm.validate();
                this.displayValidationErrors();
            }
        };

        wizardActions.getPublishAction().onExecuted(publishActionHandler);
        wizardActions.getUnpublishAction().onExecuted(publishActionHandler);
        wizardActions.getPublishTreeAction().onExecuted(publishActionHandler);

        return wizardActions;
    }

    private initListeners() {

        let shownAndLoadedHandler = () => {
            if (this.getPersistedItem()) {
                Router.setHash('edit/' + this.getPersistedItem().getId());
            } else {
                Router.setHash('new/' + this.contentType.getName());
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
            let isAppFromSiteModelUnavailable: boolean = this.siteModel.getApplicationKeys().some((applicationKey: ApplicationKey) => {
                return event.getApplicationKey().equals(applicationKey);
            });

            if (isAppFromSiteModelUnavailable) {
                this.missingOrStoppedAppKeys.push(event.getApplicationKey());

                let message = 'Required application ' + event.getApplicationKey().toString() + ' not available.';

                if (this.isVisible()) {
                    api.notify.showWarning(message);
                } else {
                    let shownHandler = () => {
                        new api.application.GetApplicationRequest(event.getApplicationKey()).sendAndParse()
                            .then(
                                (application: Application) => {
                                    if (application.getState() === 'stopped') {
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

                this.handleMissingApp();
            }
        };

        this.applicationStartedListener = (event: ApplicationEvent) => {
            let isAppFromSiteModelStarted: boolean = this.siteModel.getApplicationKeys().some((applicationKey: ApplicationKey) => {
                return event.getApplicationKey().equals(applicationKey);
            });

            if (isAppFromSiteModelStarted) {
                let indexToRemove = -1;
                this.missingOrStoppedAppKeys.some((applicationKey: ApplicationKey, index) => {
                    indexToRemove = index;
                    return event.getApplicationKey().equals(applicationKey);
                });
                if (indexToRemove > -1) {
                    this.missingOrStoppedAppKeys.splice(indexToRemove, 1);
                }
                this.handleMissingApp();
            }
        };

        api.app.wizard.MaskContentWizardPanelEvent.on(event => {
            if (this.getPersistedItem().getContentId().equals(event.getContentId())) {
                this.wizardActions.suspendActions(event.isMask());
            }
        });

    }

    protected doLoadData(): Q.Promise<api.content.Content> {
        if (ContentWizardPanel.debug) {
            console.debug('ContentWizardPanel.doLoadData at ' + new Date().toISOString());
        }
        return new ContentWizardDataLoader().loadData(this.contentParams)
            .then((loader) => {
                if (ContentWizardPanel.debug) {
                    console.debug('ContentWizardPanel.doLoadData: loaded data at ' + new Date().toISOString(), loader);
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
                this.persistedContent = this.currentContent =
                    ContentSummaryAndCompareStatus.fromContentAndCompareAndPublishStatus(
                        loader.content, loader.compareStatus, loader.publishStatus
                    );

            }).then(() => super.doLoadData());
    }

    protected createFormIcon(): ThumbnailUploaderEl {
        let thumbnailUploader = new ThumbnailUploaderEl({
            name: 'thumbnail-uploader',
            deferred: true
        });

        if (this.contentParams.createSite || this.getPersistedItem().isSite()) {
            thumbnailUploader.addClass('site');
        }

        return thumbnailUploader;
    }

    public getFormIcon(): ThumbnailUploaderEl {
        return <ThumbnailUploaderEl> super.getFormIcon();
    }

    protected createMainToolbar(): Toolbar {
        return new ContentWizardToolbar(this.contentParams.application, this.wizardActions);
    }

    public getMainToolbar(): ContentWizardToolbar {
        return <ContentWizardToolbar> super.getMainToolbar();
    }

    protected createWizardHeader(): api.app.wizard.WizardHeader {
        let header = new WizardHeaderWithDisplayNameAndNameBuilder()
            .setDisplayNameGenerator(this.displayNameScriptExecutor)
            .build();

        if (this.parentContent) {
            header.setPath(this.parentContent.getPath().prettifyUnnamedPathElements().toString() + '/');
        } else {
            header.setPath('/');
        }

        let existing = this.getPersistedItem();
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
        let liveFormPanel;
        let isSiteOrWithinSite = !!this.site || this.contentParams.createSite;
        let isPageTemplate = this.contentType.isPageTemplate();
        let isShortcut = this.contentType.isShortcut();

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

    doRenderOnDataLoaded(rendered: boolean): Q.Promise<boolean> {

        return super.doRenderOnDataLoaded(rendered, true).then((renderedAfter: boolean) => {
            if (ContentWizardPanel.debug) {
                console.debug('ContentWizardPanel.doRenderOnDataLoaded at ' + new Date().toISOString());
            }

            this.appendChild(this.getContentWizardToolbarPublishControls().getPublishButtonForMobile());

            if (this.contentType.hasContentDisplayNameScript()) {
                this.displayNameScriptExecutor.setScript(this.contentType.getContentDisplayNameScript());
            }

            this.addClass('content-wizard-panel');

            this.inMobileViewMode = false;

            ResponsiveManager.onAvailableSizeChanged(this, this.availableSizeChangedHandler.bind(this));

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onShown(() => {
                this.updateButtonsState();
            });

            this.onValidityChanged((event: api.ValidityChangedEvent) => {
                let isThisValid = this.isValid(); // event.isValid() = false will prevent the call to this.isValid()
                this.isContentFormValid = isThisValid;
                let thumbnailUploader = this.getFormIcon();
                thumbnailUploader.toggleClass('invalid', !isThisValid);
                this.getContentWizardToolbarPublishControls().setContentCanBePublished(this.checkContentCanBePublished());
                if (!this.formState.isNew()) {
                    this.displayValidationErrors();
                }
            });

            let thumbnailUploader = this.getFormIcon();
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
        let newPersistedContent: Content = event.getUploadItem().getModel();
        this.setPersistedItem(newPersistedContent);
        this.updateMetadataAndMetadataStepForms(newPersistedContent);
        this.updateThumbnailWithContent(newPersistedContent);
        let contentToDisplay = (newPersistedContent.getDisplayName() && newPersistedContent.getDisplayName().length > 0) ?
                               `"${newPersistedContent.getDisplayName()}"` : 'Content';
        api.notify.showFeedback(`${contentToDisplay} saved`);
    }

    private handleSiteConfigApply() {
        let siteConfigApplyHandler = (event: ContentRequiresSaveEvent) => {
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
        let brokenImageHandler = (event: ImageErrorEvent) => {
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
        this.scheduleWizardStepForm = new ScheduleWizardStepForm();
        this.securityWizardStepForm = new SecurityWizardStepForm();
        this.missingOrStoppedAppKeys = [];

        let applicationKeys = this.site ? this.site.getApplicationKeys() : [];
        let applicationPromises = applicationKeys.map((key: ApplicationKey) => this.fetchApplication(key));

        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            this.checkSecurityWizardStepFormAllowed(loginResult);
            return wemQ.all(applicationPromises);
        }).then((applications: Application[]) => {
            this.handleMissingApp();

            let metadataMixinPromises: wemQ.Promise<Mixin>[] = [];
            metadataMixinPromises = metadataMixinPromises.concat(
                this.contentType.getMetadata().map((name: MixinName) => {
                    return this.fetchMixin(name);
                }));

            applications.filter((app) => app != null).forEach((app: Application) => {
                metadataMixinPromises = metadataMixinPromises.concat(
                    app.getMetaSteps().map((name: MixinName) => {
                        return this.fetchMixin(name);
                    })
                );
            });

            return wemQ.all(metadataMixinPromises);
        }).then((mixins: Mixin[]) => {
            let steps: WizardStep[] = [];

            this.contentWizardStep = new WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm);
            steps.push(this.contentWizardStep);

            mixins.forEach((mixin: Mixin, index: number) => {
                if (!this.metadataStepFormByName[mixin.getMixinName().toString()]) {
                    let stepForm = new ContentWizardStepForm();
                    this.metadataStepFormByName[mixin.getMixinName().toString()] = stepForm;
                    steps.splice(index + 1, 0, new WizardStep(mixin.getDisplayName(), stepForm));
                }
            });
            this.settingsWizardStep = new WizardStep('Settings', this.settingsWizardStepForm);
            steps.push(this.settingsWizardStep);

            this.scheduleWizardStep = new WizardStep('Schedule', this.scheduleWizardStepForm);
            this.scheduleWizardStepIndex = steps.length;
            steps.push(this.scheduleWizardStep);

            if (this.isSecurityWizardStepFormAllowed) {
                steps.push(new WizardStep('Security', this.securityWizardStepForm));
            }

            this.setSteps(steps);

            // Content can be moved from its Site, but persisted content can still have metadata
            // Such content needs update
            const updateHandler = () => {
                const needUpdate = !this.getPersistedItem().extraDataEquals(this.getExtraDataFromMetadata());
                if (needUpdate) {
                    this.saveChanges();
                }
                this.unRendered(updateHandler);
            };
            this.onRendered(updateHandler);

            return mixins;
        });
    }

    close(checkCanClose: boolean = false) {
        let liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {
            liveFormPanel.skipNextReloadConfirmation(true);
        }
        super.close(checkCanClose);
    }

    private fetchMixin(name: MixinName): wemQ.Promise<Mixin> {
        let deferred = wemQ.defer<Mixin>();
        new GetMixinByQualifiedNameRequest(name).sendAndParse().then((mixin) => {
            deferred.resolve(mixin);
        }).catch((reason) => {
            const msg = `Content cannot be opened. Required mixin '${name.toString()}' not found.`;
            deferred.reject(new api.Exception(msg, api.ExceptionType.WARNING));
        }).done();
        return deferred.promise;
    }

    private fetchApplication(key: ApplicationKey): wemQ.Promise<Application> {
        let deferred = wemQ.defer<Application>();
        new api.application.GetApplicationRequest(key).sendAndParse().then((app) => {
            if (app.getState() === Application.STATE_STOPPED) {
                this.missingOrStoppedAppKeys.push(key);
            }
            deferred.resolve(app);
        }).catch((reason) => {
            this.missingOrStoppedAppKeys.push(key);
            deferred.resolve(null);
        }).done();
        return deferred.promise;
    }

    private handleMissingApp() {
        const appsIsMissing = this.missingOrStoppedAppKeys.length > 0;
        const livePanel = this.getLivePanel();
        const isRenderable = this.isContentRenderable();

        if (livePanel) {
            livePanel.toggleClass('no-preview', appsIsMissing);
        }

        this.getCycleViewModeButton().setEnabled(!appsIsMissing);

        this.getComponentsViewToggler().setVisible(isRenderable && !appsIsMissing);
        this.getContextWindowToggler().setVisible(isRenderable && !appsIsMissing);
    }

    saveChanges(): wemQ.Promise<Content> {
        let liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {
            liveFormPanel.skipNextReloadConfirmation(true);
        }
        this.setRequireValid(false);
        this.contentUpdateDisabled = true;
        new BeforeContentSavedEvent().fire();
        return super.saveChanges().then((content: Content) => {
            if (liveFormPanel) {
                this.liveEditModel.setContent(content);
                liveFormPanel.loadPage(false);
            }

            if (content.getType().isImage()) {
                this.updateWizard(content);
            } else if (this.isSecurityWizardStepFormAllowed) { // update security wizard to have new path/displayName etc.
                this.securityWizardStepForm.update(content);
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
        this.updateMetadataAndMetadataStepForms(content, unchangedOnly);
        this.resetLastFocusedElement();
    }

    private resetWizard() {

        this.getWizardHeader().resetBaseValues();

        this.contentWizardStepForm.reset();
        this.settingsWizardStepForm.reset();
        this.scheduleWizardStepForm.reset();

        for (let key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                let form = this.metadataStepFormByName[key];
                form.reset();
            }
        }

    }

    private setContent(compareStatus: CompareStatus) {
        this.persistedContent = this.currentContent.setCompareStatus(compareStatus);
        this.getContentWizardToolbarPublishControls().setContent(this.currentContent);

        this.wizardActions.refreshPendingDeleteDecorations();
    }

    private listenToContentEvents() {

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        let deleteHandler = (event: api.content.event.ContentDeletedEvent) => {
            if (!this.getPersistedItem()) {
                return;
            }

            event.getDeletedItems().filter((deletedItem) => {
                return !!deletedItem && this.getPersistedItem().getPath().equals(deletedItem.getContentPath());
            }).some((deletedItem) => {
                if (deletedItem.isPending()) {
                    this.getContentWizardToolbarPublishControls().setContentCanBePublished(true, false);
                    this.setContent(deletedItem.getCompareStatus());
                } else {
                    this.contentDeleted = true;
                    this.close();
                }

                return true;
            });

            event.getUndeletedItems().filter((undeletedItem) => {
                return !!undeletedItem && this.getPersistedItem().getPath().equals(undeletedItem.getContentPath());
            }).some((undeletedItem) => {
                this.setContent(undeletedItem.getCompareStatus());

                return true;
            });
        };

        let publishOrUnpublishHandler = (contents: ContentSummaryAndCompareStatus[]) => {
            contents.forEach(content => {
                if (this.isCurrentContentId(content.getContentId())) {
                    this.persistedContent = this.currentContent = content;
                    this.getContentWizardToolbarPublishControls().setContent(content);
                    this.refreshScheduleWizardStep();

                    this.getWizardHeader().disableNameGeneration(content.getCompareStatus() === CompareStatus.EQUAL);
                }
            });
        };

        let updateHandler = (updatedContent: ContentSummaryAndCompareStatus) => {

            if (this.isCurrentContentId(updatedContent.getContentId())) {

                this.persistedContent = this.currentContent = updatedContent;
                this.getContentWizardToolbarPublishControls().setContent(this.currentContent);

                if (this.currentContent.getCompareStatus() != null) {
                    this.refreshScheduleWizardStep();
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
            } else {
                this.doComponentsContainId(this.currentContent.getContentId()).then((contains) => {
                    if (contains) {
                        new GetContentByIdRequest(this.getPersistedItem().getContentId()).sendAndParse().done((content: Content) => {
                            this.updateWizard(content, true);
                            if (this.isEditorEnabled()) {
                                let liveFormPanel = this.getLivePanel();
                                liveFormPanel.skipNextReloadConfirmation(true);
                                liveFormPanel.loadPage(false);
                            }
                        });
                    }
                });
            }
        };

        let sortedHandler = (data: ContentSummaryAndCompareStatus[]) => {
            let indexOfCurrentContent;
            let wasSorted = data.some((sorted: ContentSummaryAndCompareStatus, index: number) => {
                indexOfCurrentContent = index;
                return this.isCurrentContentId(sorted.getContentId());
            });
            if (wasSorted) {
                this.getContentWizardToolbarPublishControls().setContent(data[indexOfCurrentContent]);
            }
        };

        let movedHandler = (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) => {
            let wasMoved = oldPaths.some((oldPath: ContentPath) => {
                return this.persistedItemPathIsDescendantOrEqual(oldPath);
            });

            if (wasMoved) {
                updateHandler(data[0]);
            }
        };

        let contentUpdatedHandler = (data: ContentSummaryAndCompareStatus[]) => {
            if (!this.contentUpdateDisabled) {
                data.forEach((updated: ContentSummaryAndCompareStatus) => {
                    updateHandler(updated);
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
        let content = this.getPersistedItem();
        let formContext = this.createFormContext(content);

        if (this.siteModel) {
            this.unbindSiteModelListeners();
        }

        let liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {

            let site = content.isSite() ? <Site>content : this.site;
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
            this.unbindSiteModelListeners();
            this.initSiteModelListeners();
        }
    }

    private doComponentsContainId(contentId: ContentId): wemQ.Promise<boolean> {
        const page = this.getPersistedItem().getPage();

        if (page) {
            if (this.doHtmlAreasContainId(contentId.toString())) {
                return wemQ(true);
            }

            return this.getPersistedItem().containsChildContentId(contentId);
        }

        return wemQ(false);
    }

    private doHtmlAreasContainId(id: string): boolean {
        let areas = this.getHtmlAreasInForm(this.getContentType().getForm());
        let data: api.data.PropertyTree = this.getPersistedItem().getContentData();

        return areas.some((area) => {
            let property = data.getProperty(area);
            if (property && property.hasNonNullValue() && property.getType().equals(api.data.ValueTypes.STRING)) {
                return property.getString().indexOf(id) >= 0;
            }
        });
    }

    private getHtmlAreasInForm(formItemContainer: api.form.FormItemContainer): string[] {
        let result: string[] = [];

        formItemContainer.getFormItems().forEach((item) => {
            if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormItemSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FieldSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSetOption)) {
                result = result.concat(this.getHtmlAreasInForm(<any>item));
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.Input)) {
                let input = <api.form.Input> item;
                if (input.getInputType().getName() === 'HtmlArea') {
                    result.push(input.getPath().toString());
                }
            }
        });

        return result;
    }

    doLayout(persistedContent: Content): wemQ.Promise<void> {

        return super.doLayout(persistedContent).then(() => {

            if (ContentWizardPanel.debug) {
                console.debug('ContentWizardPanel.doLayout at ' + new Date().toISOString(), persistedContent);
            }

            this.updateThumbnailWithContent(persistedContent);

            let publishControls = this.getContentWizardToolbarPublishControls();
            let wizardHeader = this.getWizardHeader();

            api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByContent(persistedContent).then((summaryAndStatus) => {
                this.persistedContent = this.currentContent = summaryAndStatus;

                wizardHeader.disableNameGeneration(this.currentContent.getCompareStatus() !== CompareStatus.NEW);

                publishControls.setContent(this.currentContent).setLeafContent(!this.getPersistedItem().hasChildren());
            });

            wizardHeader.setSimplifiedNameGeneration(persistedContent.getType().isDescendantOfMedia());
            publishControls.enableActionsForExisting(persistedContent);

            if (this.isRendered()) {

                let viewedContent = this.assembleViewedContent(persistedContent.newBuilder()).build();
                if (viewedContent.equals(persistedContent) || this.skipValidation) {

                    // force update wizard with server bounced values to erase incorrect ones
                    this.updateWizard(persistedContent, false);

                    let liveFormPanel = this.getLivePanel();
                    if (liveFormPanel) {
                        liveFormPanel.loadPage();
                    }
                } else {
                    console.warn(`Received Content from server differs from what's viewed:`);
                    if (!viewedContent.getContentData().equals(persistedContent.getContentData())) {
                        console.warn(' inequality found in Content.data');
                        if (persistedContent.getContentData() && viewedContent.getContentData()) {
                            console.warn(' comparing persistedContent.data against viewedContent.data:');
                            new api.data.PropertyTreeComparator().compareTree(persistedContent.getContentData(),
                                viewedContent.getContentData());
                        }
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getPage(), persistedContent.getPage())) {
                        console.warn(' inequality found in Content.page');
                        if (persistedContent.getPage() && viewedContent.getPage()) {
                            console.warn(' comparing persistedContent.page.config against viewedContent.page.config:');
                            new api.data.PropertyTreeComparator().compareTree(persistedContent.getPage().getConfig(),
                                viewedContent.getPage().getConfig());
                        }
                    }
                    if (!api.ObjectHelper.arrayEquals(viewedContent.getAllExtraData(), persistedContent.getAllExtraData())) {
                        console.warn(' inequality found in Content.meta');
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getAttachments(), persistedContent.getAttachments())) {
                        console.warn(' inequality found in Content.attachments');
                    }
                    if (!api.ObjectHelper.equals(viewedContent.getPermissions(), persistedContent.getPermissions())) {
                        console.warn(' inequality found in Content.permissions');
                    }
                    console.warn(' viewedContent: ', viewedContent);
                    console.warn(' persistedContent: ', persistedContent);

                    if (persistedContent.getType().isDescendantOfMedia()) {
                        this.updateMetadataAndMetadataStepForms(persistedContent);
                    } else {
                        const msg = 'Received Content from server differs from what you have. Would you like to load changes from server?';
                        ConfirmationDialog.get()
                            .setQuestion(msg)
                            .setYesCallback(() => this.doLayoutPersistedItem(persistedContent.clone()))
                            .setNoCallback(() => { /* empty */
                            })
                            .show();
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
        let thumbnailUploader = this.getFormIcon();

        thumbnailUploader
            .setParams({
                id: content.getContentId().toString()
            })
            .setEnabled(!content.isImage())
            .setValue(new api.content.util.ContentIconUrlResolver().setContent(content).resolve());

        thumbnailUploader.toggleClass('invalid', !content.isValid());
    }

    private initLiveEditor(formContext: ContentFormContext, content: Content): wemQ.Promise<void> {
        if (ContentWizardPanel.debug) {
            console.debug('ContentWizardPanel.initLiveEditor at ' + new Date().toISOString());
        }
        let deferred = wemQ.defer<void>();

        this.wizardActions.getShowLiveEditAction().setEnabled(false);
        this.wizardActions.getPreviewAction().setVisible(false);
        this.wizardActions.getPreviewAction().setEnabled(false);

        let liveFormPanel = this.getLivePanel();
        if (liveFormPanel) {

            if (!this.liveEditModel) {
                let site = content.isSite() ? <Site>content : this.site;
                this.siteModel = new SiteModel(site);
                this.initLiveEditModel(content, this.siteModel, formContext).then(() => {
                    liveFormPanel.setModel(this.liveEditModel);
                    liveFormPanel.loadPage();
                    this.setupWizardLiveEdit();
                    if (this.liveEditModel.getPageModel()) {
                        this.liveEditModel.getPageModel().onPageModeChanged(this.updateButtonsState.bind(this));
                    }

                    deferred.resolve(null);
                });
            } else {
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
            console.debug('ContentWizardPanel.doLayoutPersistedItem at ' + new Date().toISOString());
        }

        this.toggleClass('rendered', false);

        let formContext = this.createFormContext(content);

        return this.initLiveEditor(formContext, content).then(() => {
            this.updateButtonsState();
            return this.createSteps().then((schemas: Mixin[]) => {

                const contentData = content.getContentData();

                contentData.onChanged(this.dataChangedListener);

                let formViewLayoutPromises: wemQ.Promise<void>[] = [];
                formViewLayoutPromises.push(this.contentWizardStepForm.layout(formContext, contentData, this.contentType.getForm()));
                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor,
                // since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());
                this.settingsWizardStepForm.layout(content);
                this.settingsWizardStepForm.onPropertyChanged(this.dataChangedListener);
                this.scheduleWizardStepForm.layout(content);
                this.scheduleWizardStepForm.onPropertyChanged(this.dataChangedListener);
                this.refreshScheduleWizardStep();

                if (this.isSecurityWizardStepFormAllowed) {
                    this.securityWizardStepForm.layout(content);
                }

                schemas.forEach((schema: Mixin, index: number) => {
                    let extraData = content.getExtraData(schema.getMixinName());
                    if (!extraData) {
                        extraData = this.enrichWithExtraData(content, schema.getMixinName());
                    }
                    let metadataFormView = this.metadataStepFormByName[schema.getMixinName().toString()];
                    let metadataForm = new api.form.FormBuilder().addFormItems(schema.getFormItems()).build();

                    let data = extraData.getData();
                    data.onChanged(this.dataChangedListener);

                    formViewLayoutPromises.push(metadataFormView.layout(formContext, data, metadataForm));

                    this.synchPersistedItemWithMixinData(schema.getMixinName(), data);
                });

                return wemQ.all(formViewLayoutPromises).spread<void>(() => {

                    this.contentWizardStepForm.getFormView().addClass('panel-may-display-validation-errors');
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
                        this.unbindSiteModelListeners();
                        this.initSiteModelListeners();
                    }
                    return wemQ(null);
                });
            });
        });
    }

    // synch persisted content extra data with data from mixins
    // when rendering form - we may add extra fields from mixins;
    // as this is intended action from XP, not user - it should be present in persisted content
    private synchPersistedItemWithMixinData(mixinName: MixinName, mixinData: PropertyTree) {
        let persistedContent = this.getPersistedItem();
        let extraData = persistedContent.getExtraData(mixinName);
        if (!extraData) { // ensure ExtraData object corresponds to each step form
            this.enrichWithExtraData(persistedContent, mixinName, mixinData.copy());
        } else {
            let diff = extraData.getData().diff(mixinData);
            diff.added.forEach((property: api.data.Property) => {
                extraData.getData().addProperty(property.getName(), property.getValue());
            });
        }
    }

    private enrichWithExtraData(content: Content, mixinName: MixinName, propertyTree?: PropertyTree): ExtraData {
        let extraData = new ExtraData(mixinName, propertyTree ? propertyTree.copy() : new PropertyTree());
        content.getAllExtraData().push(extraData);
        return extraData;
    }

    private setupWizardLiveEdit() {

        let editorEnabled = this.isEditorEnabled();
        let shouldOpenEditor = this.shouldOpenEditorByDefault();

        this.toggleClass('rendered', editorEnabled);

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
        this.siteModel.onApplicationStarted(this.applicationStartedListener);
    }

    private unbindSiteModelListeners() {
        this.siteModel.unApplicationAdded(this.applicationAddedListener);
        this.siteModel.unApplicationRemoved(this.applicationRemovedListener);
        this.siteModel.unApplicationUnavailable(this.applicationUnavailableListener);
        this.siteModel.unApplicationStarted(this.applicationStartedListener);
    }

    private removeMetadataStepForms() {
        this.missingOrStoppedAppKeys = [];
        let applicationKeys = this.siteModel.getApplicationKeys();
        let applicationPromises = applicationKeys.map(
            (key: ApplicationKey) => this.fetchApplication(key));

        return wemQ.all(applicationPromises).then((applications: Application[]) => {
            let metadataMixinPromises: wemQ.Promise<Mixin>[] = [];

            applications.forEach((app: Application) => {
                if (app && app.getState() !== Application.STATE_STOPPED) {
                    metadataMixinPromises = metadataMixinPromises.concat(
                        app.getMetaSteps().map((name: MixinName) => {
                            return new GetMixinByQualifiedNameRequest(name).sendAndParse();
                        })
                    );
                }
            });

            this.handleMissingApp();

            return wemQ.all(metadataMixinPromises);
        }).then((mixins: Mixin[]) => {
            const activeMixinsNames = api.schema.mixin.MixinNames.create().fromMixins(mixins).build();

            const panelNamesToRemoveBuilder = MixinNames.create();

            const meta = this.metadataStepFormByName;
            for (const name in meta) { // check all old mixin panels
                if (meta.hasOwnProperty(name)) {
                    const mixinName = new MixinName(name);
                    if (!activeMixinsNames.contains(mixinName)) {
                        panelNamesToRemoveBuilder.addMixinName(mixinName);
                    }
                }
            }
            const panelNamesToRemove = panelNamesToRemoveBuilder.build();
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

            let mixinNames = currentApplication.getMetaSteps();

            //remove already existing extraData
            let mixinNamesToAdd = mixinNames.filter((mixinName: MixinName) => {
                return !this.metadataStepFormByName[mixinName.toString()];
            });

            let getMixinPromises: wemQ.Promise<Mixin>[] = mixinNamesToAdd.map((name: MixinName) => {
                return new GetMixinByQualifiedNameRequest(name).sendAndParse();
            });
            return wemQ.all(getMixinPromises);
        }).then((mixins: Mixin[]) => {
            mixins.forEach((mixin: Mixin) => {
                if (!this.metadataStepFormByName[mixin.getMixinName().toString()]) {

                    let stepForm = new ContentWizardStepForm();
                    this.metadataStepFormByName[mixin.getMixinName().toString()] = stepForm;

                    let wizardStep = new WizardStep(mixin.getDisplayName(), stepForm);
                    this.insertStepBefore(wizardStep, this.settingsWizardStep);

                    let extraData = new ExtraData(mixin.getMixinName(), new PropertyTree());

                    stepForm.layout(this.createFormContext(this.getPersistedItem()), extraData.getData(), mixin.toForm());
                }
            });

            return mixins;
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private initLiveEditModel(content: Content, siteModel: SiteModel, formContext: ContentFormContext): wemQ.Promise<void> {
        this.unbindSiteModelListeners();
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
        let deferred = wemQ.defer<CreateContentRequest>();

        let parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

        if (this.contentType.getContentTypeName().isMedia()) {
            deferred.resolve(null);
        } else {
            deferred.resolve(
                new CreateContentRequest()
                    .setRequireValid(this.requireValid)
                    .setName(api.content.ContentUnnamed.newUnnamed())
                    .setParent(parentPath)
                    .setContentType(this.contentType.getContentTypeName())
                    .setDisplayName('')     // new content is created on wizard open so display name is always empty
                    .setData(new PropertyTree()).setExtraData([]));
        }

        return deferred.promise;
    }

    private getOptionSetsInForm(formItemContainer: api.form.FormItemContainer): api.form.FormOptionSet[] {
        let result: api.form.FormOptionSet[] = [];

        formItemContainer.getFormItems().forEach((item) => {
            if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormItemSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FieldSet) ||
                api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSetOption)) {
                result = result.concat(this.getOptionSetsInForm(<any>item));
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(item, api.form.FormOptionSet)) {
                result.push(<api.form.FormOptionSet> item);
                result = result.concat(this.getOptionSetsInForm(<any>item));
            }
        });

        return result;
    }

    updatePersistedItem(): wemQ.Promise<Content> {
        let persistedContent = this.getPersistedItem();

        let viewedContent = this.assembleViewedContent(persistedContent.newBuilder(), true).build();

        let updatePersistedContentRoutine = new UpdatePersistedContentRoutine(this, persistedContent, viewedContent)
            .setUpdateContentRequestProducer(this.produceUpdateContentRequest);

        return updatePersistedContentRoutine.execute().then((content: Content) => {

            if (persistedContent.getName().isUnnamed() && !content.getName().isUnnamed()) {
                this.notifyContentNamed(content);
            }
            let contentToDisplay = (content.getDisplayName() && content.getDisplayName().length > 0) ?
                                   `"${content.getDisplayName()}"` : 'Content';
            api.notify.showFeedback(contentToDisplay + ' saved');
            this.getWizardHeader().resetBaseValues();

            return content;
        });
    }

    private produceUpdateContentRequest(content: Content, viewedContent: Content): UpdateContentRequest {
        const persistedContent = this.getPersistedItem();

        return new UpdateContentRequest(persistedContent.getId())
            .setRequireValid(this.requireValid)
            .setContentName(viewedContent.getName())
            .setDisplayName(viewedContent.getDisplayName())
            .setData(viewedContent.getContentData())
            .setExtraData(viewedContent.getAllExtraData())
            .setOwner(viewedContent.getOwner())
            .setLanguage(viewedContent.getLanguage())
            .setPublishFrom(viewedContent.getPublishFromTime())
            .setPublishTo(viewedContent.getPublishToTime())
            .setPermissions(viewedContent.getPermissions())
            .setInheritPermissions(viewedContent.isInheritPermissionsEnabled())
            .setOverwritePermissions(viewedContent.isOverwritePermissionsEnabled());
    }

    private isDisplayNameUpdated(): boolean {
        return this.getPersistedItem().getDisplayName() !== this.getWizardHeader().getDisplayName();
    }

    hasUnsavedChanges(): boolean {
        if (!this.isRendered()) {
            return false;
        }
        let persistedContent: Content = this.getPersistedItem();
        if (persistedContent == null) {
            return true;
        } else {
            let viewedContent = this.assembleViewedContent(persistedContent.newBuilder(), true).build();

            // ignore empty values for auto-created content that hasn't been updated yet because it doesn't have data at all
            let ignoreEmptyValues = !persistedContent.getModifiedTime() || !persistedContent.getCreatedTime() ||
                                    persistedContent.getCreatedTime().getTime() === persistedContent.getModifiedTime().getTime();

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

    private getExtraDataFromMetadata(): ExtraData[] {
        const extraData: ExtraData[] = [];
        for (let key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                extraData.push(new ExtraData(new MixinName(key), this.metadataStepFormByName[key].getData()));
            }
        }

        return extraData;
    }

    private assembleViewedContent(viewedContentBuilder: ContentBuilder, cleanFormRedundantData: boolean = false): ContentBuilder {

        viewedContentBuilder.setName(this.resolveContentNameForUpdateRequest());
        viewedContentBuilder.setDisplayName(this.getWizardHeader().getDisplayName());
        if (this.contentWizardStepForm) {
            if (!cleanFormRedundantData) {
                viewedContentBuilder.setData(this.contentWizardStepForm.getData());
            } else {
                let data: api.data.PropertyTree = new api.data.PropertyTree(this.contentWizardStepForm.getData().getRoot()); // copy
                viewedContentBuilder.setData(this.cleanFormRedundantData(data));
            }
        }

        viewedContentBuilder.setExtraData(this.getExtraDataFromMetadata());

        this.settingsWizardStepForm.apply(viewedContentBuilder);
        this.scheduleWizardStepForm.apply(viewedContentBuilder);

        viewedContentBuilder.setPage(this.assembleViewedPage());

        this.securityWizardStepForm.apply(viewedContentBuilder);

        return viewedContentBuilder;
    }

    private cleanFormRedundantData(data: api.data.PropertyTree): api.data.PropertyTree {
        let optionSets = this.getOptionSetsInForm(this.getContentType().getForm());

        optionSets.forEach((optionSet) => {
            let property = data.getProperty(optionSet.getPath().toString());
            if (!!property) {
                let optionSetProperty = property.getPropertySet();
                let selectionArray = optionSetProperty.getPropertyArray('_selected');
                if (!selectionArray) {
                    return;
                }
                optionSet.getOptions().forEach((option: api.form.FormOptionSetOption) => {
                    let isSelected = false;
                    selectionArray.forEach((selectedOptionName: api.data.Property) => {
                        if (selectedOptionName.getString() === option.getName()) {
                            isSelected = true;
                        }
                    });
                    if (!isSelected) {
                        optionSetProperty.removeProperty(option.getName(), 0);
                    }
                });
            }
        });

        return data;
    }

    private assembleViewedPage(): Page {
        let liveFormPanel = this.getLivePanel();
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

        this.getSplitPanel().addClass('toggle-live').removeClass('toggle-form toggle-split');
        this.getMainToolbar().toggleClass('live', true);
        this.toggleClass('form', false);

        this.openLiveEdit();
    }

    showSplitEdit() {
        this.getSplitPanel().addClass('toggle-split').removeClass('toggle-live toggle-form');
        this.getMainToolbar().toggleClass('live', true);
        this.toggleClass('form', false);

        this.openLiveEdit();
    }

    showForm() {
        this.getSplitPanel().addClass('toggle-form').removeClass('toggle-live toggle-split');
        this.getMainToolbar().toggleClass('live', false);
        this.toggleClass('form', true);

        this.closeLiveEdit();
    }

    private isSplitView(): boolean {
        return this.getSplitPanel() && this.getSplitPanel().hasClass('toggle-split');
    }

    private isLiveView(): boolean {
        return this.getSplitPanel() && this.getSplitPanel().hasClass('toggle-live');
    }

    private displayValidationErrors() {
        if (!this.isContentFormValid) {
            this.contentWizardStepForm.displayValidationErrors(true);
        }

        for (let key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                let form = this.metadataStepFormByName[key];
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

        let allMetadataFormsValid = true;
        let allMetadataFormsHaveValidUserInput = true;
        for (let key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {
                let form = this.metadataStepFormByName[key];
                if (!form.isValid()) {
                    allMetadataFormsValid = false;
                }
                let formHasValidUserInput = form.getFormView().hasValidUserInput();
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

    onContentNamed(listener: (event: ContentNamedEvent) => void) {
        this.contentNamedListeners.push(listener);
    }

    unContentNamed(listener: (event: ContentNamedEvent) => void) {
        this.contentNamedListeners = this.contentNamedListeners.filter((curr) => {
            return curr !== listener;
        });
        return this;
    }

    getContent(): ContentSummaryAndCompareStatus {
        return this.currentContent;
    }

    getCompareStatus(): CompareStatus {
        return this.currentContent ? this.currentContent.getCompareStatus() : null;
    }

    getPublishStatus(): PublishStatus {
        return this.currentContent ? this.currentContent.getPublishStatus() : null;
    }

    private notifyContentNamed(content: api.content.Content) {
        this.contentNamedListeners.forEach((listener: (event: ContentNamedEvent) => void) => {
            listener.call(this, new ContentNamedEvent(this, content));
        });
    }

    private createFormContext(content: Content): ContentFormContext {
        let formContext: ContentFormContext = <ContentFormContext>ContentFormContext.create().setSite(this.site).setParentContent(
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
     * Synchronizes wizard's extraData step forms with passed content -
     * erases steps forms (meta)data and populates it with content's (meta)data.
     * @param content
     */
    private updateMetadataAndMetadataStepForms(content: Content, unchangedOnly: boolean = true) {
        let contentCopy = content.clone();

        for (let key in this.metadataStepFormByName) {
            if (this.metadataStepFormByName.hasOwnProperty(key)) {

                let mixinName = new MixinName(key);
                let extraData = contentCopy.getExtraData(mixinName);
                if (!extraData) { // ensure ExtraData object corresponds to each step form
                    extraData = this.enrichWithExtraData(contentCopy, mixinName);
                }

                let form = this.metadataStepFormByName[key];
                form.getData().unChanged(this.dataChangedListener);

                let data = extraData.getData();
                data.onChanged(this.dataChangedListener);

                form.update(data, unchangedOnly);

                this.synchPersistedItemWithMixinData(mixinName, data);
            }
        }
    }

    private updateWizardStepForms(content: Content, unchangedOnly: boolean = true) {

        this.contentWizardStepForm.getData().unChanged(this.dataChangedListener);

        // remember to copy data to have persistedItem pristine
        let contentCopy = content.clone();
        contentCopy.getContentData().onChanged(this.dataChangedListener);

        this.contentWizardStepForm.update(contentCopy.getContentData(), unchangedOnly).then(() => {
            setTimeout(this.contentWizardStepForm.validate.bind(this.contentWizardStepForm), 100);
        });

        if (contentCopy.isSite()) {
            this.siteModel.update(<Site>contentCopy);
        }

        this.settingsWizardStepForm.update(contentCopy, unchangedOnly);
        this.scheduleWizardStepForm.update(contentCopy, unchangedOnly);

        if (this.isSecurityWizardStepFormAllowed) {
            this.securityWizardStepForm.update(contentCopy, unchangedOnly);
        }
    }

    private updateWizardHeader(content: Content) {

        this.updateThumbnailWithContent(content);

        this.getWizardHeader().initNames(content.getDisplayName(), content.getName().toString(), true, false);

        // case when content was moved
        this.getWizardHeader()
            .setPath(content.getPath().getParentPath().isRoot() ? '/' : content.getPath().getParentPath().toString() + '/');
    }

    private openLiveEdit() {
        let livePanel = this.getLivePanel();

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

    public isContentDeleted(): boolean {
        return this.contentDeleted;
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
        this.wizardActions.refreshPendingDeleteDecorations();
        this.getContextWindowToggler().setEnabled(isRenderable);
        this.getComponentsViewToggler().setEnabled(isRenderable);

        this.getComponentsViewToggler().setVisible(isRenderable);
        this.getContextWindowToggler().setVisible(isRenderable);
    }

    private updatePublishStatusOnDataChange() {
        let publishControls = this.getContentWizardToolbarPublishControls();

        if (this.isContentFormValid) {
            if (!this.hasUnsavedChanges()) {
                // WARN: intended to restore status to persisted value if data is changed to original values,
                // but if invoked after save this will revert status to persisted one as well
                this.currentContent = this.persistedContent;

            } else {
                if (publishControls.isOnline()) {
                    this.currentContent.setCompareStatus(CompareStatus.NEWER);
                }
                this.currentContent.setPublishStatus(this.scheduleWizardStepForm.getPublishStatus());
            }
            publishControls.setContent(this.currentContent);
        }
    }

    private refreshScheduleWizardStep() {
        let show = !this.currentContent.isNew();

        this.scheduleWizardStep.show(show);
        if (show) {
            this.getWizardStepsPanel().getHeader(this.scheduleWizardStepIndex).show();
        } else {
            this.getWizardStepsPanel().getHeader(this.scheduleWizardStepIndex).hide();
        }
    }

    getLiveMask(): api.ui.mask.LoadMask {
        return this.liveMask;
    }

}
