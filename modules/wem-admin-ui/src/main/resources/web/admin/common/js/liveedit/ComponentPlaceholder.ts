module api.liveedit {

    export class ComponentPlaceholder extends api.dom.DivEl {

        showRenderingError(url: string, errorMessage:string = "Error rendering component") {

            this.removeChildren();
            this.addClass("rendering-error");
            var errorTitle = new api.dom.PEl();
            errorTitle.setHtml(errorMessage);
            this.appendChild(errorTitle);
            var urlAnchor = new api.dom.AEl();
            urlAnchor.setHtml("Show more...");
            urlAnchor.setUrl(url, "_blank");
            this.appendChild(urlAnchor);
        }

        select() {

        }

        deselect() {

        }
    }
}