module app.wizard.page {

    export class PageTemplateSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.page.PageTemplateSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.page.PageTemplateSummary>, index:number):api.ui.combobox.SelectedOption<api.content.page.PageTemplateSummary> {
            return new api.ui.combobox.SelectedOption<api.content.page.PageTemplateSummary>(new PageTemplateSelectedOptionView(option), option, index);
        }
    }
}
