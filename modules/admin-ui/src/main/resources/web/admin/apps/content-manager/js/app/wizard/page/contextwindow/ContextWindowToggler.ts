module app.wizard.page.contextwindow {

    export class ContextWindowToggler extends api.ui.button.Button {

        constructor() {
            super("");
            this.addClass("icon-menu3 icon-medium toggler");
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