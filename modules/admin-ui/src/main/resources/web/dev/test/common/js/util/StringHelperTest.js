describe("api.util.StringHelperTest", function () {

    var sh = api.util.StringHelper;

    describe("limit", function () {

        var s = "Lorem ipsum 123% !";

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.limit(null, 11)).toBe("");
            expect(sh.limit("", 11)).toBe("");
        });
        it("should add ellipsis at the end by default", function () {
            expect(sh.limit(s, 11)).toBe(s.substr(0, 11) + "\u2026");
        });
        it("should allow to specify trailing char", function () {
            expect(sh.limit(s, 11, "!")).toBe(s.substr(0, 11) + "!");
        })

    });

    describe("capitalize", function () {

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.capitalize(null, 11)).toBe("");
            expect(sh.capitalize("", 11)).toBe("");
        });
        it("should capitalize just the first letter", function () {
            var s = "lorem ipsum 123% !";
            expect(sh.capitalize(s)).toBe(s.charAt(0).toUpperCase() + s.substr(1));
        });

    });

    describe("capitalizeAll", function () {

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.capitalizeAll(null, 11)).toBe("");
            expect(sh.capitalizeAll("", 11)).toBe("");
        });
        it("should capitalize first letters of every word", function () {
            expect(sh.capitalizeAll("lorem ipsum 123% !")).toBe("Lorem Ipsum 123% !");
        });

    });

    describe("isUpperCase", function () {

        it("should return false if invalid arguments are passed", function () {
            expect(sh.isUpperCase(null)).toBe(false);
            expect(sh.isUpperCase("")).toBe(false);
        });
        it("should be true if all letters are uppercase", function () {
            expect(sh.isUpperCase("LOREM IPSUM  123% !")).toBe(true);
        });
        it("should be false if not all letters are uppercase", function () {
            expect(sh.isUpperCase("LOReM IPSUM  123% !")).toBe(false);
        });
    });

    describe("isLowerCase", function () {

        it("should return false if invalid arguments are passed", function () {
            expect(sh.isLowerCase(null)).toBe(false);
            expect(sh.isLowerCase("")).toBe(false);
        });
        it("should be true if all letters are uppercase", function () {
            expect(sh.isLowerCase("lorem ipsum  123% !")).toBe(true);
        });
        it("should be false if not all letters are uppercase", function () {
            expect(sh.isLowerCase("lorem Ipsum  123% !")).toBe(false);
        });
    });

    describe("isMixedCase", function () {

        it("should return false if invalid arguments are passed", function () {
            expect(sh.isMixedCase(null)).toBe(false);
            expect(sh.isMixedCase("")).toBe(false);
        });
        it("should be true if value contains both cases", function () {
            expect(sh.isMixedCase("lorem ipsUm  123% !")).toBe(true);
        });
        it("should be false if all letters either lower or upper case", function () {
            expect(sh.isMixedCase("lorem ipsum  123% !")).toBe(false);
            expect(sh.isMixedCase("LOREM IPSUM  123% !")).toBe(false);
        });
    });

    describe("isEmpty", function () {

        it("should be true for null", function () {
            expect(sh.isEmpty(null)).toBeTruthy();
        });
        it("should be true for empty string", function () {
            expect(sh.isEmpty("")).toBeTruthy();
        });
        it("should be false for \' \' string", function () {
            expect(sh.isEmpty(" ")).toBeFalsy();
        });
        it("should be false for \'bob\'", function () {
            expect(sh.isEmpty("bob")).toBeFalsy();
        });
        it("should be false for \'  bob  \'", function () {
            expect(sh.isEmpty("  bob  ")).toBeFalsy();
        });

    });

    describe("isBlank", function () {

        it("should be true for null", function () {
            expect(sh.isBlank(null)).toBeTruthy();
        });
        it("should be true for empty string", function () {
            expect(sh.isBlank("")).toBeTruthy();
        });
        it("should be true for \' \' string", function () {
            expect(sh.isBlank(" ")).toBeTruthy();
        });
        it("should be false for \'bob\'", function () {
            expect(sh.isBlank("bob")).toBeFalsy();
        });
        it("should be false for \'  bob  \'", function () {
            expect(sh.isBlank("  bob  ")).toBeFalsy();
        });

    });

    describe("removeCarriageChars", function () {

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.removeCarriageChars(null)).toBe("");
            expect(sh.removeCarriageChars("")).toBe("");
        });
        it("should remove just carriage chars", function () {
            expect(sh.removeCarriageChars("Lorem \ripsumr 123\r% !")).toBe("Lorem ipsumr 123% !");
        });

    });

    describe("removeCarriageChars", function () {

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.removeWhitespaces(null)).toBe("");
            expect(sh.removeWhitespaces("")).toBe("");
        });
        it("should remove just whitespace chars", function () {
            expect(sh.removeWhitespaces("Lorem   ipsum 123% !")).toBe("Loremipsum123%!");
        });

    });

    describe("removeCarriageChars", function () {

        it("should return empty array if invalid arguments are passed", function () {
            expect(sh.removeEmptyStrings(null)).toEqual([]);
            expect(sh.removeEmptyStrings([])).toEqual([]);
        });
        it("should remove null and empty strings from array", function () {
            expect(sh.removeEmptyStrings(["Lorem", null, " ipsum ", "", "123% ", " !"])).toEqual(["Lorem", " ipsum ", "123% ", " !"]);
        });

    });

    describe("substringBetween", function () {

        var s = "to be or not to eat bee ?";

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.substringBetween("abc", null, "c")).toBe("");
            expect(sh.substringBetween("abc", "a", null)).toBe("");
            expect(sh.substringBetween(null, "a", "c")).toBe("");
            expect(sh.substringBetween("abc", "", "c")).toBe("");
            expect(sh.substringBetween("abc", "a", "")).toBe("");
            expect(sh.substringBetween("", "a", "c")).toBe("");
            expect(sh.substringBetween(s, "to", "")).toBe("");
            expect(sh.substringBetween(s, "eat", "or")).toBe("");
        });
        it("should return string between first occurrence of left and right params", function () {
            expect(sh.substringBetween("to be or not to eat bee ?", "to", "be")).toBe(" ");
        });

    });

    describe("format", function () {

        var s = "{0} {{0}} {{0}{1}} {{0{1}}} {{{0}1}} {{{0}{1}}}";

        it("should return empty string if invalid arguments are passed", function () {
            expect(sh.format(null)).toBe("");
        });
        it("should format the string correctly", function () {
            expect(sh.format(s, 1)).toBe("1 {0} {0}undefined} {0undefined} {11} {1undefined}");
            expect(sh.format(s, null, 3.14)).toBe("null {0} {0}3.14} {03.14} {null1} {null3.14}");
            expect(sh.format(s, "{1}", "a%")).toBe("{1} {0} {0}a%} {0a%} {{1}1} {{1}a%}");
        });

    });

});

