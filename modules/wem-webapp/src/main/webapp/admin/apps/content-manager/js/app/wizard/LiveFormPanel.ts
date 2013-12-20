module app_wizard {
    export class LiveFormPanel extends api_ui.Panel {

        private frame:api_dom.IFrameEl;

        private url:string;

        constructor(site:api_content.Content, url:string = api_util.getUri("portal/edit/bluman-intranett")) {
            super("LiveFormPanel");
            this.addClass("live-form-panel");

            this.url = url;

            this.frame = new api_dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.frame.setSrc(this.url);
            this.appendChild(this.frame);

            // Wait for iframe to be loaded before adding context window!
            var maxIterations = 10;
            var iterations = 0;
            var intervalId = setInterval(() => {
                if (this.frame.isLoaded()) {
                    if (this.frame.getHTMLElement()["contentWindow"].$liveEdit) {
                        var contextWindow = new app_contextwindow.ContextWindow({liveEditEl: this.frame, site: site});
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

        renderNew() {

        }

        renderExisting(content:api_content.Content, pageTemplate:api_content_page.PageTemplate) {

            var page = content.getPage();
            var config = pageTemplate.getConfig();
            if (page.hasConfig()) {
                config = page.getConfig();
            }
            var regionResolver = new api_content_page_region.RegionResolver(config);
            var regions = regionResolver.resolve();
            //var region = regions.getByName();
            //region.getPart(id);
            // TODO: live edit render request to server
        }
    }
}