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
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import ImageDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.ImageDescriptorChangedEvent;
    import LayoutDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.LayoutDescriptorChangedEvent;

    import PageComponentBuilder = api.content.page.PageComponentBuilder;
    import ComponentName = api.content.page.ComponentName;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PartComponentBuilder = api.content.page.part.PartComponentBuilder;
    import PartComponent = api.content.page.part.PartComponent;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import LayoutComponentBuilder = api.content.page.layout.LayoutComponentBuilder;

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

        private pageUrl: string;
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
                this.imageDescriptorChanged(event);
            });

            this.partInspectionPanel = new PartInspectionPanel(<PartInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });

            this.layoutInspectionPanel = new LayoutInspectionPanel(<LayoutInspectionPanelConfig>{
                siteTemplate: this.siteTemplate
            });
            this.layoutInspectionPanel.onLayoutDescriptorChanged((event: LayoutDescriptorChangedEvent) => {
                this.layoutDescriptorChanged(event);
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

        private imageDescriptorChanged(event: ImageDescriptorChangedEvent) {
            var path = event.getComponentPath();
            var component = this.liveEditPage.getComponentByPath(path);
            this.setComponentDescriptor(event.getDescriptor(), path, component);
        }

        private layoutDescriptorChanged(event: LayoutDescriptorChangedEvent) {
            var path = event.getComponentPath();
            var component = this.liveEditPage.getComponentByPath(path);
            this.setComponentDescriptor(event.getDescriptor(), path, component);
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

                if (!this.content.isPage()) {
                    // Nothing to set
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

        updateFrameContainerSize(contextWindowPinned:boolean, contextWindowWidth?:number) {
            if (contextWindowPinned && contextWindowWidth) {
                this.frameContainer.getEl().setWidth("calc(100% - " + (contextWindowWidth-1) + "px)");
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
                    component.setDescriptor(null);
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

                var newPath = this.pageRegions.moveComponent(event.getComponentPath(), event.getRegion(), event.getPrecedingComponent());
                if (newPath) {
                    event.getComponent().setComponentPath(newPath.toString());
                }
            });

            this.liveEditPage.onPageComponentAdded((event: PageComponentAddedEvent) => {

                if (!this.pageTemplate) {
                    this.initializePageFromDefault().done(() => {

                        var component = this.addComponent(event.getType(), event.getRegion(), event.getPrecedingComponent());
                        if (component) {
                            event.getElement().getEl().setData("live-edit-component", component.getPath().toString());
                        }
                        event.getElement().getEl().setData("live-edit-type", event.getType());
                    });
                }
                else {
                    var component = this.addComponent(event.getType(), event.getRegion(), event.getPrecedingComponent());
                    if (component) {
                        event.getElement().getEl().setData("live-edit-component", component.getPath().toString());
                    }
                    event.getElement().getEl().setData("live-edit-type", event.getType());
                }
            });

            this.liveEditPage.onImageComponentSetImage((event: ImageComponentSetImageEvent) => {

                if (this.pageTemplate) {

                    this.setImageComponentImage(event.getPath(), event.getImage(), event.getComponentPlaceholder(), event.getImageName());
                }
                else {
                    this.initializePageFromDefault().done(() => {

                        this.setImageComponentImage(event.getPath(), event.getImage(), event.getComponentPlaceholder(),
                            event.getImageName());
                    });
                }

            });

            this.liveEditPage.onPageComponentSetDescriptor((event: PageComponentSetDescriptorEvent) => {

                if (this.pageTemplate) {
                    this.setComponentDescriptor(event.getDescriptor(), event.getPath(), event.getComponentPlaceholder());
                }
                else {
                    this.initializePageFromDefault().done(() => {

                        this.setComponentDescriptor(event.getDescriptor(), event.getPath(), event.getComponentPlaceholder());
                    });
                }
            });

            this.liveEditPage.onPageComponentDuplicated((event: PageComponentDuplicatedEvent) => {

                this.duplicateComponent(event.getType(), event.getRegion(), event.getPath(), event.getPath())
                    .done((component: PageComponent) => {
                        this.pageSkipReload = true;
                        this.contentWizardPanel.saveChanges().done(() => {
                            this.pageSkipReload = false;
                            this.loadComponent(component.getPath(), event.getPlaceholder());
                        });
                    });
            });
        }

        private duplicateComponent(componentType: string, regionPath: RegionPath, precedingPath: ComponentPath, originPath: ComponentPath): Q.Promise<PageComponent> {
            switch (componentType) {
            case "image":
                return this.duplicateImageComponent(componentType, regionPath, precedingPath, originPath);
            case "part":
                return this.duplicatePartComponent(componentType, regionPath, precedingPath, originPath);
            case "layout":
                return this.duplicateLayoutComponent(componentType, regionPath, precedingPath, originPath);
            case "text":
                return this.duplicateTextComponent(componentType, regionPath, precedingPath, originPath);
            }
        }

        private duplicateImageComponent(componentType: string, regionPath: RegionPath, precedingPath: ComponentPath, originPath: ComponentPath): Q.Promise<PageComponent> {

            var deferred = Q.defer<PageComponent>();

            var originComponent:ImageComponent = this.pageRegions.getImageComponent(originPath),
                component:ImageComponent = <ImageComponent>this.addComponent(componentType, regionPath, precedingPath, originComponent.getName().toString());


            if (component != null && originComponent != null) {
                component.setImage(originComponent.getImage());
                if (this.defaultModels.hasImageDescriptor()) {
                    component.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
                }

                deferred.resolve(component);
            } else {
                deferred.reject(null);
            }

            return deferred.promise;
        }

        private duplicatePartComponent(componentType: string, regionPath: RegionPath, precedingPath: ComponentPath, originPath: ComponentPath): Q.Promise<PageComponent> {

            var deferred = Q.defer<PageComponent>();

            var component:PageComponent,
                originComponent:PageComponent = this.pageRegions.getComponent(originPath),
                getDescriptorsRequest = new GetPartDescriptorsByModulesRequest(this.siteTemplate.getModules());

            var originDescriptorName:string = originComponent.getDescriptor().getName().toString();

            return getDescriptorsRequest.sendAndParse().then((results:Descriptor[]): Q.Promise<PageComponent> => {
                var componentDescriptor:Descriptor;
                for (var key in results) {
                    if (results[key].getName().toString() === originDescriptorName) {
                        componentDescriptor = results[key];
                        break;
                    }
                }

                if (componentDescriptor) {
                    component = this.addComponent(componentType, regionPath, precedingPath, componentDescriptor.getName().toString());
                    this.setComponentDescriptorLocally(componentDescriptor, component.getPath());
                    deferred.resolve(component);
                } else {
                    deferred.reject(null);
                }
                return deferred.promise;
            });
        }

        // TODO: TextComponent is not implemented yet. Update, when the implementation is ready.
        private duplicateTextComponent(componentType: string, regionPath: RegionPath, precedingPath: ComponentPath, originPath: ComponentPath): Q.Promise<PageComponent> {

            var deferred = Q.defer<PageComponent>();

            var component:PageComponent = this.addComponent(componentType, regionPath, precedingPath/*, name*/);
            var originComponent:PageComponent = this.pageRegions.getComponent(originPath),
                getDescriptorsRequest;

            return deferred.promise;
        }

        private duplicateLayoutComponent(componentType: string, regionPath: RegionPath, precedingPath: ComponentPath, originPath: ComponentPath): Q.Promise<PageComponent> {

            var deferred = Q.defer<PageComponent>();

            var component:LayoutComponent,
                originComponent:LayoutComponent = this.pageRegions.getLayoutComponent(originPath),
                originDescriptorName:string = originComponent.getDescriptor().getName().toString(),
                getDescriptorsRequest = new GetLayoutDescriptorsByModulesRequest(this.siteTemplate.getModules()),
                componentDescriptor:Descriptor;

            return getDescriptorsRequest.sendAndParse().then((results:Descriptor[]): Q.Promise<PageComponent> => {
                for (var key in results) {
                    if (results[key].getName().toString() === originDescriptorName) {
                        componentDescriptor = results[key];
                        break;
                    }
                }

                if (componentDescriptor) {
                    component = <LayoutComponent>this.addComponent(componentType, regionPath, precedingPath, componentDescriptor.getName().toString())
                    this.setComponentDescriptorLocally(componentDescriptor, component.getPath());
                    deferred.resolve(null);
                } else {
                    deferred.reject(null);
                }

                return deferred.promise;

            }).then((resolvedComponent:PageComponent): Q.Promise<PageComponent> => {
                originComponent.getLayoutRegions().getRegions().forEach((region:api.content.page.region.Region, index) => {
                    var presceding:ComponentPath = null;
                    var regionPath = component.getLayoutRegions().getRegions()[index].getPath();

                    region.getComponents().forEach((pageComponent:PageComponent)=> {
                        var componentType = "";
                        if (pageComponent instanceof ImageComponent) {
                            componentType = "image";
                        } else if (pageComponent instanceof PartComponent) {
                            componentType = "part";
                        } else if (pageComponent instanceof LayoutComponent) {
                            componentType = "layout";
                        } /*else if (pageComponent instanceof TextComponent) {
                            componentType = "text";
                        }*/

                        deferred.promise = deferred.promise.then((resolvedComponent:PageComponent): Q.Promise<PageComponent> => {
                            return this.duplicateComponent(componentType, regionPath, resolvedComponent ? resolvedComponent.getPath() : null, pageComponent.getPath());
                        });
                    });
                });

                return deferred.promise;
            }).then((): Q.Promise<PageComponent> => {
                var defer = Q.defer<PageComponent>();
                defer.resolve(component);

                deferred.promise = deferred.promise.then((resolvedComponent:PageComponent) => {
                    return defer.promise;
                });

                return deferred.promise;
            });


        }

        private addComponent(componentType: string, regionPath: RegionPath, precedingComponent: ComponentPath, wantedName?:string): PageComponent {

            wantedName = api.util.capitalize(api.util.removeInvalidChars(wantedName || componentType));
            var componentName = this.pageRegions.ensureUniqueComponentName(regionPath, new ComponentName(wantedName));

            var builder: PageComponentBuilder<PageComponent>;
            switch (componentType) {
            case "image":
                builder = new ImageComponentBuilder();
                break;
            case "part":
                builder = new PartComponentBuilder();
                break;
            case "layout":
                builder = new LayoutComponentBuilder();
                break;
            case "text":
                // TODO: Implement text
                builder = null;
                break;
            }
            if (builder) {
                builder.setName(componentName);
                builder.setConfig(new api.data.RootDataSet());
                var component = builder.build();
                this.pageRegions.addComponentAfter(component, regionPath, precedingComponent);
                return component;
            }
            else {
                return null;
            }
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

            var component = this.pageRegions.getComponent(componentPath);
            api.util.assertNotNull(component, "Could not find component: " + componentPath.toString());

            if (component instanceof ImageComponent) {
                this.imageInspectionPanel.setImageComponent(<ImageComponent>component);
                this.contextWindow.showInspectionPanel(this.imageInspectionPanel);
            }
            else if (component instanceof PartComponent) {
                this.partInspectionPanel.setPartComponent(<PartComponent>component)
                this.contextWindow.showInspectionPanel(this.partInspectionPanel);
            }
            else if (component instanceof LayoutComponent) {
                this.layoutInspectionPanel.setLayoutComponent(<LayoutComponent>component)
                this.contextWindow.showInspectionPanel(this.layoutInspectionPanel);
            }
            else {
                throw new Error("PageComponent cannot be selected: " + api.util.getClassName(component));
            }
        }

        private setComponentDescriptor(descriptor: Descriptor, componentPath: ComponentPath, componentPlaceholder) {
            var component = this.pageRegions.getComponent(componentPath);
            if (!component || !descriptor) {
                return;
            }

            this.updateComponentName(descriptor.getName().toString(), componentPath);
            componentPath = component.getPath();

            this.contextWindow.hide();

            (<any>componentPlaceholder).showLoadingSpinner(); // TODO: Remove any-casting - or why is'nt this done in LiveEdit?
            component.setDescriptor(descriptor.getKey());

            // use of "instanceof" does not work because the descriptor object has been created in another frame
            var isLayoutDescriptor = (descriptor instanceof LayoutDescriptor) ||
                                     (<any>descriptor).constructor.name === 'LayoutDescriptor';
            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> descriptor;
                var layoutComponent = <LayoutComponent>component;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }

            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().done(() => {
                this.pageSkipReload = false;
                this.loadComponent(componentPath, componentPlaceholder);
            });
        }

        private setComponentDescriptorLocally(descriptor: Descriptor, componentPath: ComponentPath) {
            var component = this.pageRegions.getComponent(componentPath);
            if (!component || !descriptor) {
                return;
            }

            component.setDescriptor(descriptor.getKey());

            // use of "instanceof" does not work because the descriptor object has been created in another frame
            var isLayoutDescriptor = (descriptor instanceof LayoutDescriptor) ||
                (<any>descriptor).constructor.name === 'LayoutDescriptor';
            if (isLayoutDescriptor) {
                var layoutDescriptor = <LayoutDescriptor> descriptor;
                var layoutComponent = <LayoutComponent>component;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }
        }

        private updateComponentName(name: string, componentPath: ComponentPath, isForced?: boolean) {

            var component = this.pageRegions.getComponent(componentPath),
                type = this.liveEditPage.getComponentByPath(componentPath).getEl().getData("live-edit-type");

            if (!component || (!isForced && type == "image")) {
                return;
            }
            var removedComponent = this.pageRegions.removeComponent(componentPath);
            var newComponentEl = this.liveEditPage.getComponentByPath(componentPath);
            var newComponentName = this.pageRegions.ensureUniqueComponentName(component.getPath().getRegionPath(),
                new ComponentName(api.util.removeInvalidChars(api.util.capitalizeAll(name))));
            component.setName(newComponentName);
            var levels = component.getPath().getLevels();
            levels[levels.length - 1] = new ComponentPathRegionAndComponent(levels[levels.length - 1].getRegionName(), newComponentName);
            component.setPath(new ComponentPath(levels));
            newComponentEl.getEl().setData("live-edit-component", component.getPath().toString());

            if (removedComponent) {
                this.pageRegions.addComponentAfter(removedComponent, component.getPath().getRegionPath(),
                    ComponentPath.fromString(newComponentEl.getPrecedingComponentPath()));
            }

        }

        private setImageComponentImage(componentPath: ComponentPath, image: ContentId, componentPlaceholder: api.dom.Element,
                                       imageName?: string) {

            var imageComponent = this.pageRegions.getImageComponent(componentPath);
            if (imageComponent != null) {
                imageComponent.setImage(image);
                if (this.defaultModels.hasImageDescriptor()) {
                    imageComponent.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
                }
                this.updateComponentName(imageName, componentPath, true);
                componentPath = imageComponent.getPath();

                this.pageSkipReload = true;
                this.contentWizardPanel.saveChanges().done(() => {
                    this.pageSkipReload = false;
                    this.loadComponent(componentPath, componentPlaceholder);
                })

            }
            else {
                api.notify.showWarning("ImageComponent to set image on not found: " + componentPath);
            }
        }

        private addLayoutRegions(layoutComponent: LayoutComponent, layoutDescriptor: LayoutDescriptor) {
            var sourceRegions: LayoutRegions = layoutComponent.getLayoutRegions();
            var mergedRegions: LayoutRegions = sourceRegions.mergeRegions(layoutDescriptor.getRegions(), layoutComponent.getPath());
            layoutComponent.setLayoutRegions(mergedRegions);
        }

        private loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element) {
            this.liveEditPage.loadComponent(componentPath, componentPlaceholder, this.content);
        }
    }
}