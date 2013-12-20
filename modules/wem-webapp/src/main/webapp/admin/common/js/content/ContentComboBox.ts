module api_content {
    export class ContentComboBox extends api_ui_combobox.RichComboBox<api_content.ContentSummary> {

        private multipleSelection:boolean;

        constructor(multiple:boolean = true)
        {
            super(new api_form_inputtype_content.ContentSummaryLoader(), new RootContentSelectedOptionsView());
            this.multipleSelection = multiple;
        }


        optionFormatter(row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.Option<api_content.ContentSummary>):string {
            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("item-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api_dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        createConfig():api_ui_combobox.ComboBoxConfig<api_content.ContentSummary> {
            var config:api_ui_combobox.ComboBoxConfig<api_content.ContentSummary> = super.createConfig();
            config.maximumOccurrences = this.multipleSelection ? 0 : 1;
            return config;
        }
    }

    export class RootContentSelectedOptionsView extends api_ui_combobox.SelectedOptionsView<api_content.ContentSummary> {

        createSelectedOption(option:api_ui_combobox.Option<api_content.ContentSummary>, index:number):api_ui_combobox.SelectedOption<api_content.ContentSummary> {
            var optionView = new RootContentSelectedOptionView( option );
            return new api_ui_combobox.SelectedOption<api_content.ContentSummary>( optionView, option, index);
        }
    }

    export class RootContentSelectedOptionView extends api_ui_combobox.RichSelectedOptionView<api_content.ContentSummary> {


        constructor(option:api_ui_combobox.Option<api_content.ContentSummary>) {
            super(option);
        }

        resolveIconUrl(content:api_content.ContentSummary):string
        {
            return content.getIconUrl();
        }

        resolveTitle(content:api_content.ContentSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api_content.ContentSummary):string
        {
            return content.getPath().toString();
        }

    }
}