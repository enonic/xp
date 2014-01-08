module api.module {

    export class ModuleComboBox extends api.ui.combobox.RichComboBox<api.module.ModuleSummary>
    {
        constructor()
        {
            var builder:api.ui.combobox.RichComboBoxBuilder<api.module.ModuleSummary> = new api.ui.combobox.RichComboBoxBuilder<api.module.ModuleSummary>();
            builder.setComboBoxName("moduleSelector" ).setLoader(new api.module.ModuleLoader() ).setSelectedOptionsView(new ModuleSelectedOptionsView());
            super(builder);

        }

        optionFormatter(row:number, cell:number, moduleInst:api.module.ModuleSummary, columnDef:any, dataContext:api.ui.combobox.Option<api.module.ModuleSummary>):string {
            var img = new api.dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(api.util.getAdminUri("common/images/default.content.png"));

            var contentSummary = new api.dom.DivEl();
            contentSummary.setClass("item-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", moduleInst.getDisplayName());
            displayName.getEl().setInnerHtml(moduleInst.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", moduleInst.getUrl());
            path.getEl().setInnerHtml(moduleInst.getUrl());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }
    }

    export class ModuleSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.module.ModuleSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.module.ModuleSummary>, index:number):api.ui.combobox.SelectedOption<api.module.ModuleSummary> {
            var optionView = new ModuleSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<api.module.ModuleSummary>( optionView, option, index);
        }
    }

    export class ModuleSelectedOptionView extends api.ui.combobox.RichSelectedOptionView<api.module.ModuleSummary> {


        constructor(option:api.ui.combobox.Option<api.module.ModuleSummary>) {
            super(option);
        }

        resolveIconUrl(content:api.module.ModuleSummary):string
        {
            return api.util.getAdminUri("common/images/default.content.png");
        }

        resolveTitle(content:api.module.ModuleSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api.module.ModuleSummary):string
        {
            return content.getModuleKey().toString();
        }

    }
}