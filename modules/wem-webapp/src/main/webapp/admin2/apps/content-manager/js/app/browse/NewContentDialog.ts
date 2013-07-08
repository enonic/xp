module app_browse {

    export class NewContentDialog extends api_ui_dialog.ModalDialog {

        private cancelAction:api_ui.Action = new CancelNewDialogAction();
        private selectAction:SelectContentTypeAction;

        private recentList;
        private recommendedList;
        private allList;

        constructor() {
            super({
                title: "Select Content Type",
                width: 800,
                height: 520
            });

            this.getEl().addClass("new-dialog");


            this.recommendedList = new RecommendedContentTypesList("block recommended");
            this.recentList = new RecentContentTypesList("block recent");

            var leftColumn = new api_dom.DivEl().setClass("column column-left");
            leftColumn.appendChild(this.recommendedList);
            leftColumn.appendChild(this.recentList);
            this.appendChildToContentPanel(leftColumn);

            this.allList = new AllContentTypesList("column column-right block all");
            this.appendChildToContentPanel(this.allList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            this.setSelectAction(new SelectContentTypeAction());

            api_dom.Body.get().appendChild(this);
        }

        show() {
            this.recentList.refresh();
            this.recommendedList.setNodes(this.recentList.getNodes());
            super.show();
        }

        setSelectAction(action:SelectContentTypeAction) {
            this.recommendedList.setSelectAction(action);
            this.recentList.setSelectAction(action);
            this.allList.setSelectAction(action);
            this.selectAction = action;
            this.selectAction.addExecutionListener(()=> {
                this.close();
            });
        }

        getSelectAction():SelectContentTypeAction {
            return this.selectAction;
        }
    }

    export class CancelNewDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }

    }

    export class SelectContentTypeAction extends api_ui.Action {

        private contentType:api_remote.ContentTypeListNode;

        constructor(contentType?:api_remote.ContentTypeListNode) {
            super("SelectContentType");
            this.contentType = contentType;
        }

        setContentType(contentType:api_remote.ContentTypeListNode):SelectContentTypeAction {
            this.contentType = contentType;
            return this;
        }

        getContentType():api_remote.ContentTypeListNode {
            return this.contentType;
        }

    }


    class RecentContentTypesList extends api_dom.DivEl {

        private recentCount = 5;
        private cookieKey = 'app_browse.RecentContentTypesList';
        private cookieExpire = 30;
        private cookieSeparator = '|';

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecentContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recent");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();

            this.appendChild(this.contentTypesList);
        }

        refresh() {
            this.contentTypesList.setNodes(this.getRecentContentTypes());
        }

        getNodes():api_remote.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }

        setSelectAction(action:SelectContentTypeAction):RecentContentTypesList {
            this.contentTypesList.setSelectAction(action);
            action.addExecutionListener((action:SelectContentTypeAction) => {
                this.addRecentContentType(action.getContentType());
            });
            return this;
        }

        private getRecentContentTypes():api_remote.ContentTypeListNode[] {

            var recentRecords:api_remote.ContentTypeListNode[] = [];

            var cookies:string = <string> Ext.util.Cookies.get(this.cookieKey);
            if (cookies) {
                var recentArray = cookies.split(this.cookieSeparator);
                for (var i = 0; i < recentArray.length; i++) {
                    recentRecords.push(this.parseContentType(recentArray[i]));
                }
            }

            return recentRecords;
        }

        private addRecentContentType(contentType:api_remote.ContentTypeListNode) {

            var cookies:string = this.getCookie(this.cookieKey);
            var recentArray = cookies ? cookies.split(this.cookieSeparator) : [];

            var recentItem = this.serializeContentType(contentType);
            if (recentArray.length === 0 || recentArray[0] !== recentItem) {
                recentArray.unshift(recentItem);
            }

            if (recentArray.length > this.recentCount) {
                // constrain recent items quantity to recentCount
                recentArray = recentArray.slice(0, this.recentCount);
            }

            // add chosen item to recent list
            this.setCookie(this.cookieKey, recentArray.join(this.cookieSeparator));
        }

        private serializeContentType(contentType:api_remote.ContentTypeListNode) {
            // serialize only the crucial info
            // because cookie size is just 4093 bytes per domain
            return JSON.stringify({
                iconUrl: contentType.iconUrl,
                displayName: contentType.displayName,
                qualifiedName: contentType.qualifiedName,
                name: contentType.name
            });
        }

        private parseContentType(text:string):api_remote.ContentTypeListNode {
            var json = JSON.parse(text);
            return <api_remote.ContentTypeListNode> json;
        }

        private getCookie(name:string):string {
            var value;
            var parts = document.cookie.split(name + "=");
            if (parts.length == 2) {
                value = parts.pop().split(";").shift();
            }
            return value;
        }

        private setCookie(name:string, value:string) {
            var expDate = new Date();
            expDate.setDate(expDate.getDate() + this.cookieExpire);
            var value = value + "; expires=" + expDate.toUTCString();
            document.cookie = name + "=" + value;
        }

    }

    class RecommendedContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecommendedContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recommended");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        setNodes(nodes:api_remote.ContentTypeListNode[]) {
            this.contentTypesList.setNodes(this.recommend(nodes));
        }

        getNodes():api_remote.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }

        setSelectAction(action:SelectContentTypeAction):RecommendedContentTypesList {
            this.contentTypesList.setSelectAction(action);
            return this;
        }

        /**
         * Recommends the most frequent node in array
         * @param nodes Array to choose from
         * @returns {Array} Array of recommendations
         */
        private recommend(nodes:api_remote.ContentTypeListNode[]):api_remote.ContentTypeListNode[] {

            var recommendations = [];
            if (nodes && nodes.length > 0) {
                var node, count, maxCount = 0, maxNode;
                var namesMap = {};
                for (var i = 0; i < nodes.length; i++) {
                    node = nodes[i];
                    count = namesMap[node.qualifiedName] || 0;
                    namesMap[node.qualifiedName] = ++count;
                    if (count > maxCount) {
                        maxCount = count;
                        maxNode = node;
                    }
                }
                recommendations.push(maxNode);
            }

            return recommendations;
        }

    }

    class AllContentTypesList extends api_dom.DivEl {

        private input:api_dom.Element;
        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("AllContentTypesList", className);

            this.input = new api_dom.Element("input");
            this.input.getEl().addEventListener("keyup", function (event:Event) => {
                this.contentTypesList.filter("displayName", (<HTMLInputElement> event.target).value);
            });
            this.appendChild(this.input);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);

            api_remote.RemoteService.contentType_list({}, function (result) => {
                this.contentTypesList.setNodes(result.contentTypes);
            });
        }

        setSelectAction(action:SelectContentTypeAction):AllContentTypesList {
            this.contentTypesList.setSelectAction(action);
            return this;
        }

    }


    class ContentTypesList extends api_dom.DivEl {

        private ul:api_dom.UlEl;
        private nodes:api_remote.ContentTypeListNode[];
        private selectAction:SelectContentTypeAction;

        constructor(nodes?:api_remote.ContentTypeListNode[]) {
            super("ContentTypesList", "node-list");

            this.ul = new api_dom.UlEl("ContentTypesList");
            this.appendChild(this.ul);

            if (nodes) {
                this.setNodes(nodes);
            }
        }

        setNodes(nodes:api_remote.ContentTypeListNode[]):ContentTypesList {
            this.nodes = nodes;
            return this.layoutNodes(nodes);
        }

        getNodes():api_remote.ContentTypeListNode[] {
            return this.nodes;
        }

        filter(property:string, value:any):ContentTypesList {
            if (!value || value.length == 0) {
                this.clearFilter();
            }
            var filteredNodes:api_remote.ContentTypeListNode[] = [];
            var regexp = new RegExp(value, 'i');

            for (var i = 0; i < this.nodes.length; i++) {
                var node = this.nodes[i];
                if (regexp.test(node[property])) {
                    filteredNodes.push(node);
                }
            }
            return this.layoutNodes(filteredNodes);
        }

        clearFilter():ContentTypesList {
            this.layoutNodes(this.nodes);
            return this;
        }

        setSelectAction(selectAction:SelectContentTypeAction):ContentTypesList {
            this.selectAction = selectAction;
            return this;
        }

        private layoutNodes(nodes:api_remote.ContentTypeListNode[]):ContentTypesList {
            this.ul.removeChildren();
            for (var i = 0; i < nodes.length; i++) {
                this.ul.appendChild(this.renderListItem(nodes[i]));
            }
            return this;
        }

        private renderListItem(node:api_remote.ContentTypeListNode):api_dom.LiEl {
            var item = new api_dom.LiEl("ContentTypesListItem", "node-list-item");
            var img = new api_dom.ImgEl(node.iconUrl);
            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(node.displayName);
            var p = new api_dom.PEl();
            p.getEl().setInnerHtml(node.name);
            item.appendChild(img);
            item.appendChild(h6);
            item.appendChild(p);
            item.getEl().addEventListener("click", function (event:Event) => {
                if (this.selectAction) {
                    this.selectAction.setContentType(node).execute();
                }
            });
            return item;
        }

    }


}