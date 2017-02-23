import '../../../../../../api.ts';
import {PageTemplateOption} from './PageTemplateOption';
import PageTemplate = api.content.page.PageTemplate;

export class PageTemplateOptionViewer extends api.ui.NamesAndIconViewer<PageTemplateOption> {

    private defaultPageTemplate: PageTemplate;

    constructor(defaultPageTemplate: PageTemplate) {
        super();

        this.defaultPageTemplate = defaultPageTemplate;
    }

    resolveDisplayName(object: PageTemplateOption): string {
        let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;

        return !!object.getPageTemplate() ?
               (object.isCustom() ? pageTemplateDisplayName[pageTemplateDisplayName.Custom] : object.getPageTemplate().getDisplayName())
            : pageTemplateDisplayName[pageTemplateDisplayName.Automatic];
    }

    resolveSubName(object: PageTemplateOption, relativePath: boolean = false): string {
        if (!!object.getPageTemplate()) {
            if (object.isCustom()) {
                return 'Set up your own page';
            } else {
                return object.getPageTemplate().getPath().toString();
            }
        } else if (this.defaultPageTemplate) {
            return '(' + this.defaultPageTemplate.getDisplayName().toString() + ')';
        } else {
            return '( no default template found )';
        }
    }

    resolveIconClass(object: PageTemplateOption): string {
        let iconClass = !!object.getPageTemplate() ? (object.isCustom() ? 'icon-cog' : 'icon-newspaper') : 'icon-wand';

        return iconClass + ' icon-large';
    }

    getPreferredHeight(): number {
        return 50;
    }
}
