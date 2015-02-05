module app.browse {

    export class InstallModuleDialog extends api.ui.dialog.ModalDialog {

        private installDialogTitle: InstallModuleDialogTitle;

        private input: api.ui.text.TextInput;

        private installAction: api.ui.Action;

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            this.installDialogTitle = new InstallModuleDialogTitle("Install module", "");

            super({
                title: this.installDialogTitle
            });

            this.addClass("install-module-dialog");

            var container = new api.dom.DivEl();
            this.appendChildToContentPanel(container);

            this.input = api.ui.text.TextInput.large("url-input").setPlaceholder("Module URL");
            var title = new api.dom.H6El();
            title.setHtml("Enter the URL of the module to be installed.");
            container.appendChild(title);
            container.appendChild(this.input);


            api.dom.Body.get().appendChild(this);

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                } else if (event.keyCode === 13) {
                    this.installAction.execute();
                }
            });

            this.installAction = new api.ui.Action('Install', 'enter');
            this.installAction.onExecuted(() => {
                var url = this.input.getValue();
                if (!url || !url.trim()) {
                    return;
                }

                new api.module.InstallModuleRequest(url).sendAndParse()
                    .then(() => {
                        api.notify.showFeedback('Module \'' + url + '\' installed');
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle('Error while installing module.');
                    }).done();

                this.close();
                this.input.setValue('');
            });
            this.addAction(this.installAction, true);
        }

        show() {
            super.show();

            if (this.input.getValue()) {
                this.input.selectText();
            }
            this.input.giveFocus();
        }

    }

    export class InstallModuleDialogTitle extends api.ui.dialog.ModalDialogHeader {

        private pathEl: api.dom.PEl;

        constructor(title: string, path: string) {
            super(title);

            this.pathEl = new api.dom.PEl('path');
            this.pathEl.setHtml(path);
            this.appendChild(this.pathEl);
        }

        setPath(path: string) {
            this.pathEl.setHtml(path);
        }
    }

}