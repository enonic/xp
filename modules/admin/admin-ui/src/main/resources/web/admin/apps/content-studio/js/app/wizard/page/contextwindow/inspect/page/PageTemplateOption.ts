import '../../../../../../api.ts';

import PageModel = api.content.page.PageModel;
import PageTemplate = api.content.page.PageTemplate;

export class PageTemplateOption implements api.Equitable {

    private template: PageTemplate;

    constructor(template?: PageTemplate) {
        this.template = template;
    }

    getPageTemplate(): PageTemplate {
        return this.template;
    }

    isCustom(): boolean {
        let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;

        return this.template && this.template.getDisplayName() === pageTemplateDisplayName[pageTemplateDisplayName.Custom];
    }

    isAuto(): boolean {
        return !this.template;
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
