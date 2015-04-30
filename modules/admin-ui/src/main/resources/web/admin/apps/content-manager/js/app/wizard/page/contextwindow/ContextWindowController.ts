module app.wizard.page.contextwindow {

    import ComponentView = api.liveedit.ComponentView;
    import Component = api.content.page.region.Component;
    import PageView = api.liveedit.PageView;

    export class ContextWindowController {

        private contextWindow: ContextWindow;

        private contextWindowToggler: ContextWindowToggler;

        private togglerOverriden: boolean = false;

        constructor(contextWindow: ContextWindow, contextWindowToggler: ContextWindowToggler) {
            this.contextWindow = contextWindow;
            this.contextWindowToggler = contextWindowToggler;

            this.contextWindowToggler.onClicked((event: MouseEvent) => {
                // set overriden flag when toggle is on by click only
                if (this.contextWindowToggler.isEnabled()) {
                    this.togglerOverriden = true;
                }
            });

            this.contextWindowToggler.onActiveChanged((isActive: boolean) => {
                if (isActive) {
                    this.contextWindow.slideIn();
                } else {
                    this.contextWindow.slideOut();
                }
            });

            var liveEditShownHandler = () => {
                this.contextWindowToggler.setEnabled(true);
                this.splitModeChangedHandler();
            };

            var liveEditHiddenHandler = () => {
                this.contextWindowToggler.setEnabled(false);
                this.splitModeChangedHandler();
            };

            app.wizard.ShowLiveEditEvent.on(liveEditShownHandler);
            app.wizard.ShowSplitEditEvent.on(liveEditShownHandler);
            app.wizard.ShowContentFormEvent.on(liveEditHiddenHandler);

            this.contextWindow.onShown(() => this.splitModeChangedHandler());
        }

        public splitModeChangedHandler() {
            // reset manual toggle override flag
            this.togglerOverriden = false;
            if (!this.contextWindow.isFloating()) {
                // set toggler active by default for large screen
                this.contextWindowToggler.setActive(true);
            } else if (!this.togglerOverriden) {
                // otherwise toggle it off if not overriden manually ( don't hide the context window if inspecting and can autoslide though )
                this.contextWindowToggler.setActive(false, this.contextWindow.isInspecting() && this.contextWindow.canAutoSlide());
            }
        }

        public resizeHandler() {
            if (this.contextWindowToggler.isActive()) {
                // context window should be shown anyway when toggler is active
                return;
            }
            if (this.contextWindow.isShown() && !this.contextWindow.canAutoSlide()) {
                // hide if it's shown and can not auto slide no matter if we inspecting or not
                this.contextWindow.slideOut();
            } else if (!this.contextWindow.isShown() && this.contextWindow.canAutoSlide() && this.contextWindow.isInspecting()) {
                // show if it's not shown, but inspecting and can auto slide
                this.contextWindow.slideIn();
            }
        }

    }

}