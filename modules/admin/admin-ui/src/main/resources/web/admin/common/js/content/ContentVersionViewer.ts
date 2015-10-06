module api.content {

    export class ContentVersionViewer extends api.ui.Viewer<ContentVersion> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        private getModifierSpan(contentVersion: ContentVersion): api.dom.SpanEl {
            var span = new api.dom.SpanEl("version-modifier");

            span.setHtml(api.ui.treegrid.DateTimeFormatter.createHtml(contentVersion.modified));

            return span;
        }

        private getCommentSpan(contentVersion: ContentVersion): api.dom.SpanEl {
            if (contentVersion.comment.length = 0) {
                return null;
            }

            var span = new api.dom.SpanEl("version-comment");
            span.setHtml(contentVersion.comment);
            return span;
        }

        private getSubNameElements(contentVersion: ContentVersion): api.dom.Element[] {
            var elements: api.dom.Element[] = [this.getModifierSpan(contentVersion)]/*,
                commentSpan = this.getCommentSpan(contentVersion)*/;

/*          Uncomment to enable comments in version history
            if (commentSpan) {
                elements.push(new api.dom.BrEl(), commentSpan);
            }
*/
            return elements;
        }

        setObject(contentVersion: ContentVersion, row?: number) {
            super.setObject(contentVersion);

            //TODO: use content version image and number instead of row
            this.namesAndIconView
                .setMainName(contentVersion.modifierDisplayName)
                .setSubNameElements(this.getSubNameElements(contentVersion))
                .setIconClass("icon-user");
        }
    }

}