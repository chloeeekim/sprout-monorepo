import type { NoteSimpleResponse } from '@sprout/shared-types';
import {Link} from "react-router-dom";

interface NotePreviewPanelProps {
    note: NoteSimpleResponse;
    onClose?: () => void;
}

const NotePreviewPanel: React.FC<NotePreviewPanelProps> = ({ note, onClose }) => {
    return (
        <div className="absolute top-20 right-5 w-[350px] max-h-1/3 bg-white shadow-lg rounded-lg z-10 flex flex-col p-4">
            <div className="flex justify-between items-center mb-3 flex-shrink-0">
                <h2 className="text-lg font-semibold flex-grow overflow-hidden text-ellipsis whitespace-nowrap">
                    {note.title}
                </h2>
                <div className="flex gap-2 ml-2">
                    <Link to={`/notes/${note.id}`} className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200 text-sm no-underline text-gray-800">
                        전체 보기
                    </Link>
                    <button onClick={onClose} className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200">
                        X
                    </button>
                </div>
            </div>
            <div className="flex-grow overflow-y-auto whitespace-pre-wrap break-words bg-gray-50 p-2 rounded">
                <p className="text-sm text-gray-700">
                    {note.content || "내용이 없습니다."}
                </p>
            </div>
        </div>
    );
};

export default NotePreviewPanel;