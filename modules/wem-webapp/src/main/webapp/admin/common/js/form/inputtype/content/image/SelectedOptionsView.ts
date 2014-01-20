module api.form.inputtype.content.image {

    export class SelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.ContentSummary> {

        private numberOfOptionsPerRow:number = 3;

        private optionCount:number = 0;

        private clearer:api.dom.DivEl;

        private editedSelectedOptionView:api.ui.combobox.SelectedOptionView<api.content.ContentSummary>;

        private dialog:ImageSelectorDialog;

        constructor() {
            super();

            this.dialog = new ImageSelectorDialog();
            this.dialog.hide();
            this.appendChild(this.dialog);
            this.dialog.addSelectedOptionRemovedListener(() => {
                this.editedSelectedOptionView.notifySelectedOptionToBeRemoved();
            });

            this.clearer = new api.dom.DivEl("clearer");
            this.appendChild(this.clearer);
        }

        createSelectedOption(option:api.ui.combobox.Option<api.content.ContentSummary>, index:number):api.ui.combobox.SelectedOption<api.content.ContentSummary> {

            return new api.ui.combobox.SelectedOption<api.content.ContentSummary>(new SelectedOptionView(option), option, index);
        }

        addOptionView(selectedOption:api.ui.combobox.SelectedOption<api.content.ContentSummary>) {

            this.dialog.hide();
            var optionView:SelectedOptionView = <SelectedOptionView>selectedOption.getOptionView();

            optionView.addClickEventListener(() => {
                this.editedSelectedOptionView = optionView;
                this.showImageSelectorDialog(selectedOption.getOption(), selectedOption.getIndex());
            });

            optionView.insertBeforeEl(this.clearer);
            this.optionCount++;
            optionView.setLastInRow(this.isLastInRow(selectedOption.getIndex()));
            this.refreshStyles();
        }

        removeOptionView(selectedOption:api.ui.combobox.SelectedOption<api.content.ContentSummary>) {
            super.removeOptionView(selectedOption);

            this.optionCount--;

            this.refreshStyles();
        }

        showImageSelectorDialog(selectedOption:api.ui.combobox.Option<api.content.ContentSummary>, selectedOptionIndex:number) {

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