module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponent = api.content.page.PageComponent;
    import PageRegions = api.content.page.PageRegions;

    export class PageComponentDuplicateCommand {

        pageRegions: PageRegions;

        pathToSource: ComponentPath;

        setPageRegions(value: PageRegions): PageComponentDuplicateCommand {
            this.pageRegions = value;
            return this;
        }

        setPathToSource(value: ComponentPath): PageComponentDuplicateCommand {
            this.pathToSource = value;
            return this;
        }

        execute(): PageComponent {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.pathToSource, "pathToSource cannot be null");

            return this.pageRegions.duplicateComponent(this.pathToSource);
        }
    }
}