module app.wizard.page.contextwindow {

    export class ContextWindowController {

        private contextWindow: ContextWindow;

        private contextWindowToggler: ContextWindowToggler;

        constructor(contextWindow: ContextWindow, contextWindowToggler: ContextWindowToggler) {
            this.contextWindow = contextWindow;
            this.contextWindowToggler = contextWindowToggler;

            this.contextWindowToggler.onClicked((event: MouseEvent) => {
                var active = this.contextWindowToggler.isActive();
                var shown = this.contextWindow.isShown();

                if (active && !shown) {
                    this.contextWindow.slideIn();
                } else if (active && shown) {
                    this.contextWindowToggler.setActive(false);
                    this.contextWindow.slideOut();
                } else if (!active && shown) {
                    this.contextWindow.slideOut();
                } else {// !active && !shown
                    this.contextWindow.slideIn();
                    this.contextWindowToggler.setActive(true);
                }
            });

            this.contextWindow.onShown(() => {
                if (this.contextWindow.isPinned()) {
                    this.contextWindow.slideIn();
                    this.contextWindowToggler.setActive(true);
                } else {
                    this.contextWindow.slideOut();
                    this.contextWindowToggler.setActive(false);
                }
            });

        }
    }

}