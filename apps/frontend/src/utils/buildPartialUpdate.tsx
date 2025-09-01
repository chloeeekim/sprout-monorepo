type PartialUpdate<T> = {
    [K in keyof T]?: T[K];
};

/**
 * 변경된 필드만 추출
 * @param original 기존 데이터
 * @param updated 변경된 데이터
 * @return 변경된 필드만 포함된 객체
 */
export const buildPartialUpdate = <T extends Record<string, any>>(
    original: T,
    updated: Partial<T>
): PartialUpdate<T> => {
    const result: PartialUpdate<T> = {};

    (Object.keys(updated) as (keyof T)[]).forEach((key) => {
        const newValue = updated[key];
        const oldValue = original[key];

        // undefined는 보내지 않음
        if (newValue !== undefined && newValue !== oldValue) {
            result[key] = newValue;
        }
    });

    return result;
};