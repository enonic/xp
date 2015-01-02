module app.wizard.page {

    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;

    import PageDescriptor = api.content.page.PageDescriptor;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import LayoutDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.LayoutDescriptorChangedEvent;

    import Component = api.content.page.Component;

    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;

    import InspectionsPanelConfig = app.wizard.page.contextwindow.inspect.InspectionsPanelConfig;
    import InspectionsPanel = app.wizard.page.contextwindow.inspect.InspectionsPanel;
    import ContentInspectionPanel = app.wizard.page.contextwindow.inspect.ContentInspectionPanel;
    import PageInspectionPanel = app.wizard.page.contextwindow.inspect.PageInspectionPanel;
    import RegionInspectionPanel = app.wizard.page.contextwindow.inspect.RegionInspectionPanel;
    import ImageInspectionPanel = app.wizard.page.contextwindow.inspect.ImageInspectionPanel;
    import PartInspectionPanel = app.wizard.page.contextwindow.inspect.PartInspectionPanel;
    import LayoutInspectionPanel = app.wizard.page.contextwindow.inspect.LayoutInspectionPanel;
    import ContextWindow = app.wizard.page.contextwindow.ContextWindow;
    import ContextWindowConfig = app.wizard.page.contextwindow.ContextWindowConfig;
    import ContextWindowController = app.wizard.page.contextwindow.ContextWindowController;
    import EmulatorPanel = app.wizard.page.contextwindow.EmulatorPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;
    import RenderingMode = api.rendering.RenderingMode;

    import RegionView = api.liveedit.RegionView;
    import ComponentView = api.liveedit.ComponentView;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import DraggingComponentViewCanceledEvent = api.liveedit.DraggingComponentViewCanceledEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ImageComponentSetImageEvent = api.liveedit.image.ImageComponentSetImageEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemoveEvent = api.liveedit.ComponentRemoveEvent;
    import ComponentResetEvent = api.liveedit.ComponentResetEvent;
    import ComponentSetDescriptorEvent = api.liveedit.ComponentSetDescriptorEvent;
    import ComponentDuplicateEvent = api.liveedit.ComponentDuplicateEvent;

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

        private liveEditPage: LiveEditPageProxy;

        private selectedItemView: api.liveedit.ItemView;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.defaultModels = config.defaultModels;

            this.pageLoading = false;
            this.pageSkipReload = false;

            this.liveEditPage = new LiveEditPageProxy(<LiveEditPageProxyConfig>{
                liveFormPanel: this
            });

            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel();
            this.regionInspectionPanel = new RegionInspectionPanel();
            this.imageInspectionPanel = new ImageInspectionPanel();
            this.partInspectionPanel = new PartInspectionPanel();
            this.layoutInspectionPanel = new LayoutInspectionPanel();

            this.layoutInspectionPanel.onLayoutDescriptorChanged((event: LayoutDescriptorChangedEvent) => {

                var layoutView = event.getLayoutComponentView();
                var command = new ComponentSetDescriptorCommand().
                    setPageComponentView(layoutView).
                    setPageRegions(this.pageModel.getRegions()).
                    setDescriptor(event.getDescriptor());
                command.execute();
                this.saveAndReloadOnlyPageComponent(layoutView);
            });

            this.inspectionsPanel = new InspectionsPanel(<InspectionsPanelConfig>{
                contentInspectionPanel: this.contentInspectionPanel,
                pageInspectionPanel: this.pageInspectionPanel,
                regionInspectionPanel: this.regionInspectionPanel,
                imageInspectionPanel: this.imageInspectionPanel,
                partInspectionPanel: this.partInspectionPanel,
                layoutInspectionPanel: this.layoutInspectionPanel
            });

            this.emulatorPanel = new EmulatorPanel({
                liveEditPage: this.liveEditPage
            });

            this.insertablesPanel = new InsertablesPanel({
                liveEditPage: this.liveEditPage
            });

            this.frameContainer = new Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frameContainer.appendChild(this.liveEditPage.getIFrame());

            // append it here in order for the context window to be above
            this.appendChild(this.liveEditPage.getLoadMask());


            this.contextWindow = new ContextWindow(<ContextWindowConfig>{
                liveFormPanel: this,
                inspectionPanel: this.inspectionsPanel,
                emulatorPanel: this.emulatorPanel,
                insertablesPanel: this.insertablesPanel
            });

            this.appendChild(this.contextWindow);

            this.contextWindowController = new app.wizard.page.contextwindow.ContextWindowController(
                this,
                this.contextWindow,
                this.contentWizardPanel.getContextWindowToggler()
            );

            this.liveEditListen();
        }

        getSelectedItemView(): api.liveedit.ItemView {
            return this.selectedItemView;
        }

        remove() {

            this.liveEditPage.remove();
            super.remove();
        }

        public getPage(): Page {
            return this.pageModel.getPage();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.liveEditModel = liveEditModel;
            this.content = liveEditModel.getContent();
            this.pageModel = liveEditModel.getPageModel();

            this.liveEditPage.setModel(liveEditModel);
            this.pageInspectionPanel.setModel(liveEditModel);
            this.partInspectionPanel.setModel(liveEditModel);
            this.layoutInspectionPanel.setModel(liveEditModel);

            this.pageModel.onPropertyChanged((event: api.PropertyChangedEvent) => {

                // NB: To make the event.getSource() check work, all calls from this to PageModel that changes a property must done with this as eventSource argument.

                if (event.getPropertyName() == "controller" && this !== event.getSource()) {
                    this.saveAndReloadPage();
                }
                else if (event.getPropertyName() == "template" && this !== event.getSource()) {
                    this.saveAndReloadPage();
                }
            });
        }

        loadPage() {
            if (this.pageSkipReload == false && !this.pageLoading) {

                this.contextWindow.showInspectionPanel(this.pageInspectionPanel);

                this.pageLoading = true;
                this.liveEditPage.load();
                this.liveEditPage.onLoaded(() => {
                    this.pageLoading = false;

                });
            }
        }

        saveAndReloadPage() {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    this.liveEditPage.load();
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        saveAndReloadOnlyPageComponent(pageComponentView: ComponentView<Component>) {

            api.util.assertNotNull(pageComponentView, "pageComponentView cannot be null");

            this.pageSkipReload = true;
            var componentUrl = api.rendering.UriHelper.getComponentUri(this.content.getContentId().toString(),
                pageComponentView.getComponentPath().toString(),
                RenderingMode.EDIT,
                api.content.Workspace.STAGE);

            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    pageComponentView.showLoadingSpinner();
                    return this.liveEditPage.loadComponent(pageComponentView, componentUrl);
                }).
                catch((errorMessage: string) => {
                    pageComponentView.hideLoadingSpinner();
                    pageComponentView.showRenderingError(componentUrl, errorMessage);
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

            this.liveEditPage.onPageSelected((event: PageSelectEvent) => {
                this.selectedItemView = event.getPageView();
                this.inspectPage();
            });

            this.liveEditPage.onRegionSelected((event: RegionSelectEvent) => {
                this.selectedItemView = event.getRegionView();
                this.inspectRegion(event.getRegionView());
            });

            this.liveEditPage.onPageComponentSelected((event: ItemViewSelectedEvent) => {

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
                    this.inspectPageComponent(<ComponentView<Component>>itemView);
                }
            });

            this.liveEditPage.onDeselect((event: ItemViewDeselectEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (!toggler.isActive() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                } else if (toggler.isActive() && !this.contextWindow.isShown()) {
                    this.contextWindow.slideIn();
                }
                this.contextWindow.clearSelection();
                this.selectedItemView = null;
            });

            this.liveEditPage.onPageComponentRemoved((event: ComponentRemoveEvent) => {

                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if ((this.contextWindow.isFloating() || toggler.isActive()) && !this.contextWindow.isShown()) {
                    this.contextWindow.slideIn();
                }

                if (!this.pageModel.hasTemplate()) {
                    this.pageModel.initializePageFromDefault(this);
                }
                event.getPageComponentView().getPageComponent().removeFromParent();
                this.contextWindow.clearSelection();
            });

            this.liveEditPage.onPageComponentReset((event: ComponentResetEvent) => {

                if (!this.pageModel.hasTemplate()) {
                    this.pageModel.initializePageFromDefault(this);
                }
                var component: Component = event.getComponentView().getPageComponent();
                if (component) {
                    component.reset();
                }
            });

            this.liveEditPage.onDraggingPageComponentViewStartedEvent((event: DraggingComponentViewStartedEvent) => {

                if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });

            this.liveEditPage.onDraggingPageComponentViewCompleted((event: DraggingComponentViewCompletedEvent) => {

                var pageComponentView = event.getPageComponentView();
                if (!pageComponentView.isEmpty()) {
                    var toggler = this.contentWizardPanel.getContextWindowToggler();
                    if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                        this.contextWindow.slideIn();
                    }
                    this.inspectPageComponent(pageComponentView);
                }
            });

            this.liveEditPage.onDraggingPageComponentViewCanceled((event: DraggingComponentViewCanceledEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                    this.contextWindow.slideIn();
                }
            });

            this.liveEditPage.onPageComponentAdded((event: ComponentAddedEvent) => {

                if (!this.pageModel.hasTemplate()) {
                    this.pageModel.initializePageFromDefault(this);
                }
            });

            this.liveEditPage.onImageComponentSetImage((event: ImageComponentSetImageEvent) => {

                var command = new ImageComponentSetImageCommand().
                    setDefaultModels(this.defaultModels).
                    setPageRegions(this.pageModel.getRegions()).
                    setImage(event.getImageId()).
                    setPageComponentView(event.getImageComponentView()).
                    setImageName(event.getImageName());

                if (!this.pageModel.hasTemplate()) {
                    this.pageModel.initializePageFromDefault(this);
                }
                command.execute();
                this.saveAndReloadOnlyPageComponent(event.getImageComponentView());
            });

            this.liveEditPage.onPageComponentSetDescriptor((event: ComponentSetDescriptorEvent) => {

                var command = new ComponentSetDescriptorCommand().
                    setPageComponentView(event.getPageComponentView()).
                    setPageRegions(this.pageModel.getRegions()).
                    setDescriptor(event.getDescriptor());

                if (!this.pageModel.hasTemplate()) {
                    this.pageModel.initializePageFromDefault(this);
                }
                command.execute();
                this.saveAndReloadOnlyPageComponent(event.getPageComponentView());
            });

            this.liveEditPage.onPageComponentDuplicated((event: ComponentDuplicateEvent) => {

                this.saveAndReloadOnlyPageComponent(event.getDuplicatedPageComponentView());
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

        private inspectPageComponent(pageComponentView: ComponentView<Component>) {
            api.util.assertNotNull(pageComponentView, "pageComponentView cannot be null");

            this.selectedItemView = pageComponentView;

            if (api.ObjectHelper.iFrameSafeInstanceOf(pageComponentView, ImageComponentView)) {
                this.imageInspectionPanel.setImageComponent(<ImageComponentView>pageComponentView);
                this.contextWindow.showInspectionPanel(this.imageInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(pageComponentView, PartComponentView)) {
                this.partInspectionPanel.setPartComponent(<PartComponentView>pageComponentView);
                this.contextWindow.showInspectionPanel(this.partInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(pageComponentView, LayoutComponentView)) {
                this.layoutInspectionPanel.setLayoutComponent(<LayoutComponentView>pageComponentView);
                this.contextWindow.showInspectionPanel(this.layoutInspectionPanel);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(pageComponentView, TextComponentView)) {

            }
            else {
                throw new Error("ComponentView cannot be selected: " + api.ClassHelper.getClassName(pageComponentView));
            }
        }
    }
}