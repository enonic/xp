module api.form.inputtype.content.image {

    export class SelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<api.content.ContentSummary> {

        private numberOfOptionsPerRow:number = 3;

        private optionCount:number = 0;

        private selectedOption:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>;

        private dialog:ImageSelectorDialog;

        private editSelectedOptionListeners: {(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>): void}[] = [];

        private removeSelectedOptionListeners: {(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>): void}[] = [];

        constructor() {
            super();

            this.dialog = new ImageSelectorDialog();
            this.dialog.hide();
            this.appendChild(this.dialog);
            this.dialog.addRemoveButtonClickListener(() => {
                this.removeOptionView(this.selectedOption);
                this.notifyRemoveSelectedOption(this.selectedOption);
            });
            this.dialog.addEditButtonClickListener(() => {
                this.notifyEditSelectedOption(this.selectedOption);
            });
        }

        private notifyRemoveSelectedOption(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) {
            this.removeSelectedOptionListeners.forEach( (listener) => {
                listener(option);
            });
        }

        addRemoveSelectedOptionListener(listener: (option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => void) {
            this.removeSelectedOptionListeners.push(listener);
        }

        removeRemoveSelectedOptionListener(listener: (option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => void) {
            this.removeSelectedOptionListeners = this.removeSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditSelectedOption(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) {
            this.editSelectedOptionListeners.forEach( (listener) => {
                listener(option);
            });
        }

        addEditSelectedOptionListener(listener:(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => void) {
            this.editSelectedOptionListeners.push(listener);
        }

        removeEditSelectedOptionListener(listener: (option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => void) {
            this.editSelectedOptionListeners = this.editSelectedOptionListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        createSelectedOption(option:api.ui.selector.Option<api.content.ContentSummary>, index:number):api.ui.selector.combobox.SelectedOption<api.content.ContentSummary> {

            return new api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>(new SelectedOptionView(option), option, index);
        }

        addOptionView(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) {

            this.dialog.hide();
            var optionView:SelectedOptionView = <SelectedOptionView>option.getOptionView();

            optionView.onClicked(() => {
                this.selectedOption = option;
                this.showImageSelectorDialog(option.getOption(), option.getIndex());
            });
            optionView.addSelectedOptionToBeRemovedListener((optionView:api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary>) => {
                this.removeOptionView(option);
                this.notifyRemoveSelectedOption(option);
            });

            this.appendChild(optionView);
            this.optionCount++;
            optionView.setLastInRow(this.isLastInRow(option.getIndex()));

            this.refreshStyles();
        }

        removeOptionView(option:api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) {
            super.removeOptionView(option);

            this.optionCount--;

            this.refreshStyles();
        }

        showImageSelectorDialog(selectedOption:api.ui.selector.Option<api.content.ContentSummary>, selectedOptionIndex:number) {

            var content = selectedOption.displayValue;

            this.dialog.setContent(content);
            this.dialog.show();

            var selectedOptionViews = this.getSelectedOptionViews();

            for (var i = 0 ; i < selectedOptionViews.length ; i++) {
                var view = <SelectedOptionView>selectedOptionViews[i];
                var passedSelectedOption = i >= selectedOptionIndex;
                if( view.isLastInRow() && passedSelectedOption ) {
                    this.dialog.insertAfterEl( view );
                    break;
                }
            }

            for (var i = 0 ; i < selectedOptionViews.length ; i++) {
                selectedOptionViews[i].removeClass("editing");
            }
            selectedOptionViews[selectedOptionIndex].addClass("editing");
        }

        private isLastInRow(index:number):boolean {

            if ((index + 1) == this.optionCount) {
                // last option
                return true;
            }
            else if ((index + 1) % this.numberOfOptionsPerRow == 0) {
                // option at end of row
                return true;
            }
            else {
                return false;
            }
        }

        private refreshStyles() {
            this.getSelectedOptionViews().forEach((view:SelectedOptionView, index:number)=>{
                view.setLastInRow(this.isLastInRow(index));
            });
        }

    }

}