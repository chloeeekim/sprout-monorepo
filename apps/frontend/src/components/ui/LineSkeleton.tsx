import React from "react";
import clsx from "clsx";

interface LineSkeletonProps {
    height?: string;
    width?: string;
    rounded?: string;
}

const LineSkeleton: React.FC<LineSkeletonProps> = ({ height = "h-5", width = "w-full", rounded = "rounded-full" }) => {
    return (
        <div className={clsx("animate-pulse bg-gray-200", height, width, rounded)} />
    );
};

export default LineSkeleton;