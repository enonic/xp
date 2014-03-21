module app.wizard.page {

    import ModuleKey = api.module.ModuleKey;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplate = api.content.page.PageTemplate;
    import Content = api.content.Content;
    import ComponentPath = api.content.page.ComponentPath;
    import PageRegions = api.content.page.PageRegions;
    import RegionPath = api.content.page.RegionPath;

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
        private defaultImageDescriptor: ImageDescriptor;
        private defaultPartDescriptor: PartDescriptor;
        private defaultLayoutDescriptor: LayoutDescriptor;

        private pageContent: Content;
        private pageTemplate: PageTemplate;
        private pageRegions: PageRegions;
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

            this.mask = new api.ui.LoadMask(this);

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
                var defaultImageDescrResolved = this.resolveDefaultImageDescriptor(siteModules);
                var defaultPartDescrResolved = this.resolveDefaultPartDescriptor(siteModules);
                var defaultLayoutDescrResolved = this.resolveDefaultLayoutDescriptor(siteModules);
                var defaultDescriptorsResolved: Q.Promise<any>[] = [defaultImageDescrResolved, defaultPartDescrResolved,
                    defaultLayoutDescrResolved];

                defaultImageDescrResolved.done((imageDescriptor: ImageDescriptor)=> {
                    this.defaultImageDescriptor = imageDescriptor;
                });

                defaultPartDescrResolved.done((partDescriptor: PartDescriptor)=> {
                    this.defaultPartDescriptor = partDescriptor;
                });

                defaultLayoutDescrResolved.done((layoutDescriptor: LayoutDescriptor)=> {
                    this.defaultLayoutDescriptor = layoutDescriptor;
                });

                Q.allSettled(defaultDescriptorsResolved).then((results: Q.PromiseState<any>[])=> {
                    this.initialized = true;
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
            this.setupFrame();

            var deferred = Q.defer<void>();
            this.frame.onLoaded((event:UIEvent) => {
                var liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
                if (liveEditWindow && liveEditWindow.$liveEdit && typeof(liveEditWindow.initializeLiveEdit) === "function") {
                    // Give loaded page same CONFIG.baseUri as in admin
                    liveEditWindow.CONFIG = {};
                    liveEditWindow.CONFIG.baseUri = CONFIG.baseUri;
                    liveEditWindow.siteTemplate = this.siteTemplate;
                    liveEditWindow.content = this.pageContent;

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
            api.util.assertNotNull(pageTemplate, "Expected content not be null");

            this.pageContent = content;
            var pageTemplateChanged = this.pageTemplate && this.pageTemplate.getKey().toString() != pageTemplate.getKey().toString();
            this.pageTemplate = pageTemplate;

            this.pageUrl = this.baseUrl + content.getContentId().toString();

            if (!this.pageSkipReload) {
                if (pageTemplateChanged) {
                    console.log("pageTemplateChanged, resetting regions to regions of template");
                    this.pageRegions = this.pageTemplate.getRegions();
                } else {
                    this.pageRegions = this.resolvePageRegions();
                }
                this.resolvePageDescriptor(pageTemplate);
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

        private resolvePageDescriptor(pageTemplate: PageTemplate) {
            new GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).
                sendAndParse().
                done((pageDescriptor: PageDescriptor) => {
                    this.pageDescriptor = pageDescriptor;
                });
        }

        private resolvePageRegions(): PageRegions {

            var page = this.pageContent.getPage();
            if (page && page.hasRegions()) {
                console.log("resolvePageRegions.. from page");
                return page.getRegions();
            }
            else {
                console.log("resolvePageRegions.. from page template");
                return this.pageTemplate.getRegions();
            }
        }

        private setupFrame() {
            if (this.frame) {
                this.frame.remove();
            }

            this.frame = new api.dom.IFrameEl("live-edit-frame").setSrc(this.pageUrl);
            this.appendChild(this.frame);

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
                liveFormPanel: this
            });

            return contextWindow;
        }


        public getRegions(): PageRegions {

            return this.pageRegions;
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
            this.contextWindow.inspectPage(this.pageContent, this.pageTemplate, this.pageDescriptor);
        }

        private onContentSelected(contentIdStr: string) {
            var contentId = new api.content.ContentId(contentIdStr);
            this.contextWindow.inspectContent(null);
        }

        private onComponentReset(pathAsString: string) {
            var componentPath = ComponentPath.fromString(pathAsString);
            //this.pageRegions.removeComponent(componentPath);
        }

        private loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element) {
            $.ajax({
                url: api.util.getComponentUri(this.pageContent.getContentId().toString(), componentPath.toString(), true),
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

        setComponentDescriptor(descriptor: Descriptor, componentPath: ComponentPath,
                               componentPlaceholder) {
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

        private addLayoutRegions(layoutComponent: LayoutComponent,
                                 layoutDescriptor: LayoutDescriptor) {
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
                (event, componentEl?, regionPathAsString?: string, componentPathToAddAfterAsString?: string) => {
                    var componentType = componentEl.getComponentType().getName();
                    var regionPath = RegionPath.fromString(regionPathAsString);

                    var componentName = this.pageRegions.ensureUniqueComponentName(regionPath,
                        new api.content.page.ComponentName(api.util.capitalize(componentType)));

                    var componentToAddAfter: ComponentPath = null;
                    if (componentPathToAddAfterAsString) {
                        componentToAddAfter = ComponentPath.fromString(componentPathToAddAfterAsString);
                    }

                    var pageComponent: PageComponentBuilder<PageComponent>;
                    switch (componentType) {
                    case "image":
                        pageComponent = new ImageComponentBuilder();
                        break;
                    case "part":
                        pageComponent = new PartComponentBuilder();
                        break;
                    case "layout":
                        pageComponent = new LayoutComponentBuilder();
                        break;
                    case "paragraph":
                        //TODO: Implement paragraph
                        pageComponent = null;
                        break;
                    }
                    if (pageComponent) {
                        pageComponent.setName(componentName);
                        var componentPath = this.pageRegions.addComponentAfter(pageComponent.build(), regionPath, componentToAddAfter);
                        componentEl.getEl().setData("live-edit-component", componentPath.toString());
                    }

                    componentEl.getEl().setData("live-edit-type", componentType);
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