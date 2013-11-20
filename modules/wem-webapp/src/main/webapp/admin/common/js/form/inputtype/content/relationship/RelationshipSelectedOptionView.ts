module api_form_inputtype_content_relationship {

    export class RelationshipSelectedOptionView extends api_ui_combobox.SelectedOptionView<api_content.ContentSummary> {

        private content:api_content.ContentSummary;

        constructor(option:api_ui_combobox.Option<api_content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {

            var removeButtonEl = new api_dom.AEl(null, "remove");
            var optionValueEl = new api_dom.DivEl(null, 'option-value');

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);

            var imgEl = new api_dom.ImgEl();
            imgEl.setClass("icon");
            imgEl.getEl().setSrc(this.content.getIconUrl());
            optionValueEl.appendChild(imgEl);

            var contentSummaryEl = new api_dom.DivEl();
            contentSummaryEl.setClass("content-summary");

            var displayNameEl = new api_dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", this.content.getDisplayName());
            displayNameEl.getEl().setInnerHtml(this.content.getDisplayName());

            var path = new api_dom.DivEl();
            path.addClass("path");
            path.getEl().setAttribute("title", this.content.getPath().toString());
            path.getEl().setInnerHtml(this.content.getPath().toString());

            contentSummaryEl.appendChild(displayNameEl);
            contentSummaryEl.appendChild(path);

            optionValueEl.appendChild(contentSummaryEl);

            removeButtonEl.getEl().addEventListener('click', (event:Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }
    }

}