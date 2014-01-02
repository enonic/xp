module app.wizard {
    export class LiveFormPanel extends api.ui.Panel {

        private frame:api.dom.IFrameEl;

        private url:string;

        private site:api.content.Content;

        constructor(site:api.content.Content, url:string = api.util.getUri("portal/edit/bluman-intranett")) {
            super("LiveFormPanel");
            this.addClass("live-form-panel");
            this.url = url;
            this.site = site;
        }

        onElementShown() {
            super.onElementShown();
            this.doLoad();
        }

        renderNew() {

        }

        private doLoad() {
            if (!this.frame) {
                this.frame = new api.dom.IFrameEl();
                this.frame.addClass("live-edit-frame");
                this.frame.setSrc(this.url);
                this.appendChild(this.frame);

                // Wait for iframe to be loaded before adding context window!
                var maxIterations = 10;
                var iterations = 0;
                var intervalId = setInterval(() => {
                    if (this.frame.isLoaded()) {
                        if (this.frame.getHTMLElement()["contentWindow"].$liveEdit) {
                            var contextWindow = new app.contextwindow.ContextWindow({liveEditEl: this.frame, site: this.site});
                            this.appendChild(contextWindow);
                            clearInterval(intervalId);
                        } else {
                            iterations++;
                            if (iterations >= maxIterations) {
                                clearInterval(intervalId);
                            }
                        }
                    } else {
                        iterations++;
                        if (iterations >= maxIterations) {
                            clearInterval(intervalId);
                        }
                    }
                }, 200);
            }
        }

        renderExisting(content:api.content.Content, pageTemplate:api.content.page.PageTemplate) {

            var page = content.getPage();
            var config = pageTemplate.getConfig();
            if (page.hasConfig()) {
                config = page.getConfig();
            }
            var regionResolver = new api.content.page.region.RegionResolver(config);
            var regions = regionResolver.resolve();
            //var region = regions.getByName();
            //region.getPart(id);
            // TODO: live edit render request to server
        }
    }
}