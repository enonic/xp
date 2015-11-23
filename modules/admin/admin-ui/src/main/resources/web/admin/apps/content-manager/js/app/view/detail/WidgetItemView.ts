module app.view.detail {

    import Element = api.dom.Element;
    import LabelEl = api.dom.LabelEl;
    import LinkEl = api.dom.LinkEl;

    export class WidgetItemView extends api.dom.DivEl {

        public static debug = false;

        constructor(className?: string) {
            super("widget-item-view" + (className ? " " + className : ""));
        }

        public layout(): wemQ.Promise<any> {
            if (WidgetItemView.debug) {
                console.debug('WidgetItemView.layout: ', this);
            }
            return wemQ<any>(null);
        }

        private getFullWidgetUrl(baseUrl: string, contentPath: string, uid: string) {
            return api.rendering.UriHelper.getAdminUri(baseUrl, contentPath) + "?uid=" + uid;
        }

        public setUrl(baseUrl: string, contentPath: string): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>(),
                uid = Date.now().toString(),
                linkEl = new LinkEl(this.getFullWidgetUrl(baseUrl, contentPath, uid)),
                el = this.getEl(),
                onImportReady = function(e: CustomEvent) {
                    el.appendChild(document.importNode(<Node>e.detail, true));
                    document.removeEventListener("importready" + uid, onImportReady);
                    
                    deferred.resolve(null);
                };

            document.addEventListener("importready" + uid, onImportReady);

            this.removeChildren();
            this.appendChild(linkEl);

            return deferred.promise;
        }
    }
}