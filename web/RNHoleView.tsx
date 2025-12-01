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

import type { IRNHoleView, SanitizedAnimation } from '../src/RNHoleView.types';

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
const HOLE_NUMERIC_KEYS: Array<Exclude<keyof SanitizedHole, 'isRTL'>> = [
    'height',
    'width',
    'x',
    'y',
    'borderRadius',
    'borderTopLeftRadius',
    'borderTopRightRadius',
    'borderBottomLeftRadius',
    'borderBottomRightRadius',
    'borderTopStartRadius',
    'borderTopEndRadius',
    'borderBottomStartRadius',
    'borderBottomEndRadius',
];
const lerp = (start: number, end: number, progress: number): number => {
    if (progress <= 0) {
        return start;
    }
    if (progress >= 1) {
        return end;
    }
    return start + (end - start) * progress;
};

const getEasedProgress = (timingFunction: SanitizedAnimation['timingFunction'], progress: number): number => {
    const clamped = Math.min(Math.max(progress, 0), 1);
    switch (timingFunction) {
        case 'EASE_IN':
            return clamped * clamped;
        case 'EASE_OUT':
            return 1 - (1 - clamped) * (1 - clamped);
        case 'EASE_IN_OUT':
            return clamped < 0.5
                ? 2 * clamped * clamped
                : 1 - Math.pow(-2 * clamped + 2, 2) / 2;
        case 'LINEAR':
        default:
            return clamped;
    }
};

const interpolateHole = (fromHole: SanitizedHole, toHole: SanitizedHole, progress: number): SanitizedHole => {
    const easedProgress = Math.min(Math.max(progress, 0), 1);
    const result: SanitizedHole = { ...toHole };

    HOLE_NUMERIC_KEYS.forEach((key) => {
        result[key] = lerp(fromHole[key], toHole[key], easedProgress);
    });

    return result;
};

const holesAreEqual = (first: SanitizedHole[], second: SanitizedHole[]): boolean => {
    if (first.length !== second.length) {
        return false;
    }

    for (let i = 0; i < first.length; i += 1) {
        const a = first[i];
        const b = second[i];
        if (!a || !b) {
            return false;
        }
        if (a.isRTL !== b.isRTL) {
            return false;
        }
        for (const key of HOLE_NUMERIC_KEYS) {
            if (a[key] !== b[key]) {
                return false;
            }
        }
    }

    return true;
};

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

const useStableId = (): string => {
    const idRef = React.useRef<string | null>(null);
    if (idRef.current === null) {
        idRef.current = `rnhw-mask-${Math.random().toString(36).slice(2, 10)}`;
    }
    return idRef.current;
};

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
    const [animatedHoles, setAnimatedHoles] = React.useState<SanitizedHole[]>(holesProp);

    const lastTargetHolesRef = React.useRef<SanitizedHole[]>(holesProp);
    const renderedHolesRef = React.useRef<SanitizedHole[]>(holesProp);
    const animationFrameRef = React.useRef<number | null>(null);
    const animationStartRef = React.useRef<number | null>(null);
    const finishCallbackRef = React.useRef<(() => void) | undefined>(onAnimationFinished);

    const cancelAnimation = React.useCallback(() => {
        if (animationFrameRef.current !== null) {
            cancelAnimationFrame(animationFrameRef.current);
            animationFrameRef.current = null;
        }
        animationStartRef.current = null;
    }, []);

    React.useEffect(() => {
        finishCallbackRef.current = onAnimationFinished;
    }, [onAnimationFinished]);

    React.useEffect(() => {
        renderedHolesRef.current = animatedHoles;
    }, [animatedHoles]);

    React.useEffect(() => {
        return () => {
            cancelAnimation();
        };
    }, [cancelAnimation]);

    React.useEffect(() => {
        const prevTargetHoles = lastTargetHolesRef.current;
        const startHolesSnapshot = renderedHolesRef.current;

        const runImmediateUpdate = () => {
            cancelAnimation();
            setAnimatedHoles(holesProp);
            renderedHolesRef.current = holesProp;
            lastTargetHolesRef.current = holesProp;
        };

        if (
            !animationProp ||
            !startHolesSnapshot.length ||
            !holesProp.length ||
            holesAreEqual(prevTargetHoles, holesProp)
        ) {
            runImmediateUpdate();
            return;
        }

        const duration = animationProp.duration ?? DEFAULT_DURATION;

        cancelAnimation();

        const frame = (timestamp: number) => {
            if (animationStartRef.current === null) {
                animationStartRef.current = timestamp;
            }

            const elapsed = timestamp - animationStartRef.current;
            const progress = Math.min(Math.max(elapsed / duration, 0), 1);
            const easedProgress = getEasedProgress(animationProp.timingFunction, progress);

            const nextHoles = holesProp.map((targetHole, index) => {
                const fromHole = startHolesSnapshot[index] ?? targetHole;
                if (progress === 1) {
                    return targetHole;
                }
                return interpolateHole(fromHole, targetHole, easedProgress);
            });

            setAnimatedHoles(nextHoles);

            if (progress >= 1) {
                cancelAnimation();
                setAnimatedHoles(holesProp);
                renderedHolesRef.current = holesProp;
                lastTargetHolesRef.current = holesProp;
                finishCallbackRef.current?.();
                return;
            }

            animationFrameRef.current = requestAnimationFrame(frame);
        };

        animationFrameRef.current = requestAnimationFrame(frame);

        return () => {
            cancelAnimation();
        };
    }, [animationProp, cancelAnimation, holesProp]);

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

    const maskId = useStableId();

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
        if (!animatedHoles.length) {
            return outer;
        }

        const holesPath = animatedHoles
            .map((hole) => roundedRectPath(hole))
            .join(' ');

        return `${outer} ${holesPath}`;
    }, [layout, animatedHoles]);

    const maskStyle = React.useMemo<MaskableViewStyle | undefined>(() => {
        if (!pathD) {
            return undefined;
        }

        return {
            mask: `url(#${maskId})`,
            WebkitMask: `url(#${maskId})`,
            maskRepeat: 'no-repeat',
            maskSize: '100% 100%',
            maskMode: 'alpha',
            WebkitMaskRepeat: 'no-repeat',
            WebkitMaskSize: '100% 100%',
        };
    }, [maskId, pathD]);

    const maskDefinition = React.useMemo(() => {
        if (!pathD || !layout.width || !layout.height) {
            return null;
        }

        return (
            <svg
                aria-hidden="true"
                width={0}
                height={0}
                style={{ position: 'absolute' }}
            >
                <defs>
                    <mask
                        id={maskId}
                        maskUnits="userSpaceOnUse"
                        maskContentUnits="userSpaceOnUse"
                        x={0}
                        y={0}
                        width={layout.width}
                        height={layout.height}
                    >
                        <path d={pathD} fill="white" fillRule="evenodd" />
                    </mask>
                </defs>
            </svg>
        );
    }, [layout.height, layout.width, maskId, pathD]);

    return (
        <View
            {...rest}
            pointerEvents="box-none"
            style={maskStyle ? [containerStyle, maskStyle] : containerStyle}
            onLayout={handleLayout}
        >
            {maskDefinition}
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
            <View pointerEvents="none" style={styles.childrenContainer}>
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


