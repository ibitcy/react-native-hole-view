"use strict";
var __rest = (this && this.__rest) || function (s, e) {
    var t = {};
    for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
        t[p] = s[p];
    if (s != null && typeof Object.getOwnPropertySymbols === "function")
        for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
            if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                t[p[i]] = s[p[i]];
        }
    return t;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.RNHoleView = exports.ERNHoleViewTimingFunction = exports.RNHole = void 0;
const react_native_1 = require("react-native");
const React = require("react");
const RNHoleViewManager = (0, react_native_1.requireNativeComponent)('RNHoleView');
const DEFAULT_DURATION = 1000;
const DEFAULT_RADIUS_VALUE = -1;
class RNHole {
    constructor() {
        this.isRTL = false;
        this.borderTopLeftRadius = DEFAULT_RADIUS_VALUE;
        this.borderTopRightRadius = DEFAULT_RADIUS_VALUE;
        this.borderBottomLeftRadius = DEFAULT_RADIUS_VALUE;
        this.borderBottomRightRadius = DEFAULT_RADIUS_VALUE;
        this.borderTopStartRadius = DEFAULT_RADIUS_VALUE;
        this.borderTopEndRadius = DEFAULT_RADIUS_VALUE;
        this.borderBottomStartRadius = DEFAULT_RADIUS_VALUE;
        this.borderBottomEndRadius = DEFAULT_RADIUS_VALUE;
    }
}
exports.RNHole = RNHole;
var ERNHoleViewTimingFunction;
(function (ERNHoleViewTimingFunction) {
    ERNHoleViewTimingFunction["LINEAR"] = "LINEAR";
    ERNHoleViewTimingFunction["EASE_IN"] = "EASE_IN";
    ERNHoleViewTimingFunction["EASE_OUT"] = "EASE_OUT";
    ERNHoleViewTimingFunction["EASE_IN_OUT"] = "EASE_IN_OUT";
})(ERNHoleViewTimingFunction = exports.ERNHoleViewTimingFunction || (exports.ERNHoleViewTimingFunction = {}));
const RNHoleView = props => {
    const { animation, holes, onAnimationFinished } = props, rest = __rest(props, ["animation", "holes", "onAnimationFinished"]);
    const animationProp = animation
        ? {
            duration: typeof animation.duration === 'number' ? animation.duration : DEFAULT_DURATION,
            timingFunction: animation.timingFunction || ERNHoleViewTimingFunction.LINEAR,
        }
        : undefined;
    return (React.createElement(RNHoleViewManager, Object.assign({ holes: holes, animation: animationProp, onAnimationFinished: onAnimationFinished }, rest)));
};
exports.RNHoleView = RNHoleView;
//# sourceMappingURL=index.js.map