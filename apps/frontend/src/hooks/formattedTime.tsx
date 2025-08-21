import { useState, useEffect } from "react";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/ko";

dayjs.extend(relativeTime);
dayjs.locale("ko");

const formattedTime = (date?: string) => {
    const [now, setNow] = useState(Date.now());

    useEffect(() => {
        const interval = setInterval(() => {
            setNow(Date.now());
        }, 60 * 1000);
        return () => clearInterval(interval);
    }, []);

    if (!date) return "";

    const dayjsDate = dayjs(date);
    return dayjsDate.format("YYYY-MM-DD HH:mm") + " · " + dayjsDate.fromNow();
};

export default formattedTime;