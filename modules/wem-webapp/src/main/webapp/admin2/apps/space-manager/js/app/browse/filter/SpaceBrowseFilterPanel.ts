module app_browse_filter {

    export class SpaceBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {

            // dummy data
            var facet1Entries:api_facet.TermsFacetEntry[] = [];
            facet1Entries.push(new api_facet.TermsFacetEntry("PublicWeb", "Public Web", 8));
            facet1Entries.push(new api_facet.TermsFacetEntry("Intranet", "Intranet", 20));
            var facet1 = new api_facet.TermsFacet("Space", "Space", facet1Entries);

            var facet2Entries:api_facet.TermsFacetEntry[] = [];
            facet2Entries.push(new api_facet.TermsFacetEntry("Space", "Space", 10));
            facet2Entries.push(new api_facet.TermsFacetEntry("Part", "Part", 80));
            facet2Entries.push(new api_facet.TermsFacetEntry("PageTemplate", "Page Template", 80));
            var facet2 = new api_facet.TermsFacet("Type", "Type", facet2Entries);

            super([facet1, facet2]);
        }
    }
}