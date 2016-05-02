describe("api.NamePrettyfierTest", function () {

    describe("tests for api.NamePrettyfier.prettify() function", function () {


        it("should prettify correctly", function () {
            expect(api.NamePrettyfier.prettify(" test Name ")).toBe("test-name");
            expect(api.NamePrettyfier.prettify("?se K?lrot")).toBe("ase-kalrot");
            expect(api.NamePrettyfier.prettify("test/stuff/here ")).toBe("test-stuff-here");
            expect(api.NamePrettyfier.prettify("t@e&st^-$cha$r@s $1 ")).toBe("t-e-st-cha-r-s-1");
            expect(api.NamePrettyfier.prettify("Test\u0081Stuff\u0082Here\u0083")).toBe("teststuffhere");
            expect(api.NamePrettyfier.prettify("?videm.ment_']]")).toBe("evidem.ment");
        });

    });


});