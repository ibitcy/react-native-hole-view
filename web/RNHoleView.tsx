import * as React from 'react';
import type {
    ViewStyle,
    LayoutChangeEvent,
} from 'react-native';
import {
    View,
    StyleSheet,
} from 'react-native';

import {
    DEFAULT_DURATION,
    DEFAULT_RADIUS_VALUE,
    sanitizeAnimationProp,
    sanitizeHolesProp,
    SanitizedHole,
} from '../src/RNHoleView.types';
import type { IRNHoleView } from '../src/RNHoleView.types';

type LayoutSize = {
    width: number;
    height: number;
};

type MaskableViewStyle = ViewStyle & {
    mask?: string;
    WebkitMask?: string;
    maskImage?: string;
    WebkitMaskImage?: string;
    maskRepeat?: string;
    WebkitMaskRepeat?: string;
    maskSize?: string;
    WebkitMaskSize?: string;
    maskMode?: string;
};

type CornerRadii = {
    topLeft: number;
    topRight: number;
    bottomLeft: number;
    bottomRight: number;
};

const DEFAULT_OVERLAY_COLOR = 'rgba(0, 0, 0, 0.6)';

const clampRadius = (radius: number, width: number, height: number): number => {
    return Math.max(0, Math.min(radius, width / 2, height / 2));
};

const resolveRadius = (value: number, fallback: number): number => {
    return value > DEFAULT_RADIUS_VALUE ? value : Math.max(fallback, 0);
};

const resolveHoleRadii = (hole: SanitizedHole): CornerRadii => {
    const baseRadius = hole.borderRadius > DEFAULT_RADIUS_VALUE ? hole.borderRadius : 0;

    const borderTopLeft = resolveRadius(hole.borderTopLeftRadius, baseRadius);
    const borderTopRight = resolveRadius(hole.borderTopRightRadius, baseRadius);
    const borderBottomLeft = resolveRadius(hole.borderBottomLeftRadius, baseRadius);
    const borderBottomRight = resolveRadius(hole.borderBottomRightRadius, baseRadius);

    const borderTopStart = resolveRadius(
        hole.borderTopStartRadius,
        hole.isRTL ? borderTopRight : borderTopLeft,
    );
    const borderTopEnd = resolveRadius(
        hole.borderTopEndRadius,
        hole.isRTL ? borderTopLeft : borderTopRight,
    );
    const borderBottomStart = resolveRadius(
        hole.borderBottomStartRadius,
        hole.isRTL ? borderBottomRight : borderBottomLeft,
    );
    const borderBottomEnd = resolveRadius(
        hole.borderBottomEndRadius,
        hole.isRTL ? borderBottomLeft : borderBottomRight,
    );

    return hole.isRTL
        ? {
            topLeft: borderTopEnd,
            topRight: borderTopStart,
            bottomLeft: borderBottomEnd,
            bottomRight: borderBottomStart,
        }
        : {
            topLeft: borderTopStart,
            topRight: borderTopEnd,
            bottomLeft: borderBottomStart,
            bottomRight: borderBottomEnd,
        };
};

const roundedRectPath = (hole: SanitizedHole): string => {
    const { x, y, width, height } = hole;
    if (width <= 0 || height <= 0) {
        return '';
    }

    const radii = resolveHoleRadii(hole);
    const topLeft = clampRadius(radii.topLeft, width, height);
    const topRight = clampRadius(radii.topRight, width, height);
    const bottomRight = clampRadius(radii.bottomRight, width, height);
    const bottomLeft = clampRadius(radii.bottomLeft, width, height);

    const right = x + width;
       const bottom = y + height;

    return [
        `M${x + topLeft} ${y}`,
        `H${right - topRight}`,
        topRight > 0 ? `A${topRight} ${topRight} 0 0 1 ${right} ${y + topRight}` : `L${right} ${y}`,
        `V${bottom - bottomRight}`,
        bottomRight > 0 ? `A${bottomRight} ${bottomRight} 0 0 1 ${right - bottomRight} ${bottom}` : `L${right} ${bottom}`,
        `H${x + bottomLeft}`,
        bottomLeft > 0 ? `A${bottomLeft} ${bottomLeft} 0 0 1 ${x} ${bottom - bottomLeft}` : `L${x} ${bottom}`,
        `V${y + topLeft}`,
        topLeft > 0 ? `A${topLeft} ${topLeft} 0 0 1 ${x + topLeft} ${y}` : `L${x} ${y}`,
        'Z',
    ].join(' ');
};

const blockPointerEvent = (event: any) => {
    if (event?.stopPropagation) {
        event.stopPropagation();
    }
    if (event?.preventDefault) {
        event.preventDefault();
    }
};

declare global {
    namespace JSX {
        interface IntrinsicElements {
            svg: any;
            path: any;
        }
    }
}

export const RNHoleViewWeb = (props: IRNHoleView) => {
    const {
        animation,
        holes,
        onAnimationFinished,
        style,
        children,
        onLayout,
        ...rest
    } = props;

    const animationProp = React.useMemo(() => sanitizeAnimationProp(animation), [animation]);
    const holesProp = React.useMemo(() => sanitizeHolesProp(holes), [holes]);

    const [layout, setLayout] = React.useState<LayoutSize>({ width: 0, height: 0 });

    const flattenedStyle = React.useMemo<ViewStyle | undefined>(
        () => StyleSheet.flatten(style) as ViewStyle | undefined,
        [style],
    );

    const overlayColor = (flattenedStyle?.backgroundColor as string | undefined) || DEFAULT_OVERLAY_COLOR;

    const containerStyle = React.useMemo(
        () => {
            if (!flattenedStyle) {
                return styles.container;
            }
            const { backgroundColor: _bg, ...restStyle } = flattenedStyle;
            return [styles.container, restStyle];
        },
        [flattenedStyle],
    );

    const handleLayout = React.useCallback((event: LayoutChangeEvent) => {
        const { width, height } = event.nativeEvent.layout;
        setLayout({ width, height });
        onLayout?.(event);
    }, [onLayout]);

    const pathD = React.useMemo(() => {
        if (!layout.width || !layout.height) {
            return undefined;
        }

        const outer = `M0 0 H${layout.width} V${layout.height} H0 Z`;
        if (!holesProp.length) {
            return outer;
        }

        const holesPath = holesProp
            .map((hole) => roundedRectPath(hole))
            .join(' ');

        return `${outer} ${holesPath}`;
    }, [layout, holesProp]);

    const maskImage = React.useMemo(() => {
        if (!pathD || !layout.width || !layout.height) {
            return undefined;
        }
        const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${layout.width}" height="${layout.height}" viewBox="0 0 ${layout.width} ${layout.height}"><path d="${pathD}" fill="white" fill-rule="evenodd"/></svg>`;

        return `url('data:image/svg+xml;utf8,${encodeURIComponent(svg)}')`;
    }, [pathD, layout]);

    const maskStyle = React.useMemo<MaskableViewStyle | undefined>(() => {
        if (!maskImage) {
            return undefined;
        }

        return {
            maskImage,
            maskRepeat: 'no-repeat',
            maskSize: '100% 100%',
            maskMode: 'alpha',
            WebkitMaskImage: maskImage,
            WebkitMaskRepeat: 'no-repeat',
            WebkitMaskSize: '100% 100%',
        };
    }, [maskImage]);

    React.useEffect(() => {
        if (!animationProp || !onAnimationFinished) {
            return;
        }

        const timer = setTimeout(() => {
            onAnimationFinished();
        }, animationProp.duration ?? DEFAULT_DURATION);

        return () => clearTimeout(timer);
    }, [animationProp, onAnimationFinished]);

    return (
        <View
            {...rest}
            style={maskStyle ? [containerStyle, maskStyle] : containerStyle}
            onLayout={handleLayout}
        >
            {pathD ? (
                <svg
                    width="100%"
                    height="100%"
                    viewBox={`0 0 ${Math.max(layout.width, 0)} ${Math.max(layout.height, 0)}`}
                    preserveAspectRatio="none"
                    style={styles.overlay}
                >
                    <path
                        d={pathD}
                        fill={overlayColor}
                        fillRule="evenodd"
                        style={{ pointerEvents: 'auto' }}
                        onClick={blockPointerEvent}
                        onPointerDown={blockPointerEvent}
                        onPointerUp={blockPointerEvent}
                    />
                </svg>
            ) : null}
            <View pointerEvents="box-none" style={styles.childrenContainer}>
                {children}
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'relative',
    },
    overlay: {
        position: 'absolute',
        left: 0,
        top: 0,
        right: 0,
        bottom: 0,
        pointerEvents: 'none',
        zIndex: 0,
    },
    childrenContainer: {
        zIndex: 1,
    },
});


