import PropertyArray = api.data.PropertyArray;

describe('api.data.PropertyArray', () => {

    describe('when move', () => {

        it('given 2 string values when move(0, 1) then properties has correct index', () => {
            let tree = new PropertyTree();
            let array = PropertyArray.create().
                setType(ValueTypes.STRING).
                setParent(tree.getRoot()).
                setName('myStrings').
                build();

            array.add(ValueTypes.STRING.newValue('a'));
            array.add(ValueTypes.STRING.newValue('b'));

            // exercise
            array.move(0, 1);

            // verify
            expect(array.get(0).getString()).toBe('b');
            expect(array.get(1).getString()).toBe('a');

            expect(array.get(0).getIndex()).toBe(0);
            expect(array.get(1).getIndex()).toBe(1);
        });
    });
});
