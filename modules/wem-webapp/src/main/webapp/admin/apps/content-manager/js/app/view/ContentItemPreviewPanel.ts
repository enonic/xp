module app.view {

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        constructor() {
            super("item-preview-panel");
            this.frame = new api.dom.IFrameEl();
            this.appendChild(this.frame);
        }

        public setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            var escapedPath = item.getPath();
            if (escapedPath.charAt(0) == '/') {
                escapedPath = escapedPath.substring(1);
            }

            new api.content.page.IsRenderableRequest(item.getModel().getContentId()).send()
                .then((response: api.rest.JsonResponse<boolean>) => {
                    console.log(response);
                    if (response.getResult()) {
                        this.getEl().removeClass('no-preview icon-blocked');
                        this.frame.setSrc(api.util.getUri("portal/live/" + escapedPath));
                    } else {
                        this.getEl().addClass('no-preview icon-blocked').setInnerHtml("");
                    }
                }).done();
        }
    }
}
