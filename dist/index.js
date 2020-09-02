"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RNHoleView = exports.RNHole = void 0;
const react_native_1 = require("react-native");
const React = require("react");
const RNHoleViewManager = react_native_1.requireNativeComponent('RNHoleView');
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
exports.RNHoleView = props => {
    return React.createElement(RNHoleViewManager, Object.assign({}, props));
};
//# sourceMappingURL=index.js.map