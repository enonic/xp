module app.wizard.page {

    import ModuleKey = api.module.ModuleKey;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;
    import Content = api.content.Content;
    import ComponentPath = api.content.page.ComponentPath;
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
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import GetImageDescriptorsByModulesRequest = api.content.page.image.GetImageDescriptorsByModulesRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;

    import PageComponentBuilder = api.content.page.PageComponentBuilder;
    import ComponentName = api.content.page.ComponentName;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PartComponentBuilder = api.content.page.part.PartComponentBuilder;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import LayoutComponentBuilder = api.content.page.layout.LayoutComponentBuilder;

    import ContextWindow = app.wizard.page.contextwindow.ContextWindow;


    export interface LiveFormPanelConfig {

        siteTemplate:SiteTemplate;
        contentWizardPanel:ContentWizardPanel;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private siteTemplate: SiteTemplate;
        private initialized: boolean;
        private defaultPageTemplate: PageTemplate;
        private defaultImageDescriptor: ImageDescriptor;
        private defaultPartDescriptor: PartDescriptor;
        private defaultLayoutDescriptor: LayoutDescriptor;

        private content: Content;
        private pageTemplate: PageTemplate;
        private pageRegions: PageRegions;
        private pageConfig: RootDataSet;
        private pageDescriptor: PageDescriptor;

        private pageNeedsReload: boolean;
        private pageLoading: boolean;

        private pageSkipReload: boolean;
        private frame: api.dom.IFrameEl;
        private baseUrl: string;

        private pageUrl: string;
        private contextWindow: ContextWindow;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;

        private contentWizardPanel: ContentWizardPanel;

        private mask: api.ui.LoadMask;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.siteTemplate = config.siteTemplate;

            this.initialized = false;

            this.pageNeedsReload = true;
            this.pageLoading = false;
            this.pageSkipReload = false;

            this.baseUrl = api.util.getUri("portal/edit/");

            this.frame = new api.dom.IFrameEl("live-edit-frame");
            this.appendChild(this.frame);

            this.mask = new api.ui.LoadMask(this.frame);
            // append it here in order for the context window to be above
            this.appendChild(this.mask);

            ShowContentFormEvent.on(() => {
                if (this.mask.isVisible()) {
                    this.mask.hide();
                }
            });
        }

        private initialize(): Q.Promise<void> {
            var deferred = Q.defer<void>();

            if (!this.initialized) {

                var siteModules = this.siteTemplate.getModules();

                var defaultPageTemplatePromise = new GetDefaultPageTemplateRequest(this.siteTemplate.getKey(),
                    this.content.getType()).sendAndParse();
                var defaultImageDescrResolved = this.resolveDefaultImageDescriptor(siteModules);
                var defaultPartDescrResolved = this.resolveDefaultPartDescriptor(siteModules);
                var defaultLayoutDescrResolved = this.resolveDefaultLayoutDescriptor(siteModules);

                var allPromises: Q.Promise<any>[] = [
                    defaultPageTemplatePromise,
                    defaultImageDescrResolved,
                    defaultPartDescrResolved,
                    defaultLayoutDescrResolved];

                defaultPageTemplatePromise.done((defaultPageTemplate: PageTemplate)=> {
                    this.defaultPageTemplate = defaultPageTemplate;
                });

                defaultImageDescrResolved.done((imageDescriptor: ImageDescriptor)=> {
                    this.defaultImageDescriptor = imageDescriptor;
                });

                defaultPartDescrResolved.done((partDescriptor: PartDescriptor)=> {
                    this.defaultPartDescriptor = partDescriptor;
                });

                defaultLayoutDescrResolved.done((layoutDescriptor: LayoutDescriptor)=> {
                    this.defaultLayoutDescriptor = layoutDescriptor;
                });

                Q.allSettled(allPromises).then((results: Q.PromiseState<any>[])=> {
                    this.initialized = true;

                    if (!this.pageTemplate) {
                        this.pageConfig = this.defaultPageTemplate.getConfig();
                        this.pageRegions = this.defaultPageTemplate.getRegions();
                    }

                    deferred.resolve(null);
                });
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        loadPageIfNotLoaded(): Q.Promise<void> {

            console.log("LiveFormPanel.loadPageIfNotLoaded() this.needsReload = " + this.pageNeedsReload);
            var deferred = Q.defer<void>();

            this.initialize().done(() => {

                if (this.pageNeedsReload && !this.pageLoading) {

                    this.pageLoading = true;
                    this.doLoad().then(()=> {

                        this.pageLoading = false;
                        this.pageNeedsReload = false;

                        this.setupContextWindow();
                        deferred.resolve(null);
                    }).catch(()=> {
                        console.log("Error while loading page: ", arguments);
                        deferred.reject(arguments);
                    }).done();
                }
                else {
                    deferred.resolve(null);
                }
            });

            return deferred.promise;
        }


        private doLoad(): Q.Promise<void> {

            console.log("LiveFormPanel.doLoad() ... url: " + this.pageUrl);
            api.util.assertNotNull(this.pageUrl, "No page to load");

            this.mask.show();
            this.frame.setSrc(this.pageUrl);

            var deferred = Q.defer<void>();
            this.frame.onLoaded((event: UIEvent) => {
                var liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
                if (liveEditWindow && liveEditWindow.$liveEdit && typeof(liveEditWindow.initializeLiveEdit) === "function") {
                    // Give loaded page same CONFIG.baseUri as in admin
                    liveEditWindow.CONFIG = {};
                    liveEditWindow.CONFIG.baseUri = CONFIG.baseUri;
                    liveEditWindow.siteTemplate = this.siteTemplate;
                    liveEditWindow.content = this.content;

                    this.mask.hide();
                    liveEditWindow.initializeLiveEdit();
                    deferred.resolve(null);
                }
            });

            return deferred.promise;
        }

        setPage(content: Content, pageTemplate: PageTemplate) {

            console.log("LiveFormPanel.setPage() ...");

            api.util.assertNotNull(content, "Expected content not be null");

            this.content = content;
            var pageTemplateChanged = this.resolvePageTemplateChanged(pageTemplate);
            this.pageTemplate = pageTemplate;

            this.pageUrl = this.baseUrl + content.getContentId().toString();

            if (!this.pageSkipReload) {

                if (pageTemplateChanged && this.pageTemplate) {

                    this.pageRegions = this.pageTemplate.getRegions();
                    this.pageConfig = this.pageTemplate.getConfig();
                }
                else if (pageTemplateChanged && !this.pageTemplate) {

                    this.pageRegions = null;
                    this.pageConfig = null;
                }
                else {
                    this.pageRegions = this.resolvePageRegions(content, pageTemplate);
                    this.pageConfig = this.resolvePageConfig(content, pageTemplate);
                }
                if (pageTemplate) {
                    new GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).sendAndParse().
                        done((pageDescriptor: PageDescriptor) => {
                            this.pageDescriptor = pageDescriptor;
                        });
                }
            }

            if (!this.isVisible()) {

                this.pageNeedsReload = true;
                console.log("LiveFormPanel.setPage() ... not visible, returning");
                return;
            }

            if (this.pageSkipReload == true) {
                console.log("LiveFormPanel.setPage() ... skipReload is true, returning");
                return;
            }

            this.doLoad().
                then(() => {

                    this.setupContextWindow();
                }).fail(()=> {
                    console.log("Error while loading page: ", arguments);
                }).done();
        }

        private resolvePageTemplateChanged(pageTemplate: PageTemplate) {
            if (!pageTemplate && !this.pageTemplate) {
                return false;
            }
            else if (!pageTemplate && this.pageTemplate) {
                return true;
            }
            else if (pageTemplate && !this.pageTemplate) {
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
                return null;
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
                return null;
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

            new GetPageDescriptorByKeyRequest(this.defaultPageTemplate.getDescriptorKey()).sendAndParse().
                done((pageDescriptor: PageDescriptor) => {

                    this.pageTemplate = this.defaultPageTemplate;
                    this.pageConfig = this.defaultPageTemplate.getConfig();
                    this.pageRegions = this.defaultPageTemplate.getRegions();
                    this.pageDescriptor = pageDescriptor;

                    this.contextWindow.setPage(this.content, this.pageTemplate, pageDescriptor);

                    deferred.resolve(null);
                });

            return deferred.promise;
        }

        private setupContextWindow() {

            if (this.contextWindow) {
                // Have to remove previous ContextWindow to avoid two
                // TODO: ContextWindow should be resued with new values instead
                this.contextWindow.remove();
            }

            this.contextWindow = this.createContextWindow();

            this.appendChild(this.contextWindow);

            this.liveEditListen();
        }

        private createContextWindow(): ContextWindow {

            this.liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
            this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

            var contextWindow = new ContextWindow({
                liveEditIFrame: this.frame,
                siteTemplate: this.siteTemplate,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                liveFormPanel: this,
                contentType: this.content.getType()
            });

            return contextWindow;
        }

        public getPageTemplate(): PageTemplateKey {

            if (!this.contextWindow) {
                return this.content.isPage() ? this.content.getPage().getTemplate() : null;
            }

            return this.contextWindow.getPageTemplate();
        }

        public getRegions(): PageRegions {

            if (!this.contextWindow) {
                return this.content.isPage() ? this.content.getPage().getRegions() : null;
            }

            return this.pageRegions;
        }

        public getConfig(): RootDataSet {

            if (!this.contextWindow) {
                return this.content.isPage() ? this.content.getPage().getConfig() : null;
            }

            return this.contextWindow.getPageConfig();
        }

        getDefaultImageDescriptor(): ImageDescriptor {
            return this.defaultImageDescriptor;
        }

        getDefaultPartDescriptor(): PartDescriptor {
            return this.defaultPartDescriptor;
        }

        getDefaultLayoutDescriptor(): LayoutDescriptor {
            return this.defaultLayoutDescriptor;
        }

        private resolveDefaultImageDescriptor(moduleKeys: ModuleKey[]): Q.Promise<ImageDescriptor> {

            var d = Q.defer<ImageDescriptor>();
            new GetImageDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((imageDescriptors: ImageDescriptor[]) => {
                    if (imageDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(imageDescriptors[0]);
                    }
                });
            return d.promise;
        }

        private resolveDefaultPartDescriptor(moduleKeys: ModuleKey[]): Q.Promise<PartDescriptor> {

            var d = Q.defer<PartDescriptor>();
            new GetPartDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((partDescriptors: PartDescriptor[]) => {
                    if (partDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(partDescriptors[0]);
                    }
                });
            return d.promise;
        }

        private resolveDefaultLayoutDescriptor(moduleKeys: ModuleKey[]): Q.Promise<LayoutDescriptor> {

            var d = Q.defer<LayoutDescriptor>();
            new GetLayoutDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((layoutDescriptors: LayoutDescriptor[]) => {
                    if (layoutDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(layoutDescriptors[0]);
                    }
                });
            return d.promise;
        }

        private onComponentSelected(pathAsString: string) {
            var componentPath = ComponentPath.fromString(pathAsString);
            var component = this.pageRegions.getComponent(componentPath);
            this.contextWindow.inspectComponent(component);
        }

        private onRegionSelected(pathAsString: string) {
            var regionPath = RegionPath.fromString(pathAsString);
            var region = this.pageRegions.getRegionByPath(regionPath);

            this.contextWindow.inspectRegion(region);
        }

        private onPageSelected() {
            this.contextWindow.inspectPage(this.content, this.pageTemplate, this.pageDescriptor);
        }

        private onContentSelected(contentIdStr: string) {
            var contentId = new api.content.ContentId(contentIdStr);
            this.contextWindow.inspectContent(null);
        }

        private onComponentReset(pathAsString: string) {
            var componentPath = ComponentPath.fromString(pathAsString);
            //this.pageRegions.removeComponent(componentPath);
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
            case "paragraph":
                //TODO: Implement paragraph
                builder = null;
                break;
            }
            if (builder) {
                builder.setName(componentName);
                var component = builder.build();
                var componentPath = this.pageRegions.addComponentAfter(component, regionPath, precedingComponent);
                return component;
            }
            else {
                return null;
            }
        }

        private loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element) {
            $.ajax({
                url: api.util.getComponentUri(this.content.getContentId().toString(), componentPath.toString(), true),
                method: 'GET',
                success: (data) => {
                    var newElement = $(data);
                    $(componentPlaceholder.getHTMLElement()).replaceWith(newElement);
                    this.liveEditWindow.LiveEdit.component.Selection.deselect();

                    this.liveEditWindow.LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();

                    var comp = this.liveEditWindow.getComponentByPath(componentPath);
                    this.liveEditWindow.LiveEdit.component.Selection.handleSelect(comp.getHTMLElement(), null, true);

                    this.liveEditJQuery(this.liveEditWindow).trigger("componentLoaded.liveEdit", [comp]);
                }
            });
        }

        setComponentDescriptor(descriptor: Descriptor, componentPath: ComponentPath, componentPlaceholder) {
            var component = this.pageRegions.getComponent(componentPath);
            if (!component || !descriptor) {
                return;
            }

            this.contextWindow.hide();

            componentPlaceholder.showLoadingSpinner();
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

        getLiveEditWindow() {
            return this.liveEditWindow;
        }

        private liveEditListen() {

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit',
                (event, pathAsString?, component?) => {
                    if (component) {
                        if (component.isEmpty()) {
                            this.contextWindow.hide();
                        } else {
                            this.contextWindow.show();
                        }
                    }
                    if (pathAsString) {
                        this.onComponentSelected(pathAsString);
                    }
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageSelect.liveEdit',
                (event) => {
                    this.onPageSelected();
                });

            this.liveEditJQuery(this.liveEditWindow).on('regionSelect.liveEdit',
                (event, regionName?) => {
                    this.onRegionSelected(regionName);
                });

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {
                this.contextWindow.show();
                this.contextWindow.clearSelection();
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event, component?) => {
                this.contextWindow.show();
                if (component && component.getComponentPath()) {
                    this.pageRegions.removeComponent(ComponentPath.fromString(component.getComponentPath()));
                    this.contextWindow.clearSelection();
                }
            });
            this.liveEditJQuery(this.liveEditWindow).on('componentReset.liveEdit', (event, component?) => {
                if (component) {
                    this.contextWindow.clearSelection();
                    this.onComponentReset(component.getComponentPath());
                }
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStop.liveEdit', (event, component?) => {
                if (component) {
                    if (!component.isEmpty()) {
                        this.contextWindow.show();
                    }
                } else {
                    this.contextWindow.show();
                }
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStart.liveEdit', (event) => {
                this.contextWindow.hide();
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, componentEl?) => {

                if (componentEl) {
                    var componentPath = ComponentPath.fromString(componentEl.getComponentPath());
                    var afterComponentPath = ComponentPath.fromString(componentEl.getPrecedingComponentPath());
                    var regionPath = RegionPath.fromString(componentEl.getRegionName());
                    var newPath = this.pageRegions.moveComponent(componentPath, regionPath, afterComponentPath);
                    if (newPath) {
                        componentEl.setComponentPath(newPath.toString());
                    }
                }
            });


            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?, regionPathAsString?: string, precedingComponentPathAsString?: string) => {

                    var componentType = componentEl.getComponentType().getName();
                    var regionPath = RegionPath.fromString(regionPathAsString);

                    var preceedingComponentPath: ComponentPath = null;
                    if (precedingComponentPathAsString) {
                        preceedingComponentPath = ComponentPath.fromString(precedingComponentPathAsString);
                    }

                    if (!this.pageTemplate) {
                        this.initializePageFromDefault().done(() => {

                            var component = this.addComponent(componentType, regionPath, preceedingComponentPath);
                            if (component) {
                                componentEl.getEl().setData("live-edit-component", component.getPath().toString());
                            }
                            componentEl.getEl().setData("live-edit-type", componentType);
                        });
                    }
                    else {
                        var component = this.addComponent(componentType, regionPath, preceedingComponentPath);
                        if (component) {
                            componentEl.getEl().setData("live-edit-component", component.getPath().toString());
                        }
                        componentEl.getEl().setData("live-edit-type", componentType);
                    }
                });

            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, imageId?, componentPathAsString?: string, componentPlaceholder?) => {

                    componentPlaceholder.showLoadingSpinner();
                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    var imageComponent = this.pageRegions.getImageComponent(componentPath);
                    if (imageComponent != null) {
                        imageComponent.setImage(imageId);
                        if (this.defaultImageDescriptor) {
                            imageComponent.setDescriptor(this.defaultImageDescriptor.getKey());
                        }

                        this.pageSkipReload = true;
                        this.contentWizardPanel.saveChanges().done(() => {
                            this.pageSkipReload = false;
                            this.loadComponent(componentPath, componentPlaceholder);
                        })

                    }
                    else {
                        console.log("ImageComponent to set image on not found: " + componentPath);
                    }
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageComponentSetDescriptor.liveEdit',
                (event, descriptor?: Descriptor, componentPathAsString?: string, componentPlaceholder?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    this.setComponentDescriptor(descriptor, componentPath, componentPlaceholder);
                });
        }
    }
}