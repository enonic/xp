module app.wizard.page {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
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
    import TextComponent = api.content.page.text.TextComponent;
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

    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
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

        siteTemplate:SiteTemplate;

        contentType:ContentTypeName;

        contentWizardPanel:ContentWizardPanel;

        defaultModels: DefaultModels;
    }

    export class LiveFormPanel extends api.ui.panel.Panel {

        private siteTemplate: SiteTemplate;

        private defaultModels: DefaultModels;

        private content: Content;
        private pageTemplate: PageTemplate;
        private pageRegions: PageRegions;
        private pageConfig: RootDataSet;
        private pageDescriptor: PageDescriptor;

        private pageLoading: boolean;

        private pageSkipReload: boolean;
        private frameContainer: Panel;

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

        private liveEditPage: LiveEditPageProxy;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.siteTemplate = config.siteTemplate;
            this.defaultModels = config.defaultModels;

            this.pageLoading = false;
            this.pageSkipReload = false;

            this.liveEditPage = new LiveEditPageProxy(<LiveEditPageProxyConfig>{
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

                var imageView: ImageComponentView = event.getImageComponentView();
                var command = new PageComponentSetDescriptorCommand().
                    setPageComponentView(imageView).
                    setPageRegions(this.pageRegions).
                    setDescriptor(event.getDescriptor());
                command.execute();
                this.saveAndReloadOnlyPageComponent(imageView);
            });

            this.partInspectionPanel = new PartInspectionPanel(<PartInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });

            this.layoutInspectionPanel = new LayoutInspectionPanel(<LayoutInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });

            this.layoutInspectionPanel.onLayoutDescriptorChanged((event: LayoutDescriptorChangedEvent) => {

                var layoutView = event.getLayoutComponentView();
                var command = new PageComponentSetDescriptorCommand().
                    setPageComponentView(layoutView).
                    setPageRegions(this.pageRegions).
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

            var contextWindowController = new app.wizard.page.contextwindow.ContextWindowController(this.contextWindow,
                this.contentWizardPanel.getContextWindowToggler());

            this.pageInspectionPanel.onPageTemplateChanged((event: app.wizard.page.contextwindow.inspect.PageTemplateChangedEvent) => {

                var selectedPageTemplate = event.getPageTemplate();
                if (selectedPageTemplate) {

                    new api.content.page.GetPageTemplateByKeyRequest(selectedPageTemplate.getKey()).
                        setSiteTemplateKey(this.siteTemplate.getKey()).
                        sendAndParse().
                        then((pageTemplate: PageTemplate) => {

                            this.pageTemplate = pageTemplate;

                            this.setPageRegions(this.resolvePageRegions(this.content, this.pageTemplate));
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
                    this.setPageRegions(this.defaultModels.getPageTemplate().getRegions());
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                    this.pageInspectionPanel.setPage(this.content, null, null, this.pageConfig);

                    this.saveAndReloadPage();
                }
            });

            this.liveEditListen();
        }

        private setPageRegions(pageRegions: PageRegions) {
            this.pageRegions = pageRegions;
            this.liveEditPage.setPageRegions(pageRegions);
        }

        remove() {

            this.liveEditPage.remove();
            super.remove();
        }

        layout(content: Content, pageTemplate: PageTemplate): wemQ.Promise<void> {

            api.util.assertNotNull(content, "Expected content not be null");

            this.content = content;
            this.pageTemplate = pageTemplate;

            if (!this.pageSkipReload) {

                if (!this.pageRegions && !this.content.isPage()) {
                    this.setPageRegions(this.defaultModels.getPageTemplate().getRegions());
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                }
                else if (!this.pageRegions && this.content.isPage()) {

                    this.setPageRegions(content.getPage().getRegions());
                    this.pageConfig = content.getPage().getConfig();
                }
            }

            return this.loadPage();
        }

        loadPage(): wemQ.Promise<void> {

            if (this.pageSkipReload == true) {
                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;

            } else if (!this.isVisible()) {
                var shownListener = (event: api.dom.ElementShownEvent) => {
                    this.loadPage();
                    this.unShown(shownListener);
                };
                this.onShown(shownListener);
                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;

            } else if (!this.pageLoading) {
                this.pageLoading = true;
                return this.liveEditPage.load(this.content).then(()=> {

                    this.pageLoading = false;

                    return this.loadPageDescriptor();

                }).then((): void => {

                    this.contextWindow.showInspectionPanel(this.pageInspectionPanel);
                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                });

            } else {
                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private loadPageDescriptor(): wemQ.Promise<void> {

            if (!this.pageTemplate) {
                var deferred = wemQ.defer<void>();
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

        private initializePageFromDefault(): wemQ.Promise<void> {

            var defaultPageTemplate = this.defaultModels.getPageTemplate();

            return new GetPageDescriptorByKeyRequest(defaultPageTemplate.getDescriptorKey()).sendAndParse().
                then((pageDescriptor: PageDescriptor): void => {

                    this.pageTemplate = defaultPageTemplate;
                    this.pageConfig = defaultPageTemplate.getConfig();
                    this.setPageRegions(defaultPageTemplate.getRegions());
                    this.pageDescriptor = pageDescriptor;

                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                });
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
            this.contentWizardPanel.saveChanges().
                then(() => {
                    this.pageSkipReload = false;
                    pageComponentView.showLoadingSpinner();

                    this.liveEditPage.loadComponent(pageComponentView, this.content);
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();
        }

        updateFrameContainerSize(contextWindowShown: boolean, contextWindowWidth?: number) {
            if (contextWindowShown && contextWindowWidth) {
                this.frameContainer.getEl().setWidth("calc(100% - " + (contextWindowWidth - 1) + "px)");
            } else {
                this.frameContainer.getEl().setWidth("100%");
            }
        }

        public getFrameContainer(): Panel {
            return this.frameContainer;
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

            this.liveEditPage.onPageSelected((event: PageSelectEvent) => {

                this.inspectPage(event.getPageView());
            });

            this.liveEditPage.onRegionSelected((event: RegionSelectEvent) => {

                this.inspectRegion(event.getRegionView());
            });

            this.liveEditPage.onPageComponentSelected((event: ItemViewSelectedEvent) => {

                var itemView = event.getItemView();

                if (itemView.isEmpty() || api.ObjectHelper.iFrameSafeInstanceOf(itemView, TextComponentView)) {
                    this.contextWindow.hide();
                }
                else {
                    this.contextWindow.show();
                }

                if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                    this.inspectPageComponent(<PageComponentView<PageComponent>>itemView);
                }
            });

            this.liveEditPage.onDeselect((event: ItemViewDeselectEvent) => {

                this.contextWindow.show();
                this.contextWindow.clearSelection();
            });

            this.liveEditPage.onPageComponentRemoved((event: PageComponentRemoveEvent) => {

                this.contextWindow.show();

                wemQ(!this.pageTemplate ? this.initializePageFromDefault() : null).
                    then(() => {
                        event.getPageComponentView().getPageComponent().removeFromParent();
                        this.contextWindow.clearSelection();
                    }).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    done();

            });

            this.liveEditPage.onPageComponentReset((event: PageComponentResetEvent) => {

                wemQ(!this.pageTemplate ? this.initializePageFromDefault() : null).
                    then(() => {
                        var component: PageComponent = event.getComponentView().getPageComponent();
                        if (component) {
                            component.reset();
                        }
                    }).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    done();
            });

            this.liveEditPage.onDraggingPageComponentViewStartedEvent((event: DraggingPageComponentViewStartedEvent) => {

                this.contextWindow.hide();
            });

            this.liveEditPage.onDraggingPageComponentViewCompleted((event: DraggingPageComponentViewCompletedEvent) => {

                var pageComponentView = event.getPageComponentView();
                if (!pageComponentView.isEmpty()) {
                    this.contextWindow.show();
                    this.inspectPageComponent(pageComponentView);
                }
            });

            this.liveEditPage.onDraggingPageComponentViewCanceled((event: DraggingPageComponentViewCanceledEvent) => {
                this.contextWindow.show();
            });

            this.liveEditPage.onPageComponentAdded((event: PageComponentAddedEvent) => {

                if (!this.pageTemplate) {
                    this.initializePageFromDefault().
                        catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                        done();
                }
            });

            this.liveEditPage.onImageComponentSetImage((event: ImageComponentSetImageEvent) => {

                var command = new ImageComponentSetImageCommand().
                    setDefaultModels(this.defaultModels).
                    setPageRegions(this.pageRegions).
                    setImage(event.getImageId()).
                    setPageComponentView(event.getImageComponentView()).
                    setImageName(event.getImageName());

                wemQ(!this.pageTemplate ? this.initializePageFromDefault() : null).
                    then(() => {
                        command.execute();
                        this.saveAndReloadOnlyPageComponent(event.getImageComponentView());
                    }).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    done();
            });

            this.liveEditPage.onPageComponentSetDescriptor((event: PageComponentSetDescriptorEvent) => {

                var command = new PageComponentSetDescriptorCommand().
                    setPageComponentView(event.getPageComponentView()).
                    setPageRegions(this.pageRegions).
                    setDescriptor(event.getDescriptor());

                wemQ(!this.pageTemplate ? this.initializePageFromDefault() : null).
                    then(() => {
                        command.execute();
                        this.saveAndReloadOnlyPageComponent(event.getPageComponentView());
                    }).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    done();
            });

            this.liveEditPage.onPageComponentDuplicated((event: PageComponentDuplicateEvent) => {

                this.saveAndReloadOnlyPageComponent(event.getDuplicatedPageComponentView());
            });
        }

        private inspectContent(contentId: api.content.ContentId) {
            this.contextWindow.showInspectionPanel(this.contentInspectionPanel);
        }

        private inspectPage(pageView: PageView) {

            this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);
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