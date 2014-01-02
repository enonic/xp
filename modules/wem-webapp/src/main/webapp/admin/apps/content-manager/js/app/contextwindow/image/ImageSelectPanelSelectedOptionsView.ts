module app.contextwindow.image {

    export class ImageSelectPanelSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.ContentSummary>, index:number):api.ui.combobox.SelectedOption<api.content.ContentSummary> {
            var optionView = new ImageSelectPanelSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<api.content.ContentSummary>( optionView, option, index);
        }
    }
}