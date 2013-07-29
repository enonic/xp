module app_browse {

    export class ContentBrowsePanel extends api_app_browse.BrowsePanel {

        private toolbar:ContentBrowseToolbar;

        private filterPanel:app_browse.ContentBrowseFilterPanel;

        private grid:ContentTreeGridPanel;

        private browseItemPanel:app_browse.ContentBrowseItemPanel;

        constructor() {

            this.toolbar = new ContentBrowseToolbar();
            this.grid = components.gridPanel = new ContentTreeGridPanel('contentTreeGrid');
            this.browseItemPanel = components.detailPanel = new app_browse.ContentBrowseItemPanel();

            this.filterPanel = new app_browse.ContentBrowseFilterPanel();
            var params = createLoadContentParams({});

            api_remote.RemoteService.content_find(params, (response) => {
                if (response && response.success) {

                    // set facet data
                    this.filterPanel.updateFacets(response.facets);
                }
            });


            super(this.toolbar, this.grid, this.browseItemPanel, this.filterPanel);


            ShowPreviewEvent.on((event) => {
                this.browseItemPanel.setPreviewMode(true);
            });

            ShowDetailsEvent.on((event) => {
                this.browseItemPanel.setPreviewMode(false);
            });

            GridSelectionChangeEvent.on((event) => {

                var items:api_app_browse.BrowseItem[] = [];
                var models:api_model.ContentModel[] = event.getModels();
                var contentIds:string[] = [];
                models.forEach((model:api_model.ContentModel) => {
                    contentIds.push(model.data.id);
                });
                var getParams:api_remote_content.GetParams = {
                    contentIds: contentIds
                };
                api_remote.RemoteService.content_get(getParams, (result:api_remote_content.GetResult)=> {

                    result.content.forEach((contentGet:api_remote_content.ContentGet, index:number) => {
                        var item = new api_app_browse.BrowseItem(models[index]).
                            setDisplayName(contentGet.displayName).
                            setPath(contentGet.path).
                            setIconUrl(contentGet.iconUrl);
                        items.push(item);
                    });
                    this.browseItemPanel.setItems(items);
                });
            });
        }
    }

    export function createLoadContentParams(values) {

        var now = new Date();
        var oneDayAgo = new Date();
        var oneWeekAgo = new Date();
        var oneHourAgo = new Date();
        oneDayAgo.setDate(now.getDate() - 1);
        oneWeekAgo.setDate(now.getDate() - 7);
        Admin.lib.DateHelper.addHours(oneHourAgo, -1);

        var facets = {
            "space": {
                "terms": {
                    "field": "space",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "contentType": {
                "terms": {
                    "field": "contentType",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "< 1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            }
        };

        var ranges = [];
        if (values.ranges) {
            for (var i = 0; i < values.ranges.length; i++) {
                var lower;
                switch (values.ranges[i]) {
                case '< 1 day':
                    lower = oneDayAgo;
                    break;
                case '< 1 hour':
                    lower = oneHourAgo;
                    break;
                case '< 1 week':
                    lower = oneWeekAgo;
                    break;
                default:
                    lower = null;
                    break;
                }
                ranges.push({
                    lower: lower,
                    upper: null
                })
            }
        }

        return {
            fulltext: values.query || '',
            contentTypes: values.contentType || [],
            spaces: values.space || [],
            ranges: ranges || [],
            facets: facets || {},
            include: true
        };
    }
}
