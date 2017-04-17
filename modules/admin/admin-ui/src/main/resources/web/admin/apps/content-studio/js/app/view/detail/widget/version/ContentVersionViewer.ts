import '../../../../../api.ts';

export class ContentVersionViewer extends api.ui.Viewer<api.content.ContentVersion> {

    private namesAndIconView: api.app.NamesAndIconView;

    constructor() {
        super();
        this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
        this.appendChild(this.namesAndIconView);
    }

    private getModifierSpan(contentVersion: api.content.ContentVersion): api.dom.SpanEl {
        let span = new api.dom.SpanEl('version-modifier');

        span.setHtml(api.util.DateHelper.getModifiedString(contentVersion.modified));

        return span;
    }

    private getCommentSpan(contentVersion: api.content.ContentVersion): api.dom.SpanEl {
        if (contentVersion.comment.length === 0) {
            return null;
        }

        let span = new api.dom.SpanEl('version-comment');
        span.setHtml(contentVersion.comment);
        return span;
    }

    private getSubNameElements(contentVersion: api.content.ContentVersion): api.dom.Element[] {
        let elements: api.dom.Element[] = [this.getModifierSpan(contentVersion)]/*,
         commentSpan = this.getCommentSpan(contentVersion)*/;

        /*          Uncomment to enable comments in version history
         if (commentSpan) {
         elements.push(new api.dom.BrEl(), commentSpan);
         }
         */
        return elements;
    }

    setObject(contentVersion: api.content.ContentVersion, row?: number) {

        //TODO: use content version image and number instead of row
        this.namesAndIconView
            .setMainName(contentVersion.modifierDisplayName)
            .setSubNameElements(this.getSubNameElements(contentVersion))
            .setIconClass('icon-user');

        return super.setObject(contentVersion);
    }
}
