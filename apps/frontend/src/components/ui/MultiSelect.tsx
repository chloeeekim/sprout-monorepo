import React, { useState, useRef, useEffect } from "react";
import { X } from "lucide-react";

interface Option {
    value: string;
    label: string;
}

interface MultiSelectProps {
    options: Option[];
    selected: string[];
    onChange: (selected: string[]) => void;
    onCreate?: (inputValue: string) => void;
    placeholder?: string;
}

const MultiSelect: React.FC<MultiSelectProps> = ({
    options, selected, onChange, onCreate, placeholder = "태그를 선택하거나 생성하세요."
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const selectRef = useRef<HTMLDivElement>(null);
    const [created, setCreated] = useState(false);

    const handleSelect = (value: string) => {
        if (!selected.includes(value)) {
            onChange([...selected, value]);
        }
    };

    const handleDelete = (value: string) => {
        onChange(selected.filter((v) => v !== value));
    }

    const handleCreate = async () => {
        if (onCreate && searchTerm) {
            setCreated(true);
            await onCreate(searchTerm);
        }
    };

    const filteredOptions = options.filter((option) => option.label.toLowerCase().includes(searchTerm.toLowerCase()));

    const selectedOptions = selected.map(v => options.find(o => o.value === v)).filter(Boolean);

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

    useEffect(() => {
        if (created) {
            onChange([...selected, options.find((option) => option.label === searchTerm)?.value])
            setSearchTerm('');
            setCreated(false);
        }
    }, [options]);

    return (
        <div className="relative flex-1" ref={selectRef}>
            <div className="rounded-md px-2 py-1 w-full flex flex-wrap items-center gap-1 cursor-pointer bg-white hover:bg-gray-100"
                 onClick={() => setIsOpen(!isOpen)} >
                {selectedOptions.length > 0 ? (
                    selectedOptions.map((option) => (
                        <div key={option.value} className="bg-gray-200 text-gray-700 rounded-full px-2 py-1 flex items-center gap-1 text-sm">
                            <span>{option.label}</span>
                            {isOpen && (
                                <button onClick={(e) => { e.stopPropagation(); handleDelete(option.value); }}
                                        className="hover:text-red-500 rounded-full cursor-pointer" >
                                    <X size={14} />
                                </button>
                            )}
                        </div>
                    ))
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
                        {onCreate && searchTerm && !filteredOptions.some(o => o.label.toLowerCase() === searchTerm.toLowerCase()) && (
                            <li className="px-3 py-2 hover:bg-gray-100 cursor-pointer text-blue-600"
                                onClick={handleCreate} >
                                "{searchTerm}" 생성
                            </li>
                        )}
                        {filteredOptions.map((option) => (
                            <li key={option.value} className="px-3 py-2 hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleSelect(option.value)} >
                                <span>{option.label}</span>
                            </li>
                        ))}
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

export default MultiSelect;