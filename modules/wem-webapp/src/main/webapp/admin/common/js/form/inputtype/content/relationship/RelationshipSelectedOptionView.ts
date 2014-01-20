module api.form.inputtype.content.relationship {

    export class RelationshipSelectedOptionView extends api.ui.combobox.SelectedOptionView<api.content.ContentSummary> {

        private content:api.content.ContentSummary;

        constructor(option:api.ui.combobox.Option<api.content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {

            var removeButtonEl = new api.dom.AEl("remove");
            var optionValueEl = new api.dom.DivEl('option-value');

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);

            var imgEl = new api.dom.ImgEl();
            imgEl.setClass("icon");
            imgEl.getEl().setSrc(this.content.getIconUrl());
            optionValueEl.appendChild(imgEl);

            var contentSummaryEl = new api.dom.DivEl();
            contentSummaryEl.setClass("content-summary");

            var displayNameEl = new api.dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", this.content.getDisplayName());
            displayNameEl.getEl().setInnerHtml(this.content.getDisplayName());

            var path = new api.dom.DivEl();
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