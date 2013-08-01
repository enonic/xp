module app_browse_newcontent {

    export class RecommendedContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecommendedContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recommended");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        setNodes(nodes:api_remote_contenttype.ContentTypeListNode[]) {
            this.contentTypesList.setNodes(this.recommend(nodes));
        }

        getNodes():api_remote_contenttype.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }

        /**
         * Recommends the most frequent node in array
         * @param nodes Array to choose from
         * @returns {Array} Array of recommendations
         */
        private recommend(nodes:api_remote_contenttype.ContentTypeListNode[]):api_remote_contenttype.ContentTypeListNode[] {

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


}