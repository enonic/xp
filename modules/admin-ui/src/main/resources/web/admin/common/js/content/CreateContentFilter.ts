module api.content {

    import ModuleKey = api.module.ModuleKey;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import ContentTypeName = api.schema.content.ContentTypeName;

    var TYPES_ALLOWED_EVERYWHERE: {[key:string]: ContentTypeName} = {};
    [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
        ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
            TYPES_ALLOWED_EVERYWHERE[contentTypeName.toString()] = contentTypeName;
        });

    export class CreateContentFilter {

        private siteModulesAllowed: {[key:string]: ModuleKey};

        constructor() {
            this.siteModulesAllowed = null;
        }

        siteModulesFilter(siteModuleKeys: ModuleKey[]): CreateContentFilter {
            if (siteModuleKeys == null) {
                return this;
            }
            this.siteModulesAllowed = {};
            siteModuleKeys.forEach((moduleKey: ModuleKey) => {
                this.siteModulesAllowed[moduleKey.toString()] = moduleKey;
            });
            return this;
        }

        isCreateContentAllowed(parentContent: ContentSummary, contentType: ContentTypeSummary): boolean {
            var parentContentIsTemplateFolder = parentContent && parentContent.getType().isTemplateFolder();
            var parentContentIsSite = parentContent && parentContent.getType().isSite();
            var parentContentIsPageTemplate = parentContent && parentContent.getType().isPageTemplate();

            var contentTypeName = contentType.getContentTypeName();
            if (contentType.isAbstract()) {
                return false;
            }
            else if (parentContentIsPageTemplate) {
                return false; // children not allowed for page-template
            }
            else if (contentTypeName.isTemplateFolder()) {
                return parentContentIsSite; // template-folder only allowed under site
            }
            else if (contentTypeName.isPageTemplate()) {
                return parentContentIsTemplateFolder; // page-template only allowed under a template-folder
            }
            else if (parentContentIsTemplateFolder) {
                return contentTypeName.isPageTemplate(); // in a template-folder allow only page-template
            }
            else if (TYPES_ALLOWED_EVERYWHERE[contentTypeName.toString()]) {
                return true;
            }
            else if ((!this.siteModulesAllowed) || this.siteModulesAllowed[contentTypeName.getModuleKey().toString()]) {
                return true;
            }
            else {
                return false;
            }
        }

    }
}