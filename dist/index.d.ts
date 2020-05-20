import * as React from 'react';
export interface IRNHole {
    x: number;
    y: number;
    width: number;
    height: number;
    cornerRadius?: number;
}
export interface IRNHoleView {
    holes?: IRNHole;
}
export declare const RNHoleView: React.FC<IRNHoleView>;
