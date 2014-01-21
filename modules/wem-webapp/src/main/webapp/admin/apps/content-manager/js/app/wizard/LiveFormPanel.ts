module app.wizard {
    export class LiveFormPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        private url: string;

        private site: api.content.Content;

        constructor(site: api.content.Content) {
            super("live-form-panel");
            this.url = api.util.getUri("portal/edit/" + site.getContentId().toString());
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
                            //contextWindow.init();
                            clearInterval(intervalId);
                        }
                    }
                    iterations++;
                    if (iterations >= maxIterations) {
                        clearInterval(intervalId);
                    }
                }, 200);
            }
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

            if (content.isPage() && pageTemplate != null) {
                var page = content.getPage();

                var regions = pageTemplate.getRegions();
                if (page.hasRegions()) {
                    regions = page.getRegions();
                }

                var headerRegion = regions.getRegion("main");
                console.log("headerRegion", headerRegion);

                var fancyImageComponent = regions.getComponent(new api.content.page.ComponentName("FancyImage"));
                console.log("fancyImageComponent", fancyImageComponent);


                // TODO: live edit render request to server
            }
        }
    }
}