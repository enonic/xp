module app.wizard.page {

    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;

    import PageDescriptor = api.content.page.PageDescriptor;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import LayoutDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.LayoutDescriptorChangedEvent;

    import PageComponent = api.content.page.PageComponent;

    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;

    import InspectionPanelConfig = app.wizard.page.contextwindow.inspect.InspectionPanelConfig;
    import InspectionPanel = app.wizard.page.contextwindow.inspect.InspectionPanel;
    import ContentInspectionPanel = app.wizard.page.contextwindow.inspect.ContentInspectionPanel;
    import PageInspectionPanel = app.wizard.page.contextwindow.inspect.PageInspectionPanel;
    import PageInspectionPanelConfig = app.wizard.page.contextwindow.inspect.PageInspectionPanelConfig;
    import RegionInspectionPanel = app.wizard.page.contextwindow.inspect.RegionInspectionPanel;
    import ImageInspectionPanel = app.wizard.page.contextwindow.inspect.ImageInspectionPanel;
    import ImageInspectionPanelConfig = app.wizard.page.contextwindow.inspect.ImageInspectionPanelConfig;
    import PartInspectionPanel = app.wizard.page.contextwindow.inspect.PartInspectionPanel;
    import PartInspectionPanelConfig = app.wizard.page.contextwindow.inspect.PartInspectionPanelConfig;
    import LayoutInspectionPanel = app.wizard.page.contextwindow.inspect.LayoutInspectionPanel;
    import LayoutInspectionPanelConfig = app.wizard.page.contextwindow.inspect.LayoutInspectionPanelConfig;
    import ContextWindow = app.wizard.page.contextwindow.ContextWindow;
    import ContextWindowConfig = app.wizard.page.contextwindow.ContextWindowConfig;
    import ContextWindowController = app.wizard.page.contextwindow.ContextWindowController;
    import EmulatorPanel = app.wizard.page.contextwindow.EmulatorPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;
    import RenderingMode = api.rendering.RenderingMode;

    import RegionView = api.liveedit.RegionView;
    import PageComponentView = api.liveedit.PageComponentView;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import DraggingPageComponentViewStartedEvent = api.liveedit.DraggingPageComponentViewStartedEvent;
    import DraggingPageComponentViewCompletedEvent = api.liveedit.DraggingPageComponentViewCompletedEvent;
    import DraggingPageComponentViewCanceledEvent = api.liveedit.DraggingPageComponentViewCanceledEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ImageComponentSetImageEvent = api.liveedit.image.ImageComponentSetImageEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;
    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;

    import Panel = api.ui.panel.Panel;

    export interface LiveFormPanelConfig {

        site:Content;

        contentType:ContentTypeName;

        contentWizardPanel:ContentWizardPanel;

        defaultModels: DefaultModels;
    }

    export class LiveFormPanel extends api.ui.panel.Panel {

        private site: Content;

        private defaultModels: DefaultModels;

        private content: Content;
        private pageModel: PageModel;

        private pageLoading: boolean;

        private pageSkipReload: boolean;
        private frameContainer: Panel;

        private contextWindow: ContextWindow;
        private contextWindowController: ContextWindowController;

        private emulatorPanel: EmulatorPanel;
        private insertablesPanel: InsertablesPanel;
        private inspectionPanel: InspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: PageInspectionPanel;
        private regionInspectionPanel: RegionInspectionPanel;
        private imageInspectionPanel: ImageInspectionPanel;
        private partInspectionPanel: PartInspectionPanel;
        private layoutInspectionPanel: LayoutInspectionPanel;

        private contentWizardPanel: ContentWizardPanel;

        private liveEditPage: LiveEditPageProxy;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.site = config.site;
            this.defaultModels = config.defaultModels;

            this.pageLoading = false;
            this.pageSkipReload = false;

            this.liveEditPage = new LiveEditPageProxy(<LiveEditPageProxyConfig>{
                liveFormPanel: this,
                site: this.site
            });

            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel(<PageInspectionPanelConfig>{
                site: this.site,
                contentType: config.contentType
            });

            this.regionInspectionPanel = new RegionInspectionPanel();

            this.imageInspectionPanel = new ImageInspectionPanel(<ImageInspectionPanelConfig>{
            });

            this.partInspectionPanel = new PartInspectionPanel(<PartInspectionPanelConfig>{
                site: this.site
            });

            this.layoutInspectionPanel = new LayoutInspectionPanel(<LayoutInspectionPanelConfig>{
                site: this.site
            });

            this.layoutInspectionPanel.onLayoutDescriptorChanged((event: LayoutDescriptorChangedEvent) => {

                var layoutView = event.getLayoutComponentView();
                var command = new PageComponentSetDescriptorCommand().
                    setPageComponentView(layoutView).
                    setPageRegions(this.pageModel.getRegions()).
                    setDescriptor(event.getDescriptor());
                command.execute();
                this.saveAndReloadOnlyPageComponent(layoutView);
            });

            this.inspectionPanel = new InspectionPanel(<InspectionPanelConfig>{
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
                inspectionPanel: this.inspectionPanel,
                emulatorPanel: this.emulatorPanel,
                insertablesPanel: this.insertablesPanel
            });

            this.appendChild(this.contextWindow);

            this.contextWindowController = new app.wizard.page.contextwindow.ContextWindowController(this.contextWindow,
                this.contentWizardPanel.getContextWindowToggler());

            this.liveEditListen();
        }

        remove() {

            this.liveEditPage.remove();
            super.remove();
        }

        public getPage(): Page {
            return this.pageModel.getPage();
        }

        setContent(content: Content) {
            api.util.assertNotNull(content, "content is required");
            this.content = content;
        }

        layout() {
            api.util.assertNotNull(this.content, "content is not set");

            if (!this.pageSkipReload) {

                if (!this.pageModel) {
                    this.pageModel = new PageModel(this.content);
                    this.pageModel.onPropertyChanged((event: api.PropertyChangedEvent) => {
                        if (event.getPropertyName() == "controller" && this !== event.getSource()) {
                            this.saveAndReloadPage();
                        }
                        else if (event.getPropertyName() == "template" && this !== event.getSource()) {
                            this.saveAndReloadPage();
                        }
                    });
                }

                if (!this.pageModel.isInitialized()) {

                    if (this.content.isPageTemplate()) {
                        var pageDescriptorKey = null;
                        if( this.content.isPage() ) {
                            pageDescriptorKey = this.content.getPage().getController();
                            this.loadPageDescriptor(pageDescriptorKey).then((pageDescriptor: PageDescriptor) => {

                                this.pageModel.setController(pageDescriptor, this);

                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).done();
                        }
                        else {
                            this.pageModel.setController(null, this);
                        }
                    }
                    else {

                        if (this.content.isPage()) {
                            var pageTemplateKey = this.content.getPage().getTemplate();
                            this.loadPageTemplate(pageTemplateKey).then((pageTemplate: PageTemplate) => {

                                this.pageModel.setTemplate(pageTemplate, this.content.getPage(), this);

                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).done();
                        }
                        else {
                            if (this.defaultModels.getPageTemplate()) {
                                this.pageModel.setDefaultTemplate(this.defaultModels.getPageTemplate(), this);
                            }
                        }
                    }

                    this.liveEditPage.setPage(this.pageModel);
                    this.pageInspectionPanel.setModel(this.pageModel);
                }
            }
            this.loadPage();
        }

        private loadPageTemplate(key: PageTemplateKey): wemQ.Promise<PageTemplate> {
            return new GetPageTemplateByKeyRequest(key).sendAndParse();
        }

        private loadPageDescriptor(key: DescriptorKey): wemQ.Promise<PageDescriptor> {
            return new GetPageDescriptorByKeyRequest(key).sendAndParse();
        }

        loadPage() {
            if (this.pageSkipReload == false && !this.pageLoading) {

                this.contextWindow.showInspectionPanel(this.pageInspectionPanel);

                this.pageLoading = true;
                this.liveEditPage.load(this.content);
                this.liveEditPage.onLoaded(() => {
                    this.pageLoading = false;

                });
            }
        }

        private initializePageFromDefault() {

            var skip = false;
            if (this.pageModel.hasTemplate()) {
                skip = true;
            }
            if (this.pageModel.hasController()) {
                skip = true;
            }

            if (!skip) {
                this.pageModel.setTemplate(this.defaultModels.getPageTemplate(), this.content.getPage(), this);
            }
        }

        private saveAndReloadPage() {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    this.liveEditPage.load(this.content);
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        private saveAndReloadOnlyPageComponent(pageComponentView: PageComponentView<PageComponent>) {

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

                this.inspectPage();
            });

            this.liveEditPage.onRegionSelected((event: RegionSelectEvent) => {

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

                if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                    this.inspectPageComponent(<PageComponentView<PageComponent>>itemView);
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
            });

            this.liveEditPage.onPageComponentRemoved((event: PageComponentRemoveEvent) => {

                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if ((this.contextWindow.isFloating() || toggler.isActive()) && !this.contextWindow.isShown()) {
                    this.contextWindow.slideIn();
                }

                if (!this.pageModel.hasTemplate()) {
                    this.initializePageFromDefault();
                }
                event.getPageComponentView().getPageComponent().removeFromParent();
                this.contextWindow.clearSelection();
            });

            this.liveEditPage.onPageComponentReset((event: PageComponentResetEvent) => {

                if (!this.pageModel.hasTemplate()) {
                    this.initializePageFromDefault();
                }
                var component: PageComponent = event.getComponentView().getPageComponent();
                if (component) {
                    component.reset();
                }
            });

            this.liveEditPage.onDraggingPageComponentViewStartedEvent((event: DraggingPageComponentViewStartedEvent) => {

                if (this.contextWindow.isFloating() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });

            this.liveEditPage.onDraggingPageComponentViewCompleted((event: DraggingPageComponentViewCompletedEvent) => {

                var pageComponentView = event.getPageComponentView();
                if (!pageComponentView.isEmpty()) {
                    var toggler = this.contentWizardPanel.getContextWindowToggler();
                    if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                        this.contextWindow.slideIn();
                    }
                    this.inspectPageComponent(pageComponentView);
                }
            });

            this.liveEditPage.onDraggingPageComponentViewCanceled((event: DraggingPageComponentViewCanceledEvent) => {
                var toggler = this.contentWizardPanel.getContextWindowToggler();
                if (this.contextWindow.isFloating() && !this.contextWindow.isShown() && toggler.isActive()) {
                    this.contextWindow.slideIn();
                }
            });

            this.liveEditPage.onPageComponentAdded((event: PageComponentAddedEvent) => {

                if (!this.pageModel.hasTemplate()) {
                    this.initializePageFromDefault();
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
                    this.initializePageFromDefault();
                }
                command.execute();
                this.saveAndReloadOnlyPageComponent(event.getImageComponentView());
            });

            this.liveEditPage.onPageComponentSetDescriptor((event: PageComponentSetDescriptorEvent) => {

                var command = new PageComponentSetDescriptorCommand().
                    setPageComponentView(event.getPageComponentView()).
                    setPageRegions(this.pageModel.getRegions()).
                    setDescriptor(event.getDescriptor());

                if (!this.pageModel.hasTemplate()) {
                    this.initializePageFromDefault();
                }
                command.execute();
                this.saveAndReloadOnlyPageComponent(event.getPageComponentView());
            });

            this.liveEditPage.onPageComponentDuplicated((event: PageComponentDuplicateEvent) => {

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

        private inspectPageComponent(pageComponentView: PageComponentView<PageComponent>) {
            api.util.assertNotNull(pageComponentView, "pageComponentView cannot be null");

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
                throw new Error("PageComponentView cannot be selected: " + api.util.getClassName(pageComponentView));
            }
        }
    }
}