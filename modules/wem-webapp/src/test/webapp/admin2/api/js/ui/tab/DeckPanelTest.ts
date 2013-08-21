///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/jquery.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ExtJs.d.ts' />

///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/Observable.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/event/Event.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/event/EventBus.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ElementHelper.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/Element.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/DivEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ButtonEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/SpanEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/UlEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/LiEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/Panel.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/DeckPanelListener.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/DeckPanel.ts' />

TestCase("DeckPanel", {

    "test given DeckPanel with three panels and last panel is shown when last is removed then the second becomes the shown": function () {

        // setup
        var deckPanel = new api_ui.DeckPanel();
        var panel1 = new api_ui.Panel();
        var panel2 = new api_ui.Panel();
        var panel3 = new api_ui.Panel();
        deckPanel.addPanel(panel1);
        deckPanel.addPanel(panel2);
        deckPanel.addPanel(panel3);
        deckPanel.showPanel(2);

        // verify setup
        assertEquals(2, deckPanel.getPanelShownIndex());
        assertEquals(panel3, deckPanel.getPanelShown());

        // exercise
        deckPanel.removePanelByIndex(2);

        // verify
        assertEquals(1, deckPanel.getPanelShownIndex());
        assertEquals(panel2, deckPanel.getPanelShown());
    },

    "test given three listeners when removing second then second listener is not notified when event is triggered": function () {

        // setup
        var deckPanel = new api_ui.DeckPanel();
        var panel1 = new api_ui.Panel();
        var panel2 = new api_ui.Panel();
        deckPanel.addPanel(panel1);
        deckPanel.addPanel(panel2);

        var listener1 = {
            onPanelShown: (event) => {
            }
        };
        var listener2 = {
            onPanelShown: (event) => {
                fail("Second listener not expected to be notified");
            }
        };
        var listener3 = {
            onPanelShown: (event) => {
            }
        };
        deckPanel.addListener(listener1);
        deckPanel.addListener(listener2);
        deckPanel.addListener(listener3);

        // verify setup
        deckPanel.showPanel(1);

        // exercise
        deckPanel.removeListener(listener2);

        // verify
        deckPanel.showPanel(0);
    }

});


