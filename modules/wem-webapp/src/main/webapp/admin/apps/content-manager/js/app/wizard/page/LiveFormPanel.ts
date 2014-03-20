module app.wizard {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import PartComponentBuilder = api.content.page.part.PartComponentBuilder;
    import LayoutComponentBuilder = api.content.page.layout.LayoutComponentBuilder;
    import PageComponentBuilder = api.content.page.PageComponentBuilder;


    export interface LiveFormPanelConfig {

        siteTemplate:api.content.site.template.SiteTemplate;
        contentWizardPanel:ContentWizardPanel;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private siteTemplate: api.content.site.template.SiteTemplate;
        private initialized: boolean;
        private defaultImageDescriptor: api.content.page.image.ImageDescriptor;
        private defaultPartDescriptor: api.content.page.part.PartDescriptor;
        private defaultLayoutDescriptor: api.content.page.layout.LayoutDescriptor;

        private pageContent: api.content.Content;
        private pageTemplate: api.content.page.PageTemplate;
        private pageRegions: api.content.page.PageRegions;
        private pageDescriptor: api.content.page.PageDescriptor;

        private pageNeedsReload: boolean;
        private pageLoading: boolean;

        private pageSkipReload: boolean;
        private frame: api.dom.IFrameEl;
        private baseUrl: string;

        private pageUrl: string;
        private contextWindow: app.contextwindow.ContextWindow;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;

        private contentWizardPanel: ContentWizardPanel;

        private loader: LiveEditLoader;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.siteTemplate = config.siteTemplate;

            this.initialized = false;

            this.pageNeedsReload = true;
            this.pageLoading = false;
            this.pageSkipReload = false;

            this.baseUrl = api.util.getUri("portal/edit/");

            ShowContentFormEvent.on(() => {
                if (this.loader) {
                    this.loader.stop();
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

                defaultImageDescrResolved.done((imageDescriptor: api.content.page.image.ImageDescriptor)=> {
                    this.defaultImageDescriptor = imageDescriptor;
                });

                defaultPartDescrResolved.done((partDescriptor: api.content.page.part.PartDescriptor)=> {
                    this.defaultPartDescriptor = partDescriptor;
                });

                defaultLayoutDescrResolved.done((layoutDescriptor: api.content.page.layout.LayoutDescriptor)=> {
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

            var deferred = Q.defer<void>();

            this.setupFrame();
            this.loader = new LiveEditLoader(this);
            this.loader.start();

            var maxIterations = 100;
            var iterations = 0;
            var pageLoaded = false;

            var intervalId = setInterval(() => {

                if (this.frame.isLoaded()) {
                    var liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
                    if (liveEditWindow && liveEditWindow.$liveEdit && typeof(liveEditWindow.initializeLiveEdit) === "function") {
                        // Give loaded page same CONFIG.baseUri as in admin
                        liveEditWindow.CONFIG = {};
                        liveEditWindow.CONFIG.baseUri = CONFIG.baseUri;
                        liveEditWindow.siteTemplate = this.siteTemplate;
                        liveEditWindow.content = this.pageContent;

                        var pageLoaded = true;
                        clearInterval(intervalId);

                        this.loader.stop();
                        liveEditWindow.initializeLiveEdit();
                        deferred.resolve(null);
                    }
                }

                iterations++;
                if (!pageLoaded && iterations >= maxIterations) {
                    clearInterval(intervalId);
                    if (pageLoaded) {
                        deferred.resolve(null);
                    }
                    else {
                        deferred.reject(null);
                        this.loader.stop();
                    }
                }
            }, 50);

            return deferred.promise;
        }

        setPage(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

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

        private resolvePageDescriptor(pageTemplate: api.content.page.PageTemplate) {
            new api.content.page.GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).
                sendAndParse().
                done((pageDescriptor: api.content.page.PageDescriptor) => {
                    this.pageDescriptor = pageDescriptor;
                });
        }

        private resolvePageRegions(): api.content.page.PageRegions {

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

            this.frame = new api.dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.appendChild(this.frame);
            this.frame.setSrc(this.pageUrl);

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

        private createContextWindow(): app.contextwindow.ContextWindow {

            this.liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
            this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

            var contextWindow = new app.contextwindow.ContextWindow({
                liveEditIFrame: this.frame,
                siteTemplate: this.siteTemplate,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                liveFormPanel: this
            });

            return contextWindow;
        }


        public getRegions(): api.content.page.PageRegions {

            return this.pageRegions;
        }

        getDefaultImageDescriptor(): api.content.page.image.ImageDescriptor {
            return this.defaultImageDescriptor;
        }

        getDefaultPartDescriptor(): api.content.page.part.PartDescriptor {
            return this.defaultPartDescriptor;
        }

        getDefaultLayoutDescriptor(): api.content.page.layout.LayoutDescriptor {
            return this.defaultLayoutDescriptor;
        }

        private resolveDefaultImageDescriptor(moduleKeys: api.module.ModuleKey[]): Q.Promise<api.content.page.image.ImageDescriptor> {

            var d = Q.defer<api.content.page.image.ImageDescriptor>();
            new api.content.page.image.GetImageDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((imageDescriptors: api.content.page.image.ImageDescriptor[]) => {
                    if (imageDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(imageDescriptors[0]);
                    }
                });
            return d.promise;
        }

        private resolveDefaultPartDescriptor(moduleKeys: api.module.ModuleKey[]): Q.Promise<api.content.page.part.PartDescriptor> {

            var d = Q.defer<api.content.page.part.PartDescriptor>();
            new api.content.page.part.GetPartDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((partDescriptors: api.content.page.part.PartDescriptor[]) => {
                    if (partDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(partDescriptors[0]);
                    }
                });
            return d.promise;
        }

        private resolveDefaultLayoutDescriptor(moduleKeys: api.module.ModuleKey[]): Q.Promise<api.content.page.layout.LayoutDescriptor> {

            var d = Q.defer<api.content.page.layout.LayoutDescriptor>();
            new api.content.page.layout.GetLayoutDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((layoutDescriptors: api.content.page.layout.LayoutDescriptor[]) => {
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
            var componentPath = api.content.page.ComponentPath.fromString(pathAsString);
            var component = this.pageRegions.getComponent(componentPath);
            this.contextWindow.inspectComponent(component);
        }

        private onRegionSelected(pathAsString: string) {
            var regionPath = api.content.page.RegionPath.fromString(pathAsString);
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
            var componentPath = api.content.page.ComponentPath.fromString(pathAsString);
            //this.pageRegions.removeComponent(componentPath);
        }

        private loadComponent(componentPath: api.content.page.ComponentPath, componentPlaceholder: api.dom.Element) {
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

        setComponentDescriptor(descriptor: api.content.page.Descriptor, componentPath: api.content.page.ComponentPath,
                               componentPlaceholder) {
            var component = this.pageRegions.getComponent(componentPath);
            if (!component || !descriptor) {
                return;
            }

            this.contextWindow.hide();

            componentPlaceholder.showLoadingSpinner();
            component.setDescriptor(descriptor.getKey());

            // use of "instanceof" does not work because the descriptor object has been created in another frame
            var isLayoutDescriptor = (descriptor instanceof api.content.page.layout.LayoutDescriptor) ||
                                     (<any>descriptor).constructor.name === 'LayoutDescriptor';
            if (isLayoutDescriptor) {
                var layoutDescriptor = <api.content.page.layout.LayoutDescriptor> descriptor;
                var layoutComponent = <api.content.page.layout.LayoutComponent>component;
                this.addLayoutRegions(layoutComponent, layoutDescriptor);
            }

            this.pageSkipReload = true;
            this.contentWizardPanel.saveChanges().done(() => {
                this.pageSkipReload = false;
                this.loadComponent(componentPath, componentPlaceholder);
            });
        }

        private addLayoutRegions(layoutComponent: api.content.page.layout.LayoutComponent,
                                 layoutDescriptor: api.content.page.layout.LayoutDescriptor) {
            var layoutRegionsBuilder = new api.content.page.layout.LayoutRegionsBuilder();
            layoutDescriptor.getRegions().forEach(function (regionDescriptor: api.content.page.region.RegionDescriptor) {

                var regionPath = new api.content.page.RegionPath(layoutComponent.getPath(), regionDescriptor.getName());
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
                    this.pageRegions.removeComponent(api.content.page.ComponentPath.fromString(component.getComponentPath()));
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
                    var componentPath = api.content.page.ComponentPath.fromString(componentEl.getComponentPath());
                    var afterComponentPath = api.content.page.ComponentPath.fromString(componentEl.getPrecedingComponentPath());
                    var regionPath = api.content.page.RegionPath.fromString(componentEl.getRegionName());
                    var newPath = this.pageRegions.moveComponent(componentPath, regionPath, afterComponentPath);
                    if (newPath) {
                        componentEl.setComponentPath(newPath.toString());
                    }
                }
            });


            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?, regionPathAsString?: string, componentPathToAddAfterAsString?: string) => {
                    var componentType = componentEl.getComponentType().getName();
                    var regionPath = api.content.page.RegionPath.fromString(regionPathAsString);

                    var componentName = this.pageRegions.ensureUniqueComponentName(regionPath,
                        new api.content.page.ComponentName(api.util.capitalize(componentType)));

                    var componentToAddAfter: api.content.page.ComponentPath = null;
                    if (componentPathToAddAfterAsString) {
                        componentToAddAfter = api.content.page.ComponentPath.fromString(componentPathToAddAfterAsString);
                    }

                    var pageComponent: PageComponentBuilder<api.content.page.PageComponent>;
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
                (event, descriptor?: api.content.page.Descriptor, componentPathAsString?: string, componentPlaceholder?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    this.setComponentDescriptor(descriptor, componentPath, componentPlaceholder);
                });
        }
    }

    export class LiveEditLoader extends api.dom.DivEl {

        constructor(elementToCover: api.dom.Element) {
            super("live-edit-loader");

            this.getEl().setTopPx(elementToCover.getEl().getOffsetTop());
            var dots = new api.dom.DivEl("dots");
            this.appendChild(dots);
            document.body.appendChild(this.getHTMLElement());
        }

        start() {
        }

        stop() {
            $(this.getHTMLElement()).fadeOut(750, () => {
                var parent = this.getHTMLElement().parentNode;
                if (parent) {
                    parent.removeChild(this.getHTMLElement());
                }
            })

        }
    }
}