module LiveEdit.component {

    export class LoadingOverlay extends api.dom.DivEl {
        constructor(overlayingElement:api.dom.Element) {
            super("loading-overlay");
            var icon = new api.dom.DivEl();
            icon.addClass("live-edit-loader-splash-spinner");
            icon.addClass("live-edit-font-icon-spinner");

            this.appendChild(icon);

            overlayingElement.appendChild(this);
        }
    }
}
