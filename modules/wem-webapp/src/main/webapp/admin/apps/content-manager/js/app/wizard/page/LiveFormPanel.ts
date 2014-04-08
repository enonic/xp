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
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import ImageDescriptorChangedEvent = app.wizard.page.contextwindow.inspect.ImageDescriptorChangedEvent;

    import PageComponentBuilder = api.content.page.PageComponentBuilder;
    import ComponentName = api.content.page.ComponentName;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PartComponentBuilder = api.content.page.part.PartComponentBuilder;
    import PartComponent = api.content.page.part.PartComponent;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import LayoutComponentBuilder = api.content.page.layout.LayoutComponentBuilder;

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

            this.inspectionPanel = new InspectionPanel(<InspectionPanelConfig>{
                contentInspectionPanel: this.contentInspectionPanel,
                pageInspectionPanel: this.pageInspectionPanel,
                regionInspectionPanel: this.regionInspectionPanel,
                imageInspectionPanel: this.imageInspectionPanel,
                partInspectionPanel: this.partInspectionPanel,
                layoutInspectionPanel: this.layoutInspectionPanel
            });

            this.frameContainer = new api.ui.Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frameContainer.appendChild(this.liveEditPage.getIFrame());

            // append it here in order for the context window to be above
            this.appendChild(this.liveEditPage.getLoadMask());


            this.contextWindow = new ContextWindow(<ContextWindowConfig>{
                liveEditPage: this.liveEditPage,
                liveFormPanel: this,
                inspectionPanel: this.inspectionPanel
            });

            this.appendChild(this.contextWindow);

            this.pageInspectionPanel.onPageTemplateChanged((event: app.wizard.page.contextwindow.inspect.PageTemplateChangedEvent) => {

                var selectedPageTemplate = event.getPageTemplate();
                if (selectedPageTemplate) {

                    new api.content.page.GetPageTemplateByKeyRequest(selectedPageTemplate.getKey()).
                        setSiteTemplateKey(this.siteTemplate.getKey()).
                        sendAndParse().
                        done((pageTemplate: PageTemplate) => {

                            this.pageTemplate = pageTemplate;

                            this.pageRegions = this.resolvePageRegions(this.content, this.pageTemplate);
                            this.pageConfig = this.resolvePageConfig(this.content, this.pageTemplate);

                            new GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).
                                sendAndParse().
                                done((pageDescriptor: PageDescriptor) => {

                                    this.pageDescriptor = pageDescriptor;
                                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                                    this.saveAndReloadPage();
                                });
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

        loadPageIfNotLoaded(): Q.Promise<void> {

            console.log("LiveFormPanel.loadPageIfNotLoaded() this.needsReload = " + this.pageNeedsReload);
            var deferred = Q.defer<void>();

            if (this.pageNeedsReload && !this.pageLoading) {

                this.pageLoading = true;
                this.liveEditPage.load(this.content).then(()=> {

                    this.pageLoading = false;
                    this.pageNeedsReload = false;

                    this.loadPageDescriptor().done(() => {

                        deferred.resolve(null);
                    });

                }).catch((reason)=> {
                    deferred.reject(reason);
                }).done();
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        setPage(content: Content, pageTemplate: PageTemplate): Q.Promise<void> {

            var deferred = Q.defer<void>();

            api.util.assertNotNull(content, "Expected content not be null");

            this.content = content;
            var pageTemplateChanged = this.resolvePageTemplateChanged(pageTemplate);
            this.pageTemplate = pageTemplate;

            if (!this.pageSkipReload) {

                if (pageTemplateChanged && this.pageTemplate) {

                    this.pageRegions = this.pageTemplate.getRegions();
                    this.pageConfig = this.pageTemplate.getConfig();
                }
                else if (pageTemplateChanged && !this.pageTemplate) {

                    this.pageRegions = this.defaultModels.getPageTemplate().getRegions();
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                }
                else {
                    this.pageRegions = this.resolvePageRegions(content, pageTemplate);
                    this.pageConfig = this.resolvePageConfig(content, pageTemplate);
                }
            }

            if (!this.isVisible()) {

                this.pageNeedsReload = true;
                deferred.resolve(null);
            }
            else if (this.pageSkipReload == true) {
                deferred.resolve(null);
            }
            else {
                this.liveEditPage.load(this.content).
                    then(() => {

                        this.loadPageDescriptor().then(() => {

                            deferred.resolve(null);

                        }).catch((reason) => {
                            deferred.reject(reason);
                        }).done();
                    }).catch((reason)=> {
                        deferred.reject(reason);
                    }).done();
            }


            return deferred.promise;
        }

        private loadPageDescriptor(): Q.Promise<void> {

            var deferred = Q.defer<void>();
            if (!this.pageTemplate) {
                deferred.resolve(null);
            }
            else {
                new GetPageDescriptorByKeyRequest(this.pageTemplate.getDescriptorKey()).sendAndParse().
                    then((pageDescriptor: PageDescriptor) => {
                        this.pageDescriptor = pageDescriptor;
                        deferred.resolve(null);
                    }).catch((reason) => {
                        deferred.reject(reason);
                    }).done();
            }

            return deferred.promise;
        }

        private resolvePageTemplateChanged(pageTemplate: PageTemplate) {

            if (this.pageTemplate == undefined) {
                // initially pageTemplate is not changed
                return false;
            }

            if (!pageTemplate && this.pageTemplate == null) {
                return false;
            }
            else if (!pageTemplate && this.pageTemplate != null) {
                return true;
            }
            else if (pageTemplate && this.pageTemplate == null) {
                return true;
            }
            else if (this.pageTemplate.getKey().toString() != pageTemplate.getKey().toString()) {
                return true;
            }
            else {
                return false;
            }
        }

        private resolvePageRegions(content: Content, pageTemplate: PageTemplate): PageRegions {

            if (!pageTemplate) {
                return this.defaultModels.getPageTemplate().getRegions();
            }

            if (content.isPage() && content.getPage().hasRegions()) {
                return content.getPage().getRegions();
            }
            else {
                return pageTemplate.getRegions();
            }
        }

        private resolvePageConfig(content: Content, pageTemplate: PageTemplate): RootDataSet {

            if (!pageTemplate) {
                return this.defaultModels.getPageTemplate().getConfig();
            }

            if (content.isPage() && content.getPage().hasConfig()) {
                return content.getPage().getConfig();
            }
            else {
                return pageTemplate.getConfig();
            }
        }

        private initializePageFromDefault(): Q.Promise<void> {

            var deferred = Q.defer<void>();

            var defaultPageTemplate = this.defaultModels.getPageTemplate();

            new GetPageDescriptorByKeyRequest(defaultPageTemplate.getDescriptorKey()).sendAndParse().
                then((pageDescriptor: PageDescriptor) => {

                    this.pageTemplate = defaultPageTemplate;
                    this.pageConfig = defaultPageTemplate.getConfig();
                    this.pageRegions = defaultPageTemplate.getRegions();
                    this.pageDescriptor = pageDescriptor;

                    this.pageInspectionPanel.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                    deferred.resolve(null);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        private saveAndReloadPage() {
            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().done(() => {
                this.pageSkipReload = false;
                this.liveEditPage.load(this.content);
            });
        }

        resizeFrameContainer(width: number) {
            this.frameContainer.getEl().setWidthPx(width);
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

                console.log("LiveFormPanel.liveEditPage.onPageSelected");

                this.inspectPage();
            });

            this.liveEditPage.onRegionSelected((event: RegionSelectedEvent) => {

                console.log("LiveFormPanel.liveEditPage.onRegionSelected");

                this.inspectRegion(event.getPath());
            });

            this.liveEditPage.onPageComponentSelected((event: PageComponentSelectedEvent) => {

                console.log("LiveFormPanel.liveEditPage.onPageComponentSelected");

                if (event.isComponentEmpty()) {
                    this.contextWindow.hide();
                }
                else {
                    this.contextWindow.show();
                }

                this.inspectComponent(event.getPath());
            });

            this.liveEditPage.onDeselect((event: DeselectEvent) => {

                console.log("LiveFormPanel.liveEditPage.onDeselect");

                this.contextWindow.show();
                this.contextWindow.clearSelection();
            });

            this.liveEditPage.onComponentRemoved((event: ComponentRemovedEvent) => {

                console.log("LiveFormPanel.liveEditPage.onComponentRemoved");

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

            this.liveEditPage.onComponentReset((event: ComponentResetEvent) => {

                console.log("LiveFormPanel.liveEditPage.onComponentReset");

                var component = this.pageRegions.getComponent(event.getPath());
                if (component) {
                    component.setDescriptor(null);
                }
            });

            this.liveEditPage.onSortableStart((event: SortableStartEvent) => {

                console.log("LiveFormPanel.liveEditPage.onSortableStart");

                this.contextWindow.hide();
            });

            this.liveEditPage.onSortableStop((event: SortableStopEvent) => {

                console.log("LiveFormPanel.liveEditPage.onSortableStop");

                if (event.getPath()) {

                    if (!event.isEmpty()) {
                        this.contextWindow.show();
                    }
                }
                else {
                    this.contextWindow.show();
                }
            });

            this.liveEditPage.onSortableUpdate((event: SortableUpdateEvent) => {

                console.log("LiveFormPanel.liveEditPage.onSortableUpdate");

                var newPath = this.pageRegions.moveComponent(event.getComponentPath(), event.getRegion(), event.getPrecedingComponent());
                if (newPath) {
                    event.getComponent().setComponentPath(newPath.toString());
                }
            });

            this.liveEditPage.onPageComponentAdded((event: PageComponentAddedEvent) => {

                console.log("LiveFormPanel.liveEditPage.onPageComponentAdded");

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

            this.liveEditPage.onSetImageComponentImage((event: SetImageComponentImageEvent) => {

                console.log("LiveFormPanel.liveEditPage.onSetImageComponentImage");

                (<any>event.getComponentPlaceholder()).showLoadingSpinner(); // TODO: Remove any

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

            this.liveEditPage.onSetPageComponentDescriptor((event: SetPageComponentDescriptorEvent) => {

                console.log("LiveFormPanel.liveEditPage.onSetPageComponentDescriptor");

                if (this.pageTemplate) {
                    this.setComponentDescriptor(event.getDescriptor(), event.getPath(), event.getComponentPlaceholder());
                }
                else {
                    this.initializePageFromDefault().done(() => {

                        this.setComponentDescriptor(event.getDescriptor(), event.getPath(), event.getComponentPlaceholder());
                    });
                }
            });
        }

        private addComponent(componentType: string, regionPath: RegionPath, precedingComponent: ComponentPath): PageComponent {

            var componentName = this.pageRegions.ensureUniqueComponentName(regionPath,
                new ComponentName(api.util.capitalize(componentType)));

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
                //TODO: Implement text
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
                console.log("ImageComponent to set image on not found: " + componentPath);
            }
        }

        private addLayoutRegions(layoutComponent: LayoutComponent, layoutDescriptor: LayoutDescriptor) {
            var layoutRegionsBuilder = new api.content.page.layout.LayoutRegionsBuilder();
            layoutDescriptor.getRegions().forEach(function (regionDescriptor: RegionDescriptor) {

                var regionPath = new RegionPath(layoutComponent.getPath(), regionDescriptor.getName());
                var layoutRegion = new api.content.page.region.RegionBuilder().
                    setName(regionDescriptor.getName()).
                    setPath(regionPath).
                    build();
                layoutRegionsBuilder.addRegion(layoutRegion);
            });
            layoutComponent.setLayoutRegions(layoutRegionsBuilder.build());
        }

        private loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element) {
            this.liveEditPage.loadComponent(componentPath, componentPlaceholder, this.content);
        }
    }
}