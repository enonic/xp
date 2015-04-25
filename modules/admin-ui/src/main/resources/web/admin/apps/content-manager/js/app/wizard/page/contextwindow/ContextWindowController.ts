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

            this.contextWindowToggler.onActiveChanged((isActive: boolean) => {
                if (isActive) {
                    this.contextWindow.slideIn();
                } else {
                    this.contextWindow.slideOut();
                }
            });

            var liveEditShownHandler = () => {
                this.contextWindowToggler.setEnabled(true);
                this.activateContextWindowForLargeScreen();
                this.slideContextWindowIfNecessary();
            };

            var liveEditHiddenHandler = () => {
                this.contextWindowToggler.setEnabled(false);
            };

            app.wizard.ShowLiveEditEvent.on(liveEditShownHandler);
            app.wizard.ShowSplitEditEvent.on(liveEditShownHandler);
            app.wizard.ShowContentFormEvent.on(liveEditHiddenHandler);

            this.contextWindow.onShown(() => this.activateContextWindowForLargeScreen());
        }

        public slideContextWindowIfNecessary() {
            // triggered by screen resize or split change
            if (this.contextWindowToggler.isActive()) {
                // context window should be shown anyway when toggler is active
                return;
            }
            if (this.contextWindow.isShown() && !this.contextWindow.canAutoSlide()) {
                // hide if it's shown and can not auto slide
                this.contextWindow.slideOut();
            } else if (!this.contextWindow.isShown() && this.contextWindow.canAutoSlide() && this.contextWindow.isInspecting()) {
                // show if it's not shown, but inspecting and can auto slide
                this.contextWindow.slideIn();
            }
        }

        private activateContextWindowForLargeScreen() {
            // triggered on show, or split change
            if (this.contextWindow.isFloating()) {
                this.contextWindowToggler.setActive(false);
            } else {
                this.contextWindowToggler.setActive(true);
            }
        }

    }

}