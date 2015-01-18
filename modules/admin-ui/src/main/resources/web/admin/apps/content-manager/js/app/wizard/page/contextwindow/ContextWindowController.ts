module app.wizard.page.contextwindow {

    import ComponentView = api.liveedit.ComponentView;
    import Component = api.content.page.region.Component;
    import PageView = api.liveedit.PageView;

    export class ContextWindowController {

        private contextWindow: ContextWindow;

        private contextWindowToggler: ContextWindowToggler;

        constructor(contextWindow: ContextWindow, contextWindowToggler: ContextWindowToggler) {
            this.contextWindow = contextWindow;
            this.contextWindowToggler = contextWindowToggler;

            this.contextWindowToggler.onClicked((event: MouseEvent) => {
                var active = !this.contextWindowToggler.isActive();
                this.contextWindowToggler.setActive(active);

                if (active) {
                    this.contextWindow.slideIn();
                } else {
                    this.contextWindow.slideOut();
                }
            });

            this.contextWindow.onShown(() => {
                if (this.contextWindow.isFloating()) {
                    this.contextWindow.slideOut();
                    this.contextWindowToggler.setActive(false);
                } else {
                    this.contextWindow.slideIn();
                    this.contextWindowToggler.setActive(true);
                }
            });

            this.contextWindow.onDisplayModeChanged(() => {
                if (!this.contextWindow.isFloating() && !this.contextWindowToggler.isActive() && this.contextWindow.isShown()) {
                    this.contextWindow.slideOut();
                }
            });
        }
    }

}