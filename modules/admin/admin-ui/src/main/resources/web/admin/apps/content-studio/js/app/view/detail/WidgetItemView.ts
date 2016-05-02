import "../../../api.ts";

import Element = api.dom.Element;
import LabelEl = api.dom.LabelEl;
import LinkEl = api.dom.LinkEl;

export class WidgetItemView extends api.dom.DivEl {

    public static debug = false;
    private uid: string = "";

    constructor(className?: string) {
        super("widget-item-view" + (className ? " " + className : ""));
    }

    public layout(): wemQ.Promise<any> {
        if (WidgetItemView.debug) {
            console.debug('WidgetItemView.layout: ', this);
        }
        return wemQ<any>(null);
    }

    private getFullWidgetUrl(url: string, uid: string) {
        return url + "?uid=" + uid;
    }

    public setUrl(url: string, keepId: boolean = false): wemQ.Promise<void> {
        var deferred = wemQ.defer<void>(),
            uid = (!keepId || !this.uid) ? Date.now().toString() : this.uid,
            linkEl = new LinkEl(this.getFullWidgetUrl(url, uid)),
            el = this.getEl(),
            onLinkLoaded = ((event: UIEvent) => {
                var mainContainer = wemjq(event.target["import"]).find("div")[0];
                if (mainContainer) {
                    el.appendChild(document.importNode(<Node>mainContainer, true));
                }
                linkEl.unLoaded(onLinkLoaded);
                deferred.resolve(null);
            });

        this.uid = uid;
        this.removeChildren();

        linkEl.onLoaded(onLinkLoaded);
        this.appendChild(linkEl);

        return deferred.promise;
    }
}
