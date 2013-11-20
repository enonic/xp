module api_form_inputtype_content_relationship {

    export class RelationshipSelectedOptionsView extends api_ui_combobox.SelectedOptionsView<api_content.ContentSummary> {

        createSelectedOption(option:api_ui_combobox.Option<api_content.ContentSummary>, index:number):api_ui_combobox.SelectedOption<api_content.ContentSummary> {
            return new api_ui_combobox.SelectedOption<api_content.ContentSummary>(new RelationshipSelectedOptionView(option), option, index);
        }
    }
}