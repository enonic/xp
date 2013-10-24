module api_form_input_type {

    export class RelationshipSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        addItem(optionData:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            var item = optionData.displayValue;

            var option = new api_dom.DivEl(null, 'selected-option');
            var removeButton = new api_dom.AEl(null, "remove");
            var optionValue = new api_dom.DivEl(null, 'option-value');

            option.appendChild(removeButton);
            option.appendChild(optionValue);

            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(item.getIconUrl());
            optionValue.appendChild(img);

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", item.getDisplayName());
            displayName.getEl().setInnerHtml(item.getDisplayName());

            var path = new api_dom.DivEl();
            path.addClass("path");
            path.getEl().setAttribute("title", item.getPath().toString());
            path.getEl().setInnerHtml(item.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            optionValue.appendChild(contentSummary);

            this.appendChild(option);

            removeButton.getEl().addEventListener('click', (event:Event) => {
                option.remove();
                this.notifySelectedOptionRemoved(optionData);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }
    }

}