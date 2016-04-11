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

            formView.onLayoutFinished(() => {
                this.handleSelectorsDropdowns(formView);

                this.addClass("animated");
                this.centerMyself();
                wemjq(this.getHTMLElement()).find('input[type=text],textarea,select').first().focus();
                this.updateTabbable();
            });

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
                this.findItemViewsAndSubscribe(element, comboboxArray);
            });
            this.getContentPanel().onScroll((event) => {
                comboboxArray.forEach((comboBox: ComboBox<any>) => {
                    comboBox.hideDropdown();
                });
            });
        }

        private findItemViewsAndSubscribe(element: api.dom.Element, comboboxArray: ComboBox<any>[]) {
            if (api.ObjectHelper.iFrameSafeInstanceOf(element, InputView)) {
                this.checkItemViewAndSubscribe(<InputView> element, comboboxArray);
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(element, api.form.FieldSetView)) {
                var fieldSetView: api.form.FieldSetView = <api.form.FieldSetView> element;
                fieldSetView.getFormItemViews().forEach((formItemView: api.form.FormItemView) => {
                    this.findItemViewsAndSubscribe(formItemView, comboboxArray);
                });
            }
        }

        private checkItemViewAndSubscribe(itemView: api.form.FormItemView, comboboxArray: ComboBox<any>[]) {
            var inputView: InputView = <InputView> itemView;
            if (this.isContentOrImageOrComboSelectorInput(inputView)) {
                var combobox = this.getComboboxFromSelectorInputView(inputView);
                if (!!combobox) {
                    comboboxArray.push(combobox);
                }
                this.subscribeCombobox(combobox);
            }
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
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(inputTypeView, ImageSelector)) {
                contentComboBox = (<ImageSelector> inputTypeView).getContentComboBox();
            } else {
                return (<api.form.inputtype.combobox.ComboBox> inputTypeView).getComboBox();
            }
            return !!contentComboBox ? contentComboBox.getComboBox() : null;
        }

        private isContentOrImageOrComboSelectorInput(inputView: InputView): boolean {
            return !!inputView &&
                   (api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ContentSelector) ||
                    api.ObjectHelper.iFrameSafeInstanceOf(inputView.getInputTypeView(), ImageSelector) ||
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
