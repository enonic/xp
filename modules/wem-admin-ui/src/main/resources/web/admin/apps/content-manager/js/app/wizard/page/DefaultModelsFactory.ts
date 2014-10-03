module app.wizard.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentId = api.content.ContentId;
    import PageTemplate = api.content.page.PageTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    import DefaultPartDescriptorResolver = api.content.page.part.DefaultPartDescriptorResolver;
    import DefaultLayoutDescriptorResolver = api.content.page.layout.DefaultLayoutDescriptorResolver;

    import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;

    export interface DefaultModelsFactoryConfig {

        siteId: ContentId;

        contentType: ContentTypeName;

        modules: api.module.ModuleKey[];
    }

    export class DefaultModelsFactory {

        static create(config: DefaultModelsFactoryConfig): wemQ.Promise<DefaultModels> {

            var defaultPageTemplatePromise = new GetDefaultPageTemplateRequest(config.siteId, config.contentType).sendAndParse();
            var defaultPartDescriptorPromise = DefaultPartDescriptorResolver.resolve(config.modules);
            var defaultLayoutDescriptorPromise = DefaultLayoutDescriptorResolver.resolve(config.modules);

            var allPromises: wemQ.Promise<any>[] = [
                defaultPageTemplatePromise,
                defaultPartDescriptorPromise,
                defaultLayoutDescriptorPromise];

            return wemQ.all(allPromises).
                spread<DefaultModels>((pageTemplate: PageTemplate, partDescriptor: PartDescriptor, layoutDescriptor: LayoutDescriptor) => {

                var defaultModelsConfig: DefaultModelsConfig = <DefaultModelsConfig>{
                    pageTemplate: pageTemplate,
                    partDescriptor: partDescriptor,
                    layoutDescriptor: layoutDescriptor
                };
                return new DefaultModels(defaultModelsConfig);
            });
        }
    }
}