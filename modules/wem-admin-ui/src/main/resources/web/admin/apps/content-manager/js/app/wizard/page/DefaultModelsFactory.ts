module app.wizard.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    import DefaultImageDescriptorResolver = api.content.page.image.DefaultImageDescriptorResolver;
    import DefaultPartDescriptorResolver = api.content.page.part.DefaultPartDescriptorResolver;
    import DefaultLayoutDescriptorResolver = api.content.page.layout.DefaultLayoutDescriptorResolver;

    import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;

    export interface DefaultModelsFactoryConfig {

        siteTemplateKey: SiteTemplateKey;

        contentType: ContentTypeName;

        modules: api.module.ModuleKey[];
    }

    export class DefaultModelsFactory {

        static create(config: DefaultModelsFactoryConfig): Q.Promise<DefaultModels> {

            var defaultPageTemplatePromise = new GetDefaultPageTemplateRequest(config.siteTemplateKey, config.contentType).sendAndParse();
            var defaultImageDescriptorPromise = DefaultImageDescriptorResolver.resolve(config.modules);
            var defaultPartDescriptorPromise = DefaultPartDescriptorResolver.resolve(config.modules);
            var defaultLayoutDescriptorPromise = DefaultLayoutDescriptorResolver.resolve(config.modules);

            var allPromises: Q.Promise<any>[] = [
                defaultPageTemplatePromise,
                defaultImageDescriptorPromise,
                defaultPartDescriptorPromise,
                defaultLayoutDescriptorPromise];

            return Q.all(allPromises).
                spread<DefaultModels>((pageTemplate: PageTemplate, imageDescriptor: ImageDescriptor,
                                       partDescriptor: PartDescriptor, layoutDescriptor: LayoutDescriptor) => {

                    var defaultModelsConfig: DefaultModelsConfig = <DefaultModelsConfig>{
                        pageTemplate: pageTemplate,
                        imageDescriptor: imageDescriptor,
                        partDescriptor: partDescriptor,
                        layoutDescriptor: layoutDescriptor
                    };
                    return new DefaultModels(defaultModelsConfig);
                });
        }
    }
}