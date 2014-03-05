module app.contextwindow.image {

    export class ImageSelectPanelSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option:api.ui.selector.Option<api.content.ContentSummary>, index:number):api.ui.selector.combobox.SelectedOption<api.content.ContentSummary> {
            var optionView = new ImageSelectPanelSelectedOptionView( option );
            return new api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>( optionView, option, index);
        }
    }
}