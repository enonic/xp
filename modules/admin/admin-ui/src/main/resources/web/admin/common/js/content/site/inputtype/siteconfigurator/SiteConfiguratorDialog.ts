module api.content.site.inputtype.siteconfigurator {

    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import DivEl = api.dom.DivEl;
    import FormView = api.form.FormView;
    import ModalDialogHeader = api.ui.dialog.ModalDialogHeader;
    import InputView = api.form.InputView;
    import ContentSelector = api.content.form.inputtype.contentselector.ContentSelector;
    import ImageSelector = api.content.form.inputtype.image.ImageSelector;
    import ComboBox = api.ui.selector.combobox.ComboBox;

    export class SiteConfiguratorDialog extends api.ui.dialog.ModalDialog {

        constructor(name: string, subName: string, formView: FormView, okCallback?: () => void, cancelCallback?: () => void) {
            super({
                title: this.initHeader(name, subName)
            });

            this.getEl().addClass("site-configurator-dialog");
            this.appendChildToContentPanel(formView);
            this.handleSelectorsDropdowns(formView);

            this.addOkButton(okCallback);
            this.getCancelAction().onExecuted(() => cancelCallback());

            this.addCancelButtonToBottom();

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

        private handleSelectorsDropdowns(formView: FormView) {
            var comboboxArray = [];
            formView.getChildren().forEach((element: api.dom.Element) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(element, InputView)) {
                    var inputView: InputView = <InputView> element;
                    if (this.isContentOrImageSelector(inputView)) {
                        var combobox = this.getComboboxFromSelectorInputView(inputView);
                        if (!!combobox) {
                            comboboxArray.push(combobox);
                        }
                        this.subscribeCombobox(combobox);
                    }
                }
            });
            this.getContentPanel().onScroll((event) => {
                comboboxArray.forEach((comboBox: ComboBox<any>) => {
                    comboBox.hideDropdown();
                });
            });
        }

        private subscribeCombobox(comboBox: ComboBox<any>) {
            if (!!comboBox) {
                comboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                    if (event.isExpanded()) {
                        this.adjustSelectorDropDown(comboBox.getInput(), event.getDropdownElement().getEl());
                    }
                });
            }
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }

        private getComboboxFromSelectorInputView(inputView: InputView): ComboBox<any> {
            var contentComboBox,
                inputTypeView = inputView.getInputTypeView();
            if (api.ObjectHelper.iFrameSafeInstanceOf(inputTypeView, ContentSelector)) {
                contentComboBox = (<ContentSelector> inputTypeView).getContentComboBox();
            } else {
                contentComboBox = (<ImageSelector> inputTypeView).getContentComboBox();
            }
            return !!contentComboBox ? contentComboBox.getComboBox() : null;
        }

        private isContentOrImageSelector(inputView: InputView): boolean {
            return !!inputView &&
                   (api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ContentSelector) ||
                    api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ImageSelector));
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
            setTimeout(() => {
                this.addClass("animated");
                this.centerMyself();
                wemjq(this.getHTMLElement()).find('input[type=text],textarea,select').first().focus();
            }, 100);
        }

        close() {
            super.close();
            this.remove();
        }
    }
}
