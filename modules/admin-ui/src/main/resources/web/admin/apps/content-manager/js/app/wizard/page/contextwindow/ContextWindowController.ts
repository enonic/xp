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
                if (this.contextWindow.isLiveFormShown()) {
                    this.contextWindowToggler.setEnabled(true);
                }
            };

            var liveEditHiddenHandler = () => {
                this.contextWindowToggler.setEnabled(false);
            };

            app.wizard.ShowLiveEditEvent.on(liveEditShownHandler);
            app.wizard.ShowSplitEditEvent.on(liveEditShownHandler);
            app.wizard.ShowContentFormEvent.on(liveEditHiddenHandler);
        }
    }

}