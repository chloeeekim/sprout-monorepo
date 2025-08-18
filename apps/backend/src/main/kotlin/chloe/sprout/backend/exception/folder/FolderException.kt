package chloe.sprout.backend.exception.folder

import chloe.sprout.backend.exception.CustomException

class FolderNotFoundException() : CustomException(FolderErrorCode.FOLDER_NOT_FOUND)
class FolderNameRequiredException() : CustomException(FolderErrorCode.FOLDER_NAME_REQUIRED)
class FolderOwnerMismatchException() : CustomException(FolderErrorCode.FOLDER_OWNER_MISMATCH)
class FolderNameAlreadyExistsException() : CustomException(FolderErrorCode.FOLDER_NAME_ALREADY_EXISTS)