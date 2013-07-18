module api_app_browse {

    export class BrowseFilterPanel extends api_ui.Panel {

        filterSearchAction = new FilterSearchAction();;

        filterResetAction = new FilterResetAction();

        facetContainer;

        searchField:api_dom.InputEl;

        clearFilter:api_dom.AEl;

        lastFacetGroup;

        searchFilterTypingTimer;

        constructor(facetData?) {
            super('BrowseFilterPanel');

            this.getEl().addClass('admin-filter');
            this.searchField = this.createSearchFieldEl();
            this.facetContainer = new FacetContainer(facetData);

            this.clearFilter = this.createClearFilterEl();
            api_event.onEvent('filterSearch', (event:api_event.FilterSearchEvent) => {
                this.filterSearchAction.execute();
                if (event.getTarget()) {
                    this.lastFacetGroup = (<Facet>event.getTarget()).getFacetGroup();
                } else {
                    this.lastFacetGroup = undefined;
                }
                this.search();
            });

        }

        createSearchFieldEl():api_dom.InputEl {
            var searchField = new api_dom.InputEl('SearchField', 'admin-search-trigger');
            searchField.getEl().addEventListener('keypress', (event:any) => {
                if (event.which === 97) {
                    if (event.type === "keypress") {
                        new api_event.FilterSearchEvent().fire();
                    }
                } else {
                    if (this.searchFilterTypingTimer !== null) {
                        window.clearTimeout(this.searchFilterTypingTimer);
                        this.searchFilterTypingTimer = null;
                    }
                    this.searchFilterTypingTimer = window.setTimeout(() => {
                        new api_event.FilterSearchEvent().fire();
                    }, 500);
                }
            });
            return searchField;
        }

        updateFacets(facetsData) {
            for (var i = 0; i < facetsData.length; i++) {
                var facetGroupData = facetsData[i];
                var facetGroup:FacetGroup = this.getFacetGroup(facetGroupData.name);
                if (facetGroup != this.lastFacetGroup && facetGroup != null) {
                    facetGroup.updateFacets(facetGroupData);
                } else if (facetGroup == null) {
                    var newFacetGroup:FacetGroup = new FacetGroup(facetGroupData);
                    this.facetContainer.addFacetGroup(newFacetGroup);
                }
            }
        }

        private getFacetGroup(name:string) {
            var facetGroups:FacetGroup[] = this.facetContainer.getFacetGroups();
            for (var i in facetGroups) {
                var facetGroup:FacetGroup = facetGroups[i];
                if (facetGroup.getName() == name) {
                    return facetGroup;
                }
            }
            return null;
        }

        createClearFilterEl():api_dom.AEl {
            var clearFilter:api_dom.AEl = new api_dom.AEl('ClearFilter');
            clearFilter.getEl().setInnerHtml('Clear filter');
            clearFilter.getHTMLElement().setAttribute('href', 'javascript:;');
            clearFilter.getHTMLElement().style.display = 'block';
            clearFilter.getHTMLElement().style.visibility = 'hidden';
            clearFilter.getEl().addEventListener('click', () => {
                this.filterResetAction.execute();
                this.reset();
            });
            return clearFilter;
        }

        setFilterSearchAction(action:FilterSearchAction) {
            this.filterSearchAction = action;
        }

        setFilterResetAction(action:FilterResetAction) {
            this.filterResetAction = action;
        }

        afterRender() {
            this.appendChild(this.searchField);
            this.appendChild(this.clearFilter);
            this.appendChild(this.facetContainer);

        }

        getValues() {
            var values = this.facetContainer.getValues();
            values['query'] = this.searchField.getEl().getValue();
            return values;
        }

        reset() {
            this.searchField.getHTMLElement()['value'] = '';
            window.clearTimeout(this.searchFilterTypingTimer);
            this.facetContainer.reset();
            this.clearFilter.getHTMLElement().style.visibility = 'hidden';
        }

        search() {
            this.clearFilter.getHTMLElement().style.visibility = 'visible';
            this.filterSearchAction.setFilterValues(this.getValues());
            this.filterSearchAction.execute();
        }
    }

    class FacetContainer extends api_dom.DivEl {

        facetGroups;

        constructor(data?) {
            super('FacetContainer');
            this.facetGroups = [];
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    var facetGroup:FacetGroup = this.createFacetGroup(data[i]);
                    this.facetGroups.push(facetGroup);
                    this.appendChild(facetGroup);
                }
            }
        }

        createFacetGroup(facetGroupData):FacetGroup {
            var facetGroup:FacetGroup = new FacetGroup(facetGroupData);

            return facetGroup;

        }

        addFacetGroup(facetGroup:FacetGroup) {
            this.facetGroups.push(facetGroup);
            this.appendChild(facetGroup);
        }

        reset() {
            for (var i in this.facetGroups) {
                this.facetGroups[i].reset();
            }
        }

        getFacetGroups():FacetGroup[] {
            return this.facetGroups;
        }

        getValues() {
            var values = [];
            for (var i in this.facetGroups) {
                values[this.facetGroups[i].getName()] = this.facetGroups[i].getValues();
            }
            return values;
        }


    }

    class FacetGroup extends api_dom.DivEl {
        facets;
        name:string;

        constructor(facetGroupData) {
            super('FacetGroup', 'admin-facet-group');
            var facetTitle:api_dom.H2El = new api_dom.H2El('FacetTitle');
            this.facets = [];
            this.name = facetGroupData.name;
            facetTitle.getEl().setInnerHtml(facetGroupData.displayName || facetGroupData.name);
            this.appendChild(facetTitle);
            this.updateFacets(facetGroupData);
        }

        addFacet(facet:Facet) {
            this.appendChild(facet);
            this.facets.push(facet);
        }

        updateFacets(facetGroupData) {
            var isHidden = true;
            for (var i = 0; i < facetGroupData.terms.length; i++) {
                var facetData = facetGroupData.terms[i];
                if (facetData.count > 0) {
                    isHidden = false;
                }
                var facet:Facet = this.getFacet(facetData.name);
                if (facet != null) {
                    facet.update(facetData);
                } else {
                    facet = new Facet(facetData, this);
                    this.addFacet(facet);
                }

            }

            this.getHTMLElement().style.display = isHidden ? 'none' : 'block';
        }

        private getFacet(name:string):Facet {
            for (var i = 0; i < this.facets.length; i++) {
                var facet:Facet = this.facets[i];
                if (facet.getName() == name) {
                    return facet;
                }
            }
            return null;
        }

        reset() {
            for (var i = 0; i < this.facets.length; i++) {
                this.facets[i].reset();
            }
        }

        getName():string {
            return this.name;
        }

        getValues() {
            var values = [];
            for (var i = 0; i < this.facets.length; i++) {
                var facet = this.facets[i];
                if (facet.isSelected()) {
                    values.push(facet.getName());
                }
            }

            return values;
        }

    }

    class Facet extends api_dom.DivEl {

        checkbox:api_dom.InputEl;

        label:api_dom.LabelEl;

        name:string;

        facetGroup;

        constructor(facetData, facetGroup:FacetGroup) {
            super('Facet', 'admin-facet');
            this.name = facetData.name;

            this.facetGroup = facetGroup;
            this.checkbox = new api_dom.InputEl('FacetCheckbox', 'admin-facet-cb', 'checkbox');
            this.label = new api_dom.LabelEl('FacetLabel', 'admin-facet-lbl');
            this.label.getEl().setInnerHtml(facetData.name + ' (' + facetData.count + ')');
            this.label.getEl().addEventListener('click', () => {
                var node = this.checkbox.getHTMLElement().getAttributeNode('checked');
                if (node) {
                    this.checkbox.getHTMLElement().removeAttribute('checked');
                } else {
                    this.checkbox.getHTMLElement().setAttribute('checked', '');
                }
                new api_event.FilterSearchEvent(this).fire();
            });
            this.appendChild(this.checkbox);
            this.appendChild(this.label);

            if (facetData.count == 0) {
                this.getHTMLElement().style.display = 'none';
            }
        }

        getFacetGroup():FacetGroup {
            return this.facetGroup;
        }

        getName():string {
            return this.name;
        }

        update(facetData) {
            this.label.getEl().setInnerHtml(facetData.name + ' (' + facetData.count + ')');
            if (facetData.count > 0 || this.isSelected()) {
                this.getHTMLElement().style.display = 'block';
            } else {
                this.getHTMLElement().style.display = 'none';
            }
        }

        isSelected():bool {
            return this.checkbox.getHTMLElement().getAttributeNode('checked') != null;
        }

        reset() {
            this.checkbox.getHTMLElement().removeAttribute('checked');
        }
    }

    export class FilterSearchAction extends api_ui.Action {

        filterValues;

        constructor() {
            super('filterSearchAction');
        }

        setFilterValues(values) {
            this.filterValues = values;
        }

        getFilterValues() {
            return this.filterValues;
        }
    }

    export class FilterResetAction extends api_ui.Action {

        constructor() {
            super('filterResetAction');
        }
    }
}