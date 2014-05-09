module app.wizard.page.contextwindow {

    export class PinButton extends api.ui.Button {
        constructor(contextWindow: ContextWindow) {
            super("");
            this.addClass("pin-button icon-pushpin");
            this.setActive(contextWindow.isPinned());

            this.onClicked((event: MouseEvent) => {
                contextWindow.setDynamicPinning(false);
                contextWindow.setPinned(!contextWindow.isPinned());
                this.setActive(contextWindow.isPinned());
            });
        }
    }
}
