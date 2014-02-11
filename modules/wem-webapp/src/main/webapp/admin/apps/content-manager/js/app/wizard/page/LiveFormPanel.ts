module app.wizard {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;


    export interface LiveFormPanelConfig {

        contentWizardPanel:ContentWizardPanel;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;
        private baseUrl: string;
        private url: string;
        private contextWindow: app.contextwindow.ContextWindow;
        private skipReload: boolean;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private selectedComponent: app.contextwindow.Component;
        private pageRegions: api.content.page.PageRegions;
        private persistedContent: api.content.Content;
        private siteTemplate: api.content.site.template.SiteTemplate;
        private contentWizardPanel: ContentWizardPanel;
        private defaultImageDescriptor: api.content.page.image.ImageDescriptor

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.baseUrl = api.util.getUri("portal/edit/");
            this.contentWizardPanel = config.contentWizardPanel;
            this.skipReload = false;


            this.frame = new api.dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.appendChild(this.frame);
        }


        private doLoadLiveEditWindow(liveEditUrl: string): Q.Promise<void> {

            console.log("LiveFormPanel.doLoad() ... url: " + liveEditUrl);

            var deferred = Q.defer<void>();

            this.frame.setSrc(liveEditUrl);

            // Wait for iframe to be loaded before adding context window!
            var maxIterations = 100;
            var iterations = 0;
            var contextWindowAdded = false;
            var intervalId = setInterval(() => {

                if (this.frame.isLoaded()) {
                    var contextWindowElement = this.frame.getHTMLElement()["contentWindow"];
                    if (contextWindowElement && contextWindowElement.$liveEdit) {

                        contextWindowElement.CONFIG = {};
                        contextWindowElement.CONFIG.baseUri = CONFIG.baseUri

                        var contextWindowAdded = true;
                        clearInterval(intervalId);
                        console.log("LiveFormPanel.doLoad() ... Live edit loaded");
                        deferred.resolve(null);
                    }
                }

                iterations++;
                if (iterations >= maxIterations) {
                    clearInterval(intervalId);
                    if (contextWindowAdded) {
                        deferred.resolve(null);
                    }
                    else {
                        deferred.reject(null);
                    }
                }
            }, 200);

            return deferred.promise;
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                       siteTemplate: api.content.site.template.SiteTemplate) {


            console.log("LiveFormPanel.renderExisting() ...");

            if (content.isPage() && pageTemplate != null && !this.skipReload) {
                this.resolveDefaultImageDescriptor(siteTemplate.getModules());

                var liveEditUrl = this.baseUrl + content.getContentId().toString();

                this.doLoadLiveEditWindow(liveEditUrl).
                    then(() => {

                        if (this.contextWindow) {
                            // Have to remove previous ContextWindow to avoid two
                            // TODO: ContextWindow should be resued with new values instead
                            this.contextWindow.remove();
                        }

                        this.siteTemplate = siteTemplate;
                        this.persistedContent = content;
                        this.liveEditWindow = this.frame.getHTMLElement()["contentWindow"];
                        this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;
                        this.liveEditListen();

                        this.contextWindow = new app.contextwindow.ContextWindow({
                            liveEditIFrame: this.frame,
                            siteTemplate: this.siteTemplate,
                            liveEditWindow: this.liveEditWindow,
                            liveEditJQuery: this.liveEditJQuery,
                            liveFormPanel: this
                        });

                        this.appendChild(this.contextWindow);

                        console.log("LiveFormPanel.renderExisting() calling contextWindow.setPage ");
                        this.setPage(content, pageTemplate);
                    }).fail(()=> {
                        console.log("LiveFormPanel.renderExisting() loading Live edit failed (time out)");
                    });
            }
        }

        setPage(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {
            var page = content.getPage();
            this.pageRegions = pageTemplate.getRegions();
            if (page.hasRegions()) {
                this.pageRegions = page.getRegions();
            }
        }


        public getRegions(): api.content.page.PageRegions {

            return this.pageRegions;
        }

        getDefaultImageDescriptor(): api.content.page.image.ImageDescriptor {
            return this.defaultImageDescriptor;
        }

        private resolveDefaultImageDescriptor(moduleKeys: api.module.ModuleKey[]): Q.Promise<api.content.page.image.ImageDescriptor> {

            var d = Q.defer<api.content.page.image.ImageDescriptor>();
            new api.content.page.image.GetImageDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((imageDescriptors: api.content.page.image.ImageDescriptor[]) => {
                    if (imageDescriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        this.defaultImageDescriptor = imageDescriptors[0];
                        d.resolve(imageDescriptors[0]);
                    }
                });
            return d.promise;
        }


        private liveEditListen() {
            this.liveEditJQuery(this.liveEditWindow).on('selectComponent.liveEdit',
                (event, component?, mouseClickPagePosition?) => {
                    new app.contextwindow.SelectComponentEvent(<app.contextwindow.Component>component).fire();
                    this.selectedComponent = component;
                });

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit',
                (event, name?) => {
                    new app.contextwindow.ComponentSelectEvent(new api.content.page.ComponentName(name)).fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageSelect.liveEdit',
                (event) => {
                    new app.contextwindow.PageSelectEvent().fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('regionSelect.liveEdit',
                (event, name?) => {
                    new app.contextwindow.RegionSelectEvent(name).fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {
                new app.contextwindow.ComponentDeselectEvent().fire();
                this.selectedComponent = null;
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event, component?) => {
                new app.contextwindow.ComponentRemovedEvent().fire();
                if (component) {
                    this.pageRegions.removeComponent(api.content.page.ComponentPath.fromString(component.getComponentPath()));
                    this.selectedComponent = null;
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
            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, component?) => {
                if (component) {
                    var componentPath = api.content.page.ComponentPath.fromString(component.getComponentPath());
                    var afterComponentPath = api.content.page.ComponentPath.fromString(component.getPrecedingComponentPath());
                    this.pageRegions.moveComponent(componentPath, component.getRegionName(), afterComponentPath);
                }
            });


            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, component?, regionName?, componentPathToAddAfterAsString?: string) => {
                    //TODO: Make all components work and not only image

                    var componentName = this.pageRegions.ensureUniqueComponentName(new api.content.page.ComponentName("Image"));

                    var componentToAddAfter: api.content.page.ComponentPath = null;
                    if (componentPathToAddAfterAsString) {
                        componentToAddAfter = api.content.page.ComponentPath.fromString(componentPathToAddAfterAsString);
                    }

                    var regionPath: api.content.page.RegionPath;
                    if (componentToAddAfter != null) {
                        regionPath = componentToAddAfter.getRegionPath();
                    }
                    else {
                        regionPath = api.content.page.RegionPath.fromString(regionName);
                    }

                    var imageComponent = new ImageComponentBuilder();
                    imageComponent.setName(componentName);
                    imageComponent.setRegion(regionPath);

                    var componentPath;
                    if (componentToAddAfter) {
                        componentPath = this.pageRegions.addComponentAfter(imageComponent.build(), componentToAddAfter);
                    }
                    else {
                        this.pageRegions.addComponentFirst(imageComponent.build(), regionName);
                        componentPath = ComponentPath.fromString(regionName + "/" + componentName.toString());
                    }

                    component.getEl().setData("live-edit-component", componentPath.toString());
                    component.getEl().setData("live-edit-name", componentName.toString());
                });

            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, imageId?, componentPathAsString?, component?) => {
                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    var imageComponent = this.pageRegions.getImageComponent(componentPath);
                    if (imageComponent != null) {
                        imageComponent.setImage(imageId);
                        if (this.defaultImageDescriptor) {
                            imageComponent.setDescriptor(this.defaultImageDescriptor.getKey());
                        }

                        this.skipReload = true;
                        this.contentWizardPanel.saveChanges().done(() => {
                            $.ajax({
                                url: api.util.getComponentUri( this.persistedContent.getContentId().toString(), componentPath.toString(), true),
                                method: 'GET',
                                success: (data) => {
                                    $(component.getHTMLElement()).replaceWith(data);
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