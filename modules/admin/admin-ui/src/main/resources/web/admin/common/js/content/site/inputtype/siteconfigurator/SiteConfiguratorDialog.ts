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

    export class SiteConfiguratorDialog extends api.ui.dialog.ModalDialog {

        public static debug: boolean = false;

        private formView: FormView;

        private okCallback: () => void;

        private cancelCallback: () => void;

        constructor(name: string, subName: string, formView: FormView, okCallback?: () => void, cancelCallback?: () => void) {
            super({
                title: this.initHeader(name, subName)
            });

            this.formView = formView;
            this.okCallback = okCallback;
            this.cancelCallback = cancelCallback;

            this.addClass("site-configurator-dialog");

            CreateHtmlAreaDialogEvent.on((event: CreateHtmlAreaDialogEvent) => {
                this.addClass("masked");

                api.util.htmlarea.dialog.HTMLAreaDialogHandler.getOpenDialog().onRemoved(() => {
                    this.removeClass("masked");
                })
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

        private initHeader(name: string, subName: string): ModalDialogHeader {
            var dialogHeader = new ModalDialogHeader("");

            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(
                api.app.NamesAndIconViewSize.large)).setMainName(name).setSubName(subName).setIconClass("icon-xlarge icon-puzzle");

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
            if (this.isContentOrImageOrPrincipalOrComboSelectorInput(inputView)) {
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
