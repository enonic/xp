module app.wizard.page {

    export class PageTemplateSelectedOptionView extends api.ui.combobox.SelectedOptionView<api.content.page.PageTemplateSummary> {

        private pageTemplate: api.content.page.PageTemplateSummary;

        constructor(option: api.ui.combobox.Option<api.content.page.PageTemplateSummary>) {
            this.pageTemplate = option.displayValue;
            super(option);
            this.addClass("page-template-selected-option-view");
        }

        layout() {
            var removeButtonEl = new api.dom.AEl(null, "remove");
            var optionValueEl = new api.dom.DivEl(null, 'option-value');

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);

            var pageTemplateSummaryEl = new api.dom.DivEl();
            pageTemplateSummaryEl.setClass("page-template-summary");

            var displayNameEl = new api.dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", this.pageTemplate.getDisplayName());
            displayNameEl.getEl().setInnerHtml(this.pageTemplate.getDisplayName());

            var path = new api.dom.DivEl();
            path.addClass("name");
            path.getEl().setAttribute("title", this.pageTemplate.getName().toString());
            path.getEl().setInnerHtml(this.pageTemplate.getName().toString());

            pageTemplateSummaryEl.appendChild(displayNameEl);
            pageTemplateSummaryEl.appendChild(path);

            optionValueEl.appendChild(pageTemplateSummaryEl);

            removeButtonEl.getEl().addEventListener('click', (event: Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }

    }
}
