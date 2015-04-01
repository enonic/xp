module app.wizard.page.contextwindow {

    export class ContextWindowToggler extends api.ui.button.Button {

        constructor() {
            super();
            this.addClass("toggle-button icon-menu3 icon-medium");
            this.setActive(false);

            ShowLiveEditEvent.on(() => {
                this.setEnabled(true);
            });

            ShowSplitEditEvent.on(() => {
                this.setEnabled(true);
            });

            ShowContentFormEvent.on(() => {
                this.setEnabled(false);
            });
        }
    }
}