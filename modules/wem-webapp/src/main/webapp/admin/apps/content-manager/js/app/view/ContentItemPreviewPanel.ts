module app.view {

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        private mask: api.ui.LoadMask;

        constructor() {
            super("item-preview-panel");
            this.mask = new api.ui.LoadMask(this);
            this.frame = new api.dom.IFrameEl();
            this.frame.getEl().addEventListener("load", () => {
                this.mask.hide();
            });
            this.appendChild(this.frame);
        }

        public setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            var escapedPath = item.getPath();
            if (escapedPath.charAt(0) == '/') {
                escapedPath = escapedPath.substring(1);
            }

            this.mask.show();

            new api.content.page.IsRenderableRequest(item.getModel().getContentId()).send()
                .then((response: api.rest.JsonResponse<boolean>) => {
                    if (response.getResult()) {
                        this.getEl().removeClass('no-preview icon-blocked');
                        this.frame.setSrc(api.util.getUri("portal/live/" + escapedPath));
                    } else {
                        this.getEl().addClass('no-preview icon-blocked');
                        this.frame.setSrc("about:blank");
                        this.mask.hide();
                    }
                });
        }
    }
}
