module app.wizard.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentId = api.content.ContentId;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;

    export interface DefaultModelsFactoryConfig {

        siteId: ContentId;

        contentType: ContentTypeName;

        modules: api.module.ModuleKey[];
    }

    export class DefaultModelsFactory {

        static create(config: DefaultModelsFactoryConfig): wemQ.Promise<DefaultModels> {

            return new GetDefaultPageTemplateRequest(config.siteId, config.contentType).sendAndParse().
                then((defaultPageTemplate: PageTemplate) => {

                    var defaultPageTemplateDescriptorPromise = null;
                    if (defaultPageTemplate && defaultPageTemplate.isPage()) {
                        defaultPageTemplateDescriptorPromise = new GetPageDescriptorByKeyRequest(defaultPageTemplate.getController()).
                            sendAndParse();
                    }
                    else if (defaultPageTemplate && !defaultPageTemplate.isPage()) {
                        defaultPageTemplate = null;
                    }

                    var deferred = wemQ.defer<DefaultModels>();
                    if (defaultPageTemplateDescriptorPromise) {
                        defaultPageTemplateDescriptorPromise.then((defaultPageTemplateDescriptor: PageDescriptor) => {

                            deferred.resolve(new DefaultModels(defaultPageTemplate, defaultPageTemplateDescriptor));
                        }).catch((reason) => {

                            deferred.reject(new api.Exception("Page descriptor '" + defaultPageTemplate.getController() + "' not found.",
                                api.ExceptionType.WARNING));
                        }).done();
                    }
                    else {
                        deferred.resolve(new DefaultModels(defaultPageTemplate, null));
                    }

                    return deferred.promise;
                });
        }
    }
}