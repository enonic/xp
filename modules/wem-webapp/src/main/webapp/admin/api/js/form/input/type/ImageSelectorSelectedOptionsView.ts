module api_form_input_type {

    export class ImageSelectorSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        private selectedItems:api_ui_combobox.OptionData<api_content.ContentSummary>[] = [];

        private editedItem:api_ui_combobox.OptionData<api_content.ContentSummary>;

        private dialog:api_dom.DivEl;

        addItem(optionData:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            if (this.dialog) {
                this.dialog.remove();
                if (this.editedItem) {
                    this.getChildren()[this.selectedItems.indexOf(this.editedItem)].removeClass("editing");
                }
            }

            this.selectedItems.push(optionData);
            var item = optionData.displayValue;

            var option = new api_dom.DivEl(null, "selected-option");
            option.getEl().setBackgroundImage("url("+item.getIconUrl()+"?size=140&thumbnail=false)");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(item.getName());
            option.appendChild(label);

            option.getEl().addEventListener("click", () => {
                this.showImageSelectorDialog(optionData);
            });

            this.appendChild(option);
        }

        showImageSelectorDialog(optionData:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            var item = optionData.displayValue;

            if (this.dialog) {
                this.dialog.remove();
            }
            if (this.editedItem == optionData) {
                this.getChildren()[this.selectedItems.indexOf(optionData)].removeClass("editing");
                this.editedItem = null;
                return;
            } else {
                if (this.editedItem) {
                    this.getChildren()[this.selectedItems.indexOf(this.editedItem)].removeClass("editing");
                }
                this.getChildren()[this.selectedItems.indexOf(optionData)].addClass("editing");
                this.editedItem = optionData;
            }

            this.dialog = new api_dom.PEl().addClass("dialog");

            var name = new api_dom.H1El();
            name.getEl().setInnerHtml(item.getName());
            this.dialog.appendChild(name);

            var path = new api_dom.PEl();
            path.getEl().setInnerHtml(item.getPath().toString());
            this.dialog.appendChild(path);

            var buttonsBar = new api_dom.DivEl().addClass("buttons-bar");

            var editButton = new api_ui.Button("Edit").addClass("edit");
            buttonsBar.appendChild(editButton);

            var removeButton = new api_ui.Button("Remove").addClass("remove");
            removeButton.getEl().addEventListener("click", (event) => {
                this.dialog.remove();
                this.editedItem = null;
                var itemIndex = this.selectedItems.indexOf(optionData);
                this.selectedItems.splice(itemIndex, 1);
                this.getChildren()[itemIndex].remove();
                this.notifySelectedOptionRemoved(optionData);
            });
            buttonsBar.appendChild(removeButton);

            this.dialog.appendChild(buttonsBar);

            var itemIndex = this.selectedItems.indexOf(optionData);
            var insertIndex = Math.min(itemIndex - (itemIndex % 3) + 3, this.selectedItems.length) - 1;

            this.dialog.insertAfterEl(this.getChildren()[insertIndex]);
        }

    }


}