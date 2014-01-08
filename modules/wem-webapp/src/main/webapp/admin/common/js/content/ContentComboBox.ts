module api.content {
    export class ContentComboBox extends api.ui.combobox.RichComboBox<api.content.ContentSummary> {

        private multipleSelection:boolean;

        constructor(multiple:boolean = true)
        {
            var builder:api.ui.combobox.RichComboBoxBuilder<api.content.ContentSummary> = new api.ui.combobox.RichComboBoxBuilder<api.content.ContentSummary>();
            builder.setComboBoxName("contentSelector" ).setLoader(new api.form.inputtype.content.ContentSummaryLoader() ).
                setSelectedOptionsView(new ContentSelectedOptionsView());
            super(builder);
            this.multipleSelection = multiple;
        }


        optionFormatter(row:number, cell:number, content:api.content.ContentSummary, columnDef:any, dataContext:api.ui.combobox.Option<api.content.ContentSummary>):string {
            var img = new api.dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api.dom.DivEl();
            contentSummary.setClass("item-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        createConfig():api.ui.combobox.ComboBoxConfig<api.content.ContentSummary> {
            var config:api.ui.combobox.ComboBoxConfig<api.content.ContentSummary> = super.createConfig();
            config.maximumOccurrences = this.multipleSelection ? 0 : 1;
            return config;
        }
    }

    export class ContentSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.ContentSummary>, index:number):api.ui.combobox.SelectedOption<api.content.ContentSummary> {
            var optionView = new ContentSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<api.content.ContentSummary>( optionView, option, index);
        }
    }

    export class ContentSelectedOptionView extends api.ui.combobox.RichSelectedOptionView<api.content.ContentSummary> {


        constructor(option:api.ui.combobox.Option<api.content.ContentSummary>) {
            super(option);
        }

        resolveIconUrl(content:api.content.ContentSummary):string
        {
            return content.getIconUrl();
        }

        resolveTitle(content:api.content.ContentSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api.content.ContentSummary):string
        {
            return content.getPath().toString();
        }

    }
}