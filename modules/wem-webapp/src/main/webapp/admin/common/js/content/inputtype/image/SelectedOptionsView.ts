module api.content.inputtype.image {

    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ContentSummary = api.content.ContentSummary;

    export class SelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<ContentSummary> {

        private numberOfOptionsPerRow: number = 3;

        private editableOption: SelectedOption<ContentSummary>;

        private dialog: ImageSelectorDialog;

        private editSelectedOptionListeners: {(option: SelectedOption<ContentSummary>): void}[] = [];

        private removeSelectedOptionListeners: {(option: SelectedOption<ContentSummary>): void}[] = [];

        private mouseClickListener:{(MouseEvent): void};

        constructor() {
            super();

            this.dialog = new ImageSelectorDialog();
            this.dialog.hide();
            this.appendChild(this.dialog);
            
            jQuery(this.getHTMLElement()).sortable({
                containment: this.getHTMLElement(),
                cursor: 'move',
                tolerance: 'pointer',
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate()
            });

            this.onShown((event: api.dom.ElementShownEvent) => {
                this.updateLayout();
            });
        }

        private notifyRemoveSelectedOption(option: SelectedOption<ContentSummary>) {
            this.removeSelectedOptionListeners.forEach((listener) => {
                listener(option);
            });
        }

        addRemoveSelectedOptionListener(listener: (option: SelectedOption<ContentSummary>) => void) {
            this.removeSelectedOptionListeners.push(listener);
        }

        removeRemoveSelectedOptionListener(listener: (option: SelectedOption<ContentSummary>) => void) {
            this.removeSelectedOptionListeners = this.removeSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditSelectedOption(option: SelectedOption<ContentSummary>) {
            this.editSelectedOptionListeners.forEach((listener) => {
                listener(option);
            });
        }

        addEditSelectedOptionListener(listener: (option: SelectedOption<ContentSummary>) => void) {
            this.editSelectedOptionListeners.push(listener);
        }

        removeEditSelectedOptionListener(listener: (option: SelectedOption<ContentSummary>) => void) {
            this.editSelectedOptionListeners = this.editSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        createSelectedOption(option: api.ui.selector.Option<ContentSummary>, index: number): SelectedOption<ContentSummary> {

            return new SelectedOption<ContentSummary>(new SelectedOptionView(option), option, index);
        }

        addOptionView(option: SelectedOption<ContentSummary>) {

            this.dialog.hide();
            var optionView: SelectedOptionView = <SelectedOptionView>option.getOptionView();

            optionView.onClicked((event: MouseEvent) => {
                if (this.dialog.isVisible()) {
                    this.hideImageSelectorDialog();
                } else {
                    this.showImageSelectorDialog(option);
                }
            });
            optionView.addSelectedOptionToBeRemovedListener((optionView: SelectedOptionView) => {
                this.removeOptionView(option);
                this.notifyRemoveSelectedOption(option);
            });

            this.appendChild(optionView);
            optionView.getIcon().onLoaded((event: UIEvent) => {
                this.updateOptionViewLayout(optionView, this.calculateOptionHeight());
                jQuery(this.getHTMLElement()).sortable("refresh");
            });
        }

        showImageSelectorDialog(option: SelectedOption<ContentSummary>) {

            var selectedOptionViews = this.getSelectedOptionViews();
            for (var i = 0; i < selectedOptionViews.length; i++) {
                var view = <SelectedOptionView>selectedOptionViews[i];
                var passedSelectedOption = i >= option.getIndex();
                if ((this.isLastInRow(i) || this.isLast(i)) && passedSelectedOption) {
                    this.dialog.insertAfterEl(view);
                    break;
                }
            }

            if (this.editableOption) {
                this.editableOption.getOptionView().removeClass('editing first-in-row last-in-row');
            }
            this.editableOption = option;
            option.getOptionView().addClass('editing' + (this.isFirstInRow(option.getIndex()) ? ' first-in-row' : '') +
                                            (this.isLastInRow(option.getIndex()) ? ' last-in-row' : ''));

            this.dialog.setContent(option.getOption().displayValue);
            if (!this.dialog.isVisible()) {
                this.setOutsideClickListener();
                this.dialog.show();
                this.updateDialogLayout(this.calculateOptionHeight());
            }

            jQuery(this.getHTMLElement()).sortable("disable");
        }

        updateLayout() {
            var optionHeight = this.calculateOptionHeight();
            this.getSelectedOptionViews().forEach((optionView: SelectedOptionView) => {
                this.updateOptionViewLayout(optionView, optionHeight);
            });
            if (this.dialog.isVisible()) {
                this.updateDialogLayout(optionHeight);
            }
        }

        private updateOptionViewLayout(optionView: SelectedOptionView, optionHeight: number) {
            optionView.getEl().setHeightPx(optionHeight);
            var iconHeight = optionView.getIcon().getEl().getHeightWithBorder();
            if (iconHeight < optionHeight && iconHeight !== 0) {
                optionView.getIcon().getEl().setMarginTop((optionHeight - iconHeight) / 2 + 'px');
            }
        }

        private updateDialogLayout(optionHeight) {
            this.dialog.getEl().setMarginTop(((optionHeight / 3) - 20) + 'px');
        }

        private calculateOptionHeight(): number {
            var availableWidth = this.getEl().getWidthWithMargin();
            return Math.floor(0.3 * availableWidth);
        }

        private hideImageSelectorDialog() {
            this.dialog.hide();
            this.editableOption.getOptionView().removeClass('editing first-in-row last-in-row');
            jQuery(this.getHTMLElement()).sortable("enable");
            
            api.dom.Body.get().unClicked(this.mouseClickListener);
        }

        private setOutsideClickListener() {
            var selectedOptionsView = this;
            this.mouseClickListener = (event: MouseEvent) => {
                var viewHtmlElement = selectedOptionsView.getHTMLElement();
                for (var element = event.target; element; element = (<any>element).parentNode) {
                    if (element == viewHtmlElement) {
                        return;
                    }
                }

                selectedOptionsView.hideImageSelectorDialog();
            };

            api.dom.Body.get().onClicked(this.mouseClickListener);
        }

        private isLastInRow(index: number): boolean {
            return (index + 1) % this.numberOfOptionsPerRow == 0;
        }

        private isFirstInRow(index: number): boolean {
            return (index + 1) % this.numberOfOptionsPerRow == 1;
        }

        private isLast(index: number): boolean {
            return (index + 1) == this.getSelectedOptionViews().length;
        }

        private handleDnDUpdate() {
            var optionViews = this.getSelectedOptions().getOptionViews();
            var orderedOptions: SelectedOptionView[] = [];

            var domChildren = this.getHTMLElement().children;
            for (var i = 0; i < domChildren.length; i++) {
                var domChild = <HTMLElement> domChildren[i];
                var childEl = optionViews.filter((optionView: SelectedOptionView) => (optionView.getHTMLElement() == domChild))[0];
                if (childEl) {
                    orderedOptions.push(<SelectedOptionView>childEl);
                }
            }

            orderedOptions.forEach((view: SelectedOptionView, index: number) => {
                this.getSelectedOptions().getByView(view).setIndex(index);
            });
        }

    }

}
