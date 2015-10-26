module api.liveedit {

    export class ItemViewPlaceholder extends api.dom.DivEl {

        constructor() {
            super("item-placeholder", true);
        }

        showRenderingError(url: string, errorMessage: string = "Error rendering component") {

            this.removeChildren();
            this.addClass("rendering-error");

            var errorTitle = new api.dom.PEl().
                setHtml(errorMessage);

            var urlAnchor = new api.dom.AEl().
                setUrl(url, "_blank").
                setHtml("Show more...");

            this.appendChildren(errorTitle, urlAnchor);
        }

        select() {

        }

        deselect() {

        }
    }
}