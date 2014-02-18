module app.wizard {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;


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

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.contentWizardPanel = config.contentWizardPanel;
            this.siteTemplate = config.siteTemplate;

            this.initialized = false;

            this.pageNeedsReload = true;
            this.pageLoading = false;
            this.pageSkipReload = false;

            this.baseUrl = api.util.getUri("portal/edit/");

        }

        private initialize(): Q.Promise<void> {
            var deferred = Q.defer<void>();

            if (!this.initialized) {
                var siteModules = this.siteTemplate.getModules();
                var defaultImageDescrResolved = this.resolveDefaultImageDescriptor(siteModules);
                var defaultPartDescrResolved = this.resolveDefaultPartDescriptor(siteModules);
                var defaultLayoutDescrResolved = this.resolveDefaultLayoutDescriptor(siteModules);
                var defaultDescriptorsResolved: Q.Promise<any>[] = [defaultImageDescrResolved, defaultPartDescrResolved, defaultLayoutDescrResolved];

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

            var maxIterations = 100;
            var iterations = 0;
            var pageLoaded = false;
            var intervalId = setInterval(() => {

                if (this.frame.isLoaded()) {
                    var liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
                    if (liveEditWindow && liveEditWindow.$liveEdit) {

                        // Give loaded page same CONFIG.baseUri as in admin
                        liveEditWindow.CONFIG = {};
                        liveEditWindow.CONFIG.baseUri = CONFIG.baseUri;

                        var pageLoaded = true;
                        clearInterval(intervalId);

                        console.log("LiveFormPanel.doLoad() ... Live edit loaded");

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
                    }
                }
            }, 50);

            return deferred.promise;
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

            console.log("LiveFormPanel.renderExisting() ...");

            api.util.assertNotNull(content, "Expected content not be null");
            api.util.assertNotNull(pageTemplate, "Expected content not be null");
            api.util.assert(content.isPage(), "Expected content to be a page: " + content.getPath().toString());

            this.pageContent = content;
            this.pageTemplate = pageTemplate;
            this.pageUrl = this.baseUrl + content.getContentId().toString();

            console.log("LiveFormPanel.renderExisting() ... pageSkipReload = " + this.pageSkipReload);

            if (!this.pageSkipReload) {
                this.pageRegions = this.resolvePageRegions();
            }

            if (!this.isVisible()) {

                this.pageNeedsReload = true;

                console.log("LiveFormPanel.renderExisting() ... not visible, returning");
                return;
            }

            if (this.pageSkipReload == true) {
                console.log("LiveFormPanel.renderExisting() ... skipReload is true, returning");
                return;
            }

            this.doLoad().
                then(() => {

                    this.setupContextWindow();

                }).fail(()=> {
                    console.log("Error while loading page: ", arguments);
                }).done();
        }

        private resolvePageRegions(): api.content.page.PageRegions {

            var page = this.pageContent.getPage();
            if (page.hasRegions()) {
                return page.getRegions();
            }
            else {
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
            this.contextWindow.inspectPage(this.pageContent);
        }

        private onContentSelected(contentIdStr: string) {
            var contentId = new api.content.ContentId(contentIdStr);
            this.contextWindow.inspectContent(null);
        }

        private liveEditListen() {
            this.liveEditJQuery(this.liveEditWindow).on('selectComponent.liveEdit',
                (event, component?) => {
                    var type = component.componentType.typeName;
                    if (type === 'content') {
                        this.onContentSelected(component.name);
                    }
                });

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit',
                (event, pathAsString?) => {
                    this.onComponentSelected(pathAsString);
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
                this.contextWindow.clearSelection();
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event, component?) => {
                if (component) {
                    this.pageRegions.removeComponent(api.content.page.ComponentPath.fromString(component.getComponentPath()));
                    this.contextWindow.clearSelection();
                }
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStop.liveEdit', (event) => {
                new app.contextwindow.LiveEditDragStopEvent().fire();
                this.contextWindow.show();
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStart.liveEdit', (event) => {
                new app.contextwindow.LiveEditDragStartEvent().fire();
                this.contextWindow.hide();
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, componentEl?) => {

                if (componentEl) {
                    var componentPath = api.content.page.ComponentPath.fromString(componentEl.getComponentPath());
                    console.log("recieved sortableupdate", componentPath);
                    var afterComponentPath = api.content.page.ComponentPath.fromString(componentEl.getPrecedingComponentPath());
                    var regionPath = api.content.page.RegionPath.fromString(componentEl.getRegionName());
                    var newPath = this.pageRegions.moveComponent(componentPath, regionPath, afterComponentPath);
                    if (newPath) {
                        console.log("new path:", newPath.toString());
                        componentEl.getEl().setData("live-edit-component", newPath.toString());
                    }

                    //componentEl.getEl().setData("live-edit-name", componentName.toString());
                }
            });


            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?: api.dom.Element, regionPathAsString?: string, componentPathToAddAfterAsString?: string) => {
                    //TODO: Make all components work and not only image

                    var regionPath = api.content.page.RegionPath.fromString(regionPathAsString);

                    var componentName = this.pageRegions.ensureUniqueComponentName(regionPath, new api.content.page.ComponentName("Image"));

                    var componentToAddAfter: api.content.page.ComponentPath = null;
                    if (componentPathToAddAfterAsString) {
                        componentToAddAfter = api.content.page.ComponentPath.fromString(componentPathToAddAfterAsString);
                    }

                    var imageComponent = new ImageComponentBuilder();
                    imageComponent.setName(componentName);
                    var componentPath = this.pageRegions.addComponentAfter(imageComponent.build(), regionPath, componentToAddAfter);

                    componentEl.getEl().setData("live-edit-component", componentPath.toString());
                    //componentEl.getEl().setData("live-edit-name", componentName.toString());
                    // TODO: resolve type dynamically
                    componentEl.getEl().setData("live-edit-type", "image");
                });

            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, imageId?, componentPathAsString?, component?) => {

                    component.showLoadingSpinner();
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
                            $.ajax({
                                url: api.util.getComponentUri(this.pageContent.getContentId().toString(), componentPath.toString(),
                                    true),
                                method: 'GET',
                                success: (data) => {
                                    var newElement = $(data);
                                    $(component.getHTMLElement()).replaceWith(newElement);
                                    this.liveEditWindow.LiveEdit.component.Selection.deselect();
                                    this.liveEditWindow.LiveEdit.component.Selection.handleSelect(newElement[0]);
                                }
                            });
                        })

                    }
                    else {
                        console.log("ImageComponent to set image on not found: " + componentPath);
                    }
                });
        }
    }
}