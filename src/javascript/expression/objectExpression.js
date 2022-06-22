"use strict";

function Expression(evaluate, toString, diff, prefix, postfix) {
    this.evaluate = evaluate;
    this.toString = toString;
    this.diff = diff;
    this.prefix = prefix;
    this.postfix = postfix;
}

function Const(value) {
    Expression.call(this,
        () => +value,
        () => value.toString(),
        () => Const.ZERO,
        () => value.toString(),
        () => value.toString(),
    );
}
Const.prototype = Object.create(Expression.prototype);
Const.prototype.constructor = Const;

Const.ZERO = new Const(0);
Const.ONE = new Const(1);
Const.TWO = new Const(2);
Const.E = new Const(Math.E);

const variableDict = { "x": 0, "y": 1, "z": 2 };
function Variable(name) {
    Expression.call(this,
        (...vars) => vars[variableDict[name]],
        () => name,
        (diffName) => name === diffName ? Const.ONE : Const.ZERO,
        () => name,
        () => name,
    );
}
Variable.prototype = Object.create(Expression.prototype);
Variable.prototype.constructor = Variable;

function Operator(f, op, diffRule, args, arity) {
    Expression.call(this,
        (...vars) => f(...args.map(arg => arg.evaluate(...vars))),
        () => args.map(arg => arg.toString()).join(" ") + " " + op,
        (name) => diffRule(...args.map(arg => arg.diff(name)), ...args),
        () => "(" + op + " " + args.map(arg => arg.prefix()).join(" ") + ")",
        () => "(" + args.map(arg => arg.postfix()).join(" ") + " " + op + ")",
    );
    this.arity = arity !== undefined ? arity : f.length;
}
Operator.prototype = Object.create(Expression.prototype);
Operator.prototype.constructor = Operator;

function Negate(...args) {
    Operator.call(this,
        a => -a,
        "negate",
        (da) => new Negate(da),
        args
    );
}
Negate.prototype = Object.create(Operator.prototype);
Negate.prototype.constructor = Negate;

function Add(...args) {
    Operator.call(this,
        (a, b) => a + b,
        "+",
        (da, db) => new Add(da, db),
        args
    );
}
Add.prototype = Object.create(Operator.prototype);
Add.prototype.constructor = Add;

function Subtract(...args) {
    Operator.call(this,
        (a, b) => a - b,
        "-",
        (da, db) => new Subtract(da, db),
        args
    );
}
Subtract.prototype = Object.create(Operator.prototype);
Subtract.prototype.constructor = Subtract;

function Multiply(...args) {
    Operator.call(this,
        (a, b) => a * b,
        "*",
        (da, db, a, b) => new Add(
            new Multiply(da, b),
            new Multiply(a, db)
        ),
        args
    );
}
Multiply.prototype = Object.create(Operator.prototype);
Multiply.prototype.constructor = Multiply;

function Divide(...args) {
    Operator.call(this,
        (a, b) => a / b,
        "/",
        (da, db, a, b) => new Divide(
            new Subtract(
                new Multiply(da, b),
                new Multiply(a, db)
            ),
            new Multiply(b, b)),
        args
    );
}
Divide.prototype = Object.create(Operator.prototype);
Divide.prototype.constructor = Divide;

function Log(...args) {
    Operator.call(this,
        (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
        "log",
        (da, db, a, b) => new Divide(
            new Subtract(
                new Divide(new Multiply(new Log(Const.E, a), db), b),
                new Divide(new Multiply(new Log(Const.E, b), da), a)
            ),
            new Pow(new Log(Const.E, a), Const.TWO)
        ),
        args
    );
}
Log.prototype = Object.create(Operator.prototype);
Log.prototype.constructor = Log;

function Pow(...args) {
    Operator.call(this,
        (a, b) => Math.pow(a, b),
        "pow",
        (da, db, a, b) => new Multiply(
            new Pow(a, new Subtract(b, Const.ONE)),
            new Add(
                new Multiply(b, da),
                new Multiply(new Multiply(a, new Log(Const.E, a)), db)
            )
        ),
        args
    );
}
Pow.prototype = Object.create(Operator.prototype);
Pow.prototype.constructor = Pow;

function Min3(...args) {
    Operator.call(this,
        Math.min,
        "min3",
        (...args) => {
            const diffArg = args.splice(0, args.length / 2);
            return diffArg[args.indexOf(Math.min(args))];
        },
        args, 3,
    );
}
Min3.prototype = Object.create(Operator.prototype);
Min3.prototype.constructor = Min3;

function Max5(...args) {
    Operator.call(this,
        Math.max,
        "max5",
        (...args) => {
            const diffArg = args.splice(0, args.length / 2);
            return diffArg[args.indexOf(Math.max(args))];
        },
        args, 5,
    );
}
Max5.prototype = Object.create(Operator.prototype);
Max5.prototype.constructor = Max5;

function Mean(...args) {
    Operator.call(this,
        (...args) => {
            return args.reduce((prev, next) => prev + next) / args.length;
        },
        "mean",
        (...args) => {
            args.splice(-args.length / 2);
            return new Divide(
                args.reduce((prev, next) => new Add(prev, next)),
                new Const(args.length)
            );
        },
        args
    );
}
Mean.prototype = Object.create(Operator.prototype);
Mean.prototype.constructor = Mean;

function Var(...args) {
    Operator.call(this,
        (...args) => {
            return args.map((a) => a * a).reduce((prev, next) => prev + next) / args.length
                 - Math.pow(args.reduce((prev, next) => prev + next) / args.length, 2);
        },
        "var",
        (...args) => {
            const diffArg = args.splice(0, args.length / 2);
            return new Subtract(
                new Divide(
                    diffArg
                        .map((a, i) => new Multiply(a, args[i]))
                        .reduce((prev, next) => new Add(prev, next)),
                    new Const(args.length / 2)
                ),
                new Divide(
                    new Multiply(
                        diffArg.reduce((prev, next) => new Add(prev, next)),
                        args.reduce((prev, next) => new Add(prev, next))
                    ),
                    new Const(Math.pow(args.length, 2) / 2)
                )
            );
        },
        args
    );
}
Var.prototype = Object.create(Operator.prototype);
Var.prototype.constructor = Var;

const operatorDict = {
    "negate": Negate,
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "pow": Pow,
    "log": Log,
    "min3": Min3,
    'max5': Max5,
    'mean': Mean,
    'var': Var,
};

function parse(expression) {
    let stack = [];
    for (const token of expression.trim().split(/\s+/)) {
        if (token in operatorDict) {
            const operation = operatorDict[token];
            stack.push(new operation(...stack.splice(-(new operation).arity)));
        } else if (token in variableDict) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(parseInt(token)));
        }
    }
    return stack.pop();
}

function Parser(expression) {
    this.expression = expression;
    this.offset = 0;
}
Parser.prototype.skipWhitespaces = function() {
    while (this.offset < this.expression.length && /\s/.test(this.expression[this.offset])) {
        ++this.offset;
    }
}
Parser.prototype.parseExpr = function() {
    this.skipWhitespaces();

    if (this.expression[this.offset] === '(') {
        return this.parseScope();
    } else {
        let token = this.parseToken();
        if (token[0] in variableDict) {
            if (token.length === 1) {
                return new Variable(token);
            } else {
                throw new ParserExpression(`broken variable name '${token}'`, this.offset);
            }
        } else if (/-|\d$/.test(token[0])) {
            if (token in operatorDict) {
                return token;
            } else if(/^-?\d+$/.test(token)) {
                return new Const(parseInt(token));
            } else {
                throw new ParserExpression(`'${token}' found, but '-' or number expected`, this.offset);
            }
        } else if (token in operatorDict) {
            return token;
        }
        throw new ParserExpression(`unknown token '${token}'`, this.offset);
    }
}
Parser.prototype.parseScope = function() {
    ++this.offset;
    this.skipWhitespaces();
    let [op, expressions] = this.parseScopeImpl();
    this.skipWhitespaces();
    if (this.expression[this.offset] !== ")") {
        throw new ParserExpression(
            `'${this.expression[this.offset]}' found, but ')' expected`, this.offset);
    }
    ++this.offset;

    if (expressions.length < 1) {
        throw new ParserExpression(
            `${expressions.length} arguments found, but 1 or more expected`, this.offset);
    }

    let expr = new op(...expressions);
    let arity = expr.arity;
    if (arity !== 0 && expressions.length !== arity) {
        throw new ParserExpression(
            `${expressions.length} arguments found, but ${arity} expected`, this.offset);
    }
    return expr
}
Parser.prototype.parseScopeImpl = function () {
    let op = this.parseToken();
    if (op in operatorDict) {
        op = operatorDict[op];
    } else {
        throw new ParserExpression(`'${op}' found, but operator expected`, this.offset);
    }
    let expressions = [];
    while (this.offset < this.expression.length && this.expression[this.offset] !== ")") {
        let obj = this.parseExpr()
        if (obj instanceof Expression) {
            expressions.push(obj);
        } else {
            throw new ParserExpression(`'${obj}' found, but expression expected`, this.offset);
        }
        this.skipWhitespaces();
    }
    return [op, expressions];
}
Parser.prototype.parseToken = function() {
    let start = this.offset;
    while (this.offset < this.expression.length && /[^\s()]/.test(this.expression[this.offset])) {
        this.offset++;
    }
    if (start === this.offset) {
        throw new ParserExpression("no token in the scope", this.offset);
    }
    let token = this.expression.substring(start, this.offset);
    this.skipWhitespaces();
    return token;
}
Parser.prototype.parse = function() {
    this.skipWhitespaces();
    if (this.offset === this.expression.length) {
        throw new ParserExpression("empty input", this.offset);
    }
    let expr = this.parseExpr();
    this.skipWhitespaces();
    if (this.offset === this.expression.length) {
        return expr;
    } else {
        throw new ParserExpression(
            `'${this.expression[this.offset]}' found, but end of expr expected`, this.offset);
    }
}

function ParserExpression(message = "<<empty message>>", position = "<<unknown>>") {
    this.name = 'ParserExpression';
    this.message = `position ${position}: ${message}`;
    this.stack = (new Error()).stack;
}
ParserExpression.prototype = Object.create(Error.prototype);
ParserExpression.prototype.constructor = ParserExpression;

function PrefixParser(expression) {
    Parser.call(this, expression)
}
PrefixParser.prototype = Object.create(Parser.prototype);
PrefixParser.prototype.constructor = PrefixParser;

function PostfixParser(expression) {
    Parser.call(this, expression)
}
PostfixParser.prototype = Object.create(Parser.prototype);
PostfixParser.prototype.parseScopeImpl = function () {
    let expressions = [];
    let op = undefined;
    while (this.offset < this.expression.length && this.expression[this.offset] !== ")") {
        let obj = this.parseExpr();
        if (obj instanceof Expression) {
            expressions.push(obj);
        } else {
            op = operatorDict[obj];
            break;
        }
        this.skipWhitespaces();
    }
    if (op === undefined) {
        if (expressions.length > 0) {
            throw new ParserExpression(
                `'${expressions[expressions.length - 1]}' found, but operator expected`, this.offset);
        } else {
            throw new ParserExpression("no operator found in the scope", this.offset);
        }
    }
    return [op, expressions];
}
PostfixParser.prototype.constructor = PostfixParser;

const parsePrefix = (expression) => new PrefixParser(expression).parse();
const parsePostfix = (expression) => new PostfixParser(expression).parse();
