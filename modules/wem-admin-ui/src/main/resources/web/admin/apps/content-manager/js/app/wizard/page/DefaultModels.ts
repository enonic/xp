module app.wizard.page {

    import PageTemplate = api.content.page.PageTemplate;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    export interface DefaultModelsConfig {

        pageTemplate: PageTemplate;

        imageDescriptor: ImageDescriptor;

        partDescriptor: PartDescriptor;

        layoutDescriptor: LayoutDescriptor;
    }

    export class DefaultModels {

        private pageTemplate: PageTemplate;

        private imageDescriptor: ImageDescriptor;

        private partDescriptor: PartDescriptor;

        private layoutDescriptor: LayoutDescriptor;

        constructor(config: DefaultModelsConfig) {
            this.pageTemplate = config.pageTemplate;
            this.imageDescriptor = config.imageDescriptor;
            this.partDescriptor = config.partDescriptor;
            this.layoutDescriptor = config.layoutDescriptor;
        }

        getPageTemplate(): PageTemplate {
            return this.pageTemplate;
        }

        hasImageDescriptor(): boolean {
            return !this.imageDescriptor ? false : true;
        }

        getImageDescriptor(): ImageDescriptor {
            return this.imageDescriptor;
        }

        getPartDescriptor(): PartDescriptor {
            return this.partDescriptor;
        }

        getLayoutDescriptor(): LayoutDescriptor {
            return this.layoutDescriptor;
        }
    }
}