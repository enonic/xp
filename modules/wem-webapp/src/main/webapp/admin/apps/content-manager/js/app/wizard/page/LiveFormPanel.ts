module app.wizard {

    export interface LiveFormPanelConfig {

        contentSaveAction: api.ui.Action;
    }

    export class LiveFormPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;
        private baseUrl: string;
        private url: string;
        private contextWindow: app.contextwindow.ContextWindow;
        private contentSaveAction: api.ui.Action;

        constructor(config: LiveFormPanelConfig) {
            super("live-form-panel");
            this.baseUrl = api.util.getUri("portal/edit/");
            this.contentSaveAction = config.contentSaveAction;

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

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate, siteTemplate: api.content.site.template.SiteTemplate) {


            console.log("LiveFormPanel.renderExisting() ...");

            if (content.isPage() && pageTemplate != null) {

                var liveEditUrl = this.baseUrl + content.getContentId().toString();

                this.doLoadLiveEditWindow(liveEditUrl).
                    then(() => {

                        if( this.contextWindow ) {
                            // Have to remove previous ContextWindow to avoid two
                            // TODO: ContextWindow should be resued with new values instead
                            this.contextWindow.remove();
                        }

                        this.contextWindow = new app.contextwindow.ContextWindow({
                            liveEditIFrame: this.frame,
                            contentSaveAction: this.contentSaveAction,
                            siteTemplate: siteTemplate
                        });

                        this.appendChild(this.contextWindow);

                        console.log("LiveFormPanel.renderExisting() calling contextWindow.setPage ");
                        this.contextWindow.setPage(content, pageTemplate);
                    }).fail(()=> {
                        console.log("LiveFormPanel.renderExisting() loading Live edit failed (time out)");
                    });
            }
        }

        public getRegions(): api.content.page.PageRegions {

            if (this.contextWindow == null) {
                return null;
            }

            return this.contextWindow.getPageRegions();
        }
    }
}