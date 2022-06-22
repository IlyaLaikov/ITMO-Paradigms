"use strict";

// Constant
const cnst = value => () => value;
const pi = cnst(Math.PI);
const e = cnst(Math.E);
const constantDict = {
    "pi": pi,
    "e": e,
};

// Variable
const variableDict = { "x": 0, "y": 1, "z": 2 };
const variable = arg => (...variables) => variables[variableDict[arg]];

// Operators
function operator(f, arity) {
    let innerOperator = (...args) => (...variables) => f(...args.map(arg => arg(...variables)));
    innerOperator.arity = arity !== undefined ? arity : f.length;
    return innerOperator;
}

const negate = operator(a => -a);
const abs = operator(Math.abs);
const add = operator((a, b) => a + b);
const subtract = operator((a, b) => a - b);
const multiply = operator((a, b) => a * b);
const divide = operator((a, b) => a / b);
const iff = operator((a, b, c) => a >= 0 ? b : c);

const operatorDict = {
    "negate": negate,
    "abs": abs,
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "iff": iff,
};

function parse(expression) {
    let stack = [];
    for (const token of expression.trim().split(/\s+/)) {
        if (token in operatorDict) {
            const operation = operatorDict[token];
            stack.push(operation(...stack.splice(-operation.arity)));
        } else if (token in variableDict) {
            stack.push(variable(token));
        } else if (token in constantDict) {
            stack.push(constantDict[token]);
        } else {
            stack.push(cnst(parseInt(token)));
        }
    }
    return stack.pop();
}
