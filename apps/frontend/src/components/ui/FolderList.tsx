import React, { useEffect, useState } from "react";
import { useFolderStore } from "../../stores/folderStore";
import { FolderPlus, Trash2, Edit, Check, X, Folder as FolderIcon } from "lucide-react";

export const FolderList = () => {
    const { folders, selectedFolderId, fetchFolders, addFolder, editFolder, removeFolder, selectFolder } = useFolderStore();
    const [isCreating, setIsCreating] = useState(false);
    const [newFolderName, setNewFolderName] = useState('');
    const [editingFolderId, setEditingFolderId] = useState<string | null>(null);
    const [editingFolderName, setEditingFolderName] = useState('');

    useEffect(() => {
        fetchFolders();
    }, [fetchFolders]);

    const handleCreateFolder = async () => {
        if (newFolderName.trim()) {
            await addFolder(newFolderName.trim());
            setNewFolderName('');
            setIsCreating(false);
        }
    };

    const handleUpdateFolder = async (id: string) => {
        if (editingFolderName.trim()) {
            await editFolder(id, editingFolderName.trim());
            setEditingFolderId(null);
            setEditingFolderName('');
        }
    };

    const startEditing = (folder: {id: string, name: string}) => {
        setEditingFolderId(folder.id);
        setEditingFolderName(folder.name);
    };

    const handleCancelCreation = () => {
        setIsCreating(false);
        setNewFolderName('');
    };

    const handleCancelEditing = () => {
        setEditingFolderId(null);
        setEditingFolderName('');
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-2 px-2">
                <h3 className="text-xs font-semibold uppercase text-gray-400">Folders</h3>
                <button onClick={() => setIsCreating(true)} className="p-1 hover:bg-gray-200 rounded cursor-pointer">
                    <FolderPlus size={16} className="text-gray-500" />
                </button>
            </div>
            {isCreating && (
                <div className="flex items-center w-full px-2 py-1">
                    <FolderIcon size={16} className="mr-2 flex-shrink-0" />
                    <input type="text" value={newFolderName} onChange={(e) => setNewFolderName(e.target.value)}
                           placeholder="New folder name" className="flex-1 min-w-0 bg-transparent focus:outline-none text-sm"
                           onKeyDown={(e) => e.key === "Enter" && handleCreateFolder()}
                           onBlur={handleCancelCreation}
                           autoFocus
                    />
                    <div className="h-7 flex items-center">
                        <button onClick={handleCreateFolder} className="p-1 hover:bg-gray-200 rounded cursor-pointer"
                                onMouseDown={(e) => e.preventDefault()}>
                            <Check size={16} className="text-gray-600" />
                        </button>
                        <button onClick={handleCancelCreation} className="p-1 hover:bg-gray-200 rounded cursor-pointer"
                                onMouseDown={(e) => e.preventDefault()}>
                            <X size={16} className="text-gray-600" />
                        </button>
                    </div>
                </div>
            )}
            <ul>
                {folders.map((folder) => (
                    <li key={folder.id} className={`group flex items-center justify-between rounded-md text-sm ${selectedFolderId === folder.id ? 'bg-gray-200 font-semibold' : 'hover:bg-gray-100'}`}>
                        {editingFolderId === folder.id ? (
                            <div className="flex items-center w-full px-2 py-1">
                                <FolderIcon size={16} className="mr-2 flex-shrink-0 text-gray-600" />
                                <input type="text" value={editingFolderName} onChange={(e) => setEditingFolderName(e.target.value)}
                                       onKeyDown={(e) => e.key === "Enter" && handleUpdateFolder(folder.id)}
                                       onBlur={handleCancelEditing} className="flex-1 min-w-0 bg-transparent focus:outline-none text-gray-600"
                                       autoFocus
                                />
                                <div className="h-7 flex items-center">
                                    <button onClick={() => handleUpdateFolder(folder.id)} className="p-1 hover:bg-gray-200 rounded cursor-pointer"
                                            onMouseDown={(e) => e.preventDefault()}>
                                        <Check size={16} className="text-gray-600" />
                                    </button>
                                    <button onClick={handleCancelEditing} className="p-1 hover:bg-gray-200 rounded cursor-pointer"
                                            onMouseDown={(e) => e.preventDefault()}>
                                        <X size={16} className="text-gray-600" />
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className="flex items-center justify-between w-full cursor-pointer px-2 py-1"
                                 onClick={() => selectFolder(folder.id)}>
                                <div className="flex items-center truncate">
                                    <FolderIcon size={16} className="mr-2 flex-shrink-0 text-gray-600" />
                                    <span className="truncate text-gray-600">{folder.name}</span>
                                </div>
                                <div className="flex items-center justify-end w-12 h-7 pl-2">
                                    <span className="text-xs text-gray-500 px-2 py-1 bg-gray-200 rounded-full group-hover:hidden">{folder.count}</span>
                                    <div className="hidden items-center opacity-0 group-hover:flex group-hover:opacity-100 transition-opacity">
                                        <button onClick={(e) => {e.stopPropagation(); startEditing(folder);}}
                                                className="p-1 hover:bg-gray-200 rounded cursor-pointer">
                                            <Edit size={16} className="text-gray-600" />
                                        </button>
                                        <button onClick={(e) => {e.stopPropagation(); removeFolder(folder.id);}}
                                                className="p-1 hover:bg-gray-200 rounded cursor-pointer">
                                            <Trash2 size={16} className="text-gray-600" />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

