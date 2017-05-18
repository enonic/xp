const path = require('path');
const fs = require('fs');
const Linter = require('tslint').Linter;

const configPath = path.resolve('tslint.json');

const tslintConfig = JSON.parse(fs.readFileSync(configPath, 'utf8'));

const tslint = new Linter({fix: true});

function lintFile(linter, name, linkString, tslintConfig) {
    // results are accumulate in `linter.getResult()` instance
    linter.lint(name, linkString, tslintConfig);
    // return new array for immutability purpose
    return linter.getResult().failures.slice(0);
}

function formatPosition(position) {
    const pos = position.getLineAndCharacter();
    const line = pos.line + 1;
    const character = pos.character + 1;
    return `[${line}, ${character}]`;
}

function Fail(name, position, reason, fixed) {
    this.log = function log() {
        if (!fixed) {
            const pos = formatPosition(position);
            console.log(`${name}${pos}: ${reason}`);
        }
    }
}

function lint(files) {
    const fails = files.map((name) => {
        return new Promise((resolve, reject) => {
            fs.readFile(name, 'utf8', (err, linkString) => {
                if (err) {
                    reject(err);
                } else {
                    resolve(lintFile(tslint, name, linkString, tslintConfig));
                }
            });
        }).catch((error) => {
            // Show files that can't be opened or not present in current branch
            // console.error(error.message);
        });
    });

    return Promise.all(fails).then((failsList) => {
        // Last array of fails contains all fails so far
        const finalFails = failsList[failsList.length - 1] || [];

        return finalFails.map((fail) => {
            return new Fail(fail.fileName, fail.startPosition, fail.failure, fail.fix);
        });
    });
}

module.exports = {lint};
