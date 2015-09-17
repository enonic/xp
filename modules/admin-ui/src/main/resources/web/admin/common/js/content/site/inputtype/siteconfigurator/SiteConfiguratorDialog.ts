module api.content.site.inputtype.siteconfigurator {

    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import DivEl = api.dom.DivEl;
    import FormView = api.form.FormView;
    import ModalDialogHeader = api.ui.dialog.ModalDialogHeader;

    export class SiteConfiguratorDialog extends api.ui.dialog.ModalDialog {

        constructor(name: string, subName: string, formView: FormView, okCallback?: () => void, cancelCallback?: () => void) {
            super({
                title: this.initHeader(name, subName)
            });

            this.getEl().addClass("app-configurator-dialog");
            this.appendChildToContentPanel(formView);

            this.addOkButton(okCallback);
            this.addCancelButton(cancelCallback);
        }

        private addOkButton(okCallback: () => void) {
            var okAction = new api.ui.Action("Apply", "enter");
            this.addAction(okAction, true, true);
            okAction.onExecuted(() => {
                if (okCallback) {
                    okCallback();
                }
                this.close();
            });
        }

        addCancelButton(cancelCallback: () => void) {
            var cancelAction = new api.ui.Action("Cancel", "esc");
            cancelAction.setIconClass("cancel-button-bottom");
            cancelAction.onExecuted(()=> {
                if (cancelCallback) {
                    cancelCallback();
                }
                this.close();
            });
            this.addAction(cancelAction);
        }

        private initHeader(name: string, subName: string): ModalDialogHeader {
            var dialogHeader = new ModalDialogHeader("");

            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.large)).
                setMainName(name).
                setSubName(subName).
                setIconClass("icon-xlarge icon-puzzle");

            dialogHeader.appendChild(namesAndIconView);
            return dialogHeader;
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
            setTimeout(() => {
                this.addClass("animated");
                this.centerMyself();
            }, 100);
        }

        close() {
            super.close();
            this.remove();
        }
    }
}
