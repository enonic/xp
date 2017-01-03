import TogglerButton = api.ui.button.TogglerButton;

describe("api.ui.button.TogglerButton", () => {

    let button: TogglerButton;

    beforeEach(() => {
        button = new TogglerButton();
    });

    describe("constructor", () => {

        beforeEach(() => {
            spyOn(console, "error").and.stub();
        });

        it("contains base classes if className is not set", () => {
            expect(button.getClass().trim()).toEqual("xp-admin-common-button toggle-button icon-medium");
        });

        it("title attribute is null if it's not set", () => {
            expect(button.getEl().getAttribute("title")).toBeNull();
        });

        describe("status", () => {

            it("is inactive", () => {
                expect(button.isActive()).toBeFalsy();
            });

            it("is disabled", () => {
                expect(button.isEnabled()).toBeFalsy();
            });
        });

        describe("initialized", () => {

            beforeEach(() => {
                button = new TogglerButton("testClassName", "testTitle");
            });

            it("contains className", () => {
                expect(button.hasClass("testClassName")).toBeTruthy();
            });

            it("contains title attribute", () => {
                expect(button.getEl().getAttribute("title")).toEqual("testTitle");
            });
        });
    });

    describe("public methods", () => {

        beforeEach(() => {
            button = new TogglerButton("testClassName", "testTitle");
        });

        describe("setVisible()", () => {
            describe("inactive button", () => {

                beforeEach(() => {
                    button.setActive(false);
                });

                it("stay inactive on hide", () => {
                    button.setVisible(false);
                    expect(button.isActive()).toBeFalsy();
                });

                it("stay inactive on show", () => {
                    button.setVisible(true);
                    expect(button.isActive()).toBeFalsy();
                });
            });

            describe("active button", () => {

                beforeEach(() => {
                    button.setActive(true);
                });

                it("deactivated on hide", () => {
                    button.setVisible(false);
                    expect(button.isActive()).toBeFalsy();
                });

                it("stay active on show", () => {
                    button.setVisible(true);
                    expect(button.isActive()).toBeTruthy();
                });
            });
        });
    });

    describe("after click", () => {

        beforeEach(() => {
            button = new TogglerButton("testClassName", "testTitle");
        });

        describe("enabled", () => {

            beforeEach(() => {
                button.setEnabled(true);
            });
            describe("active button", () => {

                beforeEach(() => {
                    button.setActive(true);
                    button.getHTMLElement().click();
                });

                it("is deactivated", () => {
                    expect(button.isActive()).toBeFalsy();
                });
            });
            describe("inactive button", () => {

                beforeEach(() => {
                    button.setActive(false);
                    button.getHTMLElement().click();
                });

                it("is activated", () => {
                    expect(button.isActive()).toBeTruthy();
                });
            });
        });

        describe("disabled", () => {

            beforeEach(() => {
                button.setEnabled(false);
            });

            describe("active button", () => {

                beforeEach(() => {
                    button.setActive(true);
                    button.getHTMLElement().click();
                });

                it("stay active", () => {
                    expect(button.isActive()).toBeTruthy();
                });
            });

            describe("inactive button", () => {

                beforeEach(() => {
                    button.setActive(false);
                    button.getHTMLElement().click();
                });

                it("stay inactive", () => {
                    expect(button.isActive()).toBeFalsy();
                });
            });
        });
    });
});
