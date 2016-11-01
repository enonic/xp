import NamePrettyfier = api.NamePrettyfier;

describe("api.NamePrettyfier", () => {

    describe("tests for api.NamePrettyfier.prettify() function", () => {


        it("should prettify correctly", () => {
            expect(NamePrettyfier.prettify(" test Name ")).toBe("test-name");
            expect(NamePrettyfier.prettify("æse Kælrot")).toBe("aese-kaelrot");
            expect(NamePrettyfier.prettify("test/stuff/here ")).toBe("test-stuff-here");
            expect(NamePrettyfier.prettify("t@e&st^-$cha$r@s $1 ")).toBe("t-e-st-cha-r-s-1");
            expect(NamePrettyfier.prettify("Test\u0081Stuff\u0082Here\u0083")).toBe("teststuffhere");
            expect(NamePrettyfier.prettify("еvidem.ment_']]")).toBe("evidem.ment"); // е - \u0415
        });

    });


});