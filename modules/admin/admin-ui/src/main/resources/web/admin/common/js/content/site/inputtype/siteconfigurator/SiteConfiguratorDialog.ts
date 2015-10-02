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

        private subscribeOnDropdownExpand() {

            wemjq.each(wemjq(this.getHTMLElement()).find('.dropdown-handle'), (key, value) => {
                wemjq(value).click(() => {
                    if (wemjq(value).hasClass("down")) {
                        var optionsContainer = wemjq(value).nextAll(".options-container").get(0);
                        this.scrollToExpandedDropdownIfNotVisible(optionsContainer);
                    }
                });
                console.log(key + ": " + value);
            });
        }

        private scrollToExpandedDropdownIfNotVisible(dropdown: HTMLElement) {
            if (dropdown) {
                var optionsContainerElement: api.dom.Element = api.dom.Element.fromHtmlElement(dropdown);
                setTimeout(() => {
                    var expandedDropdownBottomPosition = this.getAbsoluteBottomPosition(optionsContainerElement),
                        siteDialogBottomPosition = this.getAbsoluteBottomPosition(this),
                        dialogBottomAreaHeight = this.getButtonRow().getEl().getHeightWithMargin() + this.getEl().getMarginBottom();
                    if (expandedDropdownBottomPosition > (siteDialogBottomPosition - dialogBottomAreaHeight)) {
                        this.getContentPanel().getEl().setScrollTop(this.getContentPanel().getEl().getScrollTop() +
                                                                    optionsContainerElement.getEl().getHeightWithBorder());
                    }
                }, 500);
            }
        }

        private getAbsoluteBottomPosition(element: api.dom.Element): number {
            var el = element.getEl(),
                bottomPosition: number = (el.getOffset().top +
                                          el.getHeightWithBorder());
            return bottomPosition;
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
            setTimeout(() => {
                this.addClass("animated");
                this.centerMyself();
                wemjq(this.getHTMLElement()).find('input[type=text],textarea,select').first().focus();
                this.subscribeOnDropdownExpand();
            }, 100);
        }

        close() {
            super.close();
            this.remove();
        }
    }
}
