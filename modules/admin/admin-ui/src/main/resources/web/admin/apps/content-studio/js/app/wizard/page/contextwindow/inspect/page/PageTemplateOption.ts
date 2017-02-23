import '../../../../../../api.ts';

import PageModel = api.content.page.PageModel;
import PageTemplate = api.content.page.PageTemplate;

export class PageTemplateOption implements api.Equitable {

    private template: PageTemplate;

    private pageModel: PageModel;

    constructor(template: PageTemplate, pageModel: PageModel) {
        this.template = template;
        this.pageModel = pageModel;
    }

    getPageTemplate(): PageTemplate {
        return this.template;
    }

    getPageModel(): PageModel {
        return this.pageModel;
    }

    isCustom(): boolean {
        let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;

        return this.template && this.template.getDisplayName() === pageTemplateDisplayName[pageTemplateDisplayName.Custom];
    }

    isAuto(): boolean {
        return !this.template;
    }

    isDefault(): boolean {
        return this.template && this.template.equals(this.pageModel.getDefaultPageTemplate());
    }

    equals(o: api.Equitable): boolean {

        if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplateOption)) {
            return false;
        }

        let other = <PageTemplateOption>o;

        if (this.isAuto() && other.isAuto()) {
            return true;
        }

        return api.ObjectHelper.equals(this.template, other.getPageTemplate());
    }
}
