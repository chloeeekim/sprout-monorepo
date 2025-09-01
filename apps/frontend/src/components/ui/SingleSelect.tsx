import React, {useState, useRef, useEffect } from "react";
import {X} from "lucide-react";

interface Option {
    value: string;
    label: string;
}

interface SingleSelectProps {
    options: Option[];
    value: string | null;
    onChange: (value: string | null) => void;
    onCreate?: (inputBalue: string) => void;
    placeholder?: string;
}

const SingleSelect: React.FC<SingleSelectProps> = ({
    options, value, onChange, onCreate, placeholder = "항목을 선택하거나 생성하세요."
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const selectRef = useRef<HTMLDivElement>(null);

    const filteredOptions = options.filter((option) => option.label.toLowerCase().includes(searchTerm.toLowerCase()));

    const selectedOption = options.find((option) => option.value === value);

    useEffect(() => {
        const handleClickOutside = (event: globalThis.MouseEvent) => {
            if (selectRef.current && !selectRef.current.contains(event.target as Node)) {
                setIsOpen(false);
                setSearchTerm('');
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const handleSelect = (optionValue: string) => {
        onChange(optionValue);
        setIsOpen(false);
        setSearchTerm('');
    };

    const handleDelete = (value: string) => {
        onChange(null);
    }

    const handleCreate = () => {
        if (onCreate && searchTerm) {
            onCreate(searchTerm);
            setIsOpen(false);
            setSearchTerm('');
        }
    };

    return (
        <div className="relative flex-1" ref={selectRef}>
            <div className="rounded-md px-2 py-1 w-full flex justify-between items-center cursor-pointer bg-white hover:bg-gray-100"
                 onClick={() => setIsOpen(!isOpen)}>
                {selectedOption ? (
                    <div key={selectedOption.value} className="bg-gray-200 text-gray-700 rounded-full px-2 py-1 flex items-center gap-1 text-sm">
                        <span>{selectedOption.label}</span>
                        {isOpen && (
                            <button onClick={(e) => {e.stopPropagation(); handleDelete(selectedOption?.value); }}
                                    className="hover:text-red-500 rounded-full cursor-pointer" >
                                <X size={14} />
                            </button>
                        )}
                    </div>
                ) : (
                    <span className="text-gray-500 py-1">{placeholder}</span>
                )}
            </div>

            {isOpen && (
                <div className="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-md shadow-lg">
                    <div className="p-2">
                        <input type="text" placeholder="검색..." className="w-full px-2 py-1 border border-gray-200 rounded-md focus:outline-none"
                               value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} autoFocus />
                    </div>
                    <ul className="max-h-60 overflow-y-auto pb-2">
                        {filteredOptions.map((option) => (
                            <li key={option.value} className="px-3 py-2 hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleSelect(option.value)} >
                                {option.label}
                            </li>
                        ))}
                        {onCreate && searchTerm && !filteredOptions.some(o => o.label.toLowerCase() === searchTerm.toLowerCase()) && (
                            <li className="px-3 py-2 hover:bg-gray-100 cursor-pointer text-blue-600"
                                onClick={handleCreate} >
                                "{searchTerm}" 생성
                            </li>
                        )}
                        {filteredOptions.length === 0 && !onCreate && (
                            <li className="px-3 py-2 text-gray-500">
                                결과가 없습니다.
                            </li>
                        )}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default SingleSelect;