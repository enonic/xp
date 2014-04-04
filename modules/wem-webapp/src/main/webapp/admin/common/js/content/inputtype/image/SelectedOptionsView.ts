module api.content.inputtype.image {

    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ContentSummary = api.content.ContentSummary;

    export class SelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<ContentSummary> {

        private numberOfOptionsPerRow:number = 3;

        private editableOption:SelectedOption<ContentSummary>;

        private dialog:ImageSelectorDialog;

        private editSelectedOptionListeners: {(option:SelectedOption<ContentSummary>): void}[] = [];

        private removeSelectedOptionListeners: {(option:SelectedOption<ContentSummary>): void}[] = [];

        constructor() {
            super();

            this.dialog = new ImageSelectorDialog();
            this.dialog.hide();
            this.appendChild(this.dialog);
            this.dialog.addRemoveButtonClickListener(() => {
                this.removeOptionView(this.editableOption);
                this.notifyRemoveSelectedOption(this.editableOption);
            });
            this.dialog.addEditButtonClickListener(() => {
                this.notifyEditSelectedOption(this.editableOption);
            });

            this.onShown((event: api.dom.ElementShownEvent) => {
                this.updateLayout();
            });
        }

        private notifyRemoveSelectedOption(option:SelectedOption<ContentSummary>) {
            this.removeSelectedOptionListeners.forEach( (listener) => {
                listener(option);
            });
        }

        addRemoveSelectedOptionListener(listener: (option:SelectedOption<ContentSummary>) => void) {
            this.removeSelectedOptionListeners.push(listener);
        }

        removeRemoveSelectedOptionListener(listener: (option:SelectedOption<ContentSummary>) => void) {
            this.removeSelectedOptionListeners = this.removeSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditSelectedOption(option:SelectedOption<ContentSummary>) {
            this.editSelectedOptionListeners.forEach( (listener) => {
                listener(option);
            });
        }

        addEditSelectedOptionListener(listener:(option:SelectedOption<ContentSummary>) => void) {
            this.editSelectedOptionListeners.push(listener);
        }

        removeEditSelectedOptionListener(listener: (option:SelectedOption<ContentSummary>) => void) {
            this.editSelectedOptionListeners = this.editSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        createSelectedOption(option:api.ui.selector.Option<ContentSummary>, index:number):SelectedOption<ContentSummary> {

            return new SelectedOption<ContentSummary>(new SelectedOptionView(option), option, index);
        }

        addOptionView(option:SelectedOption<ContentSummary>) {

            this.dialog.hide();
            var optionView:SelectedOptionView = <SelectedOptionView>option.getOptionView();

            optionView.onClicked((event: MouseEvent) => {
                this.showImageSelectorDialog(option);
            });
            optionView.addSelectedOptionToBeRemovedListener((optionView:SelectedOptionView) => {
                this.removeOptionView(option);
                this.notifyRemoveSelectedOption(option);
            });

            this.appendChild(optionView);
            optionView.getIcon().onLoaded((event: UIEvent) => {
                this.updateOptionViewLayout(optionView, this.calculateOptionHeight());
            });
        }

        showImageSelectorDialog(option:SelectedOption<ContentSummary>) {

            var selectedOptionViews = this.getSelectedOptionViews();
            for (var i = 0 ; i < selectedOptionViews.length ; i++) {
                var view = <SelectedOptionView>selectedOptionViews[i];
                var passedSelectedOption = i >= option.getIndex();
                if( (this.isLastInRow(i) || this.isLast(i)) && passedSelectedOption ) {
                    this.dialog.insertAfterEl( view );
                    break;
                }
            }

            if (this.editableOption) {
                this.editableOption.getOptionView().removeClass('editing first-in-row last-in-row');
            }
            this.editableOption = option;
            option.getOptionView().addClass('editing' + (this.isFirstInRow(option.getIndex()) ? ' first-in-row' : '') +
                                            (this.isLastInRow(option.getIndex()) ? ' last-in-row':''));

            this.dialog.setContent(option.getOption().displayValue);
            if (!this.dialog.isVisible()) {
                this.setOutsideClickListener();
                this.dialog.show();
                this.updateDialogLayout(this.calculateOptionHeight());
            }
        }

        updateLayout() {
            var optionHeight = this.calculateOptionHeight();
            this.getSelectedOptionViews().forEach((optionView:SelectedOptionView) => {
                this.updateOptionViewLayout(optionView, optionHeight);
            });
            if (this.dialog.isVisible()) {
                this.updateDialogLayout(optionHeight);
            }
        }

        private updateOptionViewLayout(optionView: SelectedOptionView, optionHeight: number) {
            optionView.getEl().setHeightPx(optionHeight);
            var iconHeight = optionView.getIcon().getEl().getHeightWithBorder();
            if (iconHeight < optionHeight) {
                optionView.getIcon().getEl().setMarginTop((optionHeight - iconHeight) / 2 + 'px');
            }
        }

        private updateDialogLayout(optionHeight) {
            this.dialog.getEl().setMarginTop(((optionHeight / 3) - 20) + 'px');
        }

        private calculateOptionHeight():number {
            var availableWidth = this.getEl().getWidthWithMargin();
            return Math.floor(0.3 * availableWidth);
        }

        private setOutsideClickListener() {
            var selectedOptionsView = this;
            var mouseClickListener = (event: MouseEvent) => {
                var viewHtmlElement = selectedOptionsView.getHTMLElement();
                for (var element = event.target; element; element = (<any>element).parentNode) {
                    if (element == viewHtmlElement) {
                        return;
                    }
                }

                selectedOptionsView.dialog.hide();
                selectedOptionsView.editableOption.getOptionView().removeClass('editing first-in-row last-in-row');
                api.dom.Body.get().unClicked(mouseClickListener);
            };

            api.dom.Body.get().onClicked(mouseClickListener);
        }

        private isLastInRow(index:number):boolean {
            return (index + 1) % this.numberOfOptionsPerRow == 0;
        }

        private isFirstInRow(index:number): boolean {
            return (index + 1) % this.numberOfOptionsPerRow == 1;
        }

        private isLast(index:number):boolean {
            return (index + 1) == this.getSelectedOptionViews().length;
        }

    }

}