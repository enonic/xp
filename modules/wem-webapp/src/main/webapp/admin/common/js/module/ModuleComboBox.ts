module api_module {

    export class ModuleComboBox extends api_ui_combobox.RichComboBox<api_module.ModuleSummary>
    {
        constructor()
        {
            super(new api_module.ModuleLoader(), new ModuleSelectedOptionsView());

        }

        optionFormatter(row:number, cell:number, moduleInst:api_module.ModuleSummary, columnDef:any, dataContext:api_ui_combobox.Option<api_module.ModuleSummary>):string {
            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(api_util.getAdminUri("common/images/default_content.png"));

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("item-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", moduleInst.getDisplayName());
            displayName.getEl().setInnerHtml(moduleInst.getDisplayName());

            var path = new api_dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", moduleInst.getUrl());
            path.getEl().setInnerHtml(moduleInst.getUrl());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }
    }

    export class ModuleSelectedOptionsView extends api_ui_combobox.SelectedOptionsView<api_module.ModuleSummary> {

        createSelectedOption(option:api_ui_combobox.Option<api_module.ModuleSummary>, index:number):api_ui_combobox.SelectedOption<api_module.ModuleSummary> {
            var optionView = new ModuleSelectedOptionView( option );
            return new api_ui_combobox.SelectedOption<api_module.ModuleSummary>( optionView, option, index);
        }
    }

    export class ModuleSelectedOptionView extends api_ui_combobox.RichSelectedOptionView<api_module.ModuleSummary> {


        constructor(option:api_ui_combobox.Option<api_module.ModuleSummary>) {
            super(option);
        }

        resolveIconUrl(content:api_module.ModuleSummary):string
        {
            return api_util.getAdminUri("common/images/default_content.png");
        }

        resolveTitle(content:api_module.ModuleSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api_module.ModuleSummary):string
        {
            return content.getModuleKey().toString();
        }

    }
}