module api.form.inputtype.content.relationship {

    export class RelationshipSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.ContentSummary>, index:number):api.ui.combobox.SelectedOption<api.content.ContentSummary> {
            return new api.ui.combobox.SelectedOption<api.content.ContentSummary>(new RelationshipSelectedOptionView(option), option, index);
        }
    }
}