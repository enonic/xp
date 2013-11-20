module app_contextwindow_image {

    export class ImageSelectPanelSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        createSelectedOption(option:api_ui_combobox.OptionData<api_content.ContentSummary>, index:number):api_ui_combobox.SelectedOption<api_content.ContentSummary> {
            var optionView = new ImageSelectPanelSelectedOptionView( option );
            return new api_ui_combobox.SelectedOption<api_content.ContentSummary>( optionView, option, index);
        }
    }
}