module api.content.site.inputtype.siteconfigurator {

    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import DivEl = api.dom.DivEl;
    import FormView = api.form.FormView;
    import ModalDialogHeader = api.ui.dialog.ModalDialogHeader;
    import InputView = api.form.InputView;
    import ContentSelector = api.content.form.inputtype.contentselector.ContentSelector;
    import PrincipalSelector = api.content.form.inputtype.principalselector.PrincipalSelector;
    import ImageSelector = api.content.form.inputtype.image.ImageSelector;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import CreateHtmlAreaDialogEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;
    import Application = api.application.Application;

    export class SiteConfiguratorDialog extends api.ui.dialog.ModalDialog {

        public static debug: boolean = false;

        private formView: FormView;

        private okCallback: () => void;

        private cancelCallback: () => void;

        constructor(application:Application, formView:FormView, okCallback?:() => void, cancelCallback?:() => void) {
            super();

            this.appendChildToHeader(this.getHeaderContent(application));

            //this.setTitleConfig(this.initHeader(application));
            this.formView = formView;
            this.okCallback = okCallback;
            this.cancelCallback = cancelCallback;

            this.addClass("site-configurator-dialog");

            CreateHtmlAreaDialogEvent.on((event: CreateHtmlAreaDialogEvent) => {
                this.addClass("masked");

                api.util.htmlarea.dialog.HTMLAreaDialogHandler.getOpenDialog().onRemoved(() => {
                    this.removeClass("masked");
                });
            });
        }

        doRender(): Q.Promise<boolean> {
            return super.doRender().then((rendered) => {
                if (SiteConfiguratorDialog.debug) {
                    console.debug("SiteConfiguratorDialog.doRender");
                }

                this.appendChildToContentPanel(this.formView);

                wemjq(this.getHTMLElement()).find('input[type=text],textarea,select').first().focus();
                this.updateTabbable();

                this.addOkButton(this.okCallback);
                this.getCancelAction().onExecuted(() => this.cancelCallback());

                this.addCancelButtonToBottom();

                return this.formView.layout().then(() => {
                    this.addClass("animated");
                    this.centerMyself();

                    this.handleSelectorsDropdowns(this.formView);
                    this.handleDialogClose(this.formView);

                    return rendered;
                });
            });
        }

        private addOkButton(okCallback: () => void) {
            var okAction = new api.ui.Action("Apply");
            this.addAction(okAction, true, true);
            okAction.onExecuted(() => {
                if (okCallback) {
                    okCallback();
                }
                this.close();
            });
        }

        protected getHeaderContent(application: Application): api.app.NamesAndIconView {
            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(
                api.app.NamesAndIconViewSize.large)).setMainName(application.getDisplayName()).setSubName(
                application.getName() + "-" + application.getVersion());

            if (application.getIconUrl()) {
                namesAndIconView.setIconUrl(application.getIconUrl());
            }

            if (application.getDescription()) {
                namesAndIconView.setSubName(application.getDescription());
            }

            return namesAndIconView;
        }

        private handleSelectorsDropdowns(formView: FormView) {
            var comboboxes = this.getComboboxesFromFormView(formView);

            this.getContentPanel().onScroll((event) => {
                comboboxes.forEach((comboBox: ComboBox<any>) => {
                    comboBox.hideDropdown();
                });
            });
        }

        private getComboboxesFromFormView(formView: FormView): ComboBox<any>[] {
            var comboboxArray = [];

            formView.getChildren().forEach((element: api.dom.Element) => {
                this.findComboboxesInElement(element, comboboxArray);
            });

            return comboboxArray;
        }

        private handleDialogClose(formView: FormView) {
            let imageSelector;
            formView.getChildren().forEach((element: api.dom.Element) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(element, InputView)) {
                    imageSelector = (<InputView> element).getInputTypeView().getElement();
                    if (api.ObjectHelper.iFrameSafeInstanceOf(imageSelector, ImageSelector)) {
                        (<ImageSelector> imageSelector).onEditContentRequest(this.close.bind(this));
                    }
                }
            });
        }

        private findComboboxesInElement(element: api.dom.Element, comboboxArray: ComboBox<any>[]) {
            if (api.ObjectHelper.iFrameSafeInstanceOf(element, InputView)) {
                this.findComboboxInItemView(<InputView> element, comboboxArray);
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(element, api.form.FieldSetView)) {
                var fieldSetView: api.form.FieldSetView = <api.form.FieldSetView> element;
                fieldSetView.getFormItemViews().forEach((formItemView: api.form.FormItemView) => {
                    this.findComboboxesInElement(formItemView, comboboxArray);
                });
            }
        }

        private findComboboxInItemView(itemView: api.form.FormItemView, comboboxArray: ComboBox<any>[]) {
            var inputView: InputView = <InputView> itemView;
            if (this.isContentOrImageOrPrincipalOrComboSelectorInput(inputView)) {
                var combobox = this.getComboboxFromSelectorInputView(inputView);
                if (!!combobox) {
                    comboboxArray.push(combobox);
                }
            }
        }

        private getComboboxFromSelectorInputView(inputView: InputView): ComboBox<any> {
            var contentComboBox,
                inputTypeView = inputView.getInputTypeView();
            if (api.ObjectHelper.iFrameSafeInstanceOf(inputTypeView, ContentSelector)) {
                contentComboBox = (<ContentSelector> inputTypeView).getContentComboBox();
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(inputTypeView, ImageSelector)) {
                contentComboBox = (<ImageSelector> inputTypeView).getContentComboBox();
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(inputTypeView, PrincipalSelector)) {
                contentComboBox = (<PrincipalSelector> inputTypeView).getPrincipalComboBox();
            } else {
                return (<api.form.inputtype.combobox.ComboBox> inputTypeView).getComboBox();
            }
            return !!contentComboBox ? contentComboBox.getComboBox() : null;
        }

        private isContentOrImageOrPrincipalOrComboSelectorInput(inputView: InputView): boolean {
            return !!inputView &&
                   (api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ContentSelector) ||
                    api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ImageSelector) ||
                    api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), PrincipalSelector) ||
                    api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), api.form.inputtype.combobox.ComboBox));
        }


        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }
    }
}
