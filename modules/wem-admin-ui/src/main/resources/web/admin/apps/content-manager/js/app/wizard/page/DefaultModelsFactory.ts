module app.wizard.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentId = api.content.ContentId;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import DefaultPartDescriptorResolver = api.content.page.part.DefaultPartDescriptorResolver;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import DefaultLayoutDescriptorResolver = api.content.page.layout.DefaultLayoutDescriptorResolver;

    export interface DefaultModelsFactoryConfig {

        siteId: ContentId;

        contentType: ContentTypeName;

        modules: api.module.ModuleKey[];
    }

    export class DefaultModelsFactory {

        static create(config: DefaultModelsFactoryConfig): wemQ.Promise<DefaultModels> {

            return new GetDefaultPageTemplateRequest(config.siteId, config.contentType).sendAndParse().
                then((defaultPageTemplate: PageTemplate) => {

                    var defaultPageTemplateDescriptorPromise;
                    if (defaultPageTemplate) {
                        defaultPageTemplateDescriptorPromise = new GetPageDescriptorByKeyRequest(defaultPageTemplate.getController()).
                            sendAndParse();
                    }
                    var defaultPartDescriptorPromise = DefaultPartDescriptorResolver.resolve(config.modules);
                    var defaultLayoutDescriptorPromise = DefaultLayoutDescriptorResolver.resolve(config.modules);

                    var allPromises: wemQ.Promise<any>[] = [
                        defaultPageTemplateDescriptorPromise,
                        defaultPartDescriptorPromise,
                        defaultLayoutDescriptorPromise];

                    return wemQ.all(allPromises).
                        spread<DefaultModels>((defaultPageTemplateDescriptor: PageDescriptor, partDescriptor: PartDescriptor,
                                               layoutDescriptor: LayoutDescriptor) => {

                        var defaultModelsConfig: DefaultModelsConfig = <DefaultModelsConfig>{
                            pageTemplate: defaultPageTemplate,
                            pageDescriptor: defaultPageTemplateDescriptor,
                            partDescriptor: partDescriptor,
                            layoutDescriptor: layoutDescriptor
                        };
                        return new DefaultModels(defaultModelsConfig);
                    });

                });
        }
    }
}