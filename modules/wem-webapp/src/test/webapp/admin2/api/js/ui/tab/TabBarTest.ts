///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/jquery.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ExtJs.d.ts' />

///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabBar.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabBarItem.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabBarItemListener.ts' />

TestCase("TabBar", {

    "test given TabBar added to DOM when getElementById then corresponding element is returned ": function () {

        var tabBar = new api_ui_tab.TabBar();
        document.body.appendChild(tabBar.getHTMLElement());

        var tabBarEl = document.getElementById(tabBar.getId());

        assertNotNull(tabBarEl);
        assertEquals(tabBar.getId(), tabBarEl.id);
    },

    "test given TabBar with two tabs when getSize then two is returned": function () {

        var tabBar = new api_ui_tab.TabBar();
        var tab1 = new api_ui_tab.TabBarItem("Tab1");
        var tab2 = new api_ui_tab.TabBarItem("Tab2");
        tabBar.addNavigationItem(tab1);
        tabBar.addNavigationItem(tab2);

        assertEquals(2, tabBar.getSize());
    },

    "test given TabBar with four tabs when second removed then the indexes of the succeeding tabs is lowered by one": function () {

        // setup
        var tabBar = new api_ui_tab.TabBar();
        var tab1 = new api_ui_tab.TabBarItem("Tab1");
        var tab2 = new api_ui_tab.TabBarItem("Tab2");
        var tab3 = new api_ui_tab.TabBarItem("Tab3");
        var tab4 = new api_ui_tab.TabBarItem("Tab4");
        tabBar.addNavigationItem(tab1);
        tabBar.addNavigationItem(tab2);
        tabBar.addNavigationItem(tab3);
        tabBar.addNavigationItem(tab4);

        // verify setup
        assertEquals(0, tab1.getIndex());
        assertEquals(1, tab2.getIndex());
        assertEquals(2, tab3.getIndex());
        assertEquals(3, tab4.getIndex());

        // exercise
        tabBar.removeNavigationItem(tab2);

        // verify
        assertEquals(0, tab1.getIndex());
        assertEquals(1, tab3.getIndex());
        assertEquals(2, tab4.getIndex());
    },

    "test given TabBar with selected tab when a tab before selected tab is removed then selected index is reduced by one": function () {

        // setup
        var tabBar = new api_ui_tab.TabBar();
        var tab1 = new api_ui_tab.TabBarItem("Tab1");
        var tab2 = new api_ui_tab.TabBarItem("Tab2");
        var tab3 = new api_ui_tab.TabBarItem("Tab3");
        var tab4 = new api_ui_tab.TabBarItem("Tab4");
        tabBar.addNavigationItem(tab1);
        tabBar.addNavigationItem(tab2);
        tabBar.addNavigationItem(tab3);
        tabBar.addNavigationItem(tab4);
        tabBar.selectNavigationItem(2);

        // verify setup
        assertEquals(2, tabBar.getSelectedIndex());

        // exercise
        tabBar.removeNavigationItem(tab2);

        // verify
        assertEquals(1, tabBar.getSelectedIndex());
    },

    "test given TabBar with selected tab when all tabs are removed then no selected": function () {

        // setup
        var tabBar = new api_ui_tab.TabBar();
        var tab1 = new api_ui_tab.TabBarItem("Tab1");
        var tab2 = new api_ui_tab.TabBarItem("Tab2");
        tabBar.addNavigationItem(tab1);
        tabBar.addNavigationItem(tab2);
        tabBar.selectNavigationItem(1);

        // verify setup
        assertEquals(1, tabBar.getSelectedIndex());

        // exercise
        tabBar.removeNavigationItem(tab1);
        tabBar.removeNavigationItem(tab2);

        // verify
        assertEquals(-1, tabBar.getSelectedIndex());
    },

    "test given TabBar with three tabs and last tab is selected when last is removed then the second becomes the selected": function () {

        // setup
        var tabBar = new api_ui_tab.TabBar();
        var tab1 = new api_ui_tab.TabBarItem("Tab1");
        var tab2 = new api_ui_tab.TabBarItem("Tab2");
        var tab3 = new api_ui_tab.TabBarItem("Tab3");
        tabBar.addNavigationItem(tab1);
        tabBar.addNavigationItem(tab2);
        tabBar.addNavigationItem(tab3);
        tabBar.selectNavigationItem(2);

        // verify setup
        assertEquals(2, tabBar.getSelectedIndex());

        // exercise
        tabBar.removeNavigationItem(tab3);

        // verify
        assertEquals(1, tabBar.getSelectedIndex());
    }

});