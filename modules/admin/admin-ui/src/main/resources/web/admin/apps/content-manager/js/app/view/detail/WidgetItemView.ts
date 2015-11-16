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

        public setUrl(baseUrl: string, contentPath: string): WidgetItemView {
            debugger;
            var resolvedUrl = api.rendering.UriHelper.getAdminUri(baseUrl, contentPath),
                linkEl = new LinkEl(resolvedUrl),
                el = this.getEl();

            this.appendChild(linkEl);

            document.addEventListener("importready", function(e: CustomEvent) {
                console.log("adding import to the dom");
                el.appendChild(document.importNode(<Node>e.detail, true));
            });

            return this;
        }
    }
}