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
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentPropertyValueChangedEvent = api.content.page.region.ComponentPropertyValueChangedEvent;

    import GetPartDescriptorsByModulesRequest = api.content.page.region.GetPartDescriptorsByModulesRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.region.GetLayoutDescriptorsByModulesRequest;

    import InspectionsPanelConfig = app.wizard.page.contextwindow.inspect.InspectionsPanelConfig;
    import InspectionsPanel = app.wizard.page.contextwindow.inspect.InspectionsPanel;
    import ContentInspectionPanel = app.wizard.page.contextwindow.inspect.ContentInspectionPanel;
    import PageInspectionPanel = app.wizard.page.contextwindow.inspect.page.PageInspectionPanel;
    import RegionInspectionPanel = app.wizard.page.contextwindow.inspect.region.RegionInspectionPanel;
    import ImageInspectionPanel = app.wizard.page.contextwindow.inspect.region.ImageInspectionPanel;
    import PartInspectionPanel = app.wizard.page.contextwindow.inspect.region.PartInspectionPanel;
    import LayoutInspectionPanel = app.wizard.page.contextwindow.inspect.region.LayoutInspectionPanel;
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
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import DraggingComponentViewCanceledEvent = api.liveedit.DraggingComponentViewCanceledEvent;
    import PageSelectedEvent = api.liveedit.PageSelectedEvent;
    import RegionSelectedEvent = api.liveedit.RegionSelectedEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemovedEvent = api.liveedit.ComponentRemovedEvent;
    import ComponentDuplicatedEvent = api.liveedit.ComponentDuplicatedEvent;

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

        private contentWizardPanel: ContentWizardPanel;

        private liveEditPageProxy: LiveEditPageProxy;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.defaultModels = config.defaultModels;

            this.pageLoading = false;
            this.pageSkipReload = false;

            this.liveEditPageProxy = new LiveEditPageProxy();

            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel();
            this.regionInspectionPanel = new RegionInspectionPanel();
            this.imageInspectionPanel = new ImageInspectionPanel();
            this.partInspectionPanel = new PartInspectionPanel();
            this.layoutInspectionPanel = new LayoutInspectionPanel();

            var saveAction = new api.ui.Action('Apply');
            saveAction.onExecuted(() => {
                var itemView = this.pageView.getSelectedView();
                if (itemView) {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                        this.saveAndReloadOnlyComponent(<ComponentView<Component>> itemView);
                    } else if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageView)) {
                        this.saveAndReloadPage();
                    }
                }
            });

            this.inspectionsPanel = new InspectionsPanel(<InspectionsPanelConfig>{
                contentInspectionPanel: this.contentInspectionPanel,
                pageInspectionPanel: this.pageInspectionPanel,
                regionInspectionPanel: this.regionInspectionPanel,
                imageInspectionPanel: this.imageInspectionPanel,
                partInspectionPanel: this.partInspectionPanel,
                layoutInspectionPanel: this.layoutInspectionPanel,
                saveAction: saveAction
            });

            this.emulatorPanel = new EmulatorPanel({
                liveEditPage: this.liveEditPageProxy
            });

            this.insertablesPanel = new InsertablesPanel({
                liveEditPage: this.liveEditPageProxy
            });

            this.frameContainer = new Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frameContainer.appendChild(this.liveEditPageProxy.getIFrame());

            // append it here in order for the context window to be above
            this.appendChild(this.liveEditPageProxy.getLoadMask());

            this.contextWindow = new ContextWindow(<ContextWindowConfig>{
                liveFormPanel: this,
                inspectionPanel: this.inspectionsPanel,
                emulatorPanel: this.emulatorPanel,
                insertablesPanel: this.insertablesPanel
            });

            this.appendChild(this.contextWindow);

            this.contextWindowController = new app.wizard.page.contextwindow.ContextWindowController(
                this.contextWindow,
                this.contentWizardPanel.getContextWindowToggler()
            );

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
            this.pageModel = liveEditModel.getPageModel();
            this.pageModel.setIgnorePropertyChanges(true);

            this.liveEditPageProxy.setModel(liveEditModel);
            this.imageInspectionPanel.setModel(liveEditModel);
            this.pageInspectionPanel.setModel(liveEditModel);
            this.partInspectionPanel.setModel(liveEditModel);
            this.layoutInspectionPanel.setModel(liveEditModel);
            this.imageInspectionPanel.setModel(liveEditModel);

            this.pageModel.setIgnorePropertyChanges(false);

            this.pageModel.onPropertyChanged((event: api.PropertyChangedEvent) => {

                // NB: To make the event.getSource() check work, all calls from this to PageModel that changes a property must done with this as eventSource argument.

                if (event.getPropertyName() == "controller" && this !== event.getSource()) {
                    this.saveAndReloadPage();
                }
                else if (event.getPropertyName() == "template" && this !== event.getSource()) {

                    if (this.pageModel.getMode() == PageMode.AUTOMATIC) {
                        this.saveAndReloadPage();
                    }
                    else {
                        // Skip save. Let user continue working instead.
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
            });
        }

        loadPage() {
            if (this.pageSkipReload == false && !this.pageLoading) {

                this.contextWindow.clearSelection();

                this.pageLoading = true;
                this.liveEditPageProxy.load();
                this.liveEditPageProxy.onLoaded(() => {
                    this.pageLoading = false;

                });
            }
        }

        saveAndReloadPage() {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    this.liveEditPageProxy.load();
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        saveAndReloadOnlyComponent(componentView: ComponentView<Component>) {

            api.util.assertNotNull(componentView, "componentView cannot be null");

            this.pageSkipReload = true;
            var componentUrl = api.rendering.UriHelper.getComponentUri(this.content.getContentId().toString(),
                componentView.getComponentPath().toString(),
                RenderingMode.EDIT,
                api.content.Workspace.DRAFT);

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

            this.liveEditPageProxy.onPageTextModeStarted((event: api.liveedit.PageTextModeStartedEvent) => {
                if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });

            this.liveEditPageProxy.onLiveEditPageViewReady((event: api.liveedit.LiveEditPageViewReadyEvent) => {
                this.pageView = event.getPageView();
            });

            this.liveEditPageProxy.onPageSelected((event: PageSelectedEvent) => {
                this.inspectPage();
            });

            this.liveEditPageProxy.onRegionSelected((event: RegionSelectedEvent) => {
                this.inspectRegion(event.getRegionView());
            });

            this.liveEditPageProxy.onItemViewSelected((event: ItemViewSelectedEvent) => {

                var itemView = event.getItemView();

                if (itemView.isEmpty() || api.ObjectHelper.iFrameSafeInstanceOf(itemView, TextComponentView)) {
                    if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                        this.contextWindow.slideOut();
                    }
                }
                else {
                    if (this.contextWindow.isFloating() && !this.contextWindow.isShown()) {
                        this.contextWindow.slideIn();
                    }
                }

                if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, ComponentView)) {
                    this.inspectComponent(<ComponentView<Component>>itemView);
                }
            });

            this.liveEditPageProxy.onItemViewDeselected((event: ItemViewDeselectEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (!toggler.isActive() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                } else if (toggler.isActive() && !this.contextWindow.isShown()) {
                    this.contextWindow.slideIn();
                }
                this.contextWindow.clearSelection();
            });

            this.liveEditPageProxy.onComponentAdded((event: ComponentAddedEvent) => {
                // do something when component is added
            });

            this.liveEditPageProxy.onComponentRemoved((event: ComponentRemovedEvent) => {

                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if ((this.contextWindow.isFloating() || toggler.isActive()) && !this.contextWindow.isShown()) {
                    this.contextWindow.slideIn();
                }

                if (!this.pageModel.isPageTemplate() && this.pageModel.getMode() == PageMode.AUTOMATIC) {
                    this.pageModel.initializePageFromDefault(this);
                }

                this.contextWindow.clearSelection();
            });

            this.liveEditPageProxy.onDraggingComponentViewStartedEvent((event: DraggingComponentViewStartedEvent) => {

                if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });

            this.liveEditPageProxy.onDraggingComponentViewCompleted((event: DraggingComponentViewCompletedEvent) => {

                var componentView = event.getComponentView();
                if (!componentView.isEmpty()) {
                    var toggler = this.contentWizardPanel.getContextWindowToggler();
                    if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                        this.contextWindow.slideIn();
                    }
                    this.inspectComponent(componentView);
                }
            });

            this.liveEditPageProxy.onDraggingComponentViewCanceled((event: DraggingComponentViewCanceledEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                    this.contextWindow.slideIn();
                }
            });

            this.liveEditPageProxy.onComponentDuplicated((event: ComponentDuplicatedEvent) => {

                this.saveAndReloadOnlyComponent(event.getDuplicatedComponentView());
            });

            this.insertablesPanel.onHideContextWindowRequest(() => {
                if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });
        }

        private inspectContent(contentId: api.content.ContentId) {
            this.contextWindow.showInspectionPanel(this.contentInspectionPanel);
        }

        private inspectPage() {

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

            }
            else {
                throw new Error("ComponentView cannot be selected: " + api.ClassHelper.getClassName(componentView));
            }
        }
    }
}