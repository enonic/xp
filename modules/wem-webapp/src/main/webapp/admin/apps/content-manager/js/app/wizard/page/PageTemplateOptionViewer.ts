module app.wizard.page {

    export class PageTemplateOptionViewer extends api.ui.Viewer<PageTemplateOption> {

        private namesAndIconView: api.app.NamesAndIconView;

        private defaultPageTemplateIconUrl:string;

        private pageTemplateIconUrl:string;

        constructor() {
            super();

            this.defaultPageTemplateIconUrl = api.util.getAdminUri('common/images/icons/icoMoon/32x32/wand.png');
            this.pageTemplateIconUrl = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');

            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(option: PageTemplateOption) {
            super.setObject(option);

            var pageTemplate = option.getPageTemplate();
            if (pageTemplate) {
                this.namesAndIconView.
                    setMainName(pageTemplate.getDisplayName()).
                    setSubName(pageTemplate.getDescriptorKey().toString()).
                    setIconUrl(this.pageTemplateIconUrl);
            }
            else {
                this.namesAndIconView.
                    setMainName("Auto").
                    setSubName("Page Template automatically chosen").
                    setIconUrl(this.defaultPageTemplateIconUrl);
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}