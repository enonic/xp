module app_wizard {
    export class LiveFormPanel extends api_ui.Panel {

        private frame:api_dom.IFrameEl;
        private url:string;

        constructor(url:string = api_util.getUri("dev/live-edit-page/bootstrap.jsp?edit=true")) {
            super("LiveFormPanel");
            this.addClass("live-form-panel");

            this.url = url;

            this.frame = new api_dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.frame.setSrc(this.url);
            this.appendChild(this.frame);

            // Wait for iframe to be loaded before adding context window!
            var intervalId = setInterval(() => {
                if (this.frame.isLoaded()) {
                    var contextWindow = new app_contextwindow.ContextWindow({liveEditEl: this.frame});
                    this.appendChild(contextWindow);
                    clearInterval(intervalId);
                }
            }, 200);

        }
    }
}