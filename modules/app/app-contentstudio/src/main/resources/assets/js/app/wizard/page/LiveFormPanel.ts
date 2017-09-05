import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {DefaultModels} from './DefaultModels';
import {EmulatorPanel} from './contextwindow/EmulatorPanel';
import {LiveEditPageProxy} from './LiveEditPageProxy';
import {TextInspectionPanel} from './contextwindow/inspect/region/TextInspectionPanel';
import {ContentInspectionPanel} from './contextwindow/inspect/ContentInspectionPanel';
import {RegionInspectionPanel} from './contextwindow/inspect/region/RegionInspectionPanel';
import {ImageInspectionPanel} from './contextwindow/inspect/region/ImageInspectionPanel';
import {LayoutInspectionPanel} from './contextwindow/inspect/region/LayoutInspectionPanel';
import {FragmentInspectionPanel} from './contextwindow/inspect/region/FragmentInspectionPanel';
import {PartInspectionPanel} from './contextwindow/inspect/region/PartInspectionPanel';
import {PageInspectionPanel} from './contextwindow/inspect/page/PageInspectionPanel';
import {InspectionsPanel, InspectionsPanelConfig} from './contextwindow/inspect/InspectionsPanel';
import {InsertablesPanel} from './contextwindow/insert/InsertablesPanel';
import {ContextWindowController} from './contextwindow/ContextWindowController';
import {ContextWindow, ContextWindowConfig} from './contextwindow/ContextWindow';
import {ShowContentFormEvent} from '../ShowContentFormEvent';
import {SaveAsTemplateAction} from '../action/SaveAsTemplateAction';
import {ShowLiveEditEvent} from '../ShowLiveEditEvent';
import {ShowSplitEditEvent} from '../ShowSplitEditEvent';

import PageTemplate = api.content.page.PageTemplate;
import PageTemplateKey = api.content.page.PageTemplateKey;
import DescriptorKey = api.content.page.DescriptorKey;
import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentTypeName = api.schema.content.ContentTypeName;
import Page = api.content.page.Page;
import PageMode = api.content.page.PageMode;
import PageModel = api.content.page.PageModel;
import SiteModel = api.content.site.SiteModel;
import LiveEditModel = api.liveedit.LiveEditModel;

import PageDescriptor = api.content.page.PageDescriptor;
import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;

import Component = api.content.page.region.Component;
import ImageComponent = api.content.page.region.ImageComponent;
import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
import PartComponent = api.content.page.region.PartComponent;
import LayoutComponent = api.content.page.region.LayoutComponent;
import FragmentComponent = api.content.page.region.FragmentComponent;
import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
import ComponentPropertyValueChangedEvent = api.content.page.region.ComponentPropertyValueChangedEvent;

import GetPartDescriptorsByApplicationsRequest = api.content.page.region.GetPartDescriptorsByApplicationsRequest;
import GetLayoutDescriptorsByApplicationsRequest = api.content.page.region.GetLayoutDescriptorsByApplicationsRequest;
import RenderingMode = api.rendering.RenderingMode;

import ItemView = api.liveedit.ItemView;
import RegionView = api.liveedit.RegionView;
import ComponentView = api.liveedit.ComponentView;
import PageView = api.liveedit.PageView;
import ImageComponentView = api.liveedit.image.ImageComponentView;
import PartComponentView = api.liveedit.part.PartComponentView;
import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
import TextComponentView = api.liveedit.text.TextComponentView;
import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
import ComponentViewDragStartedEvent = api.liveedit.ComponentViewDragStartedEvent;
import ComponentViewDragDroppedEvent = api.liveedit.ComponentViewDragDroppedEvent;
import ComponentViewDragCanceledEvent = api.liveedit.ComponentViewDragCanceledEvent;
import PageSelectedEvent = api.liveedit.PageSelectedEvent;
import RegionSelectedEvent = api.liveedit.RegionSelectedEvent;
import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
import ItemViewDeselectedEvent = api.liveedit.ItemViewDeselectedEvent;
import ComponentInspectedEvent = api.liveedit.ComponentInspectedEvent;
import PageInspectedEvent = api.liveedit.PageInspectedEvent;
import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
import ComponentRemovedEvent = api.liveedit.ComponentRemovedEvent;
import ComponentDuplicatedEvent = api.liveedit.ComponentDuplicatedEvent;
import LiveEditPageInitializationErrorEvent = api.liveedit.LiveEditPageInitializationErrorEvent;
import ComponentFragmentCreatedEvent = api.liveedit.ComponentFragmentCreatedEvent;
import ShowWarningLiveEditEvent = api.liveedit.ShowWarningLiveEditEvent;

import HtmlAreaDialogShownEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;
import HTMLAreaDialogHandler = api.util.htmlarea.dialog.HTMLAreaDialogHandler;

import Panel = api.ui.panel.Panel;
import LiveEditPageViewReadyEvent = api.liveedit.LiveEditPageViewReadyEvent;

import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import FragmentComponentReloadRequiredEvent = api.liveedit.FragmentComponentReloadRequiredEvent;
import BeforeContentSavedEvent = api.content.event.BeforeContentSavedEvent;
import ComponentPath = api.content.page.region.ComponentPath;
import i18n = api.util.i18n;
import CreatePageTemplateRequest = api.content.page.CreatePageTemplateRequest;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import EditContentEvent = api.content.event.EditContentEvent;
import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;

export interface LiveFormPanelConfig {

    contentType: ContentTypeName;

    contentWizardPanel: ContentWizardPanel;

    defaultModels: DefaultModels;
}

export class LiveFormPanel
    extends api.ui.panel.Panel {

    public static debug: boolean = false;

    private defaultModels: DefaultModels;

    private content: Content;

    private liveEditModel: LiveEditModel;

    private pageView: PageView;

    private pageModel: PageModel;

    private pageLoading: boolean;

    private pageSkipReload: boolean;
    private frameContainer: Panel;

    private lockPageAfterProxyLoad: boolean;

    private contextWindow: ContextWindow;
    private contextWindowController: ContextWindowController;

    private emulatorPanel: EmulatorPanel;
    private insertablesPanel: InsertablesPanel;
    private inspectionsPanel: InspectionsPanel;
    private contentInspectionPanel: ContentInspectionPanel;
    private pageInspectionPanel: PageInspectionPanel;
    private regionInspectionPanel: RegionInspectionPanel;
    private imageInspectionPanel: ImageInspectionPanel;
    private partInspectionPanel: PartInspectionPanel;
    private layoutInspectionPanel: LayoutInspectionPanel;
    private fragmentInspectionPanel: FragmentInspectionPanel;
    private textInspectionPanel: TextInspectionPanel;

    private contentWizardPanel: ContentWizardPanel;

    private liveEditPageProxy: LiveEditPageProxy;

    private contentEventListener: (event: any) => void;

    private saveAsTemplateAction: SaveAsTemplateAction;

    private showLoadMaskHandler: () => void;
    private hideLoadMaskHandler: () => void;
    
    constructor(config: LiveFormPanelConfig) {
        super('live-form-panel');
        this.contentWizardPanel = config.contentWizardPanel;
        this.defaultModels = config.defaultModels;

        this.pageLoading = false;
        this.pageSkipReload = false;
        this.lockPageAfterProxyLoad = false;

        this.saveAsTemplateAction = new SaveAsTemplateAction();

        const contentUpdatedHandler = summaryAndStatuses => {
            // Update action with new content on save if it gets updated
            summaryAndStatuses.some(summaryAndStatus => {
                if (this.content.getContentId().equals(summaryAndStatus.getContentId())) {
                    this.saveAsTemplateAction.setContentSummary(summaryAndStatuses[0].getContentSummary());
                    return true;
                }
            });
        };
        ContentServerEventsHandler.getInstance().onContentUpdated(contentUpdatedHandler);

        this.liveEditPageProxy = this.createLiveEditPageProxy();

        this.contextWindow = this.createContextWindow(this.liveEditPageProxy, this.liveEditModel);

        // constructor to listen to live edit events during wizard rendering
        this.contextWindowController = new ContextWindowController(
            this.contextWindow,
            this.contentWizardPanel
        );
        
        this.showLoadMaskHandler = () => {
            // in case someone tries to open live edit while it's still not loaded
            if (this.pageLoading && this.liveEditModel.isPageRenderable()) {
                this.contentWizardPanel.getLiveMask().show();
            }
        };

        this.hideLoadMaskHandler = () => {
            const liveEditMask = this.contentWizardPanel.getLiveMask();
            // in case someone tries to open live edit while it's still not loaded
            if (!!liveEditMask && liveEditMask.isVisible()) {
                liveEditMask.hide();
            }
        };
        
        ShowLiveEditEvent.on(this.showLoadMaskHandler);
        ShowSplitEditEvent.on(this.showLoadMaskHandler);
        ShowContentFormEvent.on(this.hideLoadMaskHandler);
    }

    private createLiveEditPageProxy(): LiveEditPageProxy {
        let liveEditPageProxy = new LiveEditPageProxy();
        liveEditPageProxy.onLoaded(() => {
            this.hideLoadMaskHandler();
            this.pageLoading = false;

            if (this.lockPageAfterProxyLoad) {
                this.pageView.setLocked(true);
                this.lockPageAfterProxyLoad = false;
            }

            this.imageInspectionPanel.refresh();
        });

        return liveEditPageProxy;
    }

    private createContextWindow(proxy: LiveEditPageProxy, model: LiveEditModel): ContextWindow {
        this.emulatorPanel = new EmulatorPanel({
            liveEditPage: proxy
        });

        this.inspectionsPanel = this.createInspectionsPanel(model, this.saveAsTemplateAction);

        this.insertablesPanel = new InsertablesPanel({
            liveEditPage: proxy,
            contentWizardPanel: this.contentWizardPanel,
            saveAsTemplateAction: this.saveAsTemplateAction
        });

        return new ContextWindow(<ContextWindowConfig>{
            liveEditPage: proxy,
            liveFormPanel: this,
            inspectionPanel: this.inspectionsPanel,
            emulatorPanel: this.emulatorPanel,
            insertablesPanel: this.insertablesPanel
        });
    }

    private createInspectionsPanel(model: LiveEditModel, saveAsTemplateAction: SaveAsTemplateAction): InspectionsPanel {
        let saveAction = new api.ui.Action(i18n('action.apply'));
        saveAction.onExecuted(() => {
            if (!this.pageView) {
                this.contentWizardPanel.saveChanges();
                return;
            }

            let itemView = this.pageView.getSelectedView();
            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                this.saveAndReloadOnlyComponent(<ComponentView<Component>> itemView);
            } else if (this.pageView.isLocked() || api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageView)) {
                this.contentWizardPanel.saveChanges();
            }
        });

        this.contentInspectionPanel = new ContentInspectionPanel();

        this.pageInspectionPanel = new PageInspectionPanel(saveAsTemplateAction);
        this.partInspectionPanel = new PartInspectionPanel();
        this.layoutInspectionPanel = new LayoutInspectionPanel();
        this.imageInspectionPanel = new ImageInspectionPanel();
        this.fragmentInspectionPanel = new FragmentInspectionPanel();

        this.textInspectionPanel = new TextInspectionPanel();
        this.regionInspectionPanel = new RegionInspectionPanel();

        return new InspectionsPanel(<InspectionsPanelConfig>{
            contentInspectionPanel: this.contentInspectionPanel,
            pageInspectionPanel: this.pageInspectionPanel,
            regionInspectionPanel: this.regionInspectionPanel,
            imageInspectionPanel: this.imageInspectionPanel,
            partInspectionPanel: this.partInspectionPanel,
            layoutInspectionPanel: this.layoutInspectionPanel,
            fragmentInspectionPanel: this.fragmentInspectionPanel,
            textInspectionPanel: this.textInspectionPanel,
            saveAction: saveAction
        });
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {

            api.dom.WindowDOM.get().onBeforeUnload((event) => {
                console.log('onbeforeunload ' + this.liveEditModel.getContent().getDisplayName());
                // the reload is triggered by the main frame,
                // so let the live edit know it to skip the popup
                this.liveEditPageProxy.skipNextReloadConfirmation(true);
            });

            this.liveEditPageProxy.getPlaceholderIFrame().onShown(() => {
                // If we are about to show blank placeholder in the editor then remove
                // 'rendering' class from the panel so that it's instantly visible
                this.removeClass('rendering');
            });

            this.frameContainer = new Panel('frame-container');
            this.frameContainer.appendChildren<api.dom.Element>(this.liveEditPageProxy.getIFrame(),
                this.liveEditPageProxy.getPlaceholderIFrame(), this.liveEditPageProxy.getDragMask());

            let noPreviewMessageEl = new api.dom.PEl('no-preview-message').setHtml(i18n('field.preview.failed'), false);

            // append mask here in order for the context window to be above
            this.appendChildren<api.dom.Element>(this.frameContainer, this.contextWindow,
                noPreviewMessageEl);

            this.contextWindow.onDisplayModeChanged(() => {
                const enabled = this.contentWizardPanel.getComponentsViewToggler().isEnabled();
                if (!this.contextWindow.isFloating() && enabled) {
                    this.contentWizardPanel.getContextWindowToggler().setActive(true);
                    this.contextWindow.slideIn();
                }
            });

            this.liveEditListen();

            // delay rendered event until live edit page is fully loaded
            let liveEditDeferred = wemQ.defer<boolean>();

            this.liveEditPageProxy.onLiveEditPageViewReady((event: LiveEditPageViewReadyEvent) => {
                liveEditDeferred.resolve(rendered);
            });

            this.liveEditPageProxy.onLiveEditPageInitializationError((event: LiveEditPageInitializationErrorEvent) => {
                liveEditDeferred.reject(event.getMessage());
            });

            return liveEditDeferred.promise;
        });
    }

    remove(): LiveFormPanel {
        ShowLiveEditEvent.un(this.showLoadMaskHandler);
        ShowSplitEditEvent.un(this.showLoadMaskHandler);
        ShowContentFormEvent.un(this.hideLoadMaskHandler);
        
        this.liveEditPageProxy.remove();
        super.remove();
        return this;
    }

    public getPage(): Page {
        return this.pageModel ? this.pageModel.getPage() : null;
    }

    setModel(liveEditModel: LiveEditModel) {

        this.liveEditModel = liveEditModel;

        this.content = liveEditModel.getContent();
        this.insertablesPanel.setContent(this.content);

        this.pageModel = liveEditModel.getPageModel();
        this.pageModel.setIgnorePropertyChanges(true);

        this.saveAsTemplateAction.setContentSummary(this.content).setPageModel(this.pageModel);

        this.liveEditPageProxy.setModel(liveEditModel);
        this.pageInspectionPanel.setModel(liveEditModel);
        this.partInspectionPanel.setModel(liveEditModel);
        this.layoutInspectionPanel.setModel(liveEditModel);
        this.imageInspectionPanel.setModel(liveEditModel);
        this.fragmentInspectionPanel.setModel(liveEditModel);

        this.pageModel.setIgnorePropertyChanges(false);

        this.pageModel.onPropertyChanged((event: api.PropertyChangedEvent) => {

            // NB: To make the event.getSource() check work,
            // all calls from this to PageModel that changes a property must done with this as eventSource argument
            if (!api.ObjectHelper.objectEquals(this, event.getSource())) {

                const oldValue = event.getOldValue();
                const newValue = event.getNewValue();

                if (event.getPropertyName() === PageModel.PROPERTY_CONTROLLER && !api.ObjectHelper.objectEquals(oldValue, newValue)) {
                    this.contentWizardPanel.saveChanges();
                }
                if (event.getPropertyName() === PageModel.PROPERTY_TEMPLATE) {

                    // do not reload page if there was no template in pageModel before and if new template is the default one -
                    // case when switching automatic template to default
                    // only reload when switching from customized with controller set back to template or automatic template
                    if (!(this.pageModel.getDefaultPageTemplate().equals(this.pageModel.getTemplate()) && !oldValue &&
                          !this.pageModel.hasController())) {
                        this.pageInspectionPanel.refreshInspectionHandler(liveEditModel);
                        this.lockPageAfterProxyLoad = true;
                        this.contentWizardPanel.saveChanges();
                    }
                }
            }
        });

        this.pageModel.onComponentPropertyChangedEvent((event: ComponentPropertyChangedEvent) => {

            if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), DescriptorBasedComponent)) {
                if (event.getPropertyName() === DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {

                    const componentView = this.pageView.getComponentViewByPath(event.getPath());
                    if (componentView) {
                        if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, PartComponentView)) {
                            const partView = <PartComponentView>componentView;
                            const partComponent: PartComponent = partView.getComponent();
                            if (partComponent.hasDescriptor()) {
                                this.saveAndReloadOnlyComponent(componentView);
                            }
                        } else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, LayoutComponentView)) {
                            const layoutView = <LayoutComponentView>componentView;
                            const layoutComponent: LayoutComponent = layoutView.getComponent();
                            if (layoutComponent.hasDescriptor()) {
                                this.saveAndReloadOnlyComponent(componentView);
                            }
                        }
                    } else {
                        console.debug('ComponentView by path not found: ' + event.getPath().toString());
                    }
                }
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), ImageComponent)) {
                if (event.getPropertyName() === ImageComponent.PROPERTY_IMAGE && !event.getComponent().isEmpty()) {
                    const componentView = this.pageView.getComponentViewByPath(event.getPath());
                    if (componentView) {
                        this.saveAndReloadOnlyComponent(componentView);
                    }
                }
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), FragmentComponent)) {
                if (event.getPropertyName() === FragmentComponent.PROPERTY_FRAGMENT && !event.getComponent().isEmpty()) {
                    const componentView = this.pageView.getComponentViewByPath(event.getPath());
                    if (componentView) {
                        this.saveAndReloadOnlyComponent(componentView);
                    }
                }
            }
        });

        this.pageModel.onReset(() => {
            this.contextWindow.slideOut();
            this.contentWizardPanel.getContextWindowToggler().removeClass('active');
        });

        this.handleContentUpdatedEvent();
    }

    private handleContentUpdatedEvent() {
        if (!this.contentEventListener) {
            this.contentEventListener = (event) => {
                this.propagateEvent(event);
            };

            ContentDeletedEvent.on(this.contentEventListener);
            ContentUpdatedEvent.on(this.contentEventListener);

            this.onRemoved(() => {
                ContentDeletedEvent.un(this.contentEventListener);
                ContentUpdatedEvent.un(this.contentEventListener);
            });
        }
    }

    skipNextReloadConfirmation(skip: boolean) {
        this.liveEditPageProxy.skipNextReloadConfirmation(skip);
    }

    propagateEvent(event: api.event.Event) {
        this.liveEditPageProxy.propagateEvent(event);
    }

    loadPage(clearInspection: boolean = true) {
        if (LiveFormPanel.debug) {
            console.debug('LiveFormPanel.loadPage at ' + new Date().toISOString());
        }
        if (this.pageSkipReload === false && !this.pageLoading) {

            if (clearInspection) {
                this.clearSelection();
            }

            this.pageLoading = true;

            this.liveEditPageProxy.load();

            if (clearInspection) {
                let clearInspectionFn = () => {
                    this.contextWindow.clearSelection();
                    this.liveEditPageProxy.unLoaded(clearInspectionFn);
                };
                this.liveEditPageProxy.onLoaded(clearInspectionFn);
            }
        }
    }

    saveAndReloadOnlyComponent(componentView: ComponentView<Component>) {

        api.util.assertNotNull(componentView, 'componentView cannot be null');

        this.pageSkipReload = true;
        let componentUrl = api.rendering.UriHelper.getComponentUri(this.content.getContentId().toString(),
            componentView.getComponentPath(),
            RenderingMode.EDIT,
            api.content.Branch.DRAFT);

        this.contentWizardPanel.saveChangesWithoutValidation().then(() => {
            this.pageSkipReload = false;
            componentView.showLoadingSpinner();
            return this.liveEditPageProxy.loadComponent(componentView, componentUrl);
        }).catch((errorMessage: any) => {

            api.DefaultErrorHandler.handle(errorMessage);

            componentView.hideLoadingSpinner();
            componentView.showRenderingError(componentUrl, errorMessage);
        }).done();
    }

    updateFrameContainerSize(contextWindowShown: boolean, contextWindowWidth?: number) {
        if (!this.frameContainer) {
            return;
        }
        if (contextWindowShown && contextWindowWidth) {
            this.frameContainer.getEl().setWidth('calc(100% - ' + (contextWindowWidth - 1) + 'px)');
        } else {
            this.frameContainer.getEl().setWidth('100%');
        }
    }

    private liveEditListen() {
        this.liveEditPageProxy.onPageLocked((event: api.liveedit.PageLockedEvent) => {
            this.inspectPage();
        });

        this.liveEditPageProxy.onPageUnlocked((event: api.liveedit.PageUnlockedEvent) => {
            //this.contextWindow.clearSelection();
            this.minimizeContentFormPanelIfNeeded();
        });

        let path;
        BeforeContentSavedEvent.on(() => {
            path = null;
            if (!this.pageView) {
                return;
            }
            const selected = this.pageView.getSelectedView();
            if (api.ObjectHelper.iFrameSafeInstanceOf(selected, ComponentView)) {
                path = (<ComponentView<any>>selected).getComponentPath();
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(selected, RegionView)) {
                path = (<RegionView>selected).getRegionPath();
            }
        });

        const restoreSelection = () => {
            if (path) {
                const selected = api.ObjectHelper.iFrameSafeInstanceOf(path, ComponentPath)
                    ? this.pageView.getComponentViewByPath(path)
                    : this.pageView.getRegionViewByPath(path);
                if (selected) {
                    selected.selectWithoutMenu();
                }
            }
        };

        this.liveEditPageProxy.onLiveEditPageViewReady((event: api.liveedit.LiveEditPageViewReadyEvent) => {
            this.pageView = event.getPageView();
            if (this.pageView) {
                this.insertablesPanel.setPageView(this.pageView);
                this.pageView.getContextMenuActions().push(this.saveAsTemplateAction);
                restoreSelection();
            }
        });

        this.liveEditPageProxy.onPageSelected((event: PageSelectedEvent) => {
            this.inspectPage();
        });

        this.liveEditPageProxy.onRegionSelected((event: RegionSelectedEvent) => {
            this.inspectRegion(event.getRegionView());
        });

        this.liveEditPageProxy.onItemViewSelected((event: ItemViewSelectedEvent) => {
            let itemView = event.getItemView();
            let toggler = this.contentWizardPanel.getContextWindowToggler();

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                if (!this.contextWindow.isFixed()) {
                    if (itemView.isEmpty()) {
                        if (this.contextWindow.isFloating() && this.contextWindow.isShownOrAboutToBeShown()) {
                            toggler.setActive(false);
                        }
                    } else if (event.isNew() && !toggler.isActive()) {
                        toggler.setActive(true);
                    }
                } else {
                    this.contextWindow.setFixed(false);
                }
                this.inspectComponent(<ComponentView<Component>>itemView);
            }

            if (!this.pageView.isLocked() && !event.isRightClicked()) {
                this.minimizeContentFormPanelIfNeeded();
            }
        });

        this.liveEditPageProxy.onItemViewDeselected((event: ItemViewDeselectedEvent) => {
            let toggler = this.contentWizardPanel.getContextWindowToggler();
            if (!toggler.isActive() && this.contextWindow.isShownOrAboutToBeShown()) {
                this.contextWindow.slideOut();
            } else if (toggler.isActive() && !this.contextWindow.isShownOrAboutToBeShown()) {
                this.contextWindow.slideIn();
            }
            this.clearSelection();
        });

        this.liveEditPageProxy.onComponentAdded((event: ComponentAddedEvent) => {
            // do something when component is added
            // onItemViewSelected() is not called on adding TextComponentView
            // thus calling minimizeContentFormPanelIfNeeded() for it from here
            if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponentView(), TextComponentView)) {
                this.minimizeContentFormPanelIfNeeded();
            }
        });

        this.liveEditPageProxy.onComponentRemoved((event: ComponentRemovedEvent) => {

            if (!this.pageModel.isPageTemplate() && this.pageModel.getMode() === PageMode.AUTOMATIC) {
                this.pageModel.initializePageFromDefault(this);
            }

            this.clearSelection();
        });

        this.liveEditPageProxy.onComponentViewDragDropped((event: ComponentViewDragDroppedEvent) => {

            let componentView = event.getComponentView();
            if (!componentView.isEmpty()) {
                this.inspectComponent(componentView);
            }
        });

        this.liveEditPageProxy.onComponentDuplicated((event: ComponentDuplicatedEvent) => {

            this.saveAndReloadOnlyComponent(event.getDuplicatedComponentView());
        });

        this.liveEditPageProxy.onComponentInspected((event: ComponentInspectedEvent) => {
            let componentView = event.getComponentView();
            this.contentWizardPanel.getContextWindowToggler().setActive(true);
            this.contextWindow.slideIn();
            this.inspectComponent(componentView);
        });

        this.liveEditPageProxy.onPageInspected((event: PageInspectedEvent) => {
            this.contentWizardPanel.getContextWindowToggler().setActive(true);
            this.contextWindow.slideIn();
            this.inspectPage();
        });

        this.liveEditPageProxy.onComponentFragmentCreated((event: ComponentFragmentCreatedEvent) => {
            let fragmentView: FragmentComponentView = event.getComponentView();
            let componentType = event.getSourceComponentType().getShortName();
            let componentName = fragmentView.getComponent().getName().toString();
            api.notify.showSuccess(i18n('notify.fragment.created', componentName, componentType));

            this.saveAndReloadOnlyComponent(event.getComponentView());

            let summaryAndStatus = api.content.ContentSummaryAndCompareStatus.fromContentSummary(event.getFragmentContent());
            new api.content.event.EditContentEvent([summaryAndStatus]).fire();
        });

        this.liveEditPageProxy.onFragmentReloadRequired((event: FragmentComponentReloadRequiredEvent) => {
            let fragmentView = event.getFragmentComponentView();

            let componentUrl = api.rendering.UriHelper.getComponentUri(this.content.getContentId().toString(),
                fragmentView.getComponentPath(),
                RenderingMode.EDIT,
                api.content.Branch.DRAFT);

            fragmentView.showLoadingSpinner();
            this.liveEditPageProxy.loadComponent(fragmentView, componentUrl).then(() => {
                // fragmentView.hideLoadingSpinner();
            }).catch((errorMessage: any) => {
                api.DefaultErrorHandler.handle(errorMessage);

                fragmentView.hideLoadingSpinner();
                fragmentView.showRenderingError(componentUrl, errorMessage);
            });
        });

        this.liveEditPageProxy.onShowWarning((event: ShowWarningLiveEditEvent) => {
            api.notify.showWarning(event.getMessage());
        });

        this.liveEditPageProxy.onEditContent((event: api.content.event.EditContentEvent) => {
            new api.content.event.EditContentEvent(event.getModels()).fire();
        });

        this.liveEditPageProxy.onLiveEditPageInitializationError((event: LiveEditPageInitializationErrorEvent) => {
            api.notify.showError(event.getMessage(), false);
            new ShowContentFormEvent().fire();
            this.contentWizardPanel.showForm();
        });

        this.liveEditPageProxy.onPageUnloaded((event: api.liveedit.PageUnloadedEvent) => {
            this.contentWizardPanel.close();
        });

        this.liveEditPageProxy.onLiveEditPageDialogCreate((event: HtmlAreaDialogShownEvent) => {
            let modalDialog = HTMLAreaDialogHandler.createAndOpenDialog(event);
            this.liveEditPageProxy.notifyLiveEditPageDialogCreated(modalDialog, event.getConfig());
        });

        this.liveEditPageProxy.onPageTextModeStarted(() => {
            // Collapse the panel with a delay to give HTML editor time to initialize
            setTimeout(() => {
                this.minimizeContentFormPanelIfNeeded();
            }, 200);
        });
    }

    private shade() {
        api.liveedit.Shader.get().shade(this);
    }

    private minimizeContentFormPanelIfNeeded() {
        if (this.contextWindow.isFloating() && !this.contentWizardPanel.isMinimized()) {
            this.contentWizardPanel.toggleMinimize();
        }
    }

    private inspectPage() {
        this.contextWindow.showInspectionPanel(this.pageInspectionPanel);
    }

    private clearSelection(): void {
        let pageModel = this.liveEditModel.getPageModel();
        let customizedWithController = pageModel.isCustomized() && pageModel.hasController();
        let isFragmentContent = pageModel.getMode() === PageMode.FRAGMENT;
        if (pageModel.hasDefaultPageTemplate() || customizedWithController || isFragmentContent) {
            this.contextWindow.clearSelection();
        } else {
            this.inspectPage();
        }
    }

    clearPageViewSelectionAndOpenInspectPage() {
        if (this.pageView && this.pageView.hasSelectedView()) {
            this.pageView.getSelectedView().deselect();
        }
        this.inspectPage();
    }

    private inspectRegion(regionView: RegionView) {

        let region = regionView.getRegion();

        this.regionInspectionPanel.setRegion(region);
        this.contextWindow.showInspectionPanel(this.regionInspectionPanel);
    }

    private inspectComponent(componentView: ComponentView<Component>) {
        api.util.assertNotNull(componentView, 'componentView cannot be null');

        if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, ImageComponentView)) {
            this.imageInspectionPanel.setImageComponent(<ImageComponentView>componentView);
            this.contextWindow.showInspectionPanel(this.imageInspectionPanel);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, PartComponentView)) {
            this.partInspectionPanel.setPartComponent(<PartComponentView>componentView);
            this.contextWindow.showInspectionPanel(this.partInspectionPanel);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, LayoutComponentView)) {
            this.layoutInspectionPanel.setLayoutComponent(<LayoutComponentView>componentView);
            this.contextWindow.showInspectionPanel(this.layoutInspectionPanel);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, TextComponentView)) {
            this.textInspectionPanel.setTextComponent(<TextComponentView>componentView);
            this.contextWindow.showInspectionPanel(this.textInspectionPanel);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, FragmentComponentView)) {
            this.fragmentInspectionPanel.setFragmentComponent(<FragmentComponentView>componentView);
            this.contextWindow.showInspectionPanel(this.fragmentInspectionPanel);
        } else {
            throw new Error('ComponentView cannot be selected: ' + api.ClassHelper.getClassName(componentView));
        }
    }

    isShown(): boolean {
        return !api.ObjectHelper.stringEquals(this.getHTMLElement().style.display, 'none');
    }
}
