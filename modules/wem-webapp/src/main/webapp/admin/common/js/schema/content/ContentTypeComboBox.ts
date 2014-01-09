module api.schema.content {
    export class ContentTypeComboBox extends api.ui.combobox.RichComboBox<ContentTypeSummary> {

        private multipleSelection:boolean;

        constructor(multiple:boolean = true)
        {
            super(new api.ui.combobox.RichComboBoxBuilder<ContentTypeSummary>().setLoader(new ContentTypeSummaryLoader()).setSelectedOptionsView(new RootContentSelectedOptionsView()));
            this.multipleSelection = multiple;
        }


        optionFormatter(row:number, cell:number, content:ContentTypeSummary, columnDef:any, dataContext:api.ui.combobox.Option<ContentTypeSummary>):string {
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
            path.getEl().setAttribute("title", content.getKey());
            path.getEl().setInnerHtml(content.getKey());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        createConfig():api.ui.combobox.ComboBoxConfig<ContentTypeSummary> {
            var config:api.ui.combobox.ComboBoxConfig<ContentTypeSummary> = super.createConfig();
            config.maximumOccurrences = this.multipleSelection ? 0 : 1;

            return config;
        }
    }

    export class RootContentSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option:api.ui.combobox.Option<ContentTypeSummary>, index:number):ui.combobox.SelectedOption<ContentTypeSummary> {
            var optionView = new RootContentSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<ContentTypeSummary>( optionView, option, index);
        }
    }

    export class RootContentSelectedOptionView extends ui.combobox.RichSelectedOptionView<ContentTypeSummary> {


        constructor(option:ui.combobox.Option<ContentTypeSummary>) {
            super(option);
        }

        resolveIconUrl(content:ContentTypeSummary):string
        {
            return content.getIconUrl();
        }

        resolveTitle(content:ContentTypeSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:ContentTypeSummary):string
        {
            return content.getKey();
        }

    }
}