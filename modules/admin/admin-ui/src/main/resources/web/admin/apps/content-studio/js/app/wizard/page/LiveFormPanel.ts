module app.wizard.page {

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

    import InspectionsPanelConfig = app.wizard.page.contextwindow.inspect.InspectionsPanelConfig;
    import InspectionsPanel = app.wizard.page.contextwindow.inspect.InspectionsPanel;
    import ContentInspectionPanel = app.wizard.page.contextwindow.inspect.ContentInspectionPanel;
    import PageInspectionPanel = app.wizard.page.contextwindow.inspect.page.PageInspectionPanel;
    import RegionInspectionPanel = app.wizard.page.contextwindow.inspect.region.RegionInspectionPanel;
    import ImageInspectionPanel = app.wizard.page.contextwindow.inspect.region.ImageInspectionPanel;
    import PartInspectionPanel = app.wizard.page.contextwindow.inspect.region.PartInspectionPanel;
    import LayoutInspectionPanel = app.wizard.page.contextwindow.inspect.region.LayoutInspectionPanel;
    import FragmentInspectionPanel = app.wizard.page.contextwindow.inspect.region.FragmentInspectionPanel;
    import TextInspectionPanel = app.wizard.page.contextwindow.inspect.region.TextInspectionPanel;
    import ContextWindow = app.wizard.page.contextwindow.ContextWindow;
    import ContextWindowConfig = app.wizard.page.contextwindow.ContextWindowConfig;
    import ContextWindowController = app.wizard.page.contextwindow.ContextWindowController;
    import EmulatorPanel = app.wizard.page.contextwindow.EmulatorPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;
    import RenderingMode = api.rendering.RenderingMode;

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

    export interface LiveFormPanelConfig {

        contentType:ContentTypeName;

        contentWizardPanel:ContentWizardPanel;

        defaultModels: DefaultModels;
    }

    export class LiveFormPanel extends api.ui.panel.Panel {

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

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.defaultModels = config.defaultModels;

            this.pageLoading = false;
            this.pageSkipReload = false;
            this.lockPageAfterProxyLoad = false;

            this.liveEditPageProxy = new LiveEditPageProxy();

            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel();
            this.regionInspectionPanel = new RegionInspectionPanel();
            this.imageInspectionPanel = new ImageInspectionPanel();
            this.partInspectionPanel = new PartInspectionPanel();
            this.layoutInspectionPanel = new LayoutInspectionPanel();
            this.fragmentInspectionPanel = new FragmentInspectionPanel();
            this.textInspectionPanel = new TextInspectionPanel();

            api.dom.WindowDOM.get().onBeforeUnload((event) => {
                console.log("onbeforeunload " + this.liveEditModel.getContent().getDisplayName());
                // the reload is triggered by the main frame,
                // so let the live edit know it to skip the popup
                this.liveEditPageProxy.skipNextReloadConfirmation(true);
            });

            var saveAction = new api.ui.Action('Apply');
            saveAction.onExecuted(() => {
                if (!this.pageView) {
                    this.saveAndReloadPage();
                    return;
                }

                var itemView = this.pageView.getSelectedView();
                if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                    this.saveAndReloadOnlyComponent(<ComponentView<Component>> itemView);
                } else if (this.pageView.isLocked() || api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageView)) {
                    this.saveAndReloadPage();
                }
            });

            this.inspectionsPanel = new InspectionsPanel(<InspectionsPanelConfig>{
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

            this.emulatorPanel = new EmulatorPanel({
                liveEditPage: this.liveEditPageProxy
            });

            this.insertablesPanel = new InsertablesPanel({
                liveEditPage: this.liveEditPageProxy,
                contentWizardPanel: config.contentWizardPanel,
            });

            this.frameContainer = new Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frameContainer.appendChild(this.liveEditPageProxy.getIFrame());
            this.frameContainer.appendChild(this.liveEditPageProxy.getDragMask());

            // append it here in order for the context window to be above
            this.appendChild(this.liveEditPageProxy.getLoadMask());

            this.contextWindow = new ContextWindow(<ContextWindowConfig>{
                liveEditPage: this.liveEditPageProxy,
                liveFormPanel: this,
                inspectionPanel: this.inspectionsPanel,
                emulatorPanel: this.emulatorPanel,
                insertablesPanel: this.insertablesPanel
            });

            this.appendChild(this.contextWindow);

            this.contextWindowController = new app.wizard.page.contextwindow.ContextWindowController(
                this.contextWindow,
                this.contentWizardPanel.getContextWindowToggler(),
                this.contentWizardPanel.getComponentsViewToggler()
            );

            this.contextWindow.onDisplayModeChanged(() => {
                if (!this.contextWindow.isFloating()) {
                    this.contentWizardPanel.getContextWindowToggler().setActive(true);
                    this.contextWindow.slideIn();
                }
            });

            this.liveEditListen();
        }

        remove(): LiveFormPanel {

            this.liveEditPageProxy.remove();
            super.remove();
            return this;
        }

        public getPage(): Page {
            return this.pageModel.getPage();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.liveEditModel = liveEditModel;

            this.content = liveEditModel.getContent();
            this.insertablesPanel.setContent(this.content);

            this.pageModel = liveEditModel.getPageModel();
            this.pageModel.setIgnorePropertyChanges(true);

            this.liveEditPageProxy.setModel(liveEditModel);
            this.imageInspectionPanel.setModel(liveEditModel);
            this.pageInspectionPanel.setModel(liveEditModel);
            this.partInspectionPanel.setModel(liveEditModel);
            this.layoutInspectionPanel.setModel(liveEditModel);
            this.imageInspectionPanel.setModel(liveEditModel);
            this.fragmentInspectionPanel.setModel(liveEditModel);

            this.pageModel.setIgnorePropertyChanges(false);

            this.pageModel.onPropertyChanged((event: api.PropertyChangedEvent) => {

                // NB: To make the event.getSource() check work, all calls from this to PageModel that changes a property must done with this as eventSource argument.

                if (event.getPropertyName() == PageModel.PROPERTY_CONTROLLER && this !== event.getSource()) {
                    this.saveAndReloadPage(false);
                }
                else if (event.getPropertyName() == PageModel.PROPERTY_TEMPLATE && this !== event.getSource()) {

                    // do not reload page if there was no template in pageModel before and if new template is the default one - case when switching automatic template to default
                    // only reload when switching from customized with controller set back to template or automatic template
                    if (!(this.pageModel.getDefaultPageTemplate().equals(this.pageModel.getTemplate()) && !event.getOldValue() &&
                          !this.pageModel.hasController())) {
                        this.pageInspectionPanel.refreshInspectionHandler(liveEditModel);
                        this.lockPageAfterProxyLoad = true;
                        this.saveAndReloadPage(false);
                    }
                }
            });

            this.pageModel.onComponentPropertyChangedEvent((event: ComponentPropertyChangedEvent) => {

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), DescriptorBasedComponent)) {
                    if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {

                        var componentView = this.pageView.getComponentViewByPath(event.getPath());
                        if (componentView) {
                            if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, PartComponentView)) {
                                var partView = <PartComponentView>componentView;
                                var partComponent: PartComponent = partView.getComponent();
                                if (partComponent.hasDescriptor()) {
                                    this.saveAndReloadOnlyComponent(componentView);
                                }
                            }
                            else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, LayoutComponentView)) {
                                var layoutView = <LayoutComponentView>componentView;
                                var layoutComponent: LayoutComponent = layoutView.getComponent();
                                if (layoutComponent.hasDescriptor()) {
                                    this.saveAndReloadOnlyComponent(componentView);
                                }
                            }
                        }
                        else {
                            console.debug("ComponentView by path not found: " + event.getPath().toString());
                        }
                    }
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), ImageComponent)) {
                    if (event.getPropertyName() == ImageComponent.PROPERTY_IMAGE && !event.getComponent().isEmpty()) {
                        var componentView = this.pageView.getComponentViewByPath(event.getPath());
                        if (componentView) {
                            this.saveAndReloadOnlyComponent(componentView);
                        }
                    }
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponent(), FragmentComponent)) {
                    if (event.getPropertyName() == FragmentComponent.PROPERTY_FRAGMENT && !event.getComponent().isEmpty()) {
                        var componentView = this.pageView.getComponentViewByPath(event.getPath());
                        if (componentView) {
                            this.saveAndReloadOnlyComponent(componentView);
                        }
                    }
                }
            });
        }

        skipNextReloadConfirmation(skip: boolean) {
            this.liveEditPageProxy.skipNextReloadConfirmation(skip);
        }

        loadPage() {
            if (this.pageSkipReload == false && !this.pageLoading) {

                this.clearSelection();

                this.pageLoading = true;
                this.liveEditPageProxy.load();
                this.liveEditPageProxy.onLoaded(() => {
                    this.pageLoading = false;
                    if (this.lockPageAfterProxyLoad) {
                        this.pageView.setLocked(true);
                        this.lockPageAfterProxyLoad = false;
                    }
                });
            }
        }

        saveAndReloadPage(clearInspection: boolean = false) {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    if (clearInspection) {
                        this.contextWindow.clearSelection();
                    }
                    this.liveEditPageProxy.load();
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        saveAndReloadOnlyComponent(componentView: ComponentView<Component>) {

            api.util.assertNotNull(componentView, "componentView cannot be null");

            this.pageSkipReload = true;
            var componentUrl = api.rendering.UriHelper.getComponentUri(this.content.getContentId().toString(),
                componentView.getComponentPath(),
                RenderingMode.EDIT,
                api.content.Branch.DRAFT);

            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    componentView.showLoadingSpinner();
                    return this.liveEditPageProxy.loadComponent(componentView, componentUrl);
                }).
                catch((errorMessage: any) => {

                    api.DefaultErrorHandler.handle(errorMessage);

                    componentView.hideLoadingSpinner();
                    componentView.showRenderingError(componentUrl, errorMessage);
                }).done();
        }

        updateFrameContainerSize(contextWindowShown: boolean, contextWindowWidth?: number) {
            if (contextWindowShown && contextWindowWidth) {
                this.frameContainer.getEl().setWidth("calc(100% - " + (contextWindowWidth - 1) + "px)");
            } else {
                this.frameContainer.getEl().setWidth("100%");
            }
        }


        private liveEditListen() {

            this.liveEditPageProxy.onPageLocked((event: api.liveedit.PageLockedEvent) => {
                this.inspectPage();
            });

            this.liveEditPageProxy.onPageUnlocked((event: api.liveedit.PageUnlockedEvent) => {
                this.contextWindow.clearSelection();
            });

            this.liveEditPageProxy.onLiveEditPageViewReady((event: api.liveedit.LiveEditPageViewReadyEvent) => {
                this.pageView = event.getPageView();
                if (this.pageView) {
                    this.insertablesPanel.setPageView(this.pageView);
                }
                else {
                    this.contentWizardPanel.getContextWindowToggler().hide();
                    this.contentWizardPanel.getComponentsViewToggler().hide();
                }
            });

            this.liveEditPageProxy.onPageSelected((event: PageSelectedEvent) => {
                this.inspectPage();
            });

            this.liveEditPageProxy.onRegionSelected((event: RegionSelectedEvent) => {
                this.inspectRegion(event.getRegionView());
            });

            this.liveEditPageProxy.onItemViewSelected((event: ItemViewSelectedEvent) => {
                var itemView = event.getItemView();
                var toggler = this.contentWizardPanel.getContextWindowToggler();

                if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                    if(!this.contextWindow.isFixed()) {
                        if(itemView.isEmpty() && event.isSilent()) {
                            if (this.contextWindow.isFloating() && this.contextWindow.isShownOrAboutToBeShown()) {
                                toggler.setActive(false);
                            }
                        }else if (event.isNew() && !toggler.isActive()) {
                            toggler.setActive(true);
                        }
                    } else {
                        this.contextWindow.setFixed(false);
                    }
                    this.inspectComponent(<ComponentView<Component>>itemView);
                }

                if (!this.pageView.isLocked()) {
                    this.minimizeContentFormPanelIfNeeded();
                }
            });

            this.liveEditPageProxy.onItemViewDeselected((event: ItemViewDeselectedEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (!toggler.isActive() && this.contextWindow.isShownOrAboutToBeShown()) {
                    this.contextWindow.slideOut();
                } else if (toggler.isActive() && !this.contextWindow.isShownOrAboutToBeShown()) {
                    this.contextWindow.slideIn();
                }
                this.clearSelection();
            });

            this.liveEditPageProxy.onComponentAdded((event: ComponentAddedEvent) => {
                // do something when component is added
                // onItemViewSelected() is not called on adding TextComponentView thus calling minimizeContentFormPanelIfNeeded() for it from here
                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getComponentView(), TextComponentView)) {
                    this.minimizeContentFormPanelIfNeeded();
                }
            });

            this.liveEditPageProxy.onComponentRemoved((event: ComponentRemovedEvent) => {

                if (!this.pageModel.isPageTemplate() && this.pageModel.getMode() == PageMode.AUTOMATIC) {
                    this.pageModel.initializePageFromDefault(this);
                }

                this.clearSelection();
            });

            this.liveEditPageProxy.onComponentViewDragDropped((event: ComponentViewDragDroppedEvent) => {

                var componentView = event.getComponentView();
                if (!componentView.isEmpty()) {
                    this.inspectComponent(componentView);
                }
            });

            this.liveEditPageProxy.onComponentDuplicated((event: ComponentDuplicatedEvent) => {

                this.saveAndReloadOnlyComponent(event.getDuplicatedComponentView());
            });

            this.liveEditPageProxy.onComponentInspected((event: ComponentInspectedEvent) => {
                var componentView = event.getComponentView();
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
                var fragmentView: FragmentComponentView = event.getComponentView();
                var componentType = event.getSourceComponentType().getShortName();
                var componentName = fragmentView.getComponent().getName().toString();
                api.notify.showSuccess(`Fragment created from '${componentName}' ${componentType}.`);

                this.saveAndReloadOnlyComponent(event.getComponentView());

                var summaryAndStatus = api.content.ContentSummaryAndCompareStatus.fromContentSummary(event.getFragmentContent());
                new api.content.event.EditContentEvent([summaryAndStatus]).fire();
            });

            this.liveEditPageProxy.onShowWarning((event: ShowWarningLiveEditEvent) => {
                api.notify.showWarning(event.getMessage());
            });

            this.liveEditPageProxy.onEditContent((event: api.content.event.EditContentEvent) => {
                new api.content.event.EditContentEvent(event.getModels()).fire();
            });

            this.liveEditPageProxy.onLiveEditPageInitializationError((event: LiveEditPageInitializationErrorEvent) => {
                api.notify.showError(event.getMessage(), false);
                new app.wizard.ShowContentFormEvent().fire();
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
            var pageModel = this.liveEditModel.getPageModel();
            var customizedWithController = pageModel.isCustomized() && pageModel.hasController();
            var isFragmentContent = pageModel.getMode() === PageMode.FRAGMENT;
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
            this.contextWindow.showInspectionPanel(this.pageInspectionPanel);
        }

        private inspectRegion(regionView: RegionView) {

            var region = regionView.getRegion();

            this.regionInspectionPanel.setRegion(region);
            this.contextWindow.showInspectionPanel(this.regionInspectionPanel);
        }

        private inspectComponent(componentView: ComponentView<Component>) {
            api.util.assertNotNull(componentView, "componentView cannot be null");

            if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, ImageComponentView)) {
                this.imageInspectionPanel.setImageComponent(<ImageComponentView>componentView);
                this.contextWindow.showInspectionPanel(this.imageInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, PartComponentView)) {
                this.partInspectionPanel.setPartComponent(<PartComponentView>componentView);
                this.contextWindow.showInspectionPanel(this.partInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, LayoutComponentView)) {
                this.layoutInspectionPanel.setLayoutComponent(<LayoutComponentView>componentView);
                this.contextWindow.showInspectionPanel(this.layoutInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, TextComponentView)) {
                this.textInspectionPanel.setTextComponent(<TextComponentView>componentView);
                this.contextWindow.showInspectionPanel(this.textInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, FragmentComponentView)) {
                this.fragmentInspectionPanel.setFragmentComponent(<FragmentComponentView>componentView);
                this.contextWindow.showInspectionPanel(this.fragmentInspectionPanel);
            }
            else {
                throw new Error("ComponentView cannot be selected: " + api.ClassHelper.getClassName(componentView));
            }
        }
    }
}