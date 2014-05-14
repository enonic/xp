module app.wizard.page {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import PageRegions = api.content.page.PageRegions;
    import RegionPath = api.content.page.RegionPath;
    import RootDataSet = api.data.RootDataSet;

    import PageDescriptor = api.content.page.PageDescriptor;
    import RegionDescriptor = api.content.page.region.RegionDescriptor;
    import Descriptor = api.content.page.Descriptor;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import ImageDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.ImageDescriptorChangedEvent;
    import LayoutDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.LayoutDescriptorChangedEvent;

    import PageComponentBuilder = api.content.page.PageComponentBuilder;
    import ComponentName = api.content.page.ComponentName;
    import PageComponent = api.content.page.PageComponent;
    import PageComponentType = api.content.page.PageComponentType;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import DescriptorBasedPageComponentBuilder = api.content.page.DescriptorBasedPageComponentBuilder;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PartComponent = api.content.page.part.PartComponent;
    import ImageComponent = api.content.page.image.ImageComponent;

    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import GetImageDescriptorsByModulesRequest = api.content.page.image.GetImageDescriptorsByModulesRequest;
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
    import EmulatorPanel = app.wizard.page.contextwindow.EmulatorPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;
    import RenderingMode = api.rendering.RenderingMode;

    export interface LiveFormPanelConfig {

        siteTemplate:SiteTemplate;

        contentType:ContentTypeName;

        contentWizardPanel:ContentWizardPanel;

        defaultModels: DefaultModels;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private siteTemplate: SiteTemplate;

        private defaultModels: DefaultModels;

        private content: Content;
        private pageTemplate: PageTemplate;
        private pageRegions: PageRegions;
        private pageConfig: RootDataSet;
        private pageDescriptor: PageDescriptor;

        private pageNeedsReload: boolean;
        private pageLoading: boolean;

        private pageSkipReload: boolean;
        private frameContainer: api.ui.Panel;

        private contextWindow: ContextWindow;
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

        private liveEditPage: LiveEditPage;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.siteTemplate = config.siteTemplate;
            this.defaultModels = config.defaultModels;

            this.pageNeedsReload = true;
            this.pageLoading = false;
            this.pageSkipReload = false;

            this.liveEditPage = new LiveEditPage(<LiveEditPageConfig>{
                liveFormPanel: this,
                siteTemplate: this.siteTemplate
            });

            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel(<PageInspectionPanelConfig>{
                siteTemplateKey: this.siteTemplate.getKey(),
                contentType: config.contentType
            });

            this.regionInspectionPanel = new RegionInspectionPanel();

            this.imageInspectionPanel = new ImageInspectionPanel(<ImageInspectionPanelConfig>{
                siteTemplate: this.siteTemplate,
                defaultModels: config.defaultModels
            });

            this.imageInspectionPanel.onImageDescriptorChanged((event: ImageDescriptorChangedEvent) => {

                var uiComponent = this.liveEditPage.getComponentByPath(event.getComponentPath());
                var command = new PageComponentSetDescriptorCommand().
                    setComponentView(uiComponent).
                    setPageRegions(this.pageRegions).
                    setComponentPath(event.getComponentPath()).
                    setDescriptor(event.getDescriptor());
                var newComponentPath = command.execute();
                this.saveAndReloadOnlyPageComponent(newComponentPath, uiComponent);
            });

            this.partInspectionPanel = new PartInspectionPanel(<PartInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });

            this.layoutInspectionPanel = new LayoutInspectionPanel(<LayoutInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });

            this.layoutInspectionPanel.onLayoutDescriptorChanged((event: LayoutDescriptorChangedEvent) => {

                var uiComponent = this.liveEditPage.getComponentByPath(event.getComponentPath());
                var command = new PageComponentSetDescriptorCommand().
                    setComponentView(uiComponent).
                    setPageRegions(this.pageRegions).
                    setComponentPath(event.getComponentPath()).
                    setDescriptor(event.getDescriptor());
                var newComponentPath = command.execute();
                this.saveAndReloadOnlyPageComponent(newComponentPath, uiComponent);
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

            this.frameContainer = new api.ui.Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frameContainer.appendChild(this.liveEditPage.getIFrame());

            // append it here in order for the context window to be above
            this.appendChild(this.liveEditPage.getLoadMask());


            this.contextWindow = new ContextWindow(<ContextWindowConfig>{
                liveEditPage: this.liveEditPage,
                liveFormPanel: this,
                inspectionPanel: this.inspectionPanel,
                emulatorPanel: this.emulatorPanel,
                insertablesPanel: this.insertablesPanel
            });

            this.appendChild(this.contextWindow);

            this.pageInspectionPanel.onPageTemplateChanged((event: app.wizard.page.contextwindow.inspect.PageTemplateChangedEvent) => {

                var selectedPageTemplate = event.getPageTemplate();
                if (selectedPageTemplate) {

                    new api.content.page.GetPageTemplateByKeyRequest(selectedPageTemplate.getKey()).
                        setSiteTemplateKey(this.siteTemplate.getKey()).
                        sendAndParse().
                        then((pageTemplate: PageTemplate) => {

                            this.pageTemplate = pageTemplate;

                            this.pageRegions = this.resolvePageRegions(this.content, this.pageTemplate);
                            this.pageConfig = this.resolvePageConfig(this.content, this.pageTemplate);

                            return new GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).sendAndParse();

                        }).done((pageDescriptor: PageDescriptor) => {

                            this.pageDescriptor = pageDescriptor;
                            this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                            this.saveAndReloadPage();
                        });
                }
                else {
                    this.pageTemplate = null;
                    this.pageDescriptor = null;
                    this.pageRegions = this.defaultModels.getPageTemplate().getRegions();
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                    this.pageInspectionPanel.setPage(this.content, null, null, this.pageConfig);

                    this.saveAndReloadPage();
                }
            });

            this.liveEditListen();
        }

        remove() {

            this.liveEditPage.remove();
            super.remove();
        }

        loadPageIfNotLoaded(): Q.Promise<void> {

            console.log("LiveFormPanel.loadPageIfNotLoaded() this.needsReload = " + this.pageNeedsReload);
            if (this.pageNeedsReload && !this.pageLoading) {

                this.pageLoading = true;
                return this.liveEditPage.load(this.content).then(()=> {

                    this.pageLoading = false;
                    this.pageNeedsReload = false;

                    return this.loadPageDescriptor();

                }).then((): void => {

                    this.contextWindow.showInspectionPanel(this.pageInspectionPanel);
                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                });
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        setPage(content: Content, pageTemplate: PageTemplate): Q.Promise<void> {

            api.util.assertNotNull(content, "Expected content not be null");

            this.content = content;
            this.pageTemplate = pageTemplate;

            if (!this.pageSkipReload) {

                if (!this.pageRegions && !this.content.isPage()) {
                    this.pageRegions = this.defaultModels.getPageTemplate().getRegions();
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                }
                else if (!this.pageRegions && this.content.isPage()) {

                    this.pageRegions = content.getPage().getRegions();
                    this.pageConfig = content.getPage().getConfig();
                }
            }

            if (!this.isVisible()) {
                this.pageNeedsReload = true;
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
            else if (this.pageSkipReload == true) {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.liveEditPage.load(this.content).
                    then(() => {
                        return this.loadPageDescriptor();
                    });
            }
        }

        /**
         * Called by ContentWizardPanel when content is saved.
         */
        contentSaved() {

            if (!this.pageSkipReload) {
                // Reload page to show changes
                this.liveEditPage.load(this.content).
                    done(() => {
                        return this.loadPageDescriptor();
                    });
            }
        }

        private loadPageDescriptor(): Q.Promise<void> {

            if (!this.pageTemplate) {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return new GetPageDescriptorByKeyRequest(this.pageTemplate.getDescriptorKey()).sendAndParse().
                    then((pageDescriptor: PageDescriptor): void => {
                        this.pageDescriptor = pageDescriptor;
                    });
            }
        }

        private resolvePageRegions(content: Content, pageTemplate: PageTemplate): PageRegions {

            if (content.isPage() && content.getPage().hasRegions()) {
                return content.getPage().getRegions();
            }
            else {
                return pageTemplate.getRegions();
            }
        }

        private resolvePageConfig(content: Content, pageTemplate: PageTemplate): RootDataSet {

            if (content.isPage() && content.getPage().hasConfig()) {
                return content.getPage().getConfig();
            }
            else {
                return pageTemplate.getConfig();
            }
        }

        private initializePageFromDefault(): Q.Promise<void> {

            var defaultPageTemplate = this.defaultModels.getPageTemplate();

            return new GetPageDescriptorByKeyRequest(defaultPageTemplate.getDescriptorKey()).sendAndParse().
                then((pageDescriptor: PageDescriptor): void => {

                    this.pageTemplate = defaultPageTemplate;
                    this.pageConfig = defaultPageTemplate.getConfig();
                    this.pageRegions = defaultPageTemplate.getRegions();
                    this.pageDescriptor = pageDescriptor;

                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                });
        }

        private saveAndReloadPage() {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().done(() => {
                this.pageSkipReload = false;
                this.liveEditPage.load(this.content);
            });
        }

        private saveAndReloadOnlyPageComponent(componentPath: ComponentPath, componentUI: any) {

            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().
                done(() => {
                    this.pageSkipReload = false;
                    componentUI.showLoadingSpinner();

                    this.liveEditPage.loadComponent(componentPath, componentUI, this.content);
                });
        }

        updateFrameContainerSize(contextWindowPinned: boolean, contextWindowWidth?: number) {
            if (contextWindowPinned && contextWindowWidth) {
                this.frameContainer.getEl().setWidth("calc(100% - " + (contextWindowWidth - 1) + "px)");
            } else {
                this.frameContainer.getEl().setWidth("100%");
            }
        }

        public getPageTemplate(): PageTemplateKey {

            if (!this.pageTemplate) {
                return null;
            }
            return this.pageTemplate.getKey();
        }

        public getRegions(): PageRegions {

            return this.pageRegions;
        }

        public getConfig(): RootDataSet {

            return this.pageConfig;
        }

        private liveEditListen() {

            this.liveEditPage.onPageSelected((event: PageSelectedEvent) => {

                this.inspectPage();
            });

            this.liveEditPage.onRegionSelected((event: RegionSelectedEvent) => {

                this.inspectRegion(event.getPath());
            });

            this.liveEditPage.onPageComponentSelected((event: PageComponentSelectedEvent) => {

                if (event.isComponentEmpty()) {
                    this.contextWindow.hide();
                }
                else {
                    this.contextWindow.show();
                }
                this.inspectComponent(event.getPath());
            });

            this.liveEditPage.onDeselect((event: DeselectEvent) => {

                this.contextWindow.show();
                this.contextWindow.clearSelection();
            });

            this.liveEditPage.onPageComponentRemoved((event: PageComponentRemovedEvent) => {

                this.contextWindow.show();

                if (this.pageTemplate) {

                    this.pageRegions.removeComponent(event.getPath());
                    this.contextWindow.clearSelection();
                }
                else {
                    // Make the Content a Page if it wasn't before removing
                    this.initializePageFromDefault().done(() => {

                        this.pageRegions.removeComponent(event.getPath());
                        this.contextWindow.clearSelection();
                    });
                }

            });

            this.liveEditPage.onPageComponentReset((event: PageComponentResetEvent) => {

                var component = this.pageRegions.getComponent(event.getPath());
                if (component) {
                    component.reset();
                }
            });

            this.liveEditPage.onSortableStart((event: SortableStartEvent) => {

                this.contextWindow.hide();
            });

            this.liveEditPage.onSortableStop((event: SortableStopEvent) => {

                if (event.getPath()) {

                    if (!event.isEmpty()) {
                        this.contextWindow.show();
                    }

                    if (event.getComponent().isSelected()) {
                        this.liveEditPage.selectComponent(event.getPath());
                    }
                }
                else {
                    this.contextWindow.show();
                }
            });

            this.liveEditPage.onSortableUpdate((event: SortableUpdateEvent) => {

                var newPath = this.pageRegions.moveComponent(event.getComponentPath(), event.getRegion(),
                    event.getPrecedingComponent().getComponentName());

                if (newPath) {
                    event.getComponentView().setComponentPath(newPath.toString());
                }
            });

            this.liveEditPage.onPageComponentAdded((event: PageComponentAddedEvent) => {

                var command = new PageComponentAddedCommand().
                    setPageRegions(this.pageRegions).
                    setType(event.getType()).
                    setRegion(event.getRegion()).
                    setPrecedingComponent(event.getPrecedingComponent() ? event.getPrecedingComponent().getComponentName() : null).
                    setComponentView(event.getElement());

                if (!this.pageTemplate) {
                    this.initializePageFromDefault().done(() => {

                        command.execute();
                    });
                }
                else {
                    command.execute();
                }
            });

            this.liveEditPage.onImageComponentSetImage((event: ImageComponentSetImageEvent) => {

                var command = new ImageComponentSetImageCommand().
                    setDefaultModels(this.defaultModels).
                    setPageRegions(this.pageRegions).
                    setComponentPath(event.getPath()).
                    setImage(event.getImage()).
                    setComponentView(event.getComponentView()).
                    setImageName(event.getImageName());

                if (this.pageTemplate) {
                    var newComponentPath = command.execute();
                    this.saveAndReloadOnlyPageComponent(newComponentPath, event.getComponentView());
                }
                else {
                    this.initializePageFromDefault().done(() => {

                        var newComponentPath = command.execute();
                        this.saveAndReloadOnlyPageComponent(newComponentPath, event.getComponentView());
                    });
                }
            });

            this.liveEditPage.onPageComponentSetDescriptor((event: PageComponentSetDescriptorEvent) => {

                var command = new PageComponentSetDescriptorCommand().
                    setComponentView(event.getComponentView()).
                    setPageRegions(this.pageRegions).
                    setComponentPath(event.getPath()).
                    setDescriptor(event.getDescriptor());

                if (this.pageTemplate) {
                    var newComponentPath = command.execute();
                    this.saveAndReloadOnlyPageComponent(newComponentPath, event.getComponentView());
                }
                else {
                    this.initializePageFromDefault().done(() => {

                        var newComponentPath = command.execute();
                        this.saveAndReloadOnlyPageComponent(newComponentPath, event.getComponentView());
                    });
                }
            });

            this.liveEditPage.onPageComponentDuplicated((event: PageComponentDuplicatedEvent) => {

                var newPageComponent = new PageComponentDuplicateCommand().
                    setPageRegions(this.pageRegions).
                    setPathToSource(event.getPath()).
                    execute();

                this.saveAndReloadOnlyPageComponent(newPageComponent.getPath(), event.getComponentView());
            });
        }

        private inspectContent(contentId: api.content.ContentId) {
            this.contextWindow.showInspectionPanel(this.contentInspectionPanel);
        }

        private inspectPage() {

            this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);
            this.contextWindow.showInspectionPanel(this.pageInspectionPanel);
        }

        private inspectRegion(regionPath: RegionPath) {

            var region = this.pageRegions.getRegionByPath(regionPath);

            this.regionInspectionPanel.setRegion(region);
            this.contextWindow.showInspectionPanel(this.regionInspectionPanel);
        }

        private inspectComponent(componentPath: ComponentPath) {
            api.util.assertNotNull(componentPath, "componentPath cannot be null");

            var component = this.pageRegions.getComponent(componentPath);
            api.util.assertNotNull(component, "Could not find component: " + componentPath.toString());

            if (component instanceof ImageComponent) {
                this.imageInspectionPanel.setImageComponent(<ImageComponent>component);
                this.contextWindow.showInspectionPanel(this.imageInspectionPanel);
            }
            else if (component instanceof PartComponent) {
                this.partInspectionPanel.setPartComponent(<PartComponent>component);
                this.contextWindow.showInspectionPanel(this.partInspectionPanel);
            }
            else if (component instanceof LayoutComponent) {
                this.layoutInspectionPanel.setLayoutComponent(<LayoutComponent>component);
                this.contextWindow.showInspectionPanel(this.layoutInspectionPanel);
            }
            else {
                throw new Error("PageComponent cannot be selected: " + api.util.getClassName(component));
            }
        }
    }
}