module app.wizard.page {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
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

    import PageComponentBuilder = api.content.page.PageComponentBuilder;
    import ComponentName = api.content.page.ComponentName;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PartComponentBuilder = api.content.page.part.PartComponentBuilder;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import LayoutComponentBuilder = api.content.page.layout.LayoutComponentBuilder;

    import ContextWindow = app.wizard.page.contextwindow.ContextWindow;
    import RenderingMode = api.util.RenderingMode;


    export interface LiveFormPanelConfig {

        siteTemplate:SiteTemplate;
        contentWizardPanel:ContentWizardPanel;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private siteTemplate: SiteTemplate;
        private initialized: boolean;

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

            this.frameContainer = new api.ui.Panel("frame-container");
            this.appendChild(this.frameContainer);
            this.frame = new api.dom.IFrameEl("live-edit-frame");
            this.frameContainer.appendChild(this.frame);

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

                DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                    siteTemplateKey: this.siteTemplate.getKey(),
                    contentType: this.content.getType(),
                    modules: this.siteTemplate.getModules(),
                }).done((defaultModels: DefaultModels) => {
                    this.defaultModels = defaultModels;
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

            this.initialize().then(() => {

                if (this.pageNeedsReload && !this.pageLoading) {

                    this.pageLoading = true;
                    this.doLoadPage().then(()=> {

                        this.pageLoading = false;
                        this.pageNeedsReload = false;

                        this.loadPageDescriptor().done(() => {

                            this.setupContextWindow();
                            deferred.resolve(null);
                        });

                    }).catch((reason)=> {
                        deferred.reject(reason);
                    }).done();
                }
                else {
                    deferred.resolve(null);
                }

            }).catch((reason) => {
                deferred.reject(reason);
            }).done();

            return deferred.promise;
        }


        private doLoadPage(): Q.Promise<void> {

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
                    liveEditWindow.onOpenImageUploadDialogRequest(()=> {
                        var uploadDialog = new api.form.inputtype.content.image.UploadDialog();
                        uploadDialog.onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                            liveEditWindow.notifyImageUploaded(event);
                        });
                        uploadDialog.open();

                    })
                    this.mask.hide();
                    liveEditWindow.initializeLiveEdit();
                    deferred.resolve(null);
                }
            });

            return deferred.promise;
        }

        setPage(content: Content, pageTemplate: PageTemplate): Q.Promise<void> {

            var deferred = Q.defer<void>();

            api.util.assertNotNull(content, "Expected content not be null");

            this.content = content;
            var pageTemplateChanged = this.resolvePageTemplateChanged(pageTemplate);
            this.pageTemplate = pageTemplate;

            this.pageUrl = this.baseUrl + content.getContentId().toString();

            this.initialize().then(() => {

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
                    //console.log("LiveFormPanel.setPage() ... not visible, returning");
                    deferred.resolve(null);
                }
                else if (this.pageSkipReload == true) {
                    //console.log("LiveFormPanel.setPage() ... skipReload is true, returning");
                    deferred.resolve(null);
                }
                else {
                    this.doLoadPage().
                        then(() => {

                            this.loadPageDescriptor().then(() => {

                                this.setupContextWindow();
                                deferred.resolve(null);

                            }).catch((reason) => {
                                deferred.reject(reason);
                            }).done();
                        }).catch((reason)=> {
                            deferred.reject(reason);
                        }).done();
                }

            }).catch((reason) => {
                deferred.reject(reason);
            }).done();

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

                    this.contextWindow.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

                    deferred.resolve(null);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        private setupContextWindow() {

            if (this.contextWindow) {
                // Have to remove previous ContextWindow to avoid two
                // TODO: ContextWindow should be resued with new values instead
                this.contextWindow.remove();
            }

            this.liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
            this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

            this.contextWindow = new ContextWindow({
                liveEditIFrame: this.frame,
                siteTemplate: this.siteTemplate,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                liveFormPanel: this,
                contentType: this.content.getType(),
                defaultModels: this.defaultModels
            });

            this.contextWindow.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);

            this.contextWindow.onPageTemplateChanged((event: app.wizard.page.contextwindow.inspect.PageTemplateChangedEvent) => {

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
                                    this.contextWindow.setPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);
                                });
                        });
                }
                else {
                    this.pageTemplate = null;
                    this.pageDescriptor = null;
                    this.pageRegions = this.defaultModels.getPageTemplate().getRegions();
                    this.pageConfig = this.defaultModels.getPageTemplate().getConfig();
                    this.contextWindow.setPage(this.content, null, null, this.pageConfig);
                }
            });

            this.appendChild(this.contextWindow);

            this.liveEditListen();
        }

        resizeFrameContainer(width: number) {
            this.frameContainer.getEl().setWidthPx(width);
        }

        public getPageTemplate(): PageTemplateKey {

            if (!this.contextWindow) {
                return this.content.isPage() ? this.content.getPage().getTemplate() : null;
            }

            return this.contextWindow.getPageTemplate();
        }

        public getRegions(): PageRegions {

            return this.pageRegions;
        }

        public getConfig(): RootDataSet {

            return this.pageConfig;
        }

        getDefaultImageDescriptor(): ImageDescriptor {
            return this.defaultModels.getImageDescriptor();
        }

        getDefaultPartDescriptor(): PartDescriptor {
            return this.defaultModels.getPartDescriptor();
        }

        getDefaultLayoutDescriptor(): LayoutDescriptor {
            return this.defaultModels.getLayoutDescriptor();
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
            this.contextWindow.inspectPage(this.content, this.pageTemplate, this.pageDescriptor, this.pageConfig);
        }

        private onContentSelected(contentIdStr: string) {
            var contentId = new api.content.ContentId(contentIdStr);
            this.contextWindow.inspectContent(null);
        }

        private onComponentReset(pathAsString: string) {
            var componentPath = ComponentPath.fromString(pathAsString);
            this.pageRegions.getComponent(componentPath).setDescriptor(null);
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
                var componentPath = this.pageRegions.addComponentAfter(component, regionPath, precedingComponent);
                return component;
            }
            else {
                return null;
            }
        }

        private loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element) {
            $.ajax({
                url: api.util.getComponentUri(this.content.getContentId().toString(), componentPath.toString(), RenderingMode.EDIT),
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

                    var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());

                    if (this.pageTemplate) {

                        this.pageRegions.removeComponent(componentPath);
                        this.contextWindow.clearSelection();
                    }
                    else {
                        // Make the Content a Page if it wasn't before removing
                        this.initializePageFromDefault().done(() => {

                            this.pageRegions.removeComponent(componentPath);
                            this.contextWindow.clearSelection();
                        });
                    }
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

                    if (this.pageTemplate) {

                        var imageComponent = this.pageRegions.getImageComponent(componentPath);
                        if (imageComponent != null) {
                            imageComponent.setImage(imageId);
                            if (this.defaultModels.hasImageDescriptor()) {
                                imageComponent.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
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
                    }
                    else {
                        this.initializePageFromDefault().done(() => {

                            var imageComponent = this.pageRegions.getImageComponent(componentPath);
                            if (imageComponent != null) {
                                imageComponent.setImage(imageId);
                                if (this.defaultModels.hasImageDescriptor()) {
                                    imageComponent.setDescriptor(this.defaultModels.getImageDescriptor().getKey());
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
                    }
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageComponentSetDescriptor.liveEdit',
                (event, descriptor?: Descriptor, componentPathAsString?: string, componentPlaceholder?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);

                    if (this.pageTemplate) {
                        this.setComponentDescriptor(descriptor, componentPath, componentPlaceholder);
                    }
                    else {
                        this.initializePageFromDefault().done(() => {

                            this.setComponentDescriptor(descriptor, componentPath, componentPlaceholder);
                        });
                    }
                });
        }
    }
}