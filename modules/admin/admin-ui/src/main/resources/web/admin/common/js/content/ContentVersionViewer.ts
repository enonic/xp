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

        private getModifiedString(modified: Date): string {
            var timeDiff = Math.abs(Date.now() - modified.getTime());
            var secInMs = 1000;
            var minInMs = secInMs * 60;
            var hrInMs = minInMs * 60;
            var dayInMs = hrInMs * 24;
            var monInMs = dayInMs * 31;
            var yrInMs = dayInMs * 365;

            if (timeDiff < minInMs) {
                return "less than a minute ago";
            }
            else if (timeDiff < 2 * minInMs) {
                return "a minute ago";
            }
            else if (timeDiff < hrInMs) {
                return ~~(timeDiff / minInMs) + " minutes ago";
            }
            else if (timeDiff < 2 * hrInMs) {
                return "over an hour ago";
            }
            else if (timeDiff < dayInMs) {
                return "over " + ~~(timeDiff / hrInMs) + " hours ago";
            }
            else if (timeDiff < 2 * dayInMs) {
                return "over a day ago";
            }
            else if (timeDiff < monInMs) {
                return "over " + ~~(timeDiff / dayInMs) + " days ago";
            }
            else if (timeDiff < 2 * monInMs) {
                return "over a month ago";
            }
            else if (timeDiff < yrInMs) {
                return "over " + ~~(timeDiff / monInMs) + " months ago";
            }
            else if (timeDiff < 2 * yrInMs) {
                return "over a year ago";
            }

            return "over " + ~~(timeDiff / yrInMs) + " years ago";
        }

        private getModifierSpan(contentVersion: ContentVersion): api.dom.SpanEl {
            var span = new api.dom.SpanEl("version-modifier");

            span.setHtml(this.getModifiedString(contentVersion.modified));

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