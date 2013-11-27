module api_form_inputtype_content_image {

    export class SelectedOptionsView extends api_ui_combobox.SelectedOptionsView<api_content.ContentSummary> {

        private numberOfOptionsPerRow:number = 3;

        private optionCount:number = 0;

        private clearer:api_dom.DivEl;

        private editedSelectedOptionView:api_ui_combobox.SelectedOptionView<api_content.ContentSummary>;

        private dialog:ImageSelectorDialog;

        constructor() {
            super();

            this.dialog = new ImageSelectorDialog();
            this.dialog.hide();
            this.appendChild(this.dialog);
            this.dialog.addSelectedOptionRemovedListener(() => {
                this.editedSelectedOptionView.notifySelectedOptionToBeRemoved();
            });

            this.clearer = new api_dom.DivEl(null, "clearer");
            this.appendChild(this.clearer);
        }

        createSelectedOption(option:api_ui_combobox.Option<api_content.ContentSummary>, index:number):api_ui_combobox.SelectedOption<api_content.ContentSummary> {

            return new api_ui_combobox.SelectedOption<api_content.ContentSummary>(new SelectedOptionView(option), option, index);
        }

        addOptionView(selectedOption:api_ui_combobox.SelectedOption<api_content.ContentSummary>) {

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

        removeOptionView(selectedOption:api_ui_combobox.SelectedOption<api_content.ContentSummary>) {
            super.removeOptionView(selectedOption);

            this.optionCount--;

            this.refreshStyles();
        }

        showImageSelectorDialog(selectedOption:api_ui_combobox.Option<api_content.ContentSummary>, selectedOptionIndex:number) {

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