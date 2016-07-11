import "../../api.ts";
import {DefaultModels} from "./page/DefaultModels";
import {DefaultModelsFactory, DefaultModelsFactoryConfig} from "./page/DefaultModelsFactory";
import {ContentWizardPanelParams} from "./ContentWizardPanelParams";

import ContentId = api.content.ContentId;
import ContentTypeName = api.schema.content.ContentTypeName;
import Content = api.content.Content;
import ContentSummary = api.content.ContentSummary;
import Site = api.content.site.Site;
import ContentType = api.schema.content.ContentType;

export class ContentWizardDataLoader {

    parentContent: Content;

    content: Content;

    contentType: ContentType;

    siteContent: Site;

    defaultModels: DefaultModels;

    loadData(params: ContentWizardPanelParams): wemQ.Promise<ContentWizardDataLoader> {
        if (!params.contentSummary) {
            return this.loadDataForNew(params);
        } else {
            return this.loadDataForEdit(params);
        }
    }

    private loadDataForNew(params: ContentWizardPanelParams): wemQ.Promise<ContentWizardDataLoader> {

        return this.loadContentType(params.contentTypeName).then((loadedContentType: ContentType) => {

            this.contentType = loadedContentType;
            return this.loadParentContent(params, true);

        }).then((loadedParentContent: Content) => {

            this.parentContent = loadedParentContent;
            return this.loadSite(loadedParentContent ? loadedParentContent.getContentId() : null);

        }).then((loadedSite: Site) => {

            this.siteContent = loadedSite;
            return this.loadDefaultModels(this.siteContent, params.contentTypeName);

        }).then((defaultModels: DefaultModels) => {

            this.defaultModels = defaultModels;
            return this;

        });
    }

    private loadDataForEdit(params: ContentWizardPanelParams): wemQ.Promise<ContentWizardDataLoader> {

        return this.loadContent(params.contentSummary).then((loadedContent: Content) => {

            this.content = loadedContent;
            return this.loadContentType(this.content.getType());

        }).then((contentType: ContentType) => {

            this.contentType = contentType;
            return this.loadParentContent(params, false);

        }).then((loadedParentContent: Content) => {

            this.parentContent = loadedParentContent;
            return this.loadSite(this.content.getContentId());

        }).then((loadedSite: Site) => {

            if (!!loadedSite) {
                this.siteContent = loadedSite;
            }
            return this.loadDefaultModels(this.siteContent, this.content.getType());

        }).then((defaultModels: DefaultModels) => {

            this.defaultModels = defaultModels;
            return this;

        });
    }

    private loadContent(summary: ContentSummary): wemQ.Promise<Content> {
        if (api.ObjectHelper.iFrameSafeInstanceOf(summary, Content)) {
            return wemQ(<Content> summary);
        } else {
            return new api.content.GetContentByIdRequest(summary.getContentId()).sendAndParse();
        }
    }

    private loadContentType(name: ContentTypeName): wemQ.Promise<ContentType> {
        var deferred = wemQ.defer<ContentType>();
        new api.schema.content.GetContentTypeByNameRequest(name).sendAndParse().then((contentType) => {
            deferred.resolve(contentType);
        }).catch((reason) => {
            deferred.reject(new api.Exception("Content cannot be opened. Required content type '" + name.toString() +
                                              "' not found.",
                api.ExceptionType.WARNING));
        }).done();
        return deferred.promise;
    }

    private loadSite(contentId: ContentId): wemQ.Promise<Site> {
        return contentId ? new api.content.GetNearestSiteRequest(contentId).sendAndParse() : wemQ<Site>(null);
    }

    private loadDefaultModels(site: Site, contentType: ContentTypeName): wemQ.Promise<DefaultModels> {

        if (site) {
            return DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                siteId: site.getContentId(),
                contentType: contentType,
                applications: site.getApplicationKeys()
            });
        }
        else if (contentType.isSite()) {
            return wemQ<DefaultModels>(new DefaultModels(null, null));
        }
        else {
            return wemQ<DefaultModels>(null);
        }
    }

    private loadParentContent(params: ContentWizardPanelParams, isNew: boolean = true): wemQ.Promise<Content> {

        if (params.parentContent != null) {
            return wemQ(params.parentContent);
        }

        if (!isNew && !this.content.hasParent() ||
            isNew && params.parentContent == null) {

            return wemQ<Content>(null);
        }

        return new api.content.GetContentByPathRequest(this.content.getPath().getParentPath()).sendAndParse();
    }

}
