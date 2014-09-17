module api.liveedit {

    export class PageComponentPlaceholder extends api.dom.DivEl {

        showRenderingError(url: string) {

            this.removeChildren();
            this.addClass("rendering-error");
            var urlAnchor = new api.dom.AEl();
            urlAnchor.setHtml("Error rendering");
            urlAnchor.setUrl(url, "_blank");
            this.appendChild(urlAnchor);
        }

        select() {

        }

        deselect() {

        }
    }
}