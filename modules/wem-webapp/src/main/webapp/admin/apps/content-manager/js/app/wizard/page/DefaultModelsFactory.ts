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

            var deferred = Q.defer<DefaultModels>();

            var defaultPageTemplatePromise = new GetDefaultPageTemplateRequest(config.siteTemplateKey, config.contentType).sendAndParse();
            var defaultImageDescriptorPromise = DefaultImageDescriptorResolver.resolve(config.modules);
            var defaultPartDescriptorPromise = DefaultPartDescriptorResolver.resolve(config.modules);
            var defaultLayoutDescriptorPromise = DefaultLayoutDescriptorResolver.resolve(config.modules);

            var allPromises: Q.Promise<any>[] = [
                defaultPageTemplatePromise,
                defaultImageDescriptorPromise,
                defaultPartDescriptorPromise,
                defaultLayoutDescriptorPromise];


            var defaultModelsConfig: DefaultModelsConfig = <DefaultModelsConfig>{};

            defaultPageTemplatePromise.done((pageTemplate: PageTemplate)=> {
                defaultModelsConfig.pageTemplate = pageTemplate;
            });

            defaultImageDescriptorPromise.done((imageDescriptor: ImageDescriptor)=> {
                defaultModelsConfig.imageDescriptor = imageDescriptor;
            });

            defaultPartDescriptorPromise.done((partDescriptor: PartDescriptor)=> {
                defaultModelsConfig.partDescriptor = partDescriptor;
            });

            defaultLayoutDescriptorPromise.done((layoutDescriptor: LayoutDescriptor)=> {
                defaultModelsConfig.layoutDescriptor = layoutDescriptor;
            });

            Q.allSettled(allPromises).then((results: Q.PromiseState<any>[])=> {

                var defaultModels = new DefaultModels(defaultModelsConfig);
                deferred.resolve(defaultModels);
            });

            return deferred.promise;
        }
    }
}